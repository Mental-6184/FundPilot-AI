package com.fundpilot.tool;

import com.fundpilot.common.result.PageResult;
import com.fundpilot.dto.FundQueryDTO;
import com.fundpilot.service.FundManagerService;
import com.fundpilot.service.FundService;
import com.fundpilot.tool.dto.ToolResult;
import com.fundpilot.vo.FundDetailVO;
import com.fundpilot.vo.FundListVO;
import com.fundpilot.vo.ManagerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FundInfoTool — 基金基本信息查询
 * 职责：搜索基金、查询详情、查询经理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundInfoTool {

    private final FundService fundService;
    private final FundManagerService fundManagerService;

    /**
     * 搜索基金列表
     */
    @Cacheable(value = "fund:detail", key = "#keyword + ':' + #fundType + ':' + #riskLevel", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<List<FundListVO>> searchFunds(String keyword, String fundType, String riskLevel) {
        log.info("[FundInfoTool] searchFunds: keyword={}, fundType={}, riskLevel={}", keyword, fundType, riskLevel);
        try {
            FundQueryDTO queryDTO = new FundQueryDTO();
            queryDTO.setKeyword(keyword);
            queryDTO.setFundType(fundType);
            queryDTO.setRiskLevel(riskLevel);
            queryDTO.setPageSize(20);
            PageResult<FundListVO> result = fundService.searchFunds(queryDTO);
            return ToolResult.ok("找到" + result.getTotal() + "只基金", result.getRecords());
        } catch (Exception e) {
            log.error("[FundInfoTool] searchFunds 异常", e);
            return ToolResult.fail("搜索基金失败: " + e.getMessage());
        }
    }

    /**
     * 获取单只基金的完整详情
     */
    @Cacheable(value = "fund:detail", key = "#fundCode", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<FundDetailVO> getFundDetail(String fundCode) {
        log.info("[FundInfoTool] getFundDetail: fundCode={}", fundCode);
        try {
            FundDetailVO detail = fundService.getFundDetail(fundCode);
            return ToolResult.ok(detail);
        } catch (Exception e) {
            log.error("[FundInfoTool] getFundDetail 异常: fundCode={}", fundCode, e);
            return ToolResult.fail("查询基金详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取基金经理的详细信息
     */
    @Cacheable(value = "fund:manager", key = "#fundCode", unless = "#result == null || !#result.isSuccess()")
    public ToolResult<ManagerVO> getFundManager(String fundCode) {
        log.info("[FundInfoTool] getFundManager: fundCode={}", fundCode);
        try {
            ManagerVO manager = fundManagerService.getManagerByFundCode(fundCode);
            if (manager == null) {
                return ToolResult.fail("未找到基金" + fundCode + "的经理信息");
            }
            return ToolResult.ok(manager);
        } catch (Exception e) {
            log.error("[FundInfoTool] getFundManager 异常: fundCode={}", fundCode, e);
            return ToolResult.fail("查询基金经理失败: " + e.getMessage());
        }
    }
}
