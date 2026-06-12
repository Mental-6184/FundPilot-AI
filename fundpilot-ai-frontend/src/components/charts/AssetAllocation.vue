<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getAssetAllocationOption } from '@/utils/chart'

/**
 * 资产配置图（环形图）
 *
 * Props:
 *   fundItems: Array<{
 *     fundName?: string,       // 基金名称
 *     fundCode?: string,       // 基金代码
 *     investAmount?: number,   // 投入金额
 *     actualRatio?: number,    // 实际占比(%)
 *     fundType?: string,       // 基金类型
 *   }>
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   PortfolioFundItemVO.fundName      → fundName
 *   PortfolioFundItemVO.fundCode      → fundCode
 *   PortfolioFundItemVO.investAmount  → investAmount
 *   PortfolioFundItemVO.actualRatio   → actualRatio
 *   PortfolioFundItemVO.fundType      → fundType
 */
const props = defineProps({
  fundItems: { type: Array, default: () => [] },
  title: { type: String, default: '资产配置' },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getAssetAllocationOption(props.fundItems, props.title))
</script>
