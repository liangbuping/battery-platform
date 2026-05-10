<template>
  <div ref="chartRef" class="pie-chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface ChartData {
  name: string
  value: number
}

const props = withDefaults(defineProps<{
  data: ChartData[]
  title?: string
  radius?: string[]
  showLegend?: boolean
  centerText?: string
  centerSubText?: string
  colors?: string[]
}>(), {
  title: '',
  radius: () => ['40%', '70%'],
  showLegend: true,
  centerText: '',
  centerSubText: '',
  colors: () => ['#ff4757', '#ffa502', '#2ed573', '#00d4ff', '#8899aa']
})

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chart) return

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: '#1b2838',
      borderColor: 'rgba(255,255,255,0.08)',
      textStyle: { color: '#e0e0e0', fontSize: 12 }
    },
    legend: props.showLegend ? {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12,
        color: '#8899aa'
      },
      formatter: (name: string) => {
        const item = props.data.find(d => d.name === name)
        return item ? `${name}  ${item.value}%` : name
      }
    } : undefined,
    color: props.colors,
    graphic: props.centerText ? [{
      type: 'text',
      left: props.showLegend ? '30%' : 'center',
      top: '42%',
      style: {
        text: props.centerText,
        textAlign: 'center',
        fill: '#e0e0e0',
        fontSize: 24,
        fontWeight: 'bold'
      }
    }, {
      type: 'text',
      left: props.showLegend ? '30%' : 'center',
      top: '56%',
      style: {
        text: props.centerSubText,
        textAlign: 'center',
        fill: '#8899aa',
        fontSize: 12
      }
    }] : undefined,
    series: [{
      type: 'pie',
      radius: props.radius,
      center: props.showLegend ? ['35%', '50%'] : ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#1b2838',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold',
          color: '#e0e0e0'
        }
      },
      labelLine: { show: false },
      data: props.data
    }]
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

watch(() => props.data, updateChart, { deep: true })
</script>

<style scoped>
.pie-chart {
  width: 100%;
  height: 100%;
  min-height: 250px;
}
</style>
