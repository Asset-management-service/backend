package com.backend.moamoa.domain.settlement.controller;

import com.backend.moamoa.domain.settlement.dto.response.*;
import com.backend.moamoa.domain.settlement.dto.response.settle.MonthSettleResponse;
import com.backend.moamoa.domain.settlement.dto.response.settle.WeekSettleResponse;
import com.backend.moamoa.domain.settlement.dto.response.settle.YearSettleResponse;
import com.backend.moamoa.domain.settlement.dto.response.total.ComparisonsResponse;
import com.backend.moamoa.domain.settlement.service.SettlementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "결산 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 주의 시작 날짜를 입력받아, 지난 혹은 미래의 8개의 데이터를 가져와 출력합니다.
     * @return
     */
    @GetMapping("/week")
    @ApiOperation(value = "가계부 주별 결산", notes = "입력받은 주의 속한 날짜를 기준으로 8개의 데이터를 출력합니다. 테스트 용 입니다!")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "해당 주의 속한 날짜(입력하지 않으면 현재 날짜로 설정 합니다.)", example = "2022-04-11", required = false),
            @ApiImplicitParam(name = "type", value = "과거 데이터 출력(left), 미래 데이터 출력(right)", example = "left", required = true)
    })
    public List<WeekSettleResponse> getWeekExpenditure(@RequestParam(required = false) String date, @RequestParam String type) {
        return settlementService.getWeekExpenditure(date, type);
    }

    @GetMapping("/week/detail")
    @ApiOperation(value = "가계부 주 자세한 결산 내역", notes = "입력받은 날짜를 기준으로 해당 주의 자세한 결산 내역을 출력합니다. 테스트 용 입니다!")
    @ApiImplicitParam(name = "date", value = "출력하고자 하는 주의 시작 날짜를 입력해주세요.", example = "2022-04-21", required = true)
    public WeekResponse getWeekDetail(@RequestParam String date) {
        return settlementService.getWeekDetail(date);
    }

    /**
     * 달를 입력받아 지난 주의 5개의 데이터를 가져와 출력합니다.
     * @return
     */
    @GetMapping("/month")
    @ApiOperation(value = "가계부 달별 결산", notes = "입력받은 년과 월를 기준으로 5개의 데이터를 가져와 주별 결산을 출력합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "해당 년 월 (입력하지 않으면 현재 날짜로 설정합니다.)", example = "2022-04", required = false),
            @ApiImplicitParam(name = "type", value = "달 기준 과거 데이터 출력(left), 미래 데이터 출력(right)", example = "left", required = true)
    })
    public List<MonthSettleResponse> getMonthExpenditure(@RequestParam(required = false) String date, @RequestParam String type) {
        return settlementService.getMonthExpenditure(date, type);
    }

    /**
     * 년과 달을 입력받아 자세한 결산을 출력합니다.
     * @return
     */
    @GetMapping("/detail")
    @ApiOperation(value = "가계부 달,년 자세한 결산 내역", notes = "입력받은 달,년을 기준으로 해당 날짜의 자세한 결산 내역을 출력합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "해당 달, 년 (달을 출력하려면 2022-04, 년을 출력하려면 2022 형식으로 입력해주세요.)", example = "2022-04", required = true),
            @ApiImplicitParam(name = "type", value = "달이면 month, 년이면 year을 입력해주세요.", example = "month", required = true)
    })
    public MonthResponse getDetail(@RequestParam String date, @RequestParam String type) {
        return settlementService.getDetail(date, type);
    }

    /**
     * 년과 달을 입력받아 지난 달의 5개의 데이터를 가져와 출력합니다.
     * @return
     */
    @GetMapping("/year")
    @ApiOperation(value = "가계부 년별결산", notes = "입력받은 년을 기준으로 지난 년(일년 격차)의 5개의 데이터를 가져와 년별 결산을 출력합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "해당 년(입력하지 않으면 현재 날짜로 설정합니다.)", example = "2022", required = false),
            @ApiImplicitParam(name = "type", value = "년 기준 과거 데이터 출력(LEFT), 미래 데이터 출력(RIGHT)", example = "LEFT", required = true)
    })
    public List<YearSettleResponse> getYearExpenditure(@RequestParam(required = false) String date, @RequestParam String type) {
        return settlementService.getYearExpenditure(date, type);
    }

    /**
     * 전 달 데이터와 비교하여 데이터를 출력합니다.
     * @return
     */
    @GetMapping("/month/comparison")
    @ApiOperation(value = "지난 달과 비교하기", notes = "입력받은 년과 월를 기준으로 지난 달 데이터를 가져와 비교합니다.")
    @ApiImplicitParam(name = "date", value = "해당 년 월", example = "2022-04", required = true)
    public ComparisonsResponse getMonthComparison(@RequestParam String date) {
        return settlementService.getMonthComparison(date);
    }

    /**
     * 전 년 데이터와 비교하여 데이터를 출력합니다.
     * @return
     */
    @GetMapping("/year/comparison")
    @ApiOperation(value = "지난 년과 비교하기", notes = "입력받은 년을 기준으로 지난 년의 데이터를 가져와 비교합니다.")
    @ApiImplicitParam(name = "date", value = "해당 년", example = "2022", required = true)
    public ComparisonsResponse getYearComparison(@RequestParam String date) {
        return settlementService.getYearComparison(date);
    }

}
