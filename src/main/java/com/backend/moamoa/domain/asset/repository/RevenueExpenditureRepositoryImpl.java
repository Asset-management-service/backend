package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.dto.response.QRevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.dto.response.RevenueExpenditureResponse;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;

import static com.backend.moamoa.domain.asset.entity.QRevenueExpenditure.revenueExpenditure;
import static com.backend.moamoa.domain.user.entity.QUser.user;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@RequiredArgsConstructor
public class RevenueExpenditureRepositoryImpl implements RevenueExpenditureRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RevenueExpenditureResponse> findRevenueAndExpenditureByMonth(LocalDate month, Pageable pageable, Long userId) {

        List<RevenueExpenditureResponse> content = queryFactory
                .select(new QRevenueExpenditureResponse(
                        revenueExpenditure.date,
                        revenueExpenditure.categoryName,
                        revenueExpenditure.content,
                        revenueExpenditure.paymentMethod,
                        revenueExpenditure.cost
                ))
                .from(revenueExpenditure)
                .innerJoin(revenueExpenditure.user, user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(revenueExpenditure.date
                        .between(month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()))
                        .and(user.id.eq(userId)))
                .orderBy(revenueExpenditure.date.desc())
                .fetch();

        long countQuery = queryFactory
                .selectFrom(revenueExpenditure)
                .where(revenueExpenditure.date
                        .between(month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()))
                        .and(user.id.eq(userId)))
                .fetchCount();

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery);

    }

    @Override
    public List<RevenueExpenditure> findRevenueExpenditure(LocalDate month, Long userId) {
        return queryFactory
                .selectFrom(revenueExpenditure)
                .innerJoin(revenueExpenditure.user, user)
                .where(revenueExpenditure.date
                        .between(month.withDayOfMonth(1), month.withDayOfMonth(month.lengthOfMonth()))
                        .and(user.id.eq(userId)))
                .fetch();

    }

    @Override
    public List<RevenueExpenditure> findRevenueWeekExpenditure(LocalDate week, Long userId) {
        return queryFactory
                .selectFrom(revenueExpenditure)
                .innerJoin(revenueExpenditure.user, user)
                .where(revenueExpenditure.date
                        .between(week, week.plusDays(6))
                        .and(user.id.eq(userId)))
                .fetch();
    }

    @Override
    public List<RevenueExpenditure> findRevenueYearExpenditure(LocalDate year, Long userId) {
        return queryFactory
                .selectFrom(revenueExpenditure)
                .innerJoin(revenueExpenditure.user, user)
                .where(revenueExpenditure.date
                        .between(year.with(firstDayOfYear()), year.with(lastDayOfYear()))
                        .and(user.id.eq(userId)))
                .fetch();
    }

}
