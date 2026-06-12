<template>
  <BaseChart :option="chartOption" :height="height" :loading="loading" />
</template>

<script setup>
import { computed } from 'vue'
import BaseChart from './BaseChart.vue'
import { getIndustryPieOption } from '@/utils/chart'

/**
 * 行业分布饼图
 *
 * Props:
 *   distribution: Array<{
 *     industry: string,    // 行业名称
 *     ratio: number,       // 占比(%)
 *     fundCount?: number,  // 基金数量
 *   }>
 *   title?: string
 *   height?: number
 *   loading?: boolean
 *
 * 后端字段映射:
 *   IndustryDistributionVO.industry  → industry
 *   IndustryDistributionVO.ratio     → ratio
 *   IndustryDistributionVO.fundCount → fundCount
 */
const props = defineProps({
  distribution: { type: Array, default: () => [] },
  title: { type: String, default: '行业分布' },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartOption = computed(() => getIndustryPieOption(props.distribution, props.title))
</script>
