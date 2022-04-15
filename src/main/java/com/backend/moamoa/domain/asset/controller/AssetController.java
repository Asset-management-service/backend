package com.backend.moamoa.domain.asset.controller;

import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.request.ExpenditureRequest;
import com.backend.moamoa.domain.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/category/{categoryName}")
    public ResponseEntity getCategory(@PathVariable String categoryName) {
        return assetService.getCategories(categoryName);
    }

    @PostMapping("/budget")
    public Long addBudget(@RequestBody BudgetRequest request) {
        return assetService.addBudget(request);
    }

    @PostMapping("/category")
    public Long addCategory(@RequestBody AssetCategoryRequest request) {
        return assetService.addCategory(request);
    }

    @PostMapping("/expenditure")
    public Long addExpenditure(@RequestBody ExpenditureRequest request) {
        return assetService.addExpenditure(request);
    }

}
