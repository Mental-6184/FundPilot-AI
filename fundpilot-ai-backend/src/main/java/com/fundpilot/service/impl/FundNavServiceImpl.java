package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fundpilot.common.exception.BizException;
import com.fundpilot.common.result.ResultCode;
import com.fundpilot.entity.FundNav;
import com.fundpilot.mapper.FundNavMapper;
import com.fundpilot.service.FundNavService;
import com.fundpilot.vo.FundNavVO;
import com.fundpilot.vo.NavPointVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 基金净值服务实现
 */
@Service
public class FundNavServiceImpl extends ServiceImpl<FundNavMapper, FundNav> implements FundNavService {

    @Override
    public FundNavVO getNavData(String fundCode, int days) {
        FundNav latest = getLatestNav(fundCode);
        if (latest == null) {
            throw new BizException(ResultCode.FUND_NOT_FOUND);
        }

        LocalDate endDate = latest.getNavDate();
        LocalDate startDate = endDate.minusDays(days);
        List<NavPointVO> trend = getNavTrend(fundCode, startDate, endDate);

        FundNavVO vo = new FundNavVO();
        vo.setFundCode(fundCode);
        vo.setLatestNav(latest.getUnitNav());
        vo.setLatestAccNav(latest.getAccNav());
        vo.setLatestNavDate(latest.getNavDate());
        vo.setDailyReturn(latest.getDailyReturn());
        vo.setTrend(trend);
        return vo;
    }

    @Override
    public List<NavPointVO> getNavTrend(String fundCode, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectNavTrend(fundCode, startDate, endDate);
    }

    @Override
    public FundNav getLatestNav(String fundCode) {
        return baseMapper.selectLatestNav(fundCode);
    }

    @Override
    public FundNav getNavByDate(String fundCode, LocalDate navDate) {
        return baseMapper.selectByDate(fundCode, navDate);
    }

    @Override
    public List<FundNav> getNavBetween(String fundCode, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectNavBetween(fundCode, startDate, endDate);
    }

    @Override
    public FundNav getNavBeforeDate(String fundCode, LocalDate date) {
        return baseMapper.selectNavBeforeDate(fundCode, date);
    }

    @Override
    public LocalDate getEarliestDate(String fundCode) {
        return baseMapper.selectEarliestDate(fundCode);
    }
}
