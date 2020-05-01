package com.kovacs.ferencz.HobbyHelper.controller.rest;

import com.kovacs.ferencz.HobbyHelper.controller.rest.vm.LoginVM;
import com.kovacs.ferencz.HobbyHelper.security.jwt.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserJWTControllerTest {

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    Authentication authentication;

    @Autowired
    UserJWTController underTest;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void authorizeShouldThrowExceptionIfCantAuthorize() {
        //GIVEN
        LoginVM loginVM = createLoginVM();
        //WHEN
        //THEN
        assertThrows(AuthenticationException.class, () -> {
            underTest.authorize(loginVM);
        });
    }

    private LoginVM createLoginVM() {
        LoginVM loginVM = new LoginVM();
        loginVM.setPassword("password");
        loginVM.setUsername("username");
        return  loginVM;
    }
}