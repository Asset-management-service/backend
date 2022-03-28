package com.backend.moamoa.domain.asset.salary.controller;

import com.backend.moamoa.domain.asset.salary.dto.CostRequest;
import com.backend.moamoa.domain.asset.salary.entity.Cost;
import com.backend.moamoa.domain.asset.salary.repository.CostRepository;
import com.backend.moamoa.domain.asset.salary.service.CostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/costs")
@RequiredArgsConstructor
public class CostController {

    private final CostService costService;
    private final CostRepository costRepository;

    @PostMapping
    public void createCost(@RequestBody CostRequest costRequest) {
        costService.createCost(costRequest);
    }

    @GetMapping
    public List<Cost> getCosts() {
        return costRepository.findAll();
    }
}
