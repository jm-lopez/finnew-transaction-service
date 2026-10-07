package com.juanmatiaslopez.transaction_service.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.juanmatiaslopez.transaction_service.Enum.AccountStatus;
import com.juanmatiaslopez.transaction_service.Enum.AccountType;
import com.juanmatiaslopez.transaction_service.Enum.Currency;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountDTO {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private Currency currency;

    private AccountType accountType;

    private AccountStatus accountStatus;

    private String ownerEmail;

    private LocalDateTime createdAt;
}
