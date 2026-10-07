package com.juanmatiaslopez.transaction_service.Service.Implementation;

import com.juanmatiaslopez.transaction_service.DTO.AccountDTO;
import com.juanmatiaslopez.transaction_service.DTO.ApiResponse;
import com.juanmatiaslopez.transaction_service.DTO.TransactionDTO;
import com.juanmatiaslopez.transaction_service.DTO.TransactionRequestDTO;
import com.juanmatiaslopez.transaction_service.Entity.Transaction;
import com.juanmatiaslopez.transaction_service.Enum.*;
import com.juanmatiaslopez.transaction_service.Exception.BadRequestException;
import com.juanmatiaslopez.transaction_service.Exception.NotFoundException;
import com.juanmatiaslopez.transaction_service.Feing.AccountFeignClient;
import com.juanmatiaslopez.transaction_service.Kafka.DTO.BalanceUpdateEvent;
import com.juanmatiaslopez.transaction_service.Kafka.Service.TransactionEventPublisher;
import com.juanmatiaslopez.transaction_service.Repository.TransactionRepository;
import com.juanmatiaslopez.transaction_service.Service.TransactionServiceInt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class transactionServiceImp implements TransactionServiceInt {

    private final TransactionRepository transactionRepository;
    private final AccountFeignClient accountFeignClient;
    private final ModelMapper modelMapper;
    private final TransactionEventPublisher transactionEventPublisher;

    @Override
    @Transactional
    public ApiResponse<TransactionDTO> deposit(TransactionRequestDTO request) {
        AccountDTO toAccount = fetchAndValidateAccount(request.getToAccountNumber());

        Transaction transaction = Transaction.builder()
                .reference("DEP"+ UUID.randomUUID().toString().substring(0,8))
                //TODO Better identification for from account number during deposits
                .fromAccountNumber(toAccount.getAccountNumber())
                .fromBankCode("Finnew")
                .currency(Currency.USD)
                .toAccountNumber(toAccount.getAccountNumber())
                .toBankCode("Finnew")
                .amount(request.getAmount())
                .transactionDirection(TransactionDirection.CREDIT)
                .transactionChannel(TransactionChannel.API)
                .description(request.getDescription())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        BalanceUpdateEvent event = BalanceUpdateEvent.builder()
                .accountNumber(toAccount.getAccountNumber())
                .amount(request.getAmount())
                .currency(Currency.USD)
                .transactionDirection(TransactionDirection.CREDIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .transactionReference(savedTransaction.getReference())
                .build();

        transactionEventPublisher.sendBalanceUpdate(event);

        return new ApiResponse<>(HttpStatus.SC_CREATED, "Deposit successful", modelMapper.map(savedTransaction, TransactionDTO.class));
    }

    @Override
    public ApiResponse<TransactionDTO> transfer(TransactionRequestDTO request) {
        if (request.getFromAccountNumber() == null || request.getFromAccountNumber().isEmpty()){
            throw new BadRequestException("Destination account not found");
        }

        AccountDTO sourceAccount = fetchAndValidateAccount(request.getFromAccountNumber());

        String loggedUserEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()){
            loggedUserEmail = authentication.getName();
        }

        if (!sourceAccount.getOwnerEmail().equals(loggedUserEmail)){
            throw new BadRequestException("Access denied");
        }

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE){
            throw new BadRequestException("This account can't transfer");
        }

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new BadRequestException("The account doesn't have enough balance to make the transfer");
        }

        if (sourceAccount.getAccountNumber().equals(request.getToAccountNumber())){
            throw new BadRequestException("You cannot transfer to yourself");
        }

        AccountDTO destinationAccount = fetchAndValidateAccount(request.getToAccountNumber());

        Transaction transaction = Transaction.builder()
                .reference("TRF"+ UUID.randomUUID().toString().substring(0,8))
                //TODO Better identification for from account number during deposits
                .fromAccountNumber(sourceAccount.getAccountNumber())
                .fromBankCode("Finnew")
                .currency(Currency.USD)
                .toAccountNumber(destinationAccount.getAccountNumber())
                .toBankCode("Finnew")
                .amount(request.getAmount())
                .transactionChannel(TransactionChannel.API)
                .description(request.getDescription())
                .transactionType(TransactionType.TRANSFER)
                .transactionStatus(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        BalanceUpdateEvent event = BalanceUpdateEvent.builder()
                .accountNumber(sourceAccount.getAccountNumber())
                .amount(request.getAmount())
                .currency(Currency.USD)
                .transactionDirection(TransactionDirection.DEBIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .transactionReference(savedTransaction.getReference())
                .build();

        transactionEventPublisher.sendBalanceUpdate(event);

        event = BalanceUpdateEvent.builder()
                .accountNumber(destinationAccount.getAccountNumber())
                .amount(request.getAmount())
                .currency(Currency.USD)
                .transactionDirection(TransactionDirection.CREDIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .transactionReference(savedTransaction.getReference())
                .build();

        transactionEventPublisher.sendBalanceUpdate(event);

        return new ApiResponse<>(HttpStatus.SC_CREATED, "Transfer successful", modelMapper.map(savedTransaction, TransactionDTO.class));
    }

    @Override
    public ApiResponse<TransactionDTO> withdraw(TransactionRequestDTO request) {

        AccountDTO sourceAccount = fetchAndValidateAccount(request.getFromAccountNumber());

        String loggedUserEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()){
            loggedUserEmail = authentication.getName();
        }

        if (!sourceAccount.getOwnerEmail().equals(loggedUserEmail)){
            throw new BadRequestException("Access denied");
        }

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE){
            throw new BadRequestException("Account is not active");
        }

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0){
            throw new BadRequestException("The account doesn't have enough balance to make the withdraw");
        }

        Transaction transaction = Transaction.builder()
                .reference("WTD"+ UUID.randomUUID().toString().substring(0,8))
                //TODO Better identification for from account number during deposits
                .fromAccountNumber(sourceAccount.getAccountNumber())
                .fromBankCode("Finnew")
                .currency(Currency.USD)
                .toAccountNumber(sourceAccount.getAccountNumber())
                .toBankCode("Finnew")
                .amount(request.getAmount())
                .transactionChannel(TransactionChannel.API)
                .description(request.getDescription())
                .transactionType(TransactionType.WITHDRAW)
                .transactionDirection(TransactionDirection.DEBIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        BalanceUpdateEvent event = BalanceUpdateEvent.builder()
                .accountNumber(sourceAccount.getAccountNumber())
                .amount(request.getAmount())
                .currency(Currency.USD)
                .transactionDirection(TransactionDirection.DEBIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .description(request.getDescription())
                .transactionReference(savedTransaction.getReference())
                .build();

        transactionEventPublisher.sendBalanceUpdate(event);

        return new ApiResponse<>(HttpStatus.SC_CREATED, "Withdraw successful", modelMapper.map(savedTransaction, TransactionDTO.class));
    }

    @Override
    public ApiResponse<TransactionDTO> getTransactionByReference(String reference) {

        //TODO Only the ADMIN should view ALL transactions by reference, a normal user should only view their own (his account number is either from or to for that reference)

        Transaction transaction = transactionRepository.findByReference(reference).orElseThrow(()-> new NotFoundException("Transaction not found"));

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);

        return new ApiResponse<>(HttpStatus.SC_OK, "Transaction search successful", transactionDTO);
    }

    @Override
    public ApiResponse<List<TransactionDTO>> getTransactionHistoryByAccountNumber(String accountNumber) {
        List<Transaction> list = transactionRepository.findAllByAccountNumber(accountNumber);

        List<TransactionDTO> transactionsDTO = list.stream().map(t -> modelMapper.map(t, TransactionDTO.class)).toList();

        return new ApiResponse<>(HttpStatus.SC_OK, "Transactions search successful", transactionsDTO);
    }

    @Override
    public ApiResponse<List<TransactionDTO>> getTransactionHistory(String accountNumber, LocalDateTime start, LocalDateTime end) {

        List<Transaction> list = transactionRepository.findAllAccountNumberAndDateRange(accountNumber, start, end);

        List<TransactionDTO> transactionsDTO = list.stream().map(t -> modelMapper.map(t, TransactionDTO.class)).toList();

        return new ApiResponse<>(HttpStatus.SC_OK, "Transactions search successful", transactionsDTO);
    }

    @Override
    public ApiResponse<List<TransactionDTO>> getOwnTransactionHistoryByDirection(String accountNumber, TransactionDirection transactionDirection) {

        //TODO This doesn't work because we use toAccount and fromAccount in the wrong way
        List<Transaction> list = transactionDirection.equals(TransactionDirection.DEBIT) ? transactionRepository.findByFromAccountNumber(accountNumber) : transactionRepository.findByToAccountNumber(accountNumber);

        List<TransactionDTO> transactionsDTO = list.stream().map(t -> modelMapper.map(t, TransactionDTO.class)).toList();

        return new ApiResponse<>(HttpStatus.SC_OK, "Transactions search by direction successful", transactionsDTO);
    }

    private AccountDTO fetchAndValidateAccount(String accountNumber){
        ApiResponse<AccountDTO> response = accountFeignClient.getAccountByNumber(accountNumber);

        if (response == null || response.data() == null) {
            throw new NotFoundException("Account "+ accountNumber+" Not Found");
        }

        AccountDTO account = response.data();

        if (account.getAccountStatus().equals(AccountStatus.CLOSED)){
            throw new BadRequestException("Account "+ accountNumber+" is closed");
        }

        return account;
    }
}
