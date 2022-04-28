package com.backend.moamoa.domain.settlement.service;

import com.backend.moamoa.domain.asset.entity.AssetCategory;
import com.backend.moamoa.domain.asset.entity.AssetCategoryType;
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

        TotalResponse totalResponse = new TotalResponse();
        getTotalCost(revenueExpenditure, totalResponse);

        List<AssetCategory> assetCategory = assetCategoryRepository.findByUser(user);
        List<String> fixed = new ArrayList<>();
        List<String> variable = new ArrayList<>();
        List<String> revenues = new ArrayList<>();

        for (AssetCategory category : assetCategory) {
            if (category.getAssetCategoryType().equals(AssetCategoryType.FIXED)) {
                fixed.add(category.getCategoryName());
            } else if (category.getAssetCategoryType().equals(AssetCategoryType.VARIABLE)) {
                variable.add(category.getCategoryName());
            } else if (category.getAssetCategoryType().equals(AssetCategoryType.REVENUE)) {
                revenues.add(category.getCategoryName());
            }
        }

        for (RevenueExpenditure r : revenueExpenditure) {
            AssetCategory category = assetCategory.stream().filter(a -> a.getCategoryName().equals(r.getCategoryName())).findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ASSET_CATEGORY));

            if (category.getAssetCategoryType().equals(AssetCategoryType.FIXED)) {
                totalResponse.addTotalFixed(r.getCost());
            } else if (category.getAssetCategoryType().equals(AssetCategoryType.VARIABLE)) {
                totalResponse.addTotalVariable(r.getCost());
            }
        }

        int totalFixedPercent = getRatio(totalResponse.getTotalFixed(), totalResponse.getTotalExp());
        int totalVariablePercent = getRatio(totalResponse.getTotalVariable(), totalResponse.getTotalExp());

        ExpenditureRatio expenditureRatio = expenditureRatioRepository.findByUser(user).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RATIO));

        Boolean fixedExceed = null, variableExceed = null;

        if (type.equals("month")) {
            fixedExceed = (totalFixedPercent > expenditureRatio.getFixed());
            variableExceed = (totalVariablePercent > expenditureRatio.getVariable());
        }

        List<CostResponse> fixedRes = new ArrayList<>();
        List<CostResponse> costRes = new ArrayList<>();
        List<CostResponse> revenueRes = new ArrayList<>();

        setCostResponse(revenueExpenditure, totalResponse.getTotalExp(), fixed, fixedRes);
        setCostResponse(revenueExpenditure, totalResponse.getTotalExp(), variable, costRes);

        for (String revenue : revenues) {
            CostResponse costResponse =
                    CostResponse.builder()
                            .categoryName(revenue)
                            .cost(getExpenditureByCost(revenueExpenditure, revenue))
                            .percent(getRatio(getExpenditureByCost(revenueExpenditure, revenue), totalResponse.getTotalRevenue()))
                            .build();

            if (getExpenditureByCost(revenueExpenditure, revenue) > 0)
                revenueRes.add(costResponse);
        }

        fixedRes = fixedRes.stream().sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());
        costRes = costRes.stream().sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());
        revenueRes = revenueRes.stream().sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());

        List<String> categories = getMostAndLeastCategory(fixedRes, costRes);

        return MonthResponse.builder()
                .mostExpCategory(categories.get(0))
                .leastExpCategory(categories.get(1))
                .totalExp(totalResponse.getTotalExp())
                .totalRevenue(totalResponse.getTotalRevenue())
                .totalFixed(totalResponse.getTotalFixed())
                .totalFixedPercent(totalFixedPercent)
                .totalVariable(totalResponse.getTotalVariable())
                .totalVarPercent(totalVariablePercent)
                .fixedCostResponses(fixedRes)
                .variableCostResponses(costRes)
                .revenueResponses(revenueRes)
                .netIncome(totalResponse.getTotalRevenue() - totalResponse.getTotalExp())
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
            if (getExpenditureByCost(revenueExpenditure, categoryName) > 0) {
                costResponses.add(
                        CostResponse.builder()
                                .categoryName(categoryName)
                                .cost(getExpenditureByCost(revenueExpenditure, categoryName))
                                .percent(getRatio(getExpenditureByCost(revenueExpenditure, categoryName), totalExp))
                                .build()
                );
            }
        }

        String mostCategory = null, minCategory = null;

        if (!costResponses.isEmpty()) {
            costResponses = costResponses.stream()
                    .sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());

            mostCategory = costResponses.get(0).getCategoryName();
            minCategory = costResponses.get(costResponses.size() - 1).getCategoryName();
        }

        return new WeekResponse(standard.getYear(), standard.getMonthValue(), getWeekNumber(standard), mostCategory, minCategory, totalExp, costResponses);
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
        ComparisonResponse totalRev = getComparisonResponse(prevRes.getTotalRevenue(), presentRes.getTotalRevenue(), TYPE_REVENUE);
        ComparisonResponse totalFixed = getComparisonResponse(prevRes.getTotalFixed(), presentRes.getTotalFixed(), TYPE_FIXED);
        ComparisonResponse totalVar = getComparisonResponse(prevRes.getTotalVariable(), presentRes.getTotalVariable(), TYPE_VARIABLE);

        List<ComparisonResponse> fixedResponses = getTotalComparisonResponses(prevRes, presentRes, TYPE_FIXED);
        List<ComparisonResponse> varResponses = getTotalComparisonResponses(prevRes, presentRes, TYPE_VARIABLE);
        List<ComparisonResponse> revResponses = getTotalComparisonResponses(prevRes, presentRes, TYPE_REVENUE);

        ComparisonResponse netResponse = getComparisonResponse(prevRes.getTotalRevenue() - prevRes.getTotalExp(),
                presentRes.getTotalRevenue() - presentRes.getTotalExp(), TYPE_NET_PROFIT);

        return new ComparisonsResponse(totalExp, totalFixed, fixedResponses, totalVar, varResponses, totalRev, revResponses, netResponse);
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

    private ComparisonResponse getComparisonResponse(int prevTotal, int presentTotal, String type) {
        int diff = presentTotal - prevTotal;
        int ratio = getRatio(diff, prevTotal);

        return new ComparisonResponse(type, prevTotal, presentTotal, diff, ratio);
    }

    private void setTotalComparisonResponse(List<CostResponse> prevLists, List<CostResponse> presentLists, List<ComparisonResponse> responses) {
        List<CostResponse> prev = new ArrayList<>();
        List<CostResponse> present = new ArrayList<>();

        for (CostResponse costResponse : prevLists) {
            prev.add(costResponse);
        }
        for (CostResponse costResponse : presentLists) {
            present.add(costResponse);
        }

        for (CostResponse costResponse : prevLists) {
            if (!present.stream().anyMatch(s -> s.getCategoryName().equals(costResponse.getCategoryName()))) {
                present.add(CostResponse.builder()
                        .categoryName(costResponse.getCategoryName())
                        .cost(0)
                        .percent(0)
                        .build());
            }
        }
        for (CostResponse costResponse : presentLists) {
            if (!prev.stream().anyMatch(s -> s.getCategoryName().equals(costResponse.getCategoryName()))) {
                prev.add(CostResponse.builder()
                        .categoryName(costResponse.getCategoryName())
                        .cost(0)
                        .percent(0)
                        .build());
            }
        }

        prev = prev.stream().sorted(Comparator.comparing(CostResponse::getCategoryName)).collect(Collectors.toList());
        present = present.stream().sorted(Comparator.comparing(CostResponse::getCategoryName)).collect(Collectors.toList());

        for (int i = 0; i < present.size(); i++) {

            int difference = present.get(i).getCost() - prev.get(i).getCost();
            int ratio = getRatio(difference, prev.get(i).getCost());

            ComparisonResponse categoryName = new ComparisonResponse(present.get(i).getCategoryName(), prev.get(i).getCost(), present.get(i).getCost(), difference, ratio);

            responses.add(categoryName);
        }
    }

    private int getRatio(int difference, int prev) {
        if (prev != 0) {
            if (prev < 0)
                return (int) ((double) difference / (double) prev * 100.0) * -1;
            else
                return (int) ((double) difference / (double) prev * 100.0);
        } else {
            if (difference == 0) return 0;
            else if (difference > 0) return 100;
            else return -100;
        }
    }

    private void getTotalCost(List<RevenueExpenditure> revenueExpenditureList, TotalResponse totalResponse) {
        revenueExpenditureList.stream()
                .forEach(r -> totalResponse.setTotalResponse(r.getRevenueExpenditureType().toString(), r.getCost()));
    }

    private List<String> getMostAndLeastCategory(List<CostResponse> fixedRes, List<CostResponse> costRes) {

        List<CostResponse> compareList = new ArrayList<>();
        for (CostResponse costResponse : fixedRes) {
            compareList.add(costResponse);
        }
        for (CostResponse costResponse : costRes) {
            compareList.add(costResponse);
        }
        compareList = compareList.stream().sorted(Comparator.comparing(CostResponse::getCost).reversed()).collect(Collectors.toList());

        String maxName = null, minName = null;
        if (!compareList.isEmpty()) {
            maxName = compareList.get(0).getCategoryName();
            minName = compareList.get(compareList.size() - 1).getCategoryName();

        }
        List<String> nameList = new ArrayList<>();
        nameList.add(maxName);
        nameList.add(minName);

        return nameList;
    }

    private void setCostResponse(List<RevenueExpenditure> revenueExpenditure,
                                 int totalExp, List<String> names, List<CostResponse> lists) {
        for (String name : names) {
            CostResponse costResponse = new CostResponse(name, getExpenditureByCost(revenueExpenditure, name),
                    getRatio(getExpenditureByCost(revenueExpenditure, name), totalExp));

            if (getExpenditureByCost(revenueExpenditure, name) > 0)
                lists.add(costResponse);
        }
    }

    private int getExpenditureByCost(List<RevenueExpenditure> revenueExpenditureList, String cost) {
        return revenueExpenditureList.stream().filter(r -> r.getCategoryName().equals(cost)).mapToInt(RevenueExpenditure::getCost).sum();
    }

    private int getRevenueExpenditure(List<RevenueExpenditure> revenueExpenditureList, String type) {
        return revenueExpenditureList.stream().filter(r -> r.getRevenueExpenditureType().toString().equals(type)).mapToInt(RevenueExpenditure::getCost).sum();
    }

    private List<ComparisonResponse> getTotalComparisonResponses(MonthResponse prevRes, MonthResponse presentRes, String type) {
        List<ComparisonResponse> totalResponses = new ArrayList<>();

        if (type.equals(TYPE_FIXED)) {
            List<CostResponse> fixedPrev = prevRes.getFixedCostResponses();
            List<CostResponse> fixedPresent = presentRes.getFixedCostResponses();
            setTotalComparisonResponse(fixedPrev, fixedPresent, totalResponses);
        } else if (type.equals(TYPE_VARIABLE)) {
            List<CostResponse> varPrev = prevRes.getVariableCostResponses();
            List<CostResponse> varPresent = presentRes.getVariableCostResponses();
            setTotalComparisonResponse(varPrev, varPresent, totalResponses);
        } else if (type.equals(TYPE_REVENUE)) {
            List<CostResponse> varPrev = prevRes.getRevenueResponses();
            List<CostResponse> varPresent = presentRes.getRevenueResponses();
            setTotalComparisonResponse(varPrev, varPresent, totalResponses);
        }
        return totalResponses;
    }

}
