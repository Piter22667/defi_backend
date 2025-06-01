package com.example.defi.defi.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "pancake_volume_and_transactions_on_bnb")
@Entity
public class PancakeVolumeAndTransactionsOnBnbModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String month;
    private Long transactions;

    @Column(name = "user_count")
    private Long  user;
    private double volume;


}
