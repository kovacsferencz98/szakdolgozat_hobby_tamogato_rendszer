package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.*;
import com.kovacs.ferencz.HobbyHelper.domain.Location;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.LocationService;
import com.kovacs.ferencz.HobbyHelper.service.MailService;
import com.kovacs.ferencz.HobbyHelper.service.UserDetailsService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.dto.LocationDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.PasswordChangeDTO;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.*;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDetailsDTO;
import com.kovacs.ferencz.HobbyHelper.service.util.LocaleUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    public static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final MessageSource messageSource;

    private final UserRepository userRepository;

    private final UserService userService;

    private final LocationService locationService;

    private final MailService mailService;

    private final UserDetailsService userDetailsService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService,
                           LocationService locationService, UserDetailsService userDetailsService, MessageSource messageSource) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.locationService = locationService;
        this.userDetailsService = userDetailsService;
        this.messageSource = messageSource;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param registrationVM the user to be registered View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody RegistrationVM registrationVM) {
        if (!checkPasswordLength(registrationVM.getPassword())) {
            throw new InvalidPasswordException(messageSource.getMessage("password.invalid", null, LocaleUtil.getUserLocale()));
        }
        User user = userService.registerUser(registrationVM, registrationVM.getPassword());
        mailService.sendActivationEmail(user);
        Location location = locationService.registerLocation(registrationVM);
        userDetailsService.registerUserDetails(registrationVM, user, location);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException(messageSource.getMessage("activate.notFound", null, LocaleUtil.getUserLocale()));
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AccountVM getAccount() {
        UserDTO userDTO = userService.getUserWithAuthorities()
                .map(UserDTO::new)
                .orElseThrow(() -> new AccountResourceException(messageSource.getMessage("user.notFound",
                        null, LocaleUtil.getUserLocale())));
        Optional<UserDetailsDTO> userDetailsDTO = userDetailsService.findByUserId(userDTO.getId());
        if(userDetailsDTO.isEmpty()) {
            throw new AccountResourceException(messageSource.getMessage("userDetail.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        Optional<LocationDTO> locationDTO = locationService.findOne(userDetailsDTO.get().getResidenceId());
        if(locationDTO.isEmpty()) {
            throw new AccountResourceException(messageSource.getMessage("location.notFound",
                    null, LocaleUtil.getUserLocale()));
        }
        AccountVM account = AccountVM.createAccountVM(userDTO, locationDTO.get(), userDetailsDTO.get());
        return account;
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param accountUpdateVM the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AccountUpdateVM accountUpdateVM) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AccountResourceException(messageSource.getMessage("currentUser.notFound", null, LocaleUtil.getUserLocale())));
        if(isUserTheSame(accountUpdateVM, userLogin)) {
            checkDuplicateMail(accountUpdateVM, userLogin);
            updateAccountLocation(accountUpdateVM);
            updateUserDetails(accountUpdateVM);
            userService.updateUser(accountUpdateVM.getFirstName(), accountUpdateVM.getLastName(), accountUpdateVM.getEmail());
        }
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException(messageSource.getMessage("password.invalid", null, LocaleUtil.getUserLocale()));
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param resetPasswordVM the view model containing the email address of the user.
     * @throws EmailNotFoundException {@code 400 (Bad Request)} if the email address is not registered.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody ResetPasswordVM resetPasswordVM) {
        log.debug("Request password reset: " + resetPasswordVM.getMail());
        mailService.sendPasswordResetMail(
                userService.requestPasswordReset(resetPasswordVM.getMail())
                        .orElseThrow(() -> new EmailNotFoundException(messageSource.getMessage("email.notFound", null, LocaleUtil.getUserLocale())))
        );
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException(messageSource.getMessage("password.invalid", null, LocaleUtil.getUserLocale()));
        }
        Optional<User> user =
                userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException(messageSource.getMessage("resetKey.notFound", null, LocaleUtil.getUserLocale()));
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }

    private void updateUserDetails(@RequestBody @Valid AccountUpdateVM accountUpdateVM) {
        UserDetailsDTO userDetailsDTO = userDetailsService.findOne(accountUpdateVM.getDetailsId())
                .orElseThrow(() -> new AccountResourceException(messageSource.getMessage("userDetail.notFound",
                        null, LocaleUtil.getUserLocale())));
        userDetailsDTO.setDescription(accountUpdateVM.getDescription());
        userDetailsDTO.setProfilePicId(accountUpdateVM.getProfilePicId());
        userDetailsService.save(userDetailsDTO);
    }

    private void updateAccountLocation(@RequestBody @Valid AccountUpdateVM accountUpdateVM) {
        LocationDTO locationDTO = locationService.findOne(accountUpdateVM.getLocationId())
                .orElseThrow(() -> new AccountResourceException(messageSource.getMessage("location.notFound",
                        null, LocaleUtil.getUserLocale())));
        locationDTO.setLongitude(accountUpdateVM.getLongitude());
        locationDTO.setLatitude(accountUpdateVM.getLongitude());
        locationDTO.setApartment(accountUpdateVM.getApartment());
        locationDTO.setNumber(accountUpdateVM.getNumber());
        locationDTO.setStreet(accountUpdateVM.getStreet());
        locationDTO.setZip(accountUpdateVM.getZip());
        locationDTO.setRegion(accountUpdateVM.getRegion());
        locationDTO.setCity(accountUpdateVM.getCity());
        locationDTO.setCountry(accountUpdateVM.getCountry());
        locationService.save(locationDTO);
    }

    private void checkDuplicateMail(@RequestBody @Valid AccountUpdateVM accountUpdateVM, String userLogin) {
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(accountUpdateVM.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getUsername().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException(messageSource.getMessage("email.used", null, LocaleUtil.getUserLocale()));
        }
    }

    private boolean isUserTheSame(@RequestBody @Valid AccountUpdateVM accountUpdateVM, String userLogin) {
        Optional<User> user = userService.getUserWithAuthorities(accountUpdateVM.getUserId());
        return user.isPresent() && userLogin.equals(user.get().getUsername());
    }
}
