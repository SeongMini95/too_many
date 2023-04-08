package com.ojeomme.controller.support;

import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AcceptanceTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenProvider authTokenProvider;

    protected User user;

    protected String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        user = userRepository.save(User.builder()
                .oauthId(RandomStringUtils.randomNumeric(6))
                .oauthProvider(OauthProvider.NAVER)
                .email("test123@naver.com")
                .nickname("test123")
                .profile("")
                .build());

        accessToken = authTokenProvider.createAuthToken(user.getId()).getToken();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }
}
