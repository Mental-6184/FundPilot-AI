<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getRiskRadarOption } from '@/utils/chart'

/**
 * 风险雷达图
 *
 * Props:
 *   riskData: {
 *     volatilityScore?: number,    // 波动率得分 (0-100)
 *     drawdownScore?: number,      // 回撤得分 (0-100)
 *     concentrationScore?: number, // 集中度得分 (0-100)
 *     fundRiskScore?: number,      // 基金风险得分 (0-100)
 *     sharpeScore?: number,        // 夏普比率得分 (0-100)
 *     riskScore?: number,          // 综合评分
 *   }
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   RiskScoreDetailVO.volatilityScore     → volatilityScore
 *   RiskScoreDetailVO.drawdownScore       → drawdownScore
 *   RiskScoreDetailVO.concentrationScore  → concentrationScore
 *   RiskScoreDetailVO.fundRiskScore       → fundRiskScore
 *   PortfolioAnalysisVO.riskScore         → riskScore
 */
const props = defineProps({
  riskData: { type: Object, default: null },
  title: { type: String, default: '风险评估' },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getRiskRadarOption(props.riskData, props.title))
</script>
