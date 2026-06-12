<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getReturnChartOption } from '@/utils/chart'

/**
 * 收益率走势图（多周期柱状图）
 *
 * Props:
 *   performance: {
 *     return1w?: number, return1m?: number, return3m?: number,
 *     return6m?: number, return1y?: number, return3y?: number,
 *     returnSinceEstablish?: number
 *   }
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   FundPerformanceVO.return1w            → return1w
 *   FundPerformanceVO.return1m            → return1m
 *   FundPerformanceVO.return3m            → return3m
 *   FundPerformanceVO.return6m            → return6m
 *   FundPerformanceVO.return1y            → return1y
 *   FundPerformanceVO.return3y            → return3y
 *   FundPerformanceVO.returnSinceEstablish → returnSinceEstablish
 */
const props = defineProps({
  performance: { type: Object, default: null },
  title: { type: String, default: '收益率走势' },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getReturnChartOption(props.performance, props.title))
</script>
