package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.FundNav;
import com.fundpilot.vo.NavPointVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 基金净值 Mapper
 */
@Mapper
public interface FundNavMapper extends BaseMapper<FundNav> {

    /**
     * 查询基金净值走势
     */
    List<NavPointVO> selectNavTrend(@Param("fundCode") String fundCode,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    /**
     * 查询最新净值
     */
    @Select("SELECT * FROM fund_nav WHERE fund_code = #{fundCode} AND deleted = 0 ORDER BY nav_date DESC LIMIT 1")
    FundNav selectLatestNav(@Param("fundCode") String fundCode);

    /**
     * 查询指定日期的净值
     */
    @Select("SELECT * FROM fund_nav WHERE fund_code = #{fundCode} AND nav_date = #{navDate} AND deleted = 0")
    FundNav selectByDate(@Param("fundCode") String fundCode, @Param("navDate") LocalDate navDate);

    /**
     * 查询指定日期范围内的净值列表 (按日期升序)
     */
    @Select("SELECT * FROM fund_nav WHERE fund_code = #{fundCode} AND nav_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY nav_date ASC")
    List<FundNav> selectNavBetween(@Param("fundCode") String fundCode,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    /**
     * 查询指定日期之前的净值 (用于计算区间收益)
     */
    @Select("SELECT * FROM fund_nav WHERE fund_code = #{fundCode} AND nav_date <= #{date} AND deleted = 0 ORDER BY nav_date DESC LIMIT 1")
    FundNav selectNavBeforeDate(@Param("fundCode") String fundCode, @Param("date") LocalDate date);

    /**
     * 查询最早净值日期
     */
    @Select("SELECT MIN(nav_date) FROM fund_nav WHERE fund_code = #{fundCode} AND deleted = 0")
    LocalDate selectEarliestDate(@Param("fundCode") String fundCode);
}
