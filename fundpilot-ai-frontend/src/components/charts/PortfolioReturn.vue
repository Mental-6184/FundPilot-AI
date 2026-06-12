<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getPortfolioReturnOption } from '@/utils/chart'

/**
 * 组合收益曲线
 *
 * Props:
 *   navList: Array<{ value: number, date?: string }> | number[]
 *   benchmark?: { navList: number[], name?: string }
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   PortfolioAnalysisService.buildPortfolioNav() → navList
 *   FundNavVO.unitNav → benchmark.navList
 */
const props = defineProps({
  navList: { type: Array, default: () => [] },
  benchmark: { type: Object, default: null },
  title: { type: String, default: '组合收益曲线' },
  height: { type: Number, default: 400 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getPortfolioReturnOption(props.navList, props.benchmark, props.title))
</script>
