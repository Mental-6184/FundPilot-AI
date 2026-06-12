package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.Fund;
import com.fundpilot.vo.FundListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基金基本信息 Mapper
 */
@Mapper
public interface FundMapper extends BaseMapper<Fund> {

    /**
     * 搜索基金列表（关联最新净值，支持分页）
     */
    List<FundListVO> selectFundList(@Param("keyword") String keyword,
                                     @Param("fundType") String fundType,
                                     @Param("riskLevel") String riskLevel,
                                     @Param("sortBy") String sortBy,
                                     @Param("sortOrder") String sortOrder,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    /**
     * 搜索基金总数
     */
    int selectFundCount(@Param("keyword") String keyword,
                        @Param("fundType") String fundType,
                        @Param("riskLevel") String riskLevel);

    /**
     * 根据基金代码查询
     */
    Fund selectByFundCode(@Param("fundCode") String fundCode);
}
