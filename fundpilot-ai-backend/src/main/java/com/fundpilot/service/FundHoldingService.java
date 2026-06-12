package com.fundpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fundpilot.entity.FundHolding;
import com.fundpilot.vo.FundHoldingVO;
import com.fundpilot.vo.HoldingVO;

import java.util.List;

/**
 * 基金持仓服务接口
 */
public interface FundHoldingService extends IService<FundHolding> {

    /**
     * 获取基金持仓数据（最新报告日期 + 前十大持仓）
     */
    FundHoldingVO getHoldingData(String fundCode);

    /**
     * 查询前十大持仓
     */
    List<HoldingVO> getTopHoldings(String fundCode, int limit);
}
