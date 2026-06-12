import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getPortfolioList,
  createPortfolio,
  updatePortfolio,
  deletePortfolio,
  getPortfolioDetail,
  addFundToPortfolio,
  removeFundFromPortfolio,
  getPortfolioAnalysis,
} from '@/api/portfolio'

export const usePortfolioStore = defineStore('portfolio', () => {
  const portfolios = ref([])
  const currentPortfolio = ref(null)
  const analysisData = ref(null)
  const loading = ref(false)

  // 获取组合列表
  async function fetchPortfolios() {
    loading.value = true
    try {
      const res = await getPortfolioList()
      portfolios.value = res.data || []
    } catch {
      portfolios.value = []
    } finally {
      loading.value = false
    }
  }

  // 创建组合
  async function addPortfolio(data) {
    const res = await createPortfolio(data)
    portfolios.value.unshift(res.data)
    return res.data
  }

  // 更新组合
  async function editPortfolio(data) {
    const res = await updatePortfolio(data)
    const idx = portfolios.value.findIndex((p) => p.id === data.id)
    if (idx >= 0) portfolios.value[idx] = res.data
    return res.data
  }

  // 删除组合
  async function removePortfolio(portfolioId) {
    await deletePortfolio(portfolioId)
    portfolios.value = portfolios.value.filter((p) => p.id !== portfolioId)
  }

  // 获取组合详情
  async function fetchPortfolioDetail(portfolioId) {
    loading.value = true
    try {
      const res = await getPortfolioDetail(portfolioId)
      currentPortfolio.value = res.data
    } finally {
      loading.value = false
    }
  }

  // 向组合添加基金
  async function addFund(portfolioId, data) {
    await addFundToPortfolio(portfolioId, data)
    await fetchPortfolioDetail(portfolioId)
  }

  // 从组合移除基金
  async function removeFund(portfolioId, fundCode) {
    await removeFundFromPortfolio(portfolioId, fundCode)
    await fetchPortfolioDetail(portfolioId)
  }

  // 获取组合分析数据
  async function fetchAnalysis(portfolioId) {
    loading.value = true
    try {
      const res = await getPortfolioAnalysis(portfolioId)
      analysisData.value = res.data
    } finally {
      loading.value = false
    }
  }

  return {
    portfolios,
    currentPortfolio,
    analysisData,
    loading,
    fetchPortfolios,
    addPortfolio,
    editPortfolio,
    removePortfolio,
    fetchPortfolioDetail,
    addFund,
    removeFund,
    fetchAnalysis,
  }
})
