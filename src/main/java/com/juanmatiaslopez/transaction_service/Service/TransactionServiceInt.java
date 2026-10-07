package com.juanmatiaslopez.transaction_service.Service;

import com.juanmatiaslopez.transaction_service.DTO.ApiResponse;
import com.juanmatiaslopez.transaction_service.DTO.TransactionDTO;
import com.juanmatiaslopez.transaction_service.DTO.TransactionRequestDTO;
import com.juanmatiaslopez.transaction_service.Enum.TransactionDirection;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionServiceInt {

    ApiResponse<TransactionDTO> deposit(TransactionRequestDTO request);
    ApiResponse<TransactionDTO> transfer(TransactionRequestDTO request);
    ApiResponse<TransactionDTO> withdraw(TransactionRequestDTO request);
    ApiResponse<TransactionDTO> getTransactionByReference(String reference);
    ApiResponse<List<TransactionDTO>> getTransactionHistoryByAccountNumber(String accountNumber);
    ApiResponse<List<TransactionDTO>> getTransactionHistory(String accountNumber, LocalDateTime start, LocalDateTime end);
    ApiResponse<List<TransactionDTO>> getOwnTransactionHistoryByDirection(String accountNumber, TransactionDirection transactionDirection);
}
