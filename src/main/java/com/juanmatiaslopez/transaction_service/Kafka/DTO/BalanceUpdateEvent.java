package com.juanmatiaslopez.transaction_service.Kafka.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.juanmatiaslopez.transaction_service.Enum.Currency;
import com.juanmatiaslopez.transaction_service.Enum.TransactionDirection;
import com.juanmatiaslopez.transaction_service.Enum.TransactionStatus;
import com.juanmatiaslopez.transaction_service.Enum.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceUpdateEvent {

    //Transaction
    private String accountNumber;
    private BigDecimal amount;
    private TransactionDirection transactionDirection;
    private TransactionStatus transactionStatus;
    private String transactionReference;
    private Currency currency;

    //User
    private String email;
    private String firstName;
    private BigDecimal currentBalance;
    private String description;
}
