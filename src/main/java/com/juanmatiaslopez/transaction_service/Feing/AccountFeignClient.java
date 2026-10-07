package com.juanmatiaslopez.transaction_service.Feing;

import com.juanmatiaslopez.transaction_service.DTO.AccountDTO;
import com.juanmatiaslopez.transaction_service.DTO.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-account-service") //This uses the Discovery Service, you can also pass an URL to access the resource directly
public interface AccountFeignClient {

    @GetMapping("/api/account/{accountNumber}")
    ApiResponse<AccountDTO> getAccountByNumber(@PathVariable("accountNumber") String accountNumber);
}
