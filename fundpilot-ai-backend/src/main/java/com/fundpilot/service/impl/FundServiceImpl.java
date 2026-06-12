package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fundpilot.common.exception.BizException;
import com.fundpilot.common.result.PageResult;
import com.fundpilot.common.result.ResultCode;
import com.fundpilot.dto.FundQueryDTO;
import com.fundpilot.entity.Fund;
import com.fundpilot.entity.FundNav;
import com.fundpilot.mapper.FundMapper;
import com.fundpilot.service.FundManagerService;
import com.fundpilot.service.FundNavService;
import com.fundpilot.service.FundService;
import com.fundpilot.vo.FundDetailVO;
import com.fundpilot.vo.FundListVO;
import com.fundpilot.vo.ManagerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 基金服务实现
 */
@Service
@RequiredArgsConstructor
public class FundServiceImpl extends ServiceImpl<FundMapper, Fund> implements FundService {

    private final FundNavService fundNavService;
    private final FundManagerService fundManagerService;

    @Override
    public PageResult<FundListVO> searchFunds(FundQueryDTO queryDTO) {
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        int limit = queryDTO.getPageSize();

        List<FundListVO> records = baseMapper.selectFundList(
                queryDTO.getKeyword(),
                queryDTO.getFundType(),
                queryDTO.getRiskLevel(),
                queryDTO.getSortBy(),
                queryDTO.getSortOrder(),
                offset,
                limit
        );

        int total = baseMapper.selectFundCount(
                queryDTO.getKeyword(),
                queryDTO.getFundType(),
                queryDTO.getRiskLevel()
        );

        return PageResult.of(total, records, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public FundDetailVO getFundDetail(String fundCode) {
        Fund fund = getByFundCode(fundCode);
        if (fund == null) {
            throw new BizException(ResultCode.FUND_NOT_FOUND);
        }

        FundDetailVO detail = new FundDetailVO();
        detail.setFundCode(fund.getFundCode());
        detail.setFundName(fund.getFundName());
        detail.setFundType(fund.getFundType());
        detail.setRiskLevel(fund.getRiskLevel());
        detail.setEstablishDate(fund.getEstablishDate());
        detail.setBenchmark(fund.getBenchmark());
        detail.setInvestStrategy(fund.getInvestStrategy());
        detail.setScale(fund.getScale());
        detail.setShare(fund.getShare());
        detail.setFeeRate(fund.getFeeRate());
        detail.setCustodian(fund.getCustodian());
        detail.setBuyStatus(fund.getBuyStatus());
        detail.setRedeemStatus(fund.getRedeemStatus());

        // 最新净值
        FundNav latestNav = fundNavService.getLatestNav(fundCode);
        if (latestNav != null) {
            detail.setLatestNav(latestNav.getUnitNav());
            detail.setLatestAccNav(latestNav.getAccNav());
            detail.setLatestNavDate(latestNav.getNavDate());
        }

        // 基金经理
        if (fund.getManagerId() != null) {
            ManagerVO manager = fundManagerService.getManagerDetail(fund.getManagerId());
            detail.setManager(manager);
        }

        return detail;
    }

    @Override
    public Fund getByFundCode(String fundCode) {
        return baseMapper.selectByFundCode(fundCode);
    }
}
