import request from './request'

/**
 * 基金 API
 */

// 搜索基金列表（分页）
export function searchFunds(params) {
  return request.get('/fund/search', { params })
}

// 获取基金详情
export function getFundDetail(fundCode) {
  return request.get(`/fund/${fundCode}`)
}

// 获取基金净值数据
export function getFundNav(fundCode, params) {
  return request.get(`/fund/${fundCode}/nav`, { params })
}

// 获取基金持仓数据
export function getFundHolding(fundCode) {
  return request.get(`/fund/${fundCode}/holding`)
}

// 获取基金经理信息
export function getFundManager(fundCode) {
  return request.get(`/fund/${fundCode}/manager`)
}
