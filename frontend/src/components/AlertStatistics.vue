<template>
  <div class="alert-statistics">
    <div class="stat-bar-list">
      <div class="stat-bar-item" v-for="(item, index) in barData" :key="index">
        <div class="stat-bar-header">
          <span class="stat-bar-label">{{ item.name }}</span>
          <span class="stat-bar-count">{{ item.value }}</span>
        </div>
        <div class="stat-bar-track">
          <div
            class="stat-bar-fill"
            :class="item.colorClass"
            :style="{ width: barMax > 0 ? (item.value / barMax * 100) + '%' : '0%' }"
          ></div>
        </div>
      </div>
    </div>

    <div class="alert-level-section">
      <div class="alert-level-title">告警等级分布</div>
      <div class="alert-donut-wrapper">
        <div class="alert-donut-container">
          <div ref="donutChartRef" class="donut-chart"></div>
          <div class="alert-donut-center">
            <div class="alert-donut-value">{{ totalAlerts }}</div>
            <div class="alert-donut-label">总告警</div>
          </div>
        </div>
        <div class="alert-level-legend">
          <div class="alert-level-item" v-for="(item, index) in levelData" :key="index">
            <span class="alert-level-dot" :class="item.dotClass"></span>
            <span class="alert-level-name">{{ item.name }}</span>
            <span class="alert-level-val">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface AlertCategory {
  name: string
  value: number
}

const props = defineProps<{
  data: AlertCategory[]
}>()

const donutChartRef = ref<HTMLDivElement>()
let donutChart: echarts.ECharts | null = null

// Default data matching design prototype
const defaultCategories = [
  { name: '热失控风险', value: 2, colorClass: 'red' },
  { name: '电压异常', value: 3, colorClass: 'orange' },
  { name: '温度异常', value: 2, colorClass: 'yellow' },
  { name: '绝缘故障', value: 1, colorClass: 'blue' },
  { name: '容量衰减', value: 1, colorClass: 'gray' }
]

const defaultLevels = [
  { name: '一级 (严重)', value: 2, color: '#ff4757', dotClass: 'l1' },
  { name: '二级 (一般)', value: 3, color: '#ffa502', dotClass: 'l2' },
  { name: '三级 (提示)', value: 2, color: '#f1c40f', dotClass: 'l3' }
]

const barData = computed(() => {
  if (props.data && props.data.length > 0) {
    const colorMap: Record<string, string> = {
      '热失控风险': 'red',
      '电压异常': 'orange',
      '温度异常': 'yellow',
      '绝缘故障': 'blue',
      '容量衰减': 'gray'
    }
    return props.data.map(item => ({
      name: item.name,
      value: item.value,
      colorClass: colorMap[item.name] || 'blue'
    }))
  }
  return defaultCategories
})

const barMax = computed(() => {
  return Math.max(...barData.value.map(d => d.value), 1)
})

const totalAlerts = computed(() => {
  return barData.value.reduce((sum, item) => sum + item.value, 0)
})

const levelData = computed(() => {
  return defaultLevels
})

const initDonutChart = () => {
  if (!donutChartRef.value) return
  donutChart = echarts.init(donutChartRef.value)
  updateDonutChart()
}

const updateDonutChart = () => {
  if (!donutChart) return

  const option: echarts.EChartsOption = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(13, 27, 42, 0.92)',
      borderColor: 'rgba(255,255,255,0.12)',
      textStyle: { color: '#e0e0e0', fontSize: 12 },
      formatter: '{b}: {c}'
    },
    series: [{
      type: 'pie',
      radius: ['55%', '80%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4,
        borderColor: '#1b2838',
        borderWidth: 3
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 12,
          fontWeight: 'bold',
          color: '#e0e0e0'
        }
      },
      labelLine: {
        show: false
      },
      data: levelData.value.map(item => ({
        name: item.name,
        value: item.value,
        itemStyle: {
          color: item.color
        }
      }))
    }]
  }

  donutChart.setOption(option)
}

const handleResize = () => {
  donutChart?.resize()
}

onMounted(() => {
  initDonutChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  donutChart?.dispose()
})

watch(() => props.data, () => {
  updateDonutChart()
}, { deep: true })
</script>

<style scoped>
.alert-statistics {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-bar-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 20px;
}

.stat-bar-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stat-bar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-bar-label {
  font-size: 12px;
  font-weight: 500;
  color: #8899aa;
}

.stat-bar-count {
  font-size: 13px;
  font-weight: 700;
  color: #e0e0e0;
  font-variant-numeric: tabular-nums;
}

.stat-bar-track {
  width: 100%;
  height: 8px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 4px;
  overflow: hidden;
}

.stat-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s ease;
  animation: barGrow 1s ease both;
}

.stat-bar-fill.red { background: linear-gradient(90deg, #ff4757, #c0392b); }
.stat-bar-fill.orange { background: linear-gradient(90deg, #ffa502, #e67e22); }
.stat-bar-fill.yellow { background: linear-gradient(90deg, #f1c40f, #f39c12); }
.stat-bar-fill.blue { background: linear-gradient(90deg, #00d4ff, #2471a3); }
.stat-bar-fill.gray { background: linear-gradient(90deg, #8899aa, #7f8c8d); }

.alert-level-section {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 16px;
}

.alert-level-title {
  font-size: 12px;
  font-weight: 600;
  color: #8899aa;
  margin-bottom: 12px;
}

.alert-donut-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.alert-donut-container {
  position: relative;
  width: 100px;
  height: 100px;
  flex-shrink: 0;
}

.donut-chart {
  width: 100px;
  height: 100px;
}

.alert-donut-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.alert-donut-value {
  font-size: 20px;
  font-weight: 700;
  color: #e0e0e0;
  line-height: 1;
}

.alert-donut-label {
  font-size: 9px;
  color: #8899aa;
  margin-top: 2px;
}

.alert-level-legend {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.alert-level-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
}

.alert-level-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
}

.alert-level-dot.l1 { background: #ff4757; }
.alert-level-dot.l2 { background: #ffa502; }
.alert-level-dot.l3 { background: #f1c40f; }

.alert-level-name {
  color: #8899aa;
}

.alert-level-val {
  font-weight: 700;
  color: #e0e0e0;
  margin-left: auto;
}

@keyframes barGrow {
  from { width: 0; }
}
</style>
