<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getHoldingPieOption } from '@/utils/chart'

/**
 * 基金持仓饼图
 *
 * Props:
 *   holdings: Array<{
 *     stockName: string,   // 股票名称
 *     holdRatio: number,   // 持仓占比(%)
 *   }>
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   HoldingVO.stockName → stockName
 *   HoldingVO.holdRatio → holdRatio
 */
const props = defineProps({
  holdings: { type: Array, default: () => [] },
  title: { type: String, default: '前十大持仓' },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getHoldingPieOption(props.holdings, props.title))
</script>
