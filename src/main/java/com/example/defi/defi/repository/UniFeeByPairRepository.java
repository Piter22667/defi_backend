package com.example.defi.defi.repository;

import com.example.defi.defi.model.UniFeesByPair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniFeeByPairRepository extends JpaRepository<UniFeesByPair, Long> {
}
