<template>
  <div ref="chartRef" class="bar-chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface ChartData {
  name: string
  value: number
}

interface StackData {
  name: string
  xAxis: string[]
  data: number[]
}

const props = withDefaults(defineProps<{
  data?: ChartData[]
  stackData?: StackData[]
  title?: string
  color?: string
  showLegend?: boolean
  stacked?: boolean
}>(), {
  data: () => [],
  stackData: () => [],
  title: '',
  color: '#00d4ff',
  showLegend: false,
  stacked: false
})

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const darkColors = {
  text: '#8899aa',
  textLight: '#e0e0e0',
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

  if (props.stacked && props.stackData && props.stackData.length > 0) {
    // Stacked bar chart (for alert trend)
    const categories = props.stackData[0]?.xAxis || []
    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: '#1b2838',
        borderColor: 'rgba(255,255,255,0.08)',
        textStyle: { color: '#e0e0e0', fontSize: 12 }
      },
      legend: props.showLegend ? {
        data: props.stackData.map(s => s.name),
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
        top: '8%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: categories,
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
      series: props.stackData.map((s, i) => ({
        name: s.name,
        type: 'bar',
        stack: 'total',
        data: s.data,
        barWidth: '40%',
        itemStyle: {
          color: s.name === '一级预警' ? '#ff4757' : s.name === '二级预警' ? '#ffa502' : '#00d4ff',
          borderRadius: i === props.stackData.length - 1 ? [4, 4, 0, 0] : [0, 0, 0, 0]
        }
      }))
    }
    chart.setOption(option, true)
  } else {
    // Simple bar chart
    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        backgroundColor: '#1b2838',
        borderColor: 'rgba(255,255,255,0.08)',
        textStyle: { color: '#e0e0e0', fontSize: 12 }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: props.title ? '15%' : '8%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: props.data.map(item => item.name),
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
      series: [{
        type: 'bar',
        data: props.data.map(item => item.value),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: props.color },
            { offset: 1, color: props.color + '66' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        barWidth: '60%'
      }]
    }
    chart.setOption(option, true)
  }
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

watch(() => [props.data, props.stackData], updateChart, { deep: true })
</script>

<style scoped>
.bar-chart {
  width: 100%;
  height: 100%;
  min-height: 250px;
}
</style>
