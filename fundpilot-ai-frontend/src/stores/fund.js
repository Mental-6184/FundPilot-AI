import { defineStore } from 'pinia'
import { ref } from 'vue'
import { searchFunds, getFundDetail, getFundNav, getFundHolding, getFundManager } from '@/api/fund'

export const useFundStore = defineStore('fund', () => {
  const fundList = ref([])
  const fundDetail = ref(null)
  const navData = ref(null)
  const holdingData = ref(null)
  const managerData = ref(null)
  const loading = ref(false)
  const total = ref(0)

  // 搜索基金列表
  async function fetchFundList(params = {}) {
    loading.value = true
    try {
      const res = await searchFunds(params)
      fundList.value = res.data.records
      total.value = res.data.total
    } finally {
      loading.value = false
    }
  }

  // 获取基金详情
  async function fetchFundDetail(fundCode) {
    loading.value = true
    try {
      const res = await getFundDetail(fundCode)
      fundDetail.value = res.data
    } finally {
      loading.value = false
    }
  }

  // 获取基金净值数据
  async function fetchFundNav(fundCode, params) {
    const res = await getFundNav(fundCode, params)
    navData.value = res.data
    return res.data
  }

  // 获取基金持仓数据
  async function fetchFundHolding(fundCode) {
    const res = await getFundHolding(fundCode)
    holdingData.value = res.data
    return res.data
  }

  // 获取基金经理信息
  async function fetchFundManager(fundCode) {
    const res = await getFundManager(fundCode)
    managerData.value = res.data
    return res.data
  }

  // 清空详情数据
  function clearDetail() {
    fundDetail.value = null
    navData.value = null
    holdingData.value = null
    managerData.value = null
  }

  return {
    fundList,
    fundDetail,
    navData,
    holdingData,
    managerData,
    loading,
    total,
    fetchFundList,
    fetchFundDetail,
    fetchFundNav,
    fetchFundHolding,
    fetchFundManager,
    clearDetail,
  }
})
