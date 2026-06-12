package com.fundpilot.controller;

import com.fundpilot.common.result.PageResult;
import com.fundpilot.common.result.R;
import com.fundpilot.dto.FundQueryDTO;
import com.fundpilot.service.FundHoldingService;
import com.fundpilot.service.FundManagerService;
import com.fundpilot.service.FundNavService;
import com.fundpilot.service.AnalyticsService;
import com.fundpilot.service.FundService;
import com.fundpilot.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基金控制器
 */
@Tag(name = "基金管理", description = "基金搜索、详情、净值、持仓、经理")
@RestController
@RequestMapping("/api/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;
    private final FundNavService fundNavService;
    private final FundHoldingService fundHoldingService;
    private final FundManagerService fundManagerService;
    private final AnalyticsService analyticsService;

    @Operation(summary = "搜索基金列表")
    @GetMapping("/search")
    public R<PageResult<FundListVO>> search(FundQueryDTO queryDTO) {
        return R.ok(fundService.searchFunds(queryDTO));
    }

    @Operation(summary = "获取基金详情")
    @GetMapping("/{code}")
    public R<FundDetailVO> detail(
            @Parameter(description = "基金代码") @PathVariable String code) {
        return R.ok(fundService.getFundDetail(code));
    }

    @Operation(summary = "获取基金净值走势")
    @GetMapping("/{code}/nav")
    public R<FundNavVO> nav(
            @Parameter(description = "基金代码") @PathVariable String code,
            @Parameter(description = "查询天数，默认365") @RequestParam(defaultValue = "365") int days) {
        return R.ok(fundNavService.getNavData(code, days));
    }

    @Operation(summary = "获取基金持仓信息")
    @GetMapping("/{code}/holding")
    public R<FundHoldingVO> holding(
            @Parameter(description = "基金代码") @PathVariable String code) {
        return R.ok(fundHoldingService.getHoldingData(code));
    }

    @Operation(summary = "获取基金经理信息")
    @GetMapping("/{code}/manager")
    public R<ManagerVO> manager(
            @Parameter(description = "基金代码") @PathVariable String code) {
        return R.ok(fundManagerService.getManagerByFundCode(code));
    }

    @Operation(summary = "获取基金业绩指标")
    @GetMapping("/{code}/performance")
    public R<FundPerformanceVO> performance(
            @Parameter(description = "基金代码") @PathVariable String code) {
        return R.ok(analyticsService.calculateFullPerformance(code));
    }
}
