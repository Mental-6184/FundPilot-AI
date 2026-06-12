/**
 * ECharts 图表配置中心 - Dark Theme
 *
 * 所有图表的 Option 配置集中管理
 * 字段映射来自后端 VO
 */

// ========================================================================
// 通用配置 (Dark Theme)
// ========================================================================

/** 通用颜色方案 */
export const COLORS = [
  '#3B82F6', '#8B5CF6', '#22C55E', '#F59E0B', '#EF4444',
  '#06B6D4', '#EC4899', '#14B8A6', '#F97316', '#6366F1',
]

/** 涨跌颜色 */
export const UP_COLOR = '#22C55E'
export const DOWN_COLOR = '#EF4444'

/** 通用 tooltip */
const commonTooltip = {
  trigger: 'axis',
  backgroundColor: 'rgba(17, 24, 39, 0.95)',
  borderColor: 'rgba(255, 255, 255, 0.06)',
  borderWidth: 1,
  textStyle: { color: '#F8FAFC', fontSize: 12 },
  axisPointer: { type: 'cross', crossStyle: { color: '#64748B' } },
}

/** 通用 grid */
const commonGrid = {
  left: 60,
  right: 30,
  top: 50,
  bottom: 60,
  containLabel: true,
}

/** 通用 axis 配置 */
const commonAxis = {
  axisLabel: { color: '#64748B', fontSize: 11 },
  axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
  splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.04)' } },
}

// ========================================================================
// 1. 基金净值走势图
// ========================================================================

export function getNavChartOption(trend, title = '净值走势') {
  if (!trend?.length) return {}

  const dates = trend.map((item) => item.date)
  const navValues = trend.map((item) => item.unitNav)
  const hasAccNav = trend.some((item) => item.accNav != null)

  const series = [
    {
      name: '单位净值',
      type: 'line',
      data: navValues,
      smooth: true,
      showSymbol: false,
      lineStyle: {
        width: 2,
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 1, y2: 0,
          colorStops: [
            { offset: 0, color: '#3B82F6' },
            { offset: 1, color: '#8B5CF6' },
          ],
        },
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(59, 130, 246, 0.2)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' },
          ],
        },
      },
    },
  ]

  if (hasAccNav) {
    series.push({
      name: '累计净值',
      type: 'line',
      data: trend.map((item) => item.accNav),
      smooth: true,
      showSymbol: false,
      lineStyle: { width: 2, color: '#8B5CF6', type: 'dashed' },
    })
  }

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      ...commonTooltip,
      formatter: (params) => {
        let html = `<strong>${params[0].axisValue}</strong><br/>`
        params.forEach((p) => {
          html += `${p.marker} ${p.seriesName}: ${(p.value || 0).toFixed(4)}<br/>`
        })
        return html
      },
    },
    legend: hasAccNav ? {
      bottom: 10,
      data: ['单位净值', '累计净值'],
      textStyle: { color: '#94A3B8', fontSize: 12 },
    } : undefined,
    xAxis: {
      type: 'category',
      data: dates,
      ...commonAxis,
      axisLabel: { ...commonAxis.axisLabel, rotate: 30 },
      boundaryGap: false,
    },
    yAxis: {
      type: 'value',
      scale: true,
      ...commonAxis,
      axisLabel: { ...commonAxis.axisLabel, formatter: (v) => v.toFixed(4) },
    },
    series,
    grid: commonGrid,
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      {
        type: 'slider',
        start: 0,
        end: 100,
        height: 20,
        bottom: 5,
        borderColor: 'rgba(255, 255, 255, 0.06)',
        backgroundColor: 'rgba(17, 24, 39, 0.8)',
        fillerColor: 'rgba(59, 130, 246, 0.15)',
        handleStyle: { color: '#3B82F6' },
        textStyle: { color: '#64748B' },
      },
    ],
  }
}

// ========================================================================
// 2. 收益率走势图
// ========================================================================

export function getReturnChartOption(performance, title = '收益率走势') {
  if (!performance) return {}

  const periods = []
  const values = []

  const addPeriod = (label, value) => {
    if (value != null) {
      periods.push(label)
      values.push(value)
    }
  }

  addPeriod('近1周', performance.return1w)
  addPeriod('近1月', performance.return1m)
  addPeriod('近3月', performance.return3m)
  addPeriod('近6月', performance.return6m)
  addPeriod('近1年', performance.return1y)
  addPeriod('近3年', performance.return3y)
  addPeriod('成立以来', performance.returnSinceEstablish)

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      ...commonTooltip,
      formatter: (params) => {
        const p = params[0]
        return `<strong>${p.name}</strong><br/>收益率: ${p.value >= 0 ? '+' : ''}${p.value.toFixed(2)}%`
      },
    },
    xAxis: {
      type: 'category',
      data: periods,
      ...commonAxis,
    },
    yAxis: {
      type: 'value',
      ...commonAxis,
      axisLabel: { ...commonAxis.axisLabel, formatter: '{value}%' },
    },
    series: [{
      type: 'bar',
      data: values.map((v) => ({
        value: v,
        itemStyle: {
          color: v >= 0 ? UP_COLOR : DOWN_COLOR,
          borderRadius: v >= 0 ? [4, 4, 0, 0] : [0, 0, 4, 4],
        },
      })),
      barWidth: '40%',
      label: {
        show: true,
        position: 'top',
        formatter: (p) => `${p.value >= 0 ? '+' : ''}${p.value.toFixed(2)}%`,
        fontSize: 11,
        color: '#94A3B8',
      },
    }],
    grid: { ...commonGrid, top: 50, bottom: 30 },
  }
}

// ========================================================================
// 3. 风险雷达图
// ========================================================================

export function getRiskRadarOption(riskData, title = '风险评估') {
  if (!riskData) return {}

  const indicators = [
    { name: '波动率', max: 100 },
    { name: '最大回撤', max: 100 },
    { name: '集中度', max: 100 },
    { name: '基金风险', max: 100 },
  ]

  const values = [
    riskData.volatilityScore || 0,
    riskData.drawdownScore || 0,
    riskData.concentrationScore || 0,
    riskData.fundRiskScore || 0,
  ]

  if (riskData.sharpeScore != null) {
    indicators.push({ name: '夏普比率', max: 100 })
    values.push(riskData.sharpeScore)
  }

  return {
    title: {
      text: title,
      subtext: riskData.riskScore != null ? `综合评分: ${riskData.riskScore}/100` : '',
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
      subtextStyle: { fontSize: 12, color: '#94A3B8' },
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
      formatter: (params) => {
        const dims = indicators.map((ind, i) => `${ind.name}: ${params.value[i]}`)
        return dims.join('<br/>')
      },
    },
    radar: {
      indicator: indicators,
      shape: 'polygon',
      splitNumber: 5,
      axisName: { color: '#94A3B8', fontSize: 12 },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
      splitArea: {
        areaStyle: {
          color: [
            'rgba(59, 130, 246, 0.02)',
            'rgba(59, 130, 246, 0.04)',
            'rgba(59, 130, 246, 0.02)',
            'rgba(59, 130, 246, 0.04)',
            'rgba(59, 130, 246, 0.02)',
          ],
        },
      },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.06)' } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: values,
        name: '风险评估',
        areaStyle: {
          color: {
            type: 'radial',
            x: 0.5, y: 0.5, r: 0.5,
            colorStops: [
              { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.05)' },
            ],
          },
        },
        lineStyle: { color: '#3B82F6', width: 2 },
        itemStyle: { color: '#3B82F6' },
      }],
    }],
  }
}

// ========================================================================
// 4. 行业分布图
// ========================================================================

export function getIndustryPieOption(distribution, title = '行业分布') {
  if (!distribution?.length) return {}

  const data = distribution.map((item, index) => ({
    name: item.industry,
    value: item.ratio,
    fundCount: item.fundCount,
    itemStyle: { color: COLORS[index % COLORS.length] },
  }))

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
      formatter: (params) => {
        let html = `<strong>${params.name}</strong><br/>`
        html += `占比: ${params.value.toFixed(2)}%<br/>`
        if (params.data.fundCount != null) {
          html += `基金数: ${params.data.fundCount}`
        }
        return html
      },
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { fontSize: 12, color: '#94A3B8' },
    },
    series: [{
      type: 'pie',
      radius: ['35%', '65%'],
      center: ['40%', '50%'],
      data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)',
        },
      },
      label: {
        show: true,
        formatter: '{b}: {d}%',
        fontSize: 11,
        color: '#94A3B8',
      },
      labelLine: { show: true, lineStyle: { color: 'rgba(255, 255, 255, 0.1)' } },
    }],
  }
}

// ========================================================================
// 5. 资产配置图
// ========================================================================

export function getAssetAllocationOption(fundItems, title = '资产配置') {
  if (!fundItems?.length) return {}

  const data = fundItems.map((item, index) => ({
    name: item.fundName || item.fundCode,
    value: item.actualRatio || item.investAmount || 0,
    investAmount: item.investAmount,
    fundType: item.fundType,
    itemStyle: { color: COLORS[index % COLORS.length] },
  }))

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
      formatter: (params) => {
        let html = `<strong>${params.name}</strong><br/>`
        html += `占比: ${params.value.toFixed(2)}%<br/>`
        if (params.data.investAmount != null) {
          html += `投入: ${params.data.investAmount.toLocaleString()} 元<br/>`
        }
        if (params.data.fundType) {
          html += `类型: ${params.data.fundType}`
        }
        return html
      },
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { fontSize: 11, color: '#94A3B8' },
      formatter: (name) => {
        const item = data.find((d) => d.name === name)
        return `${name} (${item?.value?.toFixed(1)}%)`
      },
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: true,
      data,
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#F8FAFC' },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)',
        },
      },
      label: { show: false },
      labelLine: { show: false },
    }],
  }
}

// ========================================================================
// 6. 组合收益曲线
// ========================================================================

export function getPortfolioReturnOption(navList, benchmark = null, title = '组合收益曲线') {
  if (!navList?.length) return {}

  const dates = navList.map((item, i) => item.date || `T${i}`)
  const values = navList.map((item) => item.value ?? item)

  const baseNav = values[0] || 1
  const returnValues = values.map((v) => ((v - baseNav) / baseNav * 100))

  const series = [{
    name: '组合收益',
    type: 'line',
    data: returnValues,
    smooth: true,
    showSymbol: false,
    lineStyle: {
      width: 2,
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 1, y2: 0,
        colorStops: [
          { offset: 0, color: '#3B82F6' },
          { offset: 1, color: '#8B5CF6' },
        ],
      },
    },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: returnValues[returnValues.length - 1] >= 0 ? 'rgba(34, 197, 94, 0.2)' : 'rgba(239, 68, 68, 0.2)' },
          { offset: 1, color: returnValues[returnValues.length - 1] >= 0 ? 'rgba(34, 197, 94, 0)' : 'rgba(239, 68, 68, 0)' },
        ],
      },
    },
  }]

  if (benchmark?.navList?.length) {
    const benchBase = benchmark.navList[0] || 1
    const benchReturns = benchmark.navList.map((v) => ((v - benchBase) / benchBase * 100))
    series.push({
      name: benchmark.name || '基准',
      type: 'line',
      data: benchReturns.slice(0, returnValues.length),
      smooth: true,
      showSymbol: false,
      lineStyle: { width: 2, color: '#8B5CF6', type: 'dashed' },
    })
  }

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      ...commonTooltip,
      formatter: (params) => {
        let html = `<strong>${params[0].axisValue}</strong><br/>`
        params.forEach((p) => {
          const sign = p.value >= 0 ? '+' : ''
          html += `${p.marker} ${p.seriesName}: ${sign}${p.value.toFixed(2)}%<br/>`
        })
        return html
      },
    },
    legend: series.length > 1 ? {
      bottom: 10,
      textStyle: { color: '#94A3B8' },
    } : undefined,
    xAxis: {
      type: 'category',
      data: dates,
      ...commonAxis,
      axisLabel: { ...commonAxis.axisLabel, rotate: 30 },
      boundaryGap: false,
    },
    yAxis: {
      type: 'value',
      ...commonAxis,
      axisLabel: { ...commonAxis.axisLabel, formatter: '{value}%' },
    },
    series,
    grid: commonGrid,
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      {
        type: 'slider',
        start: 0,
        end: 100,
        height: 20,
        bottom: 5,
        borderColor: 'rgba(255, 255, 255, 0.06)',
        backgroundColor: 'rgba(17, 24, 39, 0.8)',
        fillerColor: 'rgba(59, 130, 246, 0.15)',
        handleStyle: { color: '#3B82F6' },
        textStyle: { color: '#64748B' },
      },
    ],
  }
}

// ========================================================================
// 7. 多基金收益率对比图
// ========================================================================

export function getMultiReturnCompareOption(funds, title = '收益率对比') {
  if (!funds?.length) return {}

  const periods = ['近1周', '近1月', '近3月', '近6月', '近1年']
  const keys = ['return1w', 'return1m', 'return3m', 'return6m', 'return1y']

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      ...commonTooltip,
      formatter: (params) => {
        let html = `<strong>${params[0].name}</strong><br/>`
        params.forEach((p) => {
          const sign = p.value >= 0 ? '+' : ''
          html += `${p.marker} ${p.seriesName}: ${sign}${p.value.toFixed(2)}%<br/>`
        })
        return html
      },
    },
    legend: {
      bottom: 10,
      data: funds.map((f) => f.fundCode),
      textStyle: { color: '#94A3B8', fontSize: 12 },
    },
    xAxis: {
      type: 'category',
      data: periods,
      ...commonAxis,
    },
    yAxis: {
      type: 'value',
      ...commonAxis,
      axisLabel: { ...commonAxis.axisLabel, formatter: '{value}%' },
    },
    series: funds.map((fund, index) => ({
      name: fund.fundCode,
      type: 'bar',
      data: keys.map((k) => fund[k] ?? 0),
      itemStyle: {
        color: COLORS[index % COLORS.length],
        borderRadius: [4, 4, 0, 0],
      },
      barMaxWidth: 32,
      barGap: '10%',
    })),
    grid: { ...commonGrid, bottom: 50 },
  }
}

// ========================================================================
// 8. 持仓饼图
// ========================================================================

export function getHoldingPieOption(holdings, title = '前十大持仓') {
  if (!holdings?.length) return {}

  const data = holdings.map((item, index) => ({
    name: item.stockName,
    value: item.holdRatio,
    itemStyle: { color: COLORS[index % COLORS.length] },
  }))

  return {
    title: {
      text: title,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600, color: '#F8FAFC' },
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(17, 24, 39, 0.95)',
      borderColor: 'rgba(255, 255, 255, 0.06)',
      textStyle: { color: '#F8FAFC', fontSize: 12 },
      formatter: '{b}: {d}%',
    },
    series: [{
      type: 'pie',
      radius: ['30%', '60%'],
      data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)',
        },
      },
      label: {
        show: true,
        formatter: '{b}\n{d}%',
        fontSize: 11,
        color: '#94A3B8',
      },
      labelLine: { show: true, lineStyle: { color: 'rgba(255, 255, 255, 0.1)' } },
    }],
  }
}
