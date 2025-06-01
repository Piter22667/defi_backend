package com.example.defi.defi.controller.restApi;

import com.example.defi.defi.dto.ChainDto;
import com.example.defi.defi.dto.DexDto;
import com.example.defi.defi.dto.UsdCirculationDto;
import com.example.defi.defi.service.DefiLamaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cr")
public class DefiLamaController {
    private final DefiLamaService defiLamaService;

    public DefiLamaController(DefiLamaService defiLamaService) {
        this.defiLamaService = defiLamaService;
    }

    @GetMapping("/tvlByChain")
    public List<ChainDto> getTvlByChain() {
        return defiLamaService.getTvlByChain();
    } //отримуємо твл по топ 10 мережам + others окремим обєктом

    @GetMapping("/dexVolume")
    public List<DexDto> getVolume() {
        return defiLamaService.getDexVolume();
    } //отримуємо обєм по топ 15 дексам по всім мережам + others окремим обєктом

    @GetMapping("/usdCirculation")
    public List<UsdCirculationDto> getUsdCirculation() {
        return defiLamaService.getUsdCirculation();
    } // 16 точок снепшотів історичної циркуляції стейблкойнів (total USD MARKET CAP 2017 -2025 р)
}
