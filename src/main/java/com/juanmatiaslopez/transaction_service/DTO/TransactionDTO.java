package com.juanmatiaslopez.transaction_service.DTO;

import com.juanmatiaslopez.transaction_service.Enum.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

   private Long id;

    private String reference;

    private String fromAccountNumber;

    private String fromBankCode;

    private String toAccountNumber;

    private String toBankCode;

    private String description;

    private BigDecimal amount;

    private Currency currency;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private TransactionChannel transactionChannel;

    private TransactionDirection transactionDirection;

    private LocalDateTime createdAt;
}
