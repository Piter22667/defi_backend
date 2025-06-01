package com.example.defi.defi.repository;

import com.example.defi.defi.model.UniFeesByChainModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniFeesByChainRepository extends JpaRepository<UniFeesByChainModel, Long> {
}
