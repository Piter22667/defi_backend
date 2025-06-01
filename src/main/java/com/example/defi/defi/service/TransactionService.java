package com.example.defi.defi.service;

import com.example.defi.defi.dto.AddressRequestDto;
import com.example.defi.defi.dto.TransactionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {
    List<TransactionDto> getTransactions(AddressRequestDto addressRequestDto);
}
