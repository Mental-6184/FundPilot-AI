-- ============================================================
-- FundPilot AI — 数据库建表脚本（幂等）
-- 数据库: fundpilot    引擎: InnoDB    字符集: utf8mb4
-- MySQL 8.0+
-- 首次部署手动执行，后续启动不再自动执行（spring.sql.init.mode=never）
-- ============================================================

CREATE DATABASE IF NOT EXISTS fundpilot
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE fundpilot;

-- 1. user
CREATE TABLE IF NOT EXISTS `user` (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    username        VARCHAR(50)     NOT NULL                               COMMENT '用户名（登录用）',
    password        VARCHAR(255)    NOT NULL                               COMMENT '密码（BCrypt 加密）',
    nickname        VARCHAR(50)     DEFAULT NULL                           COMMENT '昵称（展示用）',
    email           VARCHAR(100)    DEFAULT NULL                           COMMENT '邮箱',
    phone           VARCHAR(20)     DEFAULT NULL                           COMMENT '手机号',
    avatar          VARCHAR(500)    DEFAULT NULL                           COMMENT '头像URL',
    role            VARCHAR(20)     NOT NULL DEFAULT 'USER'                COMMENT '角色: USER / ADMIN / VIP',
    status          TINYINT         NOT NULL DEFAULT 1                     COMMENT '状态: 0-禁用 1-正常',
    last_login_time DATETIME        DEFAULT NULL                           COMMENT '最后登录时间',
    last_login_ip   VARCHAR(50)     DEFAULT NULL                           COMMENT '最后登录IP',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username       (username),
    UNIQUE KEY uk_email          (email),
    UNIQUE KEY uk_phone          (phone),
    KEY        idx_status        (status),
    KEY        idx_role          (role),
    KEY        idx_create_time   (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='用户表';

-- 2. fund_manager
CREATE TABLE IF NOT EXISTS fund_manager (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL                               COMMENT '姓名',
    gender          TINYINT         DEFAULT NULL                           COMMENT '性别: 0-女 1-男',
    company         VARCHAR(100)    DEFAULT NULL                           COMMENT '所属基金公司',
    tenure_years    DECIMAL(4,1)    DEFAULT NULL                           COMMENT '从业年限（年）',
    education       VARCHAR(100)    DEFAULT NULL                           COMMENT '学历 / 毕业院校',
    biography       TEXT            DEFAULT NULL                           COMMENT '个人简介',
    invest_style    VARCHAR(50)     DEFAULT NULL                           COMMENT '投资风格: 价值/成长/均衡/量化',
    best_return     DECIMAL(8,2)    DEFAULT NULL                           COMMENT '最佳任期回报(%)',
    manage_count    INT             NOT NULL DEFAULT 0                     COMMENT '当前在管基金数',
    total_manage    INT             NOT NULL DEFAULT 0                     COMMENT '累计管理基金数',
    status          TINYINT         NOT NULL DEFAULT 1                     COMMENT '状态: 0-离任 1-在职',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name_company    (name, company),
    KEY        idx_company        (company),
    KEY        idx_tenure_years   (tenure_years),
    KEY        idx_status         (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='基金经理表';

-- 3. fund
CREATE TABLE IF NOT EXISTS fund (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    fund_code       VARCHAR(20)     NOT NULL                               COMMENT '基金代码（如 110011）',
    fund_name       VARCHAR(100)    NOT NULL                               COMMENT '基金名称',
    fund_type       VARCHAR(20)     NOT NULL                               COMMENT '基金类型: EQUITY-股票型 / BOND-债券型 / HYBRID-混合型 / MONEY-货币型 / INDEX-指数型 / QDII / FOF / ETF',
    risk_level      VARCHAR(15)     NOT NULL                               COMMENT '风险等级: LOW / MEDIUM_LOW / MEDIUM / MEDIUM_HIGH / HIGH',
    establish_date  DATE            DEFAULT NULL                           COMMENT '成立日期',
    manager_id      BIGINT          DEFAULT NULL                           COMMENT '基金经理ID（关联 fund_manager.id）',
    custodian       VARCHAR(100)    DEFAULT NULL                           COMMENT '托管银行',
    benchmark       VARCHAR(200)    DEFAULT NULL                           COMMENT '业绩比较基准',
    invest_strategy TEXT            DEFAULT NULL                           COMMENT '投资策略说明',
    scale           DECIMAL(18,2)   DEFAULT NULL                           COMMENT '最新规模（亿元）',
    share           DECIMAL(18,2)   DEFAULT NULL                           COMMENT '最新份额（亿份）',
    fee_rate        DECIMAL(6,4)    DEFAULT NULL                           COMMENT '管理费率(%)',
    buy_status      TINYINT         NOT NULL DEFAULT 1                     COMMENT '申购状态: 0-暂停 1-开放',
    redeem_status   TINYINT         NOT NULL DEFAULT 1                     COMMENT '赎回状态: 0-暂停 1-开放',
    status          TINYINT         NOT NULL DEFAULT 1                     COMMENT '状态: 0-下架 1-正常 2-募集期',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_fund_code          (fund_code),
    KEY        idx_fund_type          (fund_type),
    KEY        idx_risk_level         (risk_level),
    KEY        idx_manager_id         (manager_id),
    KEY        idx_status             (status),
    KEY        idx_establish_date     (establish_date),
    KEY        idx_fund_name          (fund_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='基金基本信息表';

-- 4. fund_nav
CREATE TABLE IF NOT EXISTS fund_nav (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    fund_code       VARCHAR(20)     NOT NULL                               COMMENT '基金代码',
    nav_date        DATE            NOT NULL                               COMMENT '净值日期',
    unit_nav        DECIMAL(12,6)   NOT NULL                               COMMENT '单位净值',
    acc_nav         DECIMAL(12,6)   DEFAULT NULL                           COMMENT '累计净值',
    total_nav       DECIMAL(12,6)   DEFAULT NULL                           COMMENT '复权净值（用于精确收益率计算）',
    daily_return    DECIMAL(10,4)   DEFAULT NULL                           COMMENT '日收益率(%)',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_fund_date           (fund_code, nav_date),
    KEY        idx_nav_date            (nav_date),
    KEY        idx_fund_code           (fund_code),
    KEY        idx_fund_date_return    (fund_code, nav_date, daily_return)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='基金净值表（日频）';

-- 5. fund_holding
CREATE TABLE IF NOT EXISTS fund_holding (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    fund_code       VARCHAR(20)     NOT NULL                               COMMENT '基金代码',
    report_date     DATE            NOT NULL                               COMMENT '报告日期（季报截止日）',
    stock_code      VARCHAR(20)     NOT NULL                               COMMENT '持仓股票代码',
    stock_name      VARCHAR(50)     DEFAULT NULL                           COMMENT '持仓股票名称',
    hold_ratio      DECIMAL(8,4)    DEFAULT NULL                           COMMENT '持仓占净值比(%)',
    hold_amount     DECIMAL(18,2)   DEFAULT NULL                           COMMENT '持仓市值（万元）',
    hold_shares     DECIMAL(18,2)   DEFAULT NULL                           COMMENT '持股数量（万股）',
    `rank`            INT             DEFAULT NULL                           COMMENT '持仓排名（1=第一大重仓）',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_fund_report_stock  (fund_code, report_date, stock_code),
    KEY        idx_fund_report        (fund_code, report_date),
    KEY        idx_stock_code         (stock_code),
    KEY        idx_report_date        (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='基金持仓表（季报前十大）';

-- 6. portfolio
CREATE TABLE IF NOT EXISTS portfolio (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                               COMMENT '所属用户ID（关联 user.id）',
    portfolio_name  VARCHAR(50)     NOT NULL                               COMMENT '组合名称',
    description     VARCHAR(500)    DEFAULT NULL                           COMMENT '组合描述 / 投资目标',
    risk_level      VARCHAR(15)     DEFAULT NULL                           COMMENT '组合整体风险等级: LOW / MEDIUM_LOW / MEDIUM / MEDIUM_HIGH / HIGH',
    total_amount    DECIMAL(18,2)   DEFAULT NULL                           COMMENT '组合总投入金额（元）',
    status          TINYINT         NOT NULL DEFAULT 1                     COMMENT '状态: 0-归档 1-正常',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_portfolio_name (user_id, portfolio_name),
    KEY        idx_user_id             (user_id),
    KEY        idx_status              (status),
    KEY        idx_create_time         (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='用户投资组合表';

-- 7. portfolio_item
CREATE TABLE IF NOT EXISTS portfolio_item (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    portfolio_id    BIGINT          NOT NULL                               COMMENT '所属组合ID（关联 portfolio.id）',
    fund_code       VARCHAR(20)     NOT NULL                               COMMENT '基金代码（关联 fund.fund_code）',
    invest_amount   DECIMAL(18,2)   DEFAULT NULL                           COMMENT '投入金额（元）',
    invest_shares   DECIMAL(18,4)   DEFAULT NULL                           COMMENT '持有份额',
    target_ratio    DECIMAL(6,2)    DEFAULT NULL                           COMMENT '目标配置比例(%)',
    actual_ratio    DECIMAL(6,2)    DEFAULT NULL                           COMMENT '实际持仓比例(%)（定期计算更新）',
    buy_price       DECIMAL(12,6)   DEFAULT NULL                           COMMENT '买入均价（单位净值）',
    buy_date        DATE            DEFAULT NULL                           COMMENT '首次买入日期',
    remark          VARCHAR(200)    DEFAULT NULL                           COMMENT '备注',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_portfolio_fund     (portfolio_id, fund_code),
    KEY        idx_portfolio_id      (portfolio_id),
    KEY        idx_fund_code         (fund_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='组合明细表（组合内基金）';

-- 8. chat_history
CREATE TABLE IF NOT EXISTS chat_history (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                               COMMENT '用户ID（关联 user.id）',
    session_id      VARCHAR(64)     NOT NULL                               COMMENT '会话ID（同一次对话共享）',
    role            VARCHAR(20)     NOT NULL                               COMMENT '消息角色: USER-用户 / ASSISTANT-AI / SYSTEM-系统',
    content         TEXT            NOT NULL                               COMMENT '消息正文',
    token_count     INT             DEFAULT NULL                           COMMENT '本条消息消耗的 Token 数',
    model_name      VARCHAR(50)     DEFAULT NULL                           COMMENT '使用的模型名称（如 qwen-max）',
    tool_calls      JSON            DEFAULT NULL                           COMMENT 'AI 调用的工具列表（JSON 数组）',
    cost_ms         INT             DEFAULT NULL                           COMMENT 'AI 响应耗时（毫秒）',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_role_time  (session_id, role, create_time),
    KEY        idx_user_id           (user_id),
    KEY        idx_session_id        (session_id),
    KEY        idx_user_session      (user_id, session_id),
    KEY        idx_create_time       (create_time),
    KEY        idx_role              (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='AI 对话历史表';

-- 9. analysis_report
CREATE TABLE IF NOT EXISTS analysis_report (
    id              BIGINT          NOT NULL AUTO_INCREMENT                COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                               COMMENT '发起用户ID（关联 user.id）',
    report_type     VARCHAR(30)     NOT NULL                               COMMENT '报告类型: FUND_ANALYSIS-单基分析 / COMPARISON-基金对比 / PORTFOLIO-组合分析 / MARKET-市场概览',
    target_code     VARCHAR(100)    DEFAULT NULL                           COMMENT '分析目标代码（基金代码或组合ID，多个用逗号分隔）',
    title           VARCHAR(200)    DEFAULT NULL                           COMMENT '报告标题',
    summary         TEXT            DEFAULT NULL                           COMMENT 'AI 生成的分析摘要',
    metrics_json    JSON            DEFAULT NULL                           COMMENT 'Java 计算的核心指标快照（JSON）',
    full_report     MEDIUMTEXT      DEFAULT NULL                           COMMENT 'AI 生成的完整分析报告（Markdown）',
    model_name      VARCHAR(50)     DEFAULT NULL                           COMMENT '使用的模型名称',
    token_count     INT             DEFAULT NULL                           COMMENT '消耗的 Token 总数',
    cost_ms         INT             DEFAULT NULL                           COMMENT '生成耗时（毫秒）',
    status          TINYINT         NOT NULL DEFAULT 1                     COMMENT '状态: 0-已失效 1-有效',
    deleted         TINYINT         NOT NULL DEFAULT 0                     COMMENT '逻辑删除: 0-正常 1-已删除',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP            COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_type_time     (user_id, report_type, create_time),
    KEY        idx_user_id           (user_id),
    KEY        idx_report_type       (report_type),
    KEY        idx_target_code       (target_code),
    KEY        idx_status            (status),
    KEY        idx_create_time       (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='分析报告表（Java计算+LLM生成）';
