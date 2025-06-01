package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Entity
@Table(name = "uni_fees_by_pair")
@Data
public class UniFeesByPair {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String trading_pair;
    private String total_fees_usd;
}
