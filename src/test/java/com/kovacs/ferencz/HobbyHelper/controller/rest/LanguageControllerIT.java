package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.TestUtil;
import com.kovacs.ferencz.HobbyHelper.controller.rest.exceptions.ExceptionTranslator;
import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.UpdateLanguageKeyVM;
import com.kovacs.ferencz.HobbyHelper.domain.Role;
import com.kovacs.ferencz.HobbyHelper.domain.User;
import com.kovacs.ferencz.HobbyHelper.repository.RoleRepository;
import com.kovacs.ferencz.HobbyHelper.repository.UserRepository;
import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import com.kovacs.ferencz.HobbyHelper.service.BasicLocaleService;
import com.kovacs.ferencz.HobbyHelper.service.UserService;
import com.kovacs.ferencz.HobbyHelper.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static com.kovacs.ferencz.HobbyHelper.TestUtil.createFormattingConversionService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LanguageControllerIT {

    private static final String DEFAULT_LOGIN = "user";
    private static final String UPDATED_LOGIN = "userNew";

    private static final Long DEFAULT_ID = 1L;

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passpassverypass";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "newmail@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "FirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "LastName";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BasicLocaleService basicLocaleService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    @Autowired
    private LanguageController languageController;

    private MockMvc restLanguageControllerMockMvc;

    private User user;

    @BeforeEach
    public void setup() {
        clearDatabase();
        user = userRepository.saveAndFlush(initUser());
        MockitoAnnotations.initMocks(this);
        this.restLanguageControllerMockMvc = MockMvcBuilders.standaloneSetup(languageController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    @Test
    @Transactional
    @WithMockUser(username="user", roles = {"USER", "ADMIN"})
    public void testUpdateLanguageKeyWhileLoggedIn() throws Exception {
        //GIVEN
        UpdateLanguageKeyVM updateLanguageKeyVM = new UpdateLanguageKeyVM();
        updateLanguageKeyVM.setLangKey("hu");
        //WHEN
        restLanguageControllerMockMvc.perform(post("/api/language")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updateLanguageKeyVM)))
                .andExpect(status().isOk());
        //THEN
        Optional<User> updated = userRepository.findById(user.getId());
        assertTrue(updated.isPresent());
        User updatedUser = updated.get();
        assertEquals("hu", updatedUser.getLangKey());
    }

    @Test
    @Transactional
    @WithAnonymousUser
    public void testUpdateLanguageKeyWhileNotLoggedIn() throws Exception {
        //GIVEN
        assertEquals(Locale.forLanguageTag("en"), basicLocaleService.getBasicLocale());
        UpdateLanguageKeyVM updateLanguageKeyVM = new UpdateLanguageKeyVM();
        updateLanguageKeyVM.setLangKey("hu");
        //WHEN
        restLanguageControllerMockMvc.perform(post("/api/language")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updateLanguageKeyVM)))
                .andExpect(status().isOk());
        //THEN
        assertEquals(Locale.forLanguageTag("hu"), basicLocaleService.getBasicLocale());
    }


    private void clearDatabase() {
        userRepository.deleteAll();
        userRepository.flush();
        roleRepository.deleteAll();
        roleRepository.flush();
    }

    private User initUser() {
        User result = new User();
        result.setLangKey(DEFAULT_LANGKEY);
        result.setLastName(DEFAULT_LASTNAME);
        result.setFirstName(DEFAULT_FIRSTNAME);
        result.setEmail(DEFAULT_EMAIL);
        Role role = new Role(AuthoritiesConstants.USER);
        roleRepository.saveAndFlush(role);
        result.setRoles(new HashSet(Arrays.asList(role)));
        result.setId(null);
        result.setUsername(DEFAULT_LOGIN);
        result.setActivated(false);
        result.setPassword(DEFAULT_PASSWORD);
        return result;
    }


}
