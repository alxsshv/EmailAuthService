package com.alxsshv.controller;

import com.alxsshv.AbstractIntegrationTest;
import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;
import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import org.json.JSONArray;
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
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class AccountControllerIntegrTest  extends AbstractIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeEach
    void fillDatabase() {
        Account account1 = Account.builder()
                .id(UUID.randomUUID())
                .email("one@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();

        Account account2 = Account.builder()
                .id(UUID.randomUUID())
                .email("two@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        accountRepository.saveAll(Set.of(account1, account2));
    }

    @AfterEach
    void clearDatabasesState() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "authorities", "accounts");
    }

    public RequestPostProcessor authorizeWith(String accessToken) {
        return request -> {
            try {
                request.addHeader("Authorization", "Bearer " + accessToken);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return request;
        };
    }

    public String getAccessTokenFor(String email) throws Exception {
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
        return authResponse.accessToken();
    }

    @Test
    @DisplayName("Test getAllAccounts method when invoke method then return collection of accounts")
    void testGetAllAccounts_whenInvokeMethode_thenReturnCollectionOfAccounts() throws Exception {
        String email = "test@email.com";
        String accessToken = getAccessTokenFor(email);
        int expectedAccountListSize = 3;

        String accounts = mockMvc.perform(MockMvcRequestBuilders.get("/account/all")
                        .with(authorizeWith(accessToken))
                        .with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        int actualAccountSize = new JSONArray(accounts).toList().size();

        Assertions.assertEquals(expectedAccountListSize, actualAccountSize);


    }


}
