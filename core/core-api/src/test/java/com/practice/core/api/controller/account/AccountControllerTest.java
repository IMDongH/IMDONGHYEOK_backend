package com.practice.core.api.controller.account;

import static org.mockito.ArgumentMatchers.any;
import com.practice.core.api.controller.account.request.CreateAccountRequest;
import com.practice.core.api.controller.account.response.TransactionResponse;
import com.practice.core.domain.account.AccountService;
import com.practice.core.domain.account.NewAccount;
import com.practice.core.enums.TransactionType;
import com.practice.core.support.RestDocsSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                                                                                fieldWithPath("accountNumber")
                                                                                                .description("계좌 번호"))
                                                                .responseFields(
                                                                                fieldWithPath("result")
                                                                                                .description("응답 결과"),
                                                                                fieldWithPath("data").description(
                                                                                                "생성된 계좌 ID"),
                                                                                fieldWithPath("error").description(
                                                                                                "에러 정보 (성공 시 null)"))
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
                                                                                fieldWithPath("amount")
                                                                                                .description("입금 금액"),
                                                                                fieldWithPath("description")
                                                                                                .description("입금 설명"))
                                                                .responseFields(
                                                                                fieldWithPath("result")
                                                                                                .description("응답 결과"),
                                                                                fieldWithPath("data")
                                                                                                .description("계좌 ID"),
                                                                                fieldWithPath("error").description(
                                                                                                "에러 정보 (성공 시 null)"))
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
                                                                                fieldWithPath("amount")
                                                                                                .description("출금 금액"),
                                                                                fieldWithPath("description")
                                                                                                .description("출금 설명"))
                                                                .responseFields(
                                                                                fieldWithPath("result")
                                                                                                .description("응답 결과"),
                                                                                fieldWithPath("data")
                                                                                                .description("계좌 ID"),
                                                                                fieldWithPath("error").description(
                                                                                                "에러 정보 (성공 시 null)"))
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
                                                                                fieldWithPath("receiverAccountNumber")
                                                                                                .description("수취 계좌 번호"),
                                                                                fieldWithPath("amount")
                                                                                                .description("이체 금액"),
                                                                                fieldWithPath("description")
                                                                                                .description("이체 설명"))
                                                                .responseFields(
                                                                                fieldWithPath("result")
                                                                                                .description("응답 결과"),
                                                                                fieldWithPath("data").description(
                                                                                                "데이터 (null)"),
                                                                                fieldWithPath("error").description(
                                                                                                "에러 정보 (성공 시 null)"))
                                                                .build())));
        }

        @Test
        @DisplayName("거래내역 조회 API - 전체")
        void getTransactions_All() throws Exception {
                // given
                Long accountId = 1L;
                com.practice.core.domain.transaction.Transaction transaction = com.practice.core.domain.transaction.Transaction
                                .builder()
                                .id(1L)
                                .type(TransactionType.DEPOSIT)
                                .amount(BigDecimal.valueOf(10000))
                                .balanceSnapshot(BigDecimal.valueOf(10000))
                                .description("Deposit")
                                .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                                .build();

                com.practice.core.support.Page<com.practice.core.domain.transaction.Transaction> page = new com.practice.core.support.Page<>(
                                List.of(transaction), false);

                given(accountService.readTransactions(eq(accountId), eq(null),
                                any(com.practice.core.support.OffsetLimit.class)))
                                .willReturn(page);

                // when & then
                mockMvc.perform(get("/api/v1/accounts/{accountId}/transactions", accountId)
                                .param("offset", "0")
                                .param("limit", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andDo(document("get-transactions-all",
                                                resource(ResourceSnippetParameters.builder()
                                                                .tag("Account")
                                                                .summary("거래내역 조회")
                                                                .description("계좌의 거래내역을 조회합니다.")
                                                                .queryParameters(
                                                                                com.epages.restdocs.apispec.ResourceDocumentation
                                                                                                .parameterWithName(
                                                                                                                "type")
                                                                                                .description("거래 유형 (DEPOSIT, WITHDRAW, TRANSFER)")
                                                                                                .optional(),
                                                                                com.epages.restdocs.apispec.ResourceDocumentation
                                                                                                .parameterWithName(
                                                                                                                "offset")
                                                                                                .description("페이지 오프셋"),
                                                                                com.epages.restdocs.apispec.ResourceDocumentation
                                                                                                .parameterWithName(
                                                                                                                "limit")
                                                                                                .description("페이지 크기"))
                                                                .responseFields(
                                                                                fieldWithPath("result")
                                                                                                .description("응답 결과"),
                                                                                fieldWithPath("data.content")
                                                                                                .description("거래내역 리스트"),
                                                                                fieldWithPath("data.content[].id")
                                                                                                .description("거래 ID"),
                                                                                fieldWithPath("data.content[].type")
                                                                                                .description("거래 유형 (DEPOSIT, WITHDRAW, TRANSFER)"),
                                                                                fieldWithPath("data.content[].amount")
                                                                                                .description("거래 금액"),
                                                                                fieldWithPath("data.content[].balanceSnapshot")
                                                                                                .description("잔액 스냅샷"),
                                                                                fieldWithPath("data.content[].description")
                                                                                                .description("거래 설명"),
                                                                                fieldWithPath("data.content[].createdAt")
                                                                                                .description("거래 일시"),
                                                                                fieldWithPath("data.hasNext")
                                                                                                .description("다음 페이지 존재 여부"),
                                                                                fieldWithPath("error").description(
                                                                                                "에러 정보 (성공 시 null)"))
                                                                .build())));
        }

        @Test
        @DisplayName("거래내역 조회 API - 타입별")
        void getTransactions_ByType() throws Exception {
                // given
                Long accountId = 1L;
                TransactionType type = TransactionType.DEPOSIT;
                com.practice.core.domain.transaction.Transaction transaction = com.practice.core.domain.transaction.Transaction
                                .builder()
                                .id(1L)
                                .type(TransactionType.DEPOSIT)
                                .amount(BigDecimal.valueOf(10000))
                                .balanceSnapshot(BigDecimal.valueOf(10000))
                                .description("Deposit")
                                .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                                .build();

                com.practice.core.support.Page<com.practice.core.domain.transaction.Transaction> page = new com.practice.core.support.Page<>(
                                List.of(transaction), false);

                given(accountService.readTransactions(eq(accountId), eq(type),
                                any(com.practice.core.support.OffsetLimit.class)))
                                .willReturn(page);

                // when & then
                mockMvc.perform(get("/api/v1/accounts/{accountId}/transactions", accountId)
                                .param("type", type.name())
                                .param("offset", "0")
                                .param("limit", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andDo(document("get-transactions-by-type",
                                                resource(ResourceSnippetParameters.builder()
                                                                .tag("Account")
                                                                .summary("거래내역 조회 (타입별)")
                                                                .description("특정 타입의 거래내역을 조회합니다.")
                                                                .queryParameters(
                                                                                com.epages.restdocs.apispec.ResourceDocumentation
                                                                                                .parameterWithName(
                                                                                                                "type")
                                                                                                .description("거래 유형 (DEPOSIT, WITHDRAW, TRANSFER)")
                                                                                                .optional(),
                                                                                com.epages.restdocs.apispec.ResourceDocumentation
                                                                                                .parameterWithName(
                                                                                                                "offset")
                                                                                                .description("페이지 오프셋"),
                                                                                com.epages.restdocs.apispec.ResourceDocumentation
                                                                                                .parameterWithName(
                                                                                                                "limit")
                                                                                                .description("페이지 크기"))
                                                                .responseFields(
                                                                                fieldWithPath("result")
                                                                                                .description("응답 결과"),
                                                                                fieldWithPath("data.content")
                                                                                                .description("거래내역 리스트"),
                                                                                fieldWithPath("data.content[].id")
                                                                                                .description("거래 ID"),
                                                                                fieldWithPath("data.content[].type")
                                                                                                .description("거래 유형 (DEPOSIT, WITHDRAW, TRANSFER)"),
                                                                                fieldWithPath("data.content[].amount")
                                                                                                .description("거래 금액"),
                                                                                fieldWithPath("data.content[].balanceSnapshot")
                                                                                                .description("잔액 스냅샷"),
                                                                                fieldWithPath("data.content[].description")
                                                                                                .description("거래 설명"),
                                                                                fieldWithPath("data.content[].createdAt")
                                                                                                .description("거래 일시"),
                                                                                fieldWithPath("data.hasNext")
                                                                                                .description("다음 페이지 존재 여부"),
                                                                                fieldWithPath("error").description(
                                                                                                "에러 정보 (성공 시 null)"))
                                                                .build())));
        }

}
