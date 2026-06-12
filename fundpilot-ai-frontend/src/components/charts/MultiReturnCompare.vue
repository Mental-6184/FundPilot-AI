<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getMultiReturnCompareOption } from '@/utils/chart'

/**
 * 多基金收益率对比图
 *
 * Props:
 *   funds: Array<{
 *     fundCode: string,
 *     return1w?: number,
 *     return1m?: number,
 *     return3m?: number,
 *     return6m?: number,
 *     return1y?: number,
 *   }>
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   FundPerformanceVO.fundCode  → fundCode
 *   FundPerformanceVO.return1w  → return1w
 *   FundPerformanceVO.return1m  → return1m
 *   FundPerformanceVO.return3m  → return3m
 *   FundPerformanceVO.return6m  → return6m
 *   FundPerformanceVO.return1y  → return1y
 */
const props = defineProps({
  funds: { type: Array, default: () => [] },
  title: { type: String, default: '收益率对比' },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getMultiReturnCompareOption(props.funds, props.title))
</script>
