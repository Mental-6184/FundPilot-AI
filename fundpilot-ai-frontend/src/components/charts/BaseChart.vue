<template>
  <div ref="chartRef" :style="{ width: '100%', height: height + 'px' }"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: { type: Object, required: true },
  height: { type: Number, default: 350 },
  loading: { type: Boolean, default: false },
})

const chartRef = ref(null)
let chartInstance = null

// 初始化图表
function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  updateChart()
  showLoading()
}

// 更新图表
function updateChart() {
  if (!chartInstance || !props.option) return
  chartInstance.setOption(props.option, true)
}

// 显示/隐藏加载状态
function showLoading() {
  if (!chartInstance) return
  if (props.loading) {
    chartInstance.showLoading({ text: '加载中...', color: '#409eff' })
  } else {
    chartInstance.hideLoading()
  }
}

// 响应窗口大小
function handleResize() {
  chartInstance?.resize()
}

// 监听 option 变化
watch(() => props.option, () => {
  nextTick(updateChart)
}, { deep: true })

// 监听 loading 变化
watch(() => props.loading, showLoading)

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})

// 暴露方法给父组件
defineExpose({
  getInstance: () => chartInstance,
  resize: handleResize,
})
</script>
