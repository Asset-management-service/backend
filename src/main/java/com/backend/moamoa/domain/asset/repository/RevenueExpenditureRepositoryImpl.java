package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.dto.response.QRevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.QAssetCategory;
import com.backend.moamoa.domain.asset.entity.QRevenueExpenditure;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import com.backend.moamoa.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;

import static com.backend.moamoa.domain.asset.entity.QAssetCategory.*;
import static com.backend.moamoa.domain.asset.entity.QRevenueExpenditure.*;
import static com.backend.moamoa.domain.user.entity.QUser.*;

@RequiredArgsConstructor
public class RevenueExpenditureRepositoryImpl implements RevenueExpenditureRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RevenueExpenditure> findRevenueAndExpenditureByMonth(LocalDate month, Pageable pageable) {
//        List<RevenueExpenditureResponse> result = queryFactory
//                .select(new QRevenueExpenditureResponse(
//                        revenueExpenditure.date,
//                        revenueExpenditure.categoryName,
//                        revenueExpenditure.content,
//                        revenueExpenditure.paymentMethod,
//                        revenueExpenditure.cost
//                        ))
//                .from(revenueExpenditure)
//                .innerJoin(assetCategory.user, user)
//                .fetch();

        return null;
    }
}
