package com.example.defi.defi.controller.restApi;

import com.example.defi.defi.dto.AddressRequestDto;
import com.example.defi.defi.dto.TransactionDto;
import com.example.defi.defi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tx")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public List<TransactionDto> getTransactions(@RequestBody AddressRequestDto addressRequestDto) {
        return transactionService.getTransactions(addressRequestDto);
    }
}
