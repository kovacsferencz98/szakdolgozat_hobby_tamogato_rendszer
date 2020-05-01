package com.kovacs.ferencz.HobbyHelper.security.jwt;

import com.kovacs.ferencz.HobbyHelper.security.AuthoritiesConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenProviderTest {

    private static final long ONE_MINUTE = 60000;

    private Key key;
    private TokenProvider underTest;

    @BeforeEach
    public void setup() {
        underTest = new TokenProvider();
        key = Keys.hmacShaKeyFor(Decoders.BASE64
                .decode("fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8"));

        ReflectionTestUtils.setField(underTest, "key", key);
        ReflectionTestUtils.setField(underTest, "tokenValidityInMilliseconds", ONE_MINUTE);
    }

    @Test
    public void testReturnFalseWhenJWThasInvalidSignature() {
        //GIVEN - in setup
        //WHEN
        boolean isTokenValid = underTest.validateToken(createTokenWithDifferentSignature());
        //THEN
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisMalformed() {
        //GIVEN
        Authentication authentication = createAuthentication();
        String token = underTest.createToken(authentication);
        String invalidToken = token.substring(1);
        //WHEN
        boolean isTokenValid = underTest.validateToken(invalidToken);
        //THEN
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisExpired() {
        //GIVEN
        ReflectionTestUtils.setField(underTest, "tokenValidityInMilliseconds", -ONE_MINUTE);
        Authentication authentication = createAuthentication();
        String token = underTest.createToken(authentication);
        //WHEN
        boolean isTokenValid = underTest.validateToken(token);
        //THEN
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisUnsupported() {
        //GIVEN
        String unsupportedToken = createUnsupportedToken();
        //WHEN
        boolean isTokenValid = underTest.validateToken(unsupportedToken);
        //THEN
        assertThat(isTokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisInvalid() {
        //GIVEN - in setup
        //WHEN
        boolean isTokenValid = underTest.validateToken("");
        //THEN
        assertThat(isTokenValid).isEqualTo(false);
    }

    private Authentication createAuthentication() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
    }

    private String createUnsupportedToken() {
        return Jwts.builder()
                .setPayload("payload")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createTokenWithDifferentSignature() {
        Key otherKey = Keys.hmacShaKeyFor(Decoders.BASE64
                .decode("Xfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8"));

        return Jwts.builder()
                .setSubject("anonymous")
                .signWith(otherKey, SignatureAlgorithm.HS512)
                .setExpiration(new Date(new Date().getTime() + ONE_MINUTE))
                .compact();
    }
}
