package com.practice.core.api.controller.account;


import static org.mockito.ArgumentMatchers.any;
import com.practice.core.api.controller.account.request.CreateAccountRequest;
import com.practice.core.domain.account.AccountService;
import com.practice.core.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.anyString;
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
        given(accountService.createAccount(anyString()))
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
}
