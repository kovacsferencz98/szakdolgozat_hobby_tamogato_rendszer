package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.UnauthorizedRequest;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.MailService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.BadRequestAlertException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.EmailAlreadyUsedException;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.LoginAlreadyUsedException;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${application.name}")
    private String applicationName;

    private final UserService userService;

    private final MailService mailService;

    private final MessageSource messageSource;

    public UserResource(UserService userService, MailService mailService,
                        MessageSource messageSource) {

        this.userService = userService;
        this.mailService = mailService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);
        if (userDTO.getId() != null) {
            throw new BadRequestAlertException(messageSource.getMessage("user.idExists", null, LocaleUtil.getUserLocale()), "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userService.getUserWithAuthoritiesByLogin(userDTO.getUsername().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException(messageSource.getMessage("username.used", null, LocaleUtil.getUserLocale()));
        } else if (userService.getUserWithAuthoritiesByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException(messageSource.getMessage("email.used", null, LocaleUtil.getUserLocale()));
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getUsername()))
                .body(newUser);
        }
    }

    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        if(!isAuthorizedForModification(userDTO.getUsername())) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        Optional<User> existingUser = userService.getUserWithAuthoritiesByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException(messageSource.getMessage("email.used", null, LocaleUtil.getUserLocale()));
        }
        existingUser = userService.getUserWithAuthoritiesByLogin(userDTO.getUsername().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException(messageSource.getMessage("username.used", null, LocaleUtil.getUserLocale()));
        }
        Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

        return updatedUser.isPresent() ? ResponseEntity.ok().body(updatedUser.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        final List<UserDTO> users = userService.getAllManagedUsers();
        return users;
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/users/authorities")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    /**
     * {@code GET /users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        Optional<UserDTO> user = userService.getUserWithAuthoritiesByLogin(login)
                .map(UserDTO::new);

        return user.isPresent() ? ResponseEntity.ok().body(user.get()) : ResponseEntity.notFound().build();
    }

    /**
     * {@code DELETE /users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        if(!isAuthorizedForModification(login)) {
            throw new UnauthorizedRequest(messageSource.getMessage("unauthorized.request",
                    null, LocaleUtil.getUserLocale()));
        }
        userService.deleteUser(login);
        return ResponseEntity.noContent().build();
    }

    private boolean isAuthorizedForModification(String username) {
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)
                || username.equals(SecurityUtils.getCurrentUserLogin().get());
    }
}
