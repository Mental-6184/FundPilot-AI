package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.Portfolio;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户组合 Mapper
 */
@Mapper
public interface PortfolioMapper extends BaseMapper<Portfolio> {
}
