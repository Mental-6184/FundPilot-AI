import { defineStore } from 'pinia'
import { ref } from 'vue'
import { compareFunds, getCompareReport } from '@/api/analytics'

export const useCompareStore = defineStore('compare', () => {
  const compareResult = ref(null)
  const reportContent = ref('')
  const loading = ref(false)
  const reportLoading = ref(false)

  // 对比基金
  async function fetchCompare(fundCodes) {
    loading.value = true
    try {
      const res = await compareFunds(fundCodes)
      compareResult.value = res.data
    } finally {
      loading.value = false
    }
  }

  // 获取对比报告
  async function fetchReport(codes) {
    reportLoading.value = true
    try {
      const res = await getCompareReport(codes)
      reportContent.value = res.data
    } finally {
      reportLoading.value = false
    }
  }

  function clear() {
    compareResult.value = null
    reportContent.value = ''
  }

  return {
    compareResult,
    reportContent,
    loading,
    reportLoading,
    fetchCompare,
    fetchReport,
    clear,
  }
})
