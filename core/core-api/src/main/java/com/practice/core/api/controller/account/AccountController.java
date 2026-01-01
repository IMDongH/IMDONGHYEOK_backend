package com.practice.core.api.controller.account;


import com.practice.core.api.controller.account.request.CreateAccountRequest;
import com.practice.core.domain.account.AccountService;
import com.practice.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/api/v1/accounts")
    public ApiResponse<Long> createAccount(
            @RequestBody CreateAccountRequest request) {
        Long accountId = accountService.createAccount(request.getAccountNumber());
        return ApiResponse.success(accountId);
    }

    @DeleteMapping("/api/v1/accounts/{accountId}")
    public ApiResponse<Object> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ApiResponse.success();
    }
}
