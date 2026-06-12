package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.PortfolioItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组合明细 Mapper
 */
@Mapper
public interface PortfolioItemMapper extends BaseMapper<PortfolioItem> {
}
