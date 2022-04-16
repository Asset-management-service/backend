package com.backend.moamoa.domain.asset.controller;

import com.backend.moamoa.domain.asset.dto.request.AssetCategoryRequest;
import com.backend.moamoa.domain.asset.dto.request.BudgetRequest;
import com.backend.moamoa.domain.asset.dto.request.CreateRevenueExpenditureRequest;
import com.backend.moamoa.domain.asset.dto.request.ExpenditureRequest;
import com.backend.moamoa.domain.asset.dto.response.*;
import com.backend.moamoa.domain.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/category")
    public ResponseEntity getCategory(@RequestParam String categoryType) {
        return ResponseEntity.ok(new AssetCategoriesResponse(assetService.getCategories(categoryType)));
    }

    @PostMapping("/budget")
    public ResponseEntity addBudget(@RequestBody BudgetRequest request) {
        return ResponseEntity.ok(new CreateBudgetResponse(assetService.addBudget(request)));
    }

    @PostMapping("/category")
    public ResponseEntity addCategory(@RequestBody AssetCategoryRequest request) {
        return ResponseEntity.ok(new AssetCategoryResponse(assetService.addCategory(request)));
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<Void> deleteCategoryName(@PathVariable Long categoryId) {
        assetService.deleteCategoryName(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/expenditure")
    public ResponseEntity addExpenditure(@RequestBody ExpenditureRequest request) {
        return ResponseEntity.ok(new CreateExpenditureResponse(assetService.addExpenditure(request)));
    }

    @PostMapping("/revenueExpenditure")
    public ResponseEntity addRevenueExpenditure(@RequestBody CreateRevenueExpenditureRequest request) {
        return ResponseEntity.ok(new CreateRevenueExpenditureResponse(assetService.addRevenueExpenditure(request)));
    }

    @GetMapping("/revenueExpenditure")
    public ResponseEntity getRevenueExpenditures(@RequestParam LocalDate month, Pageable pageable) {
        assetService.findRevenueExpenditureByMonth(month, pageable);
        return null;
    }

}
