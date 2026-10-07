package com.juanmatiaslopez.transaction_service.Controller;

import com.juanmatiaslopez.transaction_service.DTO.ApiResponse;
import com.juanmatiaslopez.transaction_service.DTO.TransactionDTO;
import com.juanmatiaslopez.transaction_service.DTO.TransactionRequestDTO;
import com.juanmatiaslopez.transaction_service.Enum.TransactionDirection;
import com.juanmatiaslopez.transaction_service.Service.TransactionServiceInt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionServiceInt transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionDTO>> transfer(@Valid @RequestBody TransactionRequestDTO transactionRequest) {
        return ResponseEntity.ok(transactionService.transfer(transactionRequest));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionDTO>> withdraw(@Valid @RequestBody TransactionRequestDTO transactionRequest) {
        return ResponseEntity.ok(transactionService.withdraw(transactionRequest));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getHistory(
            @RequestParam String accountNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime end){

        LocalDateTime startDate = (start != null) ? start.toLocalDate().atStartOfDay() : LocalDateTime.of(2026, 1,1,0,0);
        LocalDateTime endDate = (end != null) ? end.toLocalDate().atStartOfDay() : LocalDateTime.now();

        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber,startDate, endDate));
    }

    @GetMapping("/history/direction")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getHistoryByDirection(
            @RequestParam String accountNumber,
            @RequestParam TransactionDirection transactionDirection){

        return ResponseEntity.ok(transactionService.getOwnTransactionHistoryByDirection(accountNumber,transactionDirection));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionByReference(
            @PathVariable String reference){

        return ResponseEntity.ok(transactionService.getTransactionByReference(reference));
    }
}
