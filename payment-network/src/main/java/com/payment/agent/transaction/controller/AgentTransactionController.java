package com.payment.agent.transaction.controller;

import com.payment.agent.transaction.dtos.TransactionRequest;
import com.payment.agent.transaction.dtos.TransactionResponse;
import com.payment.agent.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent/transactions")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name="app.role", havingValue = "agent")
public class AgentTransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request){
        log.info("Received transaction request: {}", request);

        TransactionResponse response = transactionService.createTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getLocalTransactions() {
        log.info("Fetching local transactions");

        List<TransactionResponse> transactions = transactionService.getLocalTransactions();

        return ResponseEntity.ok(transactions);
    }
}
