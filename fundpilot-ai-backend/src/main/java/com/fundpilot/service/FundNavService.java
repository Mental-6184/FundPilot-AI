package com.fundpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fundpilot.entity.FundNav;
import com.fundpilot.vo.FundNavVO;
import com.fundpilot.vo.NavPointVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 基金净值服务接口
 */
public interface FundNavService extends IService<FundNav> {

    /**
     * 获取基金净值数据（最新净值 + 走势列表）
     *
     * @param fundCode 基金代码
     * @param days     查询天数
     * @return 净值数据
     */
    FundNavVO getNavData(String fundCode, int days);

    /**
     * 查询净值走势
     */
    List<NavPointVO> getNavTrend(String fundCode, LocalDate startDate, LocalDate endDate);

    /**
     * 查询最新净值
     */
    FundNav getLatestNav(String fundCode);

    /**
     * 查询指定日期净值
     */
    FundNav getNavByDate(String fundCode, LocalDate navDate);

    /**
     * 查询区间净值列表
     */
    List<FundNav> getNavBetween(String fundCode, LocalDate startDate, LocalDate endDate);

    /**
     * 查询指定日期之前最近的净值
     */
    FundNav getNavBeforeDate(String fundCode, LocalDate date);

    /**
     * 查询最早净值日期
     */
    LocalDate getEarliestDate(String fundCode);
}
