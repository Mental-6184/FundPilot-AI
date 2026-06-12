import request from './request'

/**
 * 组合 API
 */

// 获取用户的所有组合
export function getPortfolioList() {
  return request.get('/portfolio')
}

// 创建组合
export function createPortfolio(data) {
  return request.post('/portfolio', data)
}

// 更新组合
export function updatePortfolio(data) {
  return request.put('/portfolio', data)
}

// 删除组合
export function deletePortfolio(portfolioId) {
  return request.delete(`/portfolio/${portfolioId}`)
}

// 获取组合详情
export function getPortfolioDetail(portfolioId) {
  return request.get(`/portfolio/${portfolioId}`)
}

// 向组合添加基金
export function addFundToPortfolio(portfolioId, data) {
  return request.post(`/portfolio/${portfolioId}/fund`, data)
}

// 从组合移除基金
export function removeFundFromPortfolio(portfolioId, fundCode) {
  return request.delete(`/portfolio/${portfolioId}/fund/${fundCode}`)
}

// 获取组合分析数据
export function getPortfolioAnalysis(portfolioId) {
  return request.get(`/portfolio/${portfolioId}/analysis`)
}
