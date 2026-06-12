package com.fundpilot.controller;

import com.fundpilot.common.result.R;
import com.fundpilot.service.AnalyticsService;
import com.fundpilot.vo.FundPerformanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 基金分析接口
 */
@Tag(name = "基金分析", description = "基金业绩计算、对比分析")
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "对比多只基金")
    @PostMapping("/compare")
    public R<Map<String, FundPerformanceVO>> compare(
            @RequestBody CompareRequest request) {
        return R.ok(analyticsService.compareFunds(request.getFundCodes()));
    }

    @Data
    public static class CompareRequest {
        private List<String> fundCodes;
    }
}
