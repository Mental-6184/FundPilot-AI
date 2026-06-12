package com.fundpilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fundpilot.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 对话历史 Mapper
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
}
