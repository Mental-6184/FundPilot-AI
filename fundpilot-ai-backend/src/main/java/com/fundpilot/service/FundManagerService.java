package com.fundpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fundpilot.entity.FundManager;
import com.fundpilot.vo.ManagerVO;

/**
 * 基金经理服务接口
 */
public interface FundManagerService extends IService<FundManager> {

    /**
     * 根据经理ID查询详情
     */
    ManagerVO getManagerDetail(Long managerId);

    /**
     * 根据基金代码查询经理信息（通过 fund.manager_id 关联）
     */
    ManagerVO getManagerByFundCode(String fundCode);
}
