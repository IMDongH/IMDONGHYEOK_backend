package com.practice.core.api.controller.account;

import com.practice.core.api.controller.account.request.CreateAccountRequest;
import com.practice.core.api.controller.account.request.DepositRequest;
import com.practice.core.api.controller.account.request.WithdrawRequest;
import com.practice.core.api.controller.account.request.TransferRequest;
import com.practice.core.domain.account.AccountService;
import com.practice.core.domain.account.AccountDeposit;
import com.practice.core.domain.account.AccountWithdraw;
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
        Long accountId = accountService.createAccount(request.toNewAccount());
        return ApiResponse.success(accountId);
    }

    @DeleteMapping("/api/v1/accounts/{accountId}")
    public ApiResponse<Object> deleteAccount( @PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ApiResponse.success();
    }

    @PostMapping("/api/v1/accounts/{accountId}/deposit")
    public ApiResponse<Long> deposit(@PathVariable Long accountId,
            @RequestBody DepositRequest request) {

        Long result = accountService.deposit(request.toAccountDeposit(accountId));
        return ApiResponse.success(result);
    }

    @PostMapping("/api/v1/accounts/{accountId}/withdraw")
    public ApiResponse<Long> withdraw( @PathVariable Long accountId,
            @RequestBody WithdrawRequest request) {
        Long result = accountService.withdraw(request.toAccountWithdraw(accountId));
        return ApiResponse.success(result);
    }

    @PostMapping("/api/v1/accounts/{accountId}/transfer")
    public ApiResponse<Object> transfer(@PathVariable Long accountId,
            @RequestBody TransferRequest request) {
        accountService.transfer(request.toAccountTransfer(accountId));
        return ApiResponse.success();
    }
}
