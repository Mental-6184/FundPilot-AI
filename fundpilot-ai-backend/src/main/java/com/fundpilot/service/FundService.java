package com.fundpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fundpilot.common.result.PageResult;
import com.fundpilot.dto.FundQueryDTO;
import com.fundpilot.entity.Fund;
import com.fundpilot.vo.FundDetailVO;
import com.fundpilot.vo.FundListVO;

/**
 * 基金服务接口
 */
public interface FundService extends IService<Fund> {

    /**
     * 搜索基金列表（分页）
     */
    PageResult<FundListVO> searchFunds(FundQueryDTO queryDTO);

    /**
     * 获取基金详情
     */
    FundDetailVO getFundDetail(String fundCode);

    /**
     * 根据基金代码查询实体
     */
    Fund getByFundCode(String fundCode);
}
