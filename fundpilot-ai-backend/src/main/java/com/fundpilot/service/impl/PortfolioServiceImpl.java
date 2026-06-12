package com.fundpilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fundpilot.common.exception.BizException;
import com.fundpilot.common.result.ResultCode;
import com.fundpilot.dto.PortfolioCreateDTO;
import com.fundpilot.dto.PortfolioItemDTO;
import com.fundpilot.dto.PortfolioUpdateDTO;
import com.fundpilot.entity.Fund;
import com.fundpilot.entity.FundNav;
import com.fundpilot.entity.Portfolio;
import com.fundpilot.entity.PortfolioItem;
import com.fundpilot.mapper.PortfolioItemMapper;
import com.fundpilot.mapper.PortfolioMapper;
import com.fundpilot.service.FundNavService;
import com.fundpilot.service.FundService;
import com.fundpilot.service.PortfolioService;
import com.fundpilot.vo.PortfolioFundItemVO;
import com.fundpilot.vo.PortfolioVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户组合服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl extends ServiceImpl<PortfolioMapper, Portfolio> implements PortfolioService {

    private final PortfolioItemMapper portfolioItemMapper;
    private final FundService fundService;
    private final FundNavService fundNavService;

    private static final int MAX_PORTFOLIOS = 10;
    private static final int MAX_FUNDS_PER_PORTFOLIO = 20;

    @Override
    public List<PortfolioVO> getUserPortfolios(Long userId) {
        try {
            List<Portfolio> portfolios = list(new LambdaQueryWrapper<Portfolio>()
                    .eq(Portfolio::getUserId, userId)
                    .orderByDesc(Portfolio::getUpdateTime));
            return portfolios.stream().map(this::toVO).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("[Portfolio] 获取组合列表失败: userId={}", userId, e);
            return List.of();
        }
    }

    @Override
    @Transactional
    public PortfolioVO createPortfolio(Long userId, PortfolioCreateDTO dto) {
        long count = count(new LambdaQueryWrapper<Portfolio>().eq(Portfolio::getUserId, userId));
        if (count >= MAX_PORTFOLIOS) {
            throw new BizException(ResultCode.PORTFOLIO_LIMIT_EXCEEDED);
        }

        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setPortfolioName(dto.getPortfolioName());
        portfolio.setDescription(dto.getDescription());
        portfolio.setRiskLevel(dto.getRiskLevel());
        portfolio.setTotalAmount(dto.getTotalAmount());
        portfolio.setStatus(1);
        save(portfolio);

        log.info("用户{}创建组合: {}", userId, dto.getPortfolioName());
        return toVO(portfolio);
    }

    @Override
    @Transactional
    public PortfolioVO updatePortfolio(Long userId, PortfolioUpdateDTO dto) {
        Portfolio portfolio = checkOwner(userId, dto.getPortfolioId());

        if (dto.getPortfolioName() != null) {
            portfolio.setPortfolioName(dto.getPortfolioName());
        }
        if (dto.getDescription() != null) {
            portfolio.setDescription(dto.getDescription());
        }
        if (dto.getRiskLevel() != null) {
            portfolio.setRiskLevel(dto.getRiskLevel());
        }
        if (dto.getTotalAmount() != null) {
            portfolio.setTotalAmount(dto.getTotalAmount());
        }
        updateById(portfolio);

        log.info("用户{}修改组合: {}", userId, portfolio.getPortfolioName());
        return toVO(portfolio);
    }

    @Override
    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        checkOwner(userId, portfolioId);
        removeById(portfolioId);
        portfolioItemMapper.delete(new LambdaQueryWrapper<PortfolioItem>()
                .eq(PortfolioItem::getPortfolioId, portfolioId));
        log.info("用户{}删除组合: {}", userId, portfolioId);
    }

    @Override
    public PortfolioVO getPortfolioDetail(Long userId, Long portfolioId) {
        Portfolio portfolio = checkOwner(userId, portfolioId);
        return toVO(portfolio);
    }

    @Override
    @Transactional
    public void addFundToPortfolio(Long userId, Long portfolioId, PortfolioItemDTO itemDTO) {
        checkOwner(userId, portfolioId);
        checkFundLimit(portfolioId);
        checkFundDuplicate(portfolioId, itemDTO.getFundCode());

        PortfolioItem item = new PortfolioItem();
        item.setPortfolioId(portfolioId);
        item.setFundCode(itemDTO.getFundCode());
        item.setInvestAmount(itemDTO.getInvestAmount());
        item.setInvestShares(itemDTO.getInvestShares());
        item.setTargetRatio(itemDTO.getTargetRatio());
        item.setBuyPrice(itemDTO.getBuyPrice());
        item.setBuyDate(itemDTO.getBuyDate() != null ? LocalDate.parse(itemDTO.getBuyDate()) : null);
        item.setRemark(itemDTO.getRemark());
        portfolioItemMapper.insert(item);

        log.info("组合{}添加基金: {}", portfolioId, itemDTO.getFundCode());
    }

    @Override
    @Transactional
    public void updateFundInPortfolio(Long userId, Long portfolioId, PortfolioItemDTO itemDTO) {
        checkOwner(userId, portfolioId);

        PortfolioItem item = getPortfolioItem(portfolioId, itemDTO.getFundCode());
        if (item == null) {
            throw new BizException("该基金不在组合中");
        }

        if (itemDTO.getInvestAmount() != null) item.setInvestAmount(itemDTO.getInvestAmount());
        if (itemDTO.getInvestShares() != null) item.setInvestShares(itemDTO.getInvestShares());
        if (itemDTO.getTargetRatio() != null) item.setTargetRatio(itemDTO.getTargetRatio());
        if (itemDTO.getBuyPrice() != null) item.setBuyPrice(itemDTO.getBuyPrice());
        if (itemDTO.getBuyDate() != null) item.setBuyDate(LocalDate.parse(itemDTO.getBuyDate()));
        if (itemDTO.getRemark() != null) item.setRemark(itemDTO.getRemark());
        portfolioItemMapper.updateById(item);
    }

    @Override
    @Transactional
    public void removeFundFromPortfolio(Long userId, Long portfolioId, String fundCode) {
        checkOwner(userId, portfolioId);
        portfolioItemMapper.delete(new LambdaQueryWrapper<PortfolioItem>()
                .eq(PortfolioItem::getPortfolioId, portfolioId)
                .eq(PortfolioItem::getFundCode, fundCode));
        log.info("组合{}移除基金: {}", portfolioId, fundCode);
    }

    // ==================== 内部方法 ====================

    private Portfolio checkOwner(Long userId, Long portfolioId) {
        Portfolio portfolio = getById(portfolioId);
        if (portfolio == null || !portfolio.getUserId().equals(userId)) {
            throw new BizException(ResultCode.PORTFOLIO_NOT_FOUND);
        }
        return portfolio;
    }

    private void checkFundLimit(Long portfolioId) {
        Long count = portfolioItemMapper.selectCount(
                new LambdaQueryWrapper<PortfolioItem>().eq(PortfolioItem::getPortfolioId, portfolioId));
        if (count >= MAX_FUNDS_PER_PORTFOLIO) {
            throw new BizException("组合最多包含" + MAX_FUNDS_PER_PORTFOLIO + "只基金");
        }
    }

    private void checkFundDuplicate(Long portfolioId, String fundCode) {
        Long count = portfolioItemMapper.selectCount(new LambdaQueryWrapper<PortfolioItem>()
                .eq(PortfolioItem::getPortfolioId, portfolioId)
                .eq(PortfolioItem::getFundCode, fundCode));
        if (count > 0) {
            throw new BizException("该基金已在组合中");
        }
    }

    private PortfolioItem getPortfolioItem(Long portfolioId, String fundCode) {
        return portfolioItemMapper.selectOne(new LambdaQueryWrapper<PortfolioItem>()
                .eq(PortfolioItem::getPortfolioId, portfolioId)
                .eq(PortfolioItem::getFundCode, fundCode));
    }

    /**
     * 组装完整 PortfolioVO
     */
    PortfolioVO toVO(Portfolio portfolio) {
        PortfolioVO vo = new PortfolioVO();
        vo.setId(portfolio.getId());
        vo.setPortfolioName(portfolio.getPortfolioName());
        vo.setDescription(portfolio.getDescription());
        vo.setRiskLevel(portfolio.getRiskLevel());
        vo.setTotalAmount(portfolio.getTotalAmount());
        vo.setStatus(portfolio.getStatus());
        vo.setCreateTime(portfolio.getCreateTime());
        vo.setUpdateTime(portfolio.getUpdateTime());

        // 查询组合内基金明细
        List<PortfolioItem> items = portfolioItemMapper.selectList(
                new LambdaQueryWrapper<PortfolioItem>()
                        .eq(PortfolioItem::getPortfolioId, portfolio.getId()));

        vo.setFundCount(items.size());
        vo.setFunds(buildFundItems(items));
        return vo;
    }

    /**
     * 构建组合内基金列表（带最新净值和盈亏）
     */
    private List<PortfolioFundItemVO> buildFundItems(List<PortfolioItem> items) {
        if (items.isEmpty()) return List.of();

        // 批量查询基金信息
        List<String> fundCodes = items.stream().map(PortfolioItem::getFundCode).toList();
        Map<String, Fund> fundMap = fundService.list(
                new LambdaQueryWrapper<Fund>().in(Fund::getFundCode, fundCodes))
                .stream().collect(Collectors.toMap(Fund::getFundCode, f -> f));

        // 批量查询最新净值
        Map<String, FundNav> navMap = new java.util.HashMap<>();
        for (String code : fundCodes) {
            FundNav nav = fundNavService.getLatestNav(code);
            if (nav != null) navMap.put(code, nav);
        }

        List<PortfolioFundItemVO> result = new ArrayList<>();
        for (PortfolioItem item : items) {
            PortfolioFundItemVO vo = new PortfolioFundItemVO();
            vo.setFundCode(item.getFundCode());
            vo.setInvestAmount(item.getInvestAmount());
            vo.setInvestShares(item.getInvestShares());
            vo.setTargetRatio(item.getTargetRatio());
            vo.setBuyPrice(item.getBuyPrice());
            vo.setBuyDate(item.getBuyDate());

            Fund fund = fundMap.get(item.getFundCode());
            if (fund != null) {
                vo.setFundName(fund.getFundName());
                vo.setFundType(fund.getFundType());
                vo.setRiskLevel(fund.getRiskLevel());
            }

            FundNav nav = navMap.get(item.getFundCode());
            if (nav != null) {
                vo.setLatestNav(nav.getUnitNav());
                // 计算盈亏
                if (item.getBuyPrice() != null && item.getInvestShares() != null
                        && item.getBuyPrice().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal currentValue = nav.getUnitNav().multiply(item.getInvestShares());
                    BigDecimal costValue = item.getBuyPrice().multiply(item.getInvestShares());
                    vo.setProfit(currentValue.subtract(costValue).setScale(2, RoundingMode.HALF_UP));
                    vo.setReturnRate(currentValue.subtract(costValue)
                            .divide(costValue, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100")));
                }
            }

            result.add(vo);
        }
        return result;
    }
}
