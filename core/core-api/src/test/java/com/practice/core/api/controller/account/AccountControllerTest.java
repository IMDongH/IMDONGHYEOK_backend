package com.practice.core.api.controller.account;

import static org.mockito.ArgumentMatchers.any;
import com.practice.core.api.controller.account.request.CreateAccountRequest;
import com.practice.core.domain.account.AccountService;
import com.practice.core.domain.account.NewAccount;
import com.practice.core.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

class AccountControllerTest extends RestDocsSupport {

    private final AccountService accountService = mock(AccountService.class);

    @Override
    protected Object initController() {
        return new AccountController(accountService);
    }

    @Test
    @DisplayName("계좌 생성 API")
    void createAccount() throws Exception {
        // given
        CreateAccountRequest request = new CreateAccountRequest("123-456-789");
        given(accountService.createAccount(any(NewAccount.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/v1/accounts")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("create-account",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Account")
                                .summary("계좌 생성")
                                .description("새로운 계좌를 생성합니다.")
                                .requestFields(
                                        fieldWithPath("accountNumber").description("계좌 번호"))
                                .responseFields(
                                        fieldWithPath("result").description("응답 결과"),
                                        fieldWithPath("data").description("생성된 계좌 ID"),
                                        fieldWithPath("error").description("에러 정보 (성공 시 null)"))
                                .build())));
    }

    @Test
    @DisplayName("계좌 삭제 API")
    void deleteAccount() throws Exception {
        // given
        Long accountId = 1L;

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-account",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Account")
                                .summary("계좌 삭제")
                                .description("계좌를 삭제합니다.")
                                .build())));
    }

    @Test
    @DisplayName("입금 API")
    void deposit() throws Exception {
        // given
        Long accountId = 1L;
        com.practice.core.api.controller.account.request.DepositRequest request = new com.practice.core.api.controller.account.request.DepositRequest(
                java.math.BigDecimal.valueOf(10000), "Deposit");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/{accountId}/deposit", accountId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("deposit-account",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Account")
                                .summary("계좌 입금")
                                .description("계좌에 입금합니다.")
                                .requestFields(
                                        fieldWithPath("amount").description("입금 금액"),
                                        fieldWithPath("description").description("입금 설명"))
                                .responseFields(
                                        fieldWithPath("result").description("응답 결과"),
                                        fieldWithPath("data").description("계좌 ID"),
                                        fieldWithPath("error").description("에러 정보 (성공 시 null)"))
                                .build())));
    }

    @Test
    @DisplayName("출금 API")
    void withdraw() throws Exception {
        // given
        Long accountId = 1L;
        com.practice.core.api.controller.account.request.WithdrawRequest request = new com.practice.core.api.controller.account.request.WithdrawRequest(
                java.math.BigDecimal.valueOf(5000), "Withdraw");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/{accountId}/withdraw", accountId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("withdraw-account",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Account")
                                .summary("계좌 출금")
                                .description("계좌에서 출금합니다.")
                                .requestFields(
                                        fieldWithPath("amount").description("출금 금액"),
                                        fieldWithPath("description").description("출금 설명"))
                                .responseFields(
                                        fieldWithPath("result").description("응답 결과"),
                                        fieldWithPath("data").description("계좌 ID"),
                                        fieldWithPath("error").description("에러 정보 (성공 시 null)"))
                                .build())));
    }

    @Test
    @DisplayName("계좌 이체 API")
    void transfer() throws Exception {
        // given
        Long accountId = 1L;
        com.practice.core.api.controller.account.request.TransferRequest request = new com.practice.core.api.controller.account.request.TransferRequest(
                "RECEIVER-123", java.math.BigDecimal.valueOf(1000), "Transfer");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/{accountId}/transfer", accountId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("transfer-account",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Account")
                                .summary("계좌 이체")
                                .description("계좌 이체를 수행합니다.")
                                .requestFields(
                                        fieldWithPath("receiverAccountNumber").description("수취 계좌 번호"),
                                        fieldWithPath("amount").description("이체 금액"),
                                        fieldWithPath("description").description("이체 설명"))
                                .responseFields(
                                        fieldWithPath("result").description("응답 결과"),
                                        fieldWithPath("data").description("데이터 (null)"),
                                        fieldWithPath("error").description("에러 정보 (성공 시 null)"))
                                .build())));
    }
}
