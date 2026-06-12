package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fundpilot.entity.FundHolding;
import com.fundpilot.mapper.FundHoldingMapper;
import com.fundpilot.service.FundHoldingService;
import com.fundpilot.vo.FundHoldingVO;
import com.fundpilot.vo.HoldingVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 基金持仓服务实现
 */
@Service
public class FundHoldingServiceImpl extends ServiceImpl<FundHoldingMapper, FundHolding> implements FundHoldingService {

    @Override
    public FundHoldingVO getHoldingData(String fundCode) {
        LocalDate latestReportDate = baseMapper.selectLatestReportDate(fundCode);
        List<HoldingVO> holdings = List.of();
        if (latestReportDate != null) {
            holdings = baseMapper.selectHoldingsByDate(fundCode, latestReportDate);
        }

        FundHoldingVO vo = new FundHoldingVO();
        vo.setFundCode(fundCode);
        vo.setReportDate(latestReportDate);
        vo.setTopHoldings(holdings);
        return vo;
    }

    @Override
    public List<HoldingVO> getTopHoldings(String fundCode, int limit) {
        LocalDate latestReportDate = baseMapper.selectLatestReportDate(fundCode);
        if (latestReportDate == null) {
            return List.of();
        }
        return baseMapper.selectHoldingsByDate(fundCode, latestReportDate);
    }
}
