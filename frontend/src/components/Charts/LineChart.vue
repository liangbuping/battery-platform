<template>
  <div ref="chartRef" class="line-chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface SeriesData {
  name: string
  data: number[]
  color?: string
}

const props = withDefaults(defineProps<{
  xAxis: string[]
  series: SeriesData[]
  title?: string
  showLegend?: boolean
  areaStyle?: boolean
}>(), {
  title: '',
  showLegend: true,
  areaStyle: true
})

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const defaultColors = ['#00d4ff', '#2ed573', '#ffa502', '#ff4757', '#8899aa']

const darkColors = {
  text: '#8899aa',
  line: 'rgba(255,255,255,0.08)',
  lineLight: 'rgba(255,255,255,0.05)'
}

const initChart = () => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chart) return

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      backgroundColor: '#1b2838',
      borderColor: 'rgba(255,255,255,0.08)',
      textStyle: { color: '#e0e0e0', fontSize: 12 }
    },
    legend: props.showLegend ? {
      data: props.series.map(s => s.name),
      bottom: 0,
      textStyle: { color: darkColors.text, fontSize: 12 },
      itemWidth: 12,
      itemHeight: 12,
      itemGap: 20
    } : undefined,
    grid: {
      left: '3%',
      right: '4%',
      bottom: props.showLegend ? '15%' : '3%',
      top: props.title ? '15%' : '8%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.xAxis,
      axisLine: { lineStyle: { color: darkColors.line } },
      axisLabel: { color: darkColors.text, fontSize: 12 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: darkColors.lineLight } },
      axisLabel: { color: darkColors.text, fontSize: 12 }
    },
    series: props.series.map((s, index) => {
      const color = s.color || defaultColors[index % defaultColors.length]
      return {
        name: s.name,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: s.data,
        itemStyle: { color },
        lineStyle: { width: 2 },
        areaStyle: props.areaStyle ? {
          opacity: 0.15,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color },
            { offset: 1, color: 'rgba(0,0,0,0)' }
          ])
        } : undefined
      }
    })
  }

  chart.setOption(option, true)
}

const handleResize = () => {
  chart?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})

watch(() => [props.xAxis, props.series], updateChart, { deep: true })
</script>

<style scoped>
.line-chart {
  width: 100%;
  height: 100%;
  min-height: 250px;
}
</style>
