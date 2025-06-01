package com.example.defi.defi.repository;

import com.example.defi.defi.model.ChainTvlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChainTvlRepository extends JpaRepository<ChainTvlEntity, Long> {
}
