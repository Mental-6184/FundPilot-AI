import dayjs from 'dayjs'

/**
 * 格式化数字 (千分位)
 */
export function formatNumber(num, decimals = 2) {
  if (num == null) return '--'
  return Number(num).toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  })
}

/**
 * 格式化百分比
 */
export function formatPercent(val, decimals = 2) {
  if (val == null) return '--'
  const prefix = val >= 0 ? '+' : ''
  return prefix + Number(val).toFixed(decimals) + '%'
}

/**
 * 格式化日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '--'
  return dayjs(date).format(format)
}

/**
 * 格式化净值
 */
export function formatNav(nav) {
  if (nav == null) return '--'
  return Number(nav).toFixed(4)
}

/**
 * 获取涨跌颜色 class
 */
export function getReturnClass(val) {
  if (val == null) return ''
  return val >= 0 ? 'text-red' : 'text-green'
}
