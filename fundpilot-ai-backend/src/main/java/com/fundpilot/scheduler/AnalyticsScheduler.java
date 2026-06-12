package com.fundpilot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 分析计算定时任务
 * 负责定期预计算基金指标、缓存热数据
 */
@Slf4j
@Component
public class AnalyticsScheduler {

    /**
     * 每个交易日 19:00 预计算热门基金指标
     */
    @Scheduled(cron = "0 0 19 * * MON-FRI")
    public void preCalculateMetrics() {
        log.info("[定时任务] 开始预计算基金业绩指标...");
        // TODO:
        // 1. 查询 Top 100 热门基金
        // 2. 计算各周期收益率、最大回撤、夏普比率
        // 3. 结果写入 Redis 缓存
        log.info("[定时任务] 基金指标预计算完成");
    }

    /**
     * 每天 03:00 清理过期缓存
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanExpiredCache() {
        log.info("[定时任务] 开始清理过期缓存...");
        // TODO: 清理 Redis 中过期的分析报告缓存
        log.info("[定时任务] 缓存清理完成");
    }
}
