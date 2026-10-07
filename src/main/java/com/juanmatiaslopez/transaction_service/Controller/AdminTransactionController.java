package com.juanmatiaslopez.transaction_service.Controller;

import com.juanmatiaslopez.transaction_service.DTO.ApiResponse;
import com.juanmatiaslopez.transaction_service.DTO.TransactionDTO;
import com.juanmatiaslopez.transaction_service.DTO.TransactionRequestDTO;
import com.juanmatiaslopez.transaction_service.Service.TransactionServiceInt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminTransactionController {

    private final TransactionServiceInt transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionDTO>> deposit(@Valid @RequestBody TransactionRequestDTO transactionRequest) {
        return ResponseEntity.ok(transactionService.deposit(transactionRequest));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getHistory(@PathVariable String accountNumber){
        return ResponseEntity.ok(transactionService.getTransactionHistoryByAccountNumber(accountNumber));
    }
}
