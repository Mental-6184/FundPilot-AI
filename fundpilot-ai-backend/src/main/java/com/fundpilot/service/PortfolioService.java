package com.fundpilot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fundpilot.dto.PortfolioCreateDTO;
import com.fundpilot.dto.PortfolioItemDTO;
import com.fundpilot.dto.PortfolioUpdateDTO;
import com.fundpilot.entity.Portfolio;
import com.fundpilot.vo.PortfolioVO;

import java.util.List;

/**
 * 用户组合服务接口
 */
public interface PortfolioService extends IService<Portfolio> {

    /**
     * 查询用户的所有组合
     */
    List<PortfolioVO> getUserPortfolios(Long userId);

    /**
     * 创建组合
     */
    PortfolioVO createPortfolio(Long userId, PortfolioCreateDTO dto);

    /**
     * 修改组合
     */
    PortfolioVO updatePortfolio(Long userId, PortfolioUpdateDTO dto);

    /**
     * 删除组合
     */
    void deletePortfolio(Long userId, Long portfolioId);

    /**
     * 查询组合详情
     */
    PortfolioVO getPortfolioDetail(Long userId, Long portfolioId);

    /**
     * 向组合添加基金
     */
    void addFundToPortfolio(Long userId, Long portfolioId, PortfolioItemDTO itemDTO);

    /**
     * 更新组合内基金
     */
    void updateFundInPortfolio(Long userId, Long portfolioId, PortfolioItemDTO itemDTO);

    /**
     * 从组合移除基金
     */
    void removeFundFromPortfolio(Long userId, Long portfolioId, String fundCode);
}
