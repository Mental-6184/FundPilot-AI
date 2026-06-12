package com.fundpilot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 基金数据定时任务
 * 负责定期同步基金净值、持仓等数据
 */
@Slf4j
@Component
public class FundDataScheduler {

    /**
     * 每个交易日 18:00 同步基金净值
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 18 * * MON-FRI")
    public void syncFundNav() {
        log.info("[定时任务] 开始同步基金净值数据...");
        // TODO: 调用外部数据源同步净值
        // 1. 查询所有正常状态的基金
        // 2. 逐个拉取最新净值
        // 3. 入库 fund_nav 表
        // 4. 更新 Redis 缓存
        log.info("[定时任务] 基金净值同步完成");
    }

    /**
     * 每季度初同步基金持仓（季报发布后）
     * 每月1号 02:00 检查
     */
    @Scheduled(cron = "0 0 2 1 1,4,7,10 *")
    public void syncFundHolding() {
        log.info("[定时任务] 开始同步基金持仓数据...");
        // TODO: 调用外部数据源同步季报持仓
        log.info("[定时任务] 基金持仓同步完成");
    }

    /**
     * 每天 08:00 更新基金基本信息
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void syncFundInfo() {
        log.info("[定时任务] 开始更新基金基本信息...");
        // TODO: 同步基金规模、份额、费率等信息
        log.info("[定时任务] 基金信息更新完成");
    }
}
