package com.fundpilot.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fundpilot.common.result.R;
import com.fundpilot.dto.PortfolioCreateDTO;
import com.fundpilot.dto.PortfolioItemDTO;
import com.fundpilot.dto.PortfolioUpdateDTO;
import com.fundpilot.service.PortfolioAnalysisService;
import com.fundpilot.service.PortfolioService;
import com.fundpilot.vo.PortfolioAnalysisVO;
import com.fundpilot.vo.PortfolioVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户组合控制器
 */
@Tag(name = "投资组合", description = "组合增删改查 + 组合分析")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PortfolioAnalysisService portfolioAnalysisService;

    private Long currentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    @Operation(summary = "获取当前用户的所有组合")
    @GetMapping({"", "/list"})
    public R<List<PortfolioVO>> list() {
        return R.ok(portfolioService.getUserPortfolios(currentUserId()));
    }

    @Operation(summary = "创建组合")
    @PostMapping
    public R<PortfolioVO> create(@Valid @RequestBody PortfolioCreateDTO dto) {
        return R.ok(portfolioService.createPortfolio(currentUserId(), dto));
    }

    @Operation(summary = "修改组合")
    @PutMapping
    public R<PortfolioVO> update(@Valid @RequestBody PortfolioUpdateDTO dto) {
        return R.ok(portfolioService.updatePortfolio(currentUserId(), dto));
    }

    @Operation(summary = "获取组合详情")
    @GetMapping("/{portfolioId}")
    public R<PortfolioVO> detail(@PathVariable Long portfolioId) {
        return R.ok(portfolioService.getPortfolioDetail(currentUserId(), portfolioId));
    }

    @Operation(summary = "删除组合")
    @DeleteMapping("/{portfolioId}")
    public R<Void> delete(@PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(currentUserId(), portfolioId);
        return R.ok();
    }

    @Operation(summary = "向组合添加基金")
    @PostMapping("/{portfolioId}/fund")
    public R<Void> addFund(@PathVariable Long portfolioId, @Valid @RequestBody PortfolioItemDTO dto) {
        portfolioService.addFundToPortfolio(currentUserId(), portfolioId, dto);
        return R.ok();
    }

    @Operation(summary = "修改组合内基金")
    @PutMapping("/{portfolioId}/fund")
    public R<Void> updateFund(@PathVariable Long portfolioId, @Valid @RequestBody PortfolioItemDTO dto) {
        portfolioService.updateFundInPortfolio(currentUserId(), portfolioId, dto);
        return R.ok();
    }

    @Operation(summary = "从组合移除基金")
    @DeleteMapping("/{portfolioId}/fund/{fundCode}")
    public R<Void> removeFund(@PathVariable Long portfolioId, @PathVariable String fundCode) {
        portfolioService.removeFundFromPortfolio(currentUserId(), portfolioId, fundCode);
        return R.ok();
    }

    @Operation(summary = "分析组合")
    @GetMapping("/{portfolioId}/analysis")
    public R<PortfolioAnalysisVO> analysis(@PathVariable Long portfolioId) {
        return R.ok(portfolioAnalysisService.analyze(currentUserId(), portfolioId));
    }
}
