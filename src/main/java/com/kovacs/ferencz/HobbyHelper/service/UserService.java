package com.kovacs.ferencz.HobbyHelper.service;

import com.kovacs.ferencz.HobbyHelper.config.Constants;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.security.SecurityUtils;
import com.kovacs.ferencz.HobbyHelper.service.dto.UserDTO;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
import com.kovacs.ferencz.HobbyHelper.service.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final UserDetailsService userDetailsService;

    private final EventService eventService;

    private final EventParticipantService eventParticipantService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                       UserMapper userMapper, UserDetailsService userDetailsService,
                       EventService eventService, EventParticipantService eventParticipantService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.userDetailsService = userDetailsService;
        this.eventParticipantService = eventParticipantService;
        this.eventService = eventService;
    }

    /**
     * Activates user based on activation key
     *
     * @param key activation key of the user
     * @return entity
     */
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    userRepository.save(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    /**
     * Completes the reset of the password based on reset key
     * @param newPassword the new password
     * @param key the reset key of the user
     * @return entity
     */
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    userRepository.save(user);
                    return user;
                });
    }

    /**
     * Initiates password reset.
     * @param mail the email address of the user
     * @return
     */
    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
                .filter(User::getActivated)
                .map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    userRepository.save(user);
                    return user;
                });
    }

    /**
     * Changes the language key of the logged in user.
     *
     * @param key the new language key
     */
    public void changeLanguageKey(String key) {
        SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByUsername)
                .ifPresent(user -> {
                    user.setLangKey(key);
                    userRepository.save(user);
                    log.debug("Changed language key for User: {}", user);
                });
    }

    /**
     * Registers a new user.
     *
     * @param userDTO contains the data of the new user
     * @param password the password of the new user
     * @return entity
     */
    public User registerUser(UserDTO userDTO, String password) {
        userRepository.findOneByUsername(userDTO.getUsername().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });
        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            log.debug("Try to remove duplicate: " + userDTO.getEmail());
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });
        User newUser = createUserForRegistration(userDTO, password);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * Creates new user entity.
     *
     * @param userDTO contains the data of the new user
     * @return
     */
    public User createUser(UserDTO userDTO) {
        User user = userFromUserDTO(userDTO);
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     */
    public void updateUser(String firstName, String lastName, String email) {
        SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByUsername)
                .ifPresent(user -> {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email.toLowerCase());
                    userRepository.save(user);
                    log.debug("Changed Information for User: {}", user);
                });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
                .findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    modifyUserBasedOnDTO(userDTO, user);
                    log.debug("Changed Information for User: {}", user);
                    userRepository.save(user);
                    return user;
                })
                .map(UserDTO::new);
    }

    /**
     * Deletes the user.
     *
     * @param username the username of the user to be deleted.
     */
    public void deleteUser(String username) {
        userRepository.findOneByUsername(username).ifPresent(user -> {
            userDetailsService.deleteDetailOfUser(user.getId());
            eventService.deleteEventsOfUser(user.getId());
            eventParticipantService.deleteByUser(user.getId());
            userRepository.delete(user);
            log.debug("Deleted User: {}", user);
        });
    }

    /**
     * Changes the password of the currently logged in user.
     *
     * @param currentClearTextPassword current password in plain text, not encrypted
     * @param newPassword the new password
     */
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByUsername)
                .ifPresent(user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    userRepository.save(user);
                    log.debug("Changed password for User: {}", user);
                });
    }

    /**
     * Return every user.
     *
     * @return entities
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllManagedUsers() {
        return userRepository.findAllByUsernameNot(Constants.ANONYMOUS_USER).stream().map(userMapper::userToUserDTO)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Return user based on username
     *
     * @param username username of the user
     * @return entity
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String username) {
        return userRepository.findOneWithRolesByUsername(username);
    }

    /**
     *  Return user based on email address
     *
     * @param email email address of the user
     * @return entity
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByEmail(String email) {
        return userRepository.findOneByEmailIgnoreCase(email);
    }

    /**
     *  Return user based on id
     *
     * @param id id of the user
     * @return entity
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findOneWithRolesById(id);
    }

    /**
     * Return the currently logged in user
     *
     * @return entity
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithRolesByUsername);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
                .findAllByActivatedIsFalseAndActivationKeyIsNotNull()
                .forEach(user -> {
                    log.debug("Deleting not activated user {}", user.getUsername());
                    deleteUser(user.getUsername());
                });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    public List<String> getAuthorities() {
        return roleRepository.findAll().stream().map(Role::getName).collect(Collectors.toList());
    }

    private User userFromUserDTO(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getRoles() != null) {
            Set<Role> authorities = userDTO.getRoles().stream()
                    .map(roleRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setRoles(authorities);
        }
        return user;
    }

    private User createUserForRegistration(UserDTO userDTO, String password) {
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setUsername(userDTO.getUsername().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail().toLowerCase());
        newUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Role> roles = new HashSet<>();
        roleRepository.findById(AuthoritiesConstants.USER).ifPresent(roles::add);
        newUser.setRoles(roles);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser){
        if (existingUser.getActivated()) {
            return false;
        }

        deleteUser(existingUser.getUsername());
        return true;
    }

    private void modifyUserBasedOnDTO(UserDTO userDTO, User user) {
        user.setUsername(userDTO.getUsername().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setActivated(userDTO.isActivated());
        user.setLangKey(userDTO.getLangKey());
        Set<Role> managedAuthorities = user.getRoles();
        managedAuthorities.clear();
        userDTO.getRoles().stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(managedAuthorities::add);
    }
}
