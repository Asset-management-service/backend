package com.backend.moamoa.domain.settlement.service;

import com.backend.moamoa.domain.settlement.dto.response.*;
import com.backend.moamoa.domain.settlement.dto.response.settle.MonthSettleResponse;
import com.backend.moamoa.domain.settlement.dto.response.settle.WeekSettleResponse;
import com.backend.moamoa.domain.settlement.dto.response.settle.YearSettleResponse;
import com.backend.moamoa.domain.settlement.dto.response.total.ComparisonsResponse;
import com.backend.moamoa.domain.settlement.dto.response.total.TotalComparisonResponse;
import com.backend.moamoa.domain.settlement.dto.response.total.ComparisonResponse;
import com.backend.moamoa.domain.asset.entity.ExpenditureRatio;
import com.backend.moamoa.domain.asset.entity.RevenueExpenditure;
import com.backend.moamoa.domain.asset.repository.AssetCategoryRepository;
import com.backend.moamoa.domain.asset.repository.ExpenditureRatioRepository;
import com.backend.moamoa.domain.asset.repository.RevenueExpenditureRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private static final String TYPE_REVENUE = "REVENUE";
    private static final String TYPE_EXPENDITURE = "EXPENDITURE";
    private static final String TYPE_NET_PROFIT = "NET_PROFIT";
    private static final String TYPE_FIXED = "FIXED";
    private static final String TYPE_VARIABLE = "VARIABLE";

    private final RevenueExpenditureRepository revenueExpenditureRepository;
    private final ExpenditureRatioRepository expenditureRatioRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final UserUtil userUtil;

    // 주별 지출 내역 출력
    public List<WeekSettleResponse> getWeekExpenditure(String date, String type) {
        User user = userUtil.findCurrentUser();
        LocalDate standard;

        if (date == null) {
            standard = LocalDate.now();
        } else {
            standard = LocalDate.parse(date);
        }

        LocalDate startDate = standard.with(ChronoField.ALIGNED_WEEK_OF_YEAR, standard.get(ChronoField.ALIGNED_WEEK_OF_YEAR))
                .with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        Integer weekNumber = getWeekNumber(standard);
        List<WeekSettleResponse> weekSettleResponses = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            Integer cost = revenueExpenditureRepository.getExpenditure(startDate, endDate, user);

            WeekSettleResponse weekSettleResponse = new WeekSettleResponse(standard.getYear(), standard.getMonthValue(), weekNumber, cost, startDate, endDate);
            weekSettleResponses.add(weekSettleResponse);

            if (type.equals("left")) {
                weekNumber = weekNumber - 1;
                startDate = startDate.minusWeeks(1);
                endDate = endDate.minusWeeks(1);
                if (weekNumber <= 0) {
                    standard = standard.minusMonths(1);
                    weekNumber = getWeekNumber(standard.with(TemporalAdjusters.lastDayOfMonth()));
                }
            } else {
                weekNumber = weekNumber + 1;
                startDate = startDate.plusWeeks(1);
                endDate = endDate.plusWeeks(1);
                if (weekNumber > getWeekNumber(standard.with(TemporalAdjusters.lastDayOfMonth()))) {
                    standard = standard.plusMonths(1);
                    weekNumber = 1;
                }
            }
        }
        return weekSettleResponses;
    }

    // 달별 지출 내역 출력
    public List<MonthSettleResponse> getMonthExpenditure(String date, String type) {
        User user = userUtil.findCurrentUser();
        LocalDate standard;

        if (date == null) {
            standard = LocalDate.now();
        } else {
            standard = LocalDate.parse(date + "-01");
        }
        List<MonthSettleResponse> monthSettleResponses = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int year = standard.getYear();
            int month = standard.getMonthValue();

            LocalDate monthStart = LocalDate.of(year, month, 1);
            LocalDate monthEnd = monthStart.plusDays(monthStart.lengthOfMonth() - 1);

            Integer cost = revenueExpenditureRepository.getExpenditure(monthStart, monthEnd, user);
            Integer revenue = revenueExpenditureRepository.getRevenue(monthStart, monthEnd, user);

            MonthSettleResponse monthSettleResponse = new MonthSettleResponse(month, year, cost, revenue);
            monthSettleResponses.add(monthSettleResponse);

            if (type.equals("left")) {
                standard = standard.minusMonths(1);
            } else {
                standard = standard.plusMonths(1);
            }
        }
        return monthSettleResponses;
    }

    // 달, 년 별 자세한 지출 내역 출력
    public MonthResponse getDetail(String date, String type) {

        User user = userUtil.findCurrentUser();
        List<RevenueExpenditure> revenueExpenditure;

        if (type.equals("month")) {
            revenueExpenditure = revenueExpenditureRepository.findRevenueExpenditure(LocalDate.parse(date + "-01"), user.getId());
        } else {
            revenueExpenditure = revenueExpenditureRepository.findRevenueYearExpenditure(LocalDate.parse(date + "-01-01"), user.getId());
        }

        Integer totalExp = getRevenueExpenditure(revenueExpenditure, TYPE_EXPENDITURE);
        Integer totalRevenue = getRevenueExpenditure(revenueExpenditure, TYPE_REVENUE);
        Integer totalFixed = getExpenditureByCategory(revenueExpenditure, TYPE_FIXED);
        Integer totalVariable = getExpenditureByCategory(revenueExpenditure, TYPE_VARIABLE);

        int totalFixedPercent = (int) ((double) totalFixed / totalExp * 100.0);
        int totalVariablePercent = (int) ((double) totalVariable / totalExp * 100.0);

        List<String> fixed = assetCategoryRepository.findByAssetCategoryTypeAndUserId(TYPE_FIXED, user.getId());
        List<String> variable = assetCategoryRepository.findByAssetCategoryTypeAndUserId(TYPE_VARIABLE, user.getId());
        List<String> revenues = assetCategoryRepository.findByAssetCategoryTypeAndUserId(TYPE_REVENUE, user.getId());

        ExpenditureRatio expenditureRatio = expenditureRatioRepository.findByUser(user).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RATIO));

        Boolean fixedExceed = null, variableExceed = null;

        if (type.equals("month")) {
            fixedExceed = (totalFixedPercent > expenditureRatio.getFixed());
            variableExceed = (totalVariablePercent > expenditureRatio.getVariable());
        }

        List<CostResponse> fixedRes = new ArrayList<>();
        List<CostResponse> costRes = new ArrayList<>();
        List<RevenueResponse> revenueRes = new ArrayList<>();

        setCostResponse(revenueExpenditure, totalExp, totalFixed, totalFixedPercent, fixed, fixedRes);
        setCostResponse(revenueExpenditure, totalExp, totalVariable, totalVariablePercent, variable, costRes);

        for (String revenue : revenues) {
            RevenueResponse revenueResponse =
                    RevenueResponse.builder()
                            .CategoryName(revenue)
                            .revenue(getExpenditureByCost(revenueExpenditure, revenue))
                            .percent((int) ((double) getExpenditureByCost(revenueExpenditure, revenue) / totalRevenue * 100.0))
                            .build();

            revenueRes.add(revenueResponse);
        }

        fixedRes = fixedRes.stream().sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());
        costRes = costRes.stream().sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());

        List<String> categories = getMostAndLeastCategory(fixedRes, costRes);

        return MonthResponse.builder()
                .mostExpCategory(categories.get(0))
                .leastExpCategory(categories.get(1))
                .totalExp(totalExp)
                .totalRevenue(totalRevenue)
                .fixedCostResponses(fixedRes)
                .variableCostResponses(costRes)
                .revenueResponses(revenueRes)
                .netIncome(totalRevenue - totalExp)
                .fixedExceed(fixedExceed)
                .variableExceed(variableExceed)
                .build();
    }

    // 주의 자세한 지출 내역 출력
    public WeekResponse getWeekDetail(String date) {
        User user = userUtil.findCurrentUser();
        LocalDate standard = LocalDate.parse(date);

        List<RevenueExpenditure> revenueExpenditure = revenueExpenditureRepository.findRevenueWeekExpenditure(standard, user.getId());
        int totalExp = getRevenueExpenditure(revenueExpenditure, TYPE_EXPENDITURE);

        List<String> categoryNames = assetCategoryRepository.findByTwoAssetCategoriesAndUserId(TYPE_FIXED, TYPE_VARIABLE, user.getId());
        List<CostResponse> costResponses = new ArrayList<>();

        for (String categoryName : categoryNames) {
            costResponses.add(
                    CostResponse.builder()
                            .totalCost(0)
                            .totalPercent(0)
                            .CategoryName(categoryName)
                            .cost(getExpenditureByCost(revenueExpenditure, categoryName))
                            .percent((int) ((double) getExpenditureByCost(revenueExpenditure, categoryName) / totalExp * 100.0))
                            .build()
            );
        }

        String mostCategory = null, minCategory = null;

        if (!costResponses.isEmpty()) {
            costResponses = costResponses.stream()
                    .sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());

            mostCategory = costResponses.get(0).getCategoryName();
            minCategory = costResponses.get(costResponses.size() - 1).getCategoryName();
        }

        return new WeekResponse(standard.getYear(), getWeekNumber(standard), mostCategory, minCategory, totalExp, costResponses);
    }

    // 달 기준 자세한 지출 내역 비교하기
    public ComparisonsResponse getMonthComparison(String date) {
        LocalDate standard = LocalDate.parse(date + "-01");
        String prev = standard.minusMonths(1).toString().substring(0, 7);

        MonthResponse prevRes = getDetail(prev, "month");
        MonthResponse presentRes = getDetail(date, "month");

        return getComparisonRes(prevRes, presentRes);
    }

    // 년 기준 자세한 지출 내역 비교하기
    public ComparisonsResponse getYearComparison(String date) {
        LocalDate standard = LocalDate.parse(date + "-01-01");
        String prev = standard.minusYears(1).toString().substring(0, 4);

        MonthResponse prevRes = getDetail(prev, "year");
        MonthResponse presentRes = getDetail(date, "year");

        return getComparisonRes(prevRes, presentRes);
    }

    // 달, 년 자세한 지출 내역 비교하기
    private ComparisonsResponse getComparisonRes(MonthResponse prevRes, MonthResponse presentRes) {

        ComparisonResponse totalExp = getComparisonResponse(prevRes.getTotalExp(), presentRes.getTotalExp(), TYPE_EXPENDITURE);

        List<TotalComparisonResponse> totalFixedResponses = new ArrayList<>();
        List<CostResponse> fixedPrev = prevRes.getFixedCostResponses();
        List<CostResponse> fixedPresent = presentRes.getFixedCostResponses();
        setTotalComparisonResponse(fixedPrev, fixedPresent, totalFixedResponses, TYPE_FIXED);

        List<TotalComparisonResponse> totalVarResponses = new ArrayList<>();
        List<CostResponse> varPrev = prevRes.getVariableCostResponses();
        List<CostResponse> varPresent = presentRes.getVariableCostResponses();
        setTotalComparisonResponse(varPrev, varPresent, totalVarResponses, TYPE_VARIABLE);

        ComparisonResponse netResponse = getComparisonResponse(prevRes.getTotalRevenue() - prevRes.getTotalExp(),
                presentRes.getTotalRevenue() - presentRes.getTotalExp(), TYPE_NET_PROFIT);

        return new ComparisonsResponse(totalExp, totalFixedResponses, totalVarResponses, netResponse);
    }

    // 년 지출 내역 구하기
    public List<YearSettleResponse> getYearExpenditure(String date, String type) {

        User user = userUtil.findCurrentUser();
        LocalDate standard;

        if (date == null) {
            standard = LocalDate.now();
        } else {
            standard = LocalDate.parse(date + "-01-01");
        }
        List<YearSettleResponse> yearSettleResponses = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            int year = standard.getYear();
            LocalDate yearStart = standard.with(firstDayOfYear());
            LocalDate yearEnd = standard.with(lastDayOfYear());

            Integer cost = revenueExpenditureRepository.getExpenditure(yearStart, yearEnd, user);
            Integer revenue = revenueExpenditureRepository.getRevenue(yearStart, yearEnd, user);

            YearSettleResponse yearSettleResponse = new YearSettleResponse(year, cost, revenue);
            yearSettleResponses.add(yearSettleResponse);

            if (type.equals("LEFT")) {
                standard = standard.minusYears(1);
            } else {
                standard = standard.plusYears(1);
            }
        }
        return yearSettleResponses;
    }

    private ComparisonResponse getComparisonResponse(int prevTotal, int presentTotal, String type) {
        int diff = presentTotal - prevTotal;
        int ratio = getRatio(diff, prevTotal);

        return new ComparisonResponse(type, prevTotal, presentTotal, diff, ratio);
    }

    private void setTotalComparisonResponse(List<CostResponse> prevLists, List<CostResponse> presentLists, List<TotalComparisonResponse> responses, String type) {
        for (int i = 0; i < presentLists.size(); i++) {
            int difference = presentLists.get(i).getCost() - prevLists.get(i).getCost();
            int ratio = getRatio(difference, prevLists.get(i).getCost());

            ComparisonResponse categoryName = new ComparisonResponse(presentLists.get(i).getCategoryName(), prevLists.get(i).getCost(), presentLists.get(i).getCost(), difference, ratio);
            ComparisonResponse categoryType = new ComparisonResponse(type, prevLists.get(i).getTotalCost(), presentLists.get(i).getTotalCost(), presentLists.get(i).getTotalCost() - prevLists.get(i).getTotalCost(), getRatio(presentLists.get(i).getTotalCost() - prevLists.get(i).getTotalCost(), prevLists.get(i).getTotalCost()));

            TotalComparisonResponse response = new TotalComparisonResponse(categoryType, categoryName);
            responses.add(response);
        }
    }

    private int getRatio(int difference, int prev) {
        if (prev != 0) {
            if(prev < 0)
                return (int) ((double) difference / (double) prev * 100.0) * -1;
            else
                return (int) ((double) difference / (double) prev * 100.0);
        } else {
            if (difference == 0) return 0;
            else if (difference > 0) return 100;
            else return -100;
        }
    }

    private int getRevenueExpenditure(List<RevenueExpenditure> revenueExpenditureList, String type) {
        return revenueExpenditureList.stream().filter(r -> r.getRevenueExpenditureType().toString().equals(type)).mapToInt(RevenueExpenditure::getCost).sum();
    }

    private int getExpenditureByCategory(List<RevenueExpenditure> revenueExpenditureList, String category) {
        return revenueExpenditureList.stream().filter(r -> r.getCategoryName().equals(category)).mapToInt(RevenueExpenditure::getCost).sum();
    }

    private int getExpenditureByCost(List<RevenueExpenditure> revenueExpenditureList, String cost) {
        return revenueExpenditureList.stream().filter(r -> r.getContent().equals(cost)).mapToInt(RevenueExpenditure::getCost).sum();
    }

    private List<String> getMostAndLeastCategory(List<CostResponse> fixedRes, List<CostResponse> costRes) {

        CostResponse maxFixed = fixedRes.get(0);
        CostResponse maxVar = costRes.get(0);

        CostResponse minFixed = fixedRes.get(fixedRes.size() - 1);
        CostResponse minVar = costRes.get(costRes.size() - 1);

        String maxName = maxFixed.getCost() >= maxVar.getCost() ? maxFixed.getCategoryName() : maxVar.getCategoryName();
        String minName = minFixed.getCost() >= minVar.getCost() ? minVar.getCategoryName() : maxFixed.getCategoryName();

        List<String> nameList = new ArrayList<>();
        nameList.add(maxName);
        nameList.add(minName);

        return nameList;
    }

    public Integer getWeekNumber(LocalDate date) {
        LocalDate firstMondayOfMonth = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        // 첫 월요일이면 바로 리턴
        if (firstMondayOfMonth.isEqual(date)) return 1;

        if (date.isAfter(firstMondayOfMonth)) {
            // 첫 월요일 이후일 때
            int diffFromFirstMonday = date.getDayOfMonth() - firstMondayOfMonth.getDayOfMonth();
            int weekNumber = (int) Math.ceil(diffFromFirstMonday / 7.0);
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) weekNumber += 1;
            return weekNumber;
        }
        // 첫 월요일 이전이면 회귀식으로 전 달 마지막 주차를 구함
        return getWeekNumber(date.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
    }

    private void setCostResponse(List<RevenueExpenditure> revenueExpenditure,
                                 int totalExp, int totalCost, int percent, List<String> names, List<CostResponse> lists) {
        for (String name : names) {
            CostResponse costResponse = new CostResponse(totalCost, percent, name, getExpenditureByCost(revenueExpenditure, name),
                    (int) ((double) getExpenditureByCost(revenueExpenditure, name) / (double) totalExp * 100.0));

            lists.add(costResponse);
        }
    }

}
