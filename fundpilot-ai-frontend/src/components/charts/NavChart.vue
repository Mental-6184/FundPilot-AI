<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getNavChartOption } from '@/utils/chart'

/**
 * 基金净值走势图
 *
 * Props:
 *   trend: Array<{ date: string, unitNav: number, accNav?: number }>
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   FundNavVO.navDate         → date
 *   FundNavVO.unitNav         → unitNav
 *   FundNavVO.accumulateNav   → accNav
 */
const props = defineProps({
  trend: { type: Array, default: () => [] },
  title: { type: String, default: '净值走势' },
  height: { type: Number, default: 400 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getNavChartOption(props.trend, props.title))
</script>
