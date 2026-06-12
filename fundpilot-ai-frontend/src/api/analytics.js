import request from './request'

/**
 * 基金分析 API
 */

// 计算基金业绩指标
export function getPerformance(fundCode, params) {
  return request.get(`/fund/${fundCode}/performance`, { params })
}

// 对比多只基金
export function compareFunds(fundCodes) {
  return request.post('/analytics/compare', { fundCodes })
}

// 获取基金分析报告
export function getFundReport(fundCode) {
  return request.get(`/report/fund/${fundCode}`)
}

// 获取基金对比报告
export function getCompareReport(codes) {
  return request.get('/report/compare', { params: { codes } })
}

// 获取组合诊断报告
export function getPortfolioReport(portfolioId) {
  return request.get(`/report/portfolio/${portfolioId}`)
}

// 获取基金推荐报告
export function getRecommendReport(params) {
  return request.get('/report/recommend', { params })
}
