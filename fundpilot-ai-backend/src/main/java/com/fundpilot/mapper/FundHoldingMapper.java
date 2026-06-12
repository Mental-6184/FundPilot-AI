package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.FundHolding;
import com.fundpilot.vo.HoldingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 基金持仓 Mapper
 */
@Mapper
public interface FundHoldingMapper extends BaseMapper<FundHolding> {

    /**
     * 查询最新报告日期
     */
    @Select("SELECT MAX(report_date) FROM fund_holding WHERE fund_code = #{fundCode} AND deleted = 0")
    LocalDate selectLatestReportDate(@Param("fundCode") String fundCode);

    /**
     * 查询指定报告日期的持仓列表
     */
    List<HoldingVO> selectHoldingsByDate(@Param("fundCode") String fundCode,
                                          @Param("reportDate") LocalDate reportDate);
}
