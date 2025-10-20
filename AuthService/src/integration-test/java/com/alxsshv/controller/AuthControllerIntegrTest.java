package com.alxsshv.controller;

import com.alxsshv.AbstractIntegrationTest;
import com.alxsshv.dto.AccountDto;
import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.entity.Status;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AuthControllerIntegrTest extends AbstractIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @AfterEach
    void clearDatabasesState() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "authorities", "accounts");
    }

    public RequestPostProcessor signInWithUser(String accessToken) {
        return request -> {
            try {
                request.addHeader("Authorization", "Bearer " + accessToken);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return request;
        };
    }

    @Nested
    class TestGetAuthorizationCode {

        @Test
        @DisplayName("Test getAuthorizationCode method when send valid email then return ok status")
        void getAuthorizationCodeMethod_thenSendValidEmail_thenReturnSuccessResponse() throws Exception {
            String email = "test@email.com";

            mockMvc.perform(MockMvcRequestBuilders.post("/auth/code")
                            .param("email", email)
                            .with(csrf()))
                    .andExpect(status().is2xxSuccessful());

            Optional<Account> accountOpt = accountRepository.findByEmail(email);
            Optional<AuthPair> authPairOpt = authPairRepository.findByEmail(email);

            Assertions.assertTrue(accountOpt.isPresent());
            Assertions.assertTrue(authPairOpt.isPresent());
        }

        @Test
        @DisplayName("Test getAuthorizationCode method then email is null then return bad request")
        void getAuthorizationCode_thenEmailIsNull_thenReturnBadRequest() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders.post("/auth/code").with(csrf()))
                    .andExpect(status().isBadRequest());


        }

    }

    @Nested
    class TestLoginMethod {


        @Test
        @DisplayName("Test login method when send valid request then return access token")
        void testLogin_whenSendValidRequest_thenReturnAccessToken() throws Exception {
            String email = "test@email.com";

            mockMvc.perform(MockMvcRequestBuilders.post("/auth/code")
                            .param("email", email)
                            .with(csrf()))
                    .andExpect(status().is2xxSuccessful());
            Optional<AuthPair> authPairOpt = authPairRepository.findByEmail(email);
            AuthPair authPair = authPairOpt.orElseThrow();


            AuthRequest request = new AuthRequest(email, authPair.getCode());
            String jsonRequest = objectMapper.writeValueAsString(request);
            MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest)
                            .with(csrf()))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse();

            AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), AuthResponse.class);

            Assertions.assertFalse(authResponse.accessToken().isEmpty());
        }
    }

    @Nested
    class TestGetCurrentUserMethod {

        @Test
        @DisplayName("Test getCurrentUser when send valid access token then return current user")
        void testGetCurrentUser_whenSendValidToken_thenReturnCurrentUser() throws Exception {
            String email = "test@email.com";

            mockMvc.perform(MockMvcRequestBuilders.post("/auth/code")
                            .param("email", email)
                            .with(csrf()))
                    .andExpect(status().is2xxSuccessful());
            Optional<AuthPair> authPairOpt = authPairRepository.findByEmail(email);
            AuthPair authPair = authPairOpt.orElseThrow();


            AuthRequest signInRequest = new AuthRequest(email, authPair.getCode());
            String signInRequestJson = objectMapper.writeValueAsString(signInRequest);
            MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(signInRequestJson)
                            .with(csrf()))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn().getResponse();
            AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), AuthResponse.class);


            String currentUserJson = mockMvc.perform(MockMvcRequestBuilders.get("/auth")
                            .with(signInWithUser(authResponse.accessToken()))
                            .with(csrf()))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            AccountDto currentUserAccountDto = objectMapper.readValue(currentUserJson, AccountDto.class);
            Assertions.assertEquals(email, currentUserAccountDto.getEmail());
            Assertions.assertEquals(Status.ENABLED.name(), currentUserAccountDto.getStatus());

        }

    }

}
