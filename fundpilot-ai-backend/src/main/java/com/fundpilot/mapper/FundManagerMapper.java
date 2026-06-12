package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.FundManager;
import com.fundpilot.vo.ManagerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 基金经理 Mapper
 */
@Mapper
public interface FundManagerMapper extends BaseMapper<FundManager> {

    /**
     * 根据基金代码查询经理信息（通过 fund.manager_id 关联）
     */
    ManagerVO selectManagerByFundCode(@Param("fundCode") String fundCode);
}
