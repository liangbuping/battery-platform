<template>
  <div class="vehicle-map-container">
    <div ref="mapRef" class="map"></div>

    <!-- Search bar -->
    <div class="map-search">
      <span class="map-search-icon">&#128269;</span>
      <input type="text" placeholder="搜索车辆编号..." v-model="searchText" />
    </div>

    <!-- Zoom controls -->
    <div class="map-controls">
      <button title="放大" @click="handleZoomIn">+</button>
      <button title="缩小" @click="handleZoomOut">&minus;</button>
    </div>

    <!-- Legend -->
    <div class="map-legend">
      <div class="map-legend-title">图例</div>
      <div class="map-legend-item">
        <span class="legend-circle green"></span>
        <span>正常车辆 ({{ normalCount }})</span>
      </div>
      <div class="map-legend-item">
        <span class="legend-circle orange"></span>
        <span>预警车辆 ({{ warningCount }})</span>
      </div>
      <div class="map-legend-item">
        <span class="legend-circle red"></span>
        <span>严重告警 ({{ errorCount }})</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import * as echarts from 'echarts'

interface MapMarker {
  id: string
  lng: number
  lat: number
  status: 'online' | 'offline' | 'warning' | 'error'
  info: {
    vin: string
    plateNumber: string
    batteryLevel: number
  }
}

const props = defineProps<{
  markers: MapMarker[]
}>()

const mapRef = ref<HTMLDivElement>()
const searchText = ref('')
let chart: echarts.ECharts | null = null

// Guangzhou area bounds
const LNG_MIN = 113.2
const LNG_MAX = 113.4
const LAT_MIN = 23.05
const LAT_MAX = 23.15

const normalCount = computed(() => props.markers.filter(m => m.status === 'online').length)
const warningCount = computed(() => props.markers.filter(m => m.status === 'warning').length)
const errorCount = computed(() => props.markers.filter(m => m.status === 'error').length)

// Generate grid lines to simulate city roads
const generateGridLines = () => {
  const lines: any[] = []

  // Horizontal roads (latitude lines)
  const hRoads = [23.06, 23.075, 23.09, 23.105, 23.12, 23.135]
  hRoads.forEach(lat => {
    lines.push({
      type: 'line',
      coordinateSystem: 'cartesian2d',
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: {
          color: 'rgba(255,255,255,0.08)',
          width: lat === 23.09 || lat === 23.12 ? 3 : 1
        },
        data: [[{ coord: [LNG_MIN, lat] }, { coord: [LNG_MAX, lat] }]]
      }
    })
  })

  // Vertical roads (longitude lines)
  const vRoads = [113.22, 113.25, 113.28, 113.31, 113.34, 113.37, 113.39]
  vRoads.forEach(lng => {
    lines.push({
      type: 'line',
      coordinateSystem: 'cartesian2d',
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: {
          color: 'rgba(255,255,255,0.08)',
          width: lng === 113.28 || lng === 113.34 ? 3 : 1
        },
        data: [[{ coord: [lng, LAT_MIN] }, { coord: [lng, LAT_MAX] }]]
      }
    })
  })

  return lines
}

// Generate simulated building blocks
const generateBuildings = () => {
  const buildings: any[] = []
  const areas = [
    [113.205, 23.055, 0.015, 0.012],
    [113.255, 23.053, 0.02, 0.015],
    [113.345, 23.055, 0.012, 0.013],
    [113.205, 23.095, 0.018, 0.02],
    [113.255, 23.095, 0.02, 0.02],
    [113.345, 23.095, 0.012, 0.02],
    [113.205, 23.125, 0.018, 0.012],
    [113.255, 23.125, 0.02, 0.012],
    [113.345, 23.125, 0.012, 0.012],
  ]

  areas.forEach(([x, y, w, h]) => {
    buildings.push({
      value: [x + w / 2, y + h / 2],
      itemStyle: {
        color: 'rgba(255,255,255,0.03)',
        borderColor: 'rgba(255,255,255,0.05)',
        borderWidth: 1
      },
      symbol: 'rect',
      symbolSize: [w / (LNG_MAX - LNG_MIN) * 600, h / (LAT_MAX - LAT_MIN) * 400]
    })
  })

  return buildings
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    online: '#2ed573',
    offline: '#8899aa',
    warning: '#ffa502',
    error: '#ff4757'
  }
  return colors[status] || '#8899aa'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    online: '正常',
    offline: '离线',
    warning: '预警',
    error: '严重告警'
  }
  return texts[status] || status
}

const initChart = () => {
  if (!mapRef.value) return

  chart = echarts.init(mapRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chart) return

  const filteredMarkers = searchText.value
    ? props.markers.filter(m =>
        m.info.plateNumber.includes(searchText.value) ||
        m.info.vin.includes(searchText.value)
      )
    : props.markers

  // Separate markers by status for different series
  const greenData = filteredMarkers
    .filter(m => m.status === 'online')
    .map(m => ({
      value: [m.lng, m.lat],
      ...m
    }))

  const orangeData = filteredMarkers
    .filter(m => m.status === 'warning')
    .map(m => ({
      value: [m.lng, m.lat],
      ...m
    }))

  const redData = filteredMarkers
    .filter(m => m.status === 'error')
    .map(m => ({
      value: [m.lng, m.lat],
      ...m
    }))

  const buildings = generateBuildings()

  const option: echarts.EChartsOption = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(13, 27, 42, 0.92)',
      borderColor: 'rgba(255,255,255,0.12)',
      textStyle: {
        color: '#e0e0e0',
        fontSize: 12
      },
      formatter: (params: any) => {
        const data = params.data
        if (!data || !data.info) return ''
        const statusColor = getStatusColor(data.status)
        return `
          <div style="padding: 6px 10px; min-width: 160px;">
            <div style="font-weight: 700; margin-bottom: 6px; font-size: 14px; color: #e0e0e0;">
              ${data.info.plateNumber}
            </div>
            <div style="display: flex; align-items: center; gap: 6px; margin-bottom: 4px;">
              <span style="width:8px;height:8px;border-radius:50%;background:${statusColor};display:inline-block;"></span>
              <span style="color: ${statusColor}; font-weight: 600;">${getStatusText(data.status)}</span>
            </div>
            <div style="color: #8899aa; font-size: 11px; margin-top: 4px;">
              <div>VIN: ${data.info.vin}</div>
              <div>电量: ${data.info.batteryLevel}%</div>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: 10,
      right: 10,
      top: 10,
      bottom: 10,
      containLabel: false
    },
    xAxis: {
      show: false,
      min: LNG_MIN,
      max: LNG_MAX
    },
    yAxis: {
      show: false,
      min: LAT_MIN,
      max: LAT_MAX
    },
    series: [
      // Building blocks (background)
      {
        type: 'scatter',
        data: buildings,
        silent: true,
        animation: false,
        z: 0
      },
      // Grid lines - horizontal
      {
        type: 'line',
        coordinateSystem: 'cartesian2d',
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: 'rgba(255,255,255,0.06)', width: 1 },
          data: [
            [{ coord: [LNG_MIN, 23.06] }, { coord: [LNG_MAX, 23.06] }],
            [{ coord: [LNG_MIN, 23.075] }, { coord: [LNG_MAX, 23.075] }],
            [{ coord: [LNG_MIN, 23.09] }, { coord: [LNG_MAX, 23.09] }],
            [{ coord: [LNG_MIN, 23.105] }, { coord: [LNG_MAX, 23.105] }],
            [{ coord: [LNG_MIN, 23.12] }, { coord: [LNG_MAX, 23.12] }],
            [{ coord: [LNG_MIN, 23.135] }, { coord: [LNG_MAX, 23.135] }],
          ]
        },
        silent: true,
        animation: false,
        z: 1
      },
      // Grid lines - vertical
      {
        type: 'line',
        coordinateSystem: 'cartesian2d',
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: 'rgba(255,255,255,0.06)', width: 1 },
          data: [
            [{ coord: [113.22, LAT_MIN] }, { coord: [113.22, LAT_MAX] }],
            [{ coord: [113.25, LAT_MIN] }, { coord: [113.25, LAT_MAX] }],
            [{ coord: [113.28, LAT_MIN] }, { coord: [113.28, LAT_MAX] }],
            [{ coord: [113.31, LAT_MIN] }, { coord: [113.31, LAT_MAX] }],
            [{ coord: [113.34, LAT_MIN] }, { coord: [113.34, LAT_MAX] }],
            [{ coord: [113.37, LAT_MIN] }, { coord: [113.37, LAT_MAX] }],
            [{ coord: [113.39, LAT_MIN] }, { coord: [113.39, LAT_MAX] }],
          ]
        },
        silent: true,
        animation: false,
        z: 1
      },
      // Main roads - horizontal (wider)
      {
        type: 'line',
        coordinateSystem: 'cartesian2d',
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: 'rgba(255,255,255,0.1)', width: 4 },
          data: [
            [{ coord: [LNG_MIN, 23.09] }, { coord: [LNG_MAX, 23.09] }],
            [{ coord: [LNG_MIN, 23.12] }, { coord: [LNG_MAX, 23.12] }],
          ]
        },
        silent: true,
        animation: false,
        z: 2
      },
      // Main roads - vertical (wider)
      {
        type: 'line',
        coordinateSystem: 'cartesian2d',
        markLine: {
          silent: true,
          symbol: 'none',
          lineStyle: { color: 'rgba(255,255,255,0.1)', width: 4 },
          data: [
            [{ coord: [113.28, LAT_MIN] }, { coord: [113.28, LAT_MAX] }],
            [{ coord: [113.34, LAT_MIN] }, { coord: [113.34, LAT_MAX] }],
          ]
        },
        silent: true,
        animation: false,
        z: 2
      },
      // Green (normal) vehicles
      {
        type: 'scatter',
        symbolSize: 18,
        data: greenData,
        itemStyle: {
          color: '#2ed573',
          shadowBlur: 6,
          shadowColor: 'rgba(46, 213, 115, 0.4)',
          borderColor: '#fff',
          borderWidth: 2
        },
        emphasis: {
          scale: 1.4,
          itemStyle: {
            shadowBlur: 16,
            shadowColor: 'rgba(46, 213, 115, 0.6)'
          }
        },
        z: 10
      },
      // Orange (warning) vehicles
      {
        type: 'effectScatter',
        symbolSize: 18,
        data: orangeData,
        itemStyle: {
          color: '#ffa502',
          shadowBlur: 10,
          shadowColor: 'rgba(255, 165, 2, 0.5)',
          borderColor: '#fff',
          borderWidth: 2
        },
        emphasis: {
          scale: 1.4,
          itemStyle: {
            shadowBlur: 20,
            shadowColor: 'rgba(255, 165, 2, 0.7)'
          }
        },
        rippleEffect: {
          brushType: 'stroke',
          scale: 3,
          period: 2
        },
        z: 11
      },
      // Red (error) vehicles
      {
        type: 'effectScatter',
        symbolSize: 20,
        data: redData,
        itemStyle: {
          color: '#ff4757',
          shadowBlur: 12,
          shadowColor: 'rgba(255, 71, 87, 0.6)',
          borderColor: '#fff',
          borderWidth: 2.5
        },
        emphasis: {
          scale: 1.4,
          itemStyle: {
            shadowBlur: 24,
            shadowColor: 'rgba(255, 71, 87, 0.8)'
          }
        },
        rippleEffect: {
          brushType: 'stroke',
          scale: 4,
          period: 1.5
        },
        z: 12
      }
    ]
  }

  chart.setOption(option, true)
}

const handleZoomIn = () => {
  if (!chart) return
  const opt = chart.getOption() as any
  if (opt.xAxis && opt.xAxis[0]) {
    const xMin = opt.xAxis[0].min
    const xMax = opt.xAxis[0].max
    const yMin = opt.yAxis[0].min
    const yMax = opt.yAxis[0].max
    const xRange = (xMax - xMin) * 0.8
    const yRange = (yMax - yMin) * 0.8
    const xCenter = (xMin + xMax) / 2
    const yCenter = (yMin + yMax) / 2
    chart.setOption({
      xAxis: { min: xCenter - xRange / 2, max: xCenter + xRange / 2 },
      yAxis: { min: yCenter - yRange / 2, max: yCenter + yRange / 2 }
    })
  }
}

const handleZoomOut = () => {
  if (!chart) return
  const opt = chart.getOption() as any
  if (opt.xAxis && opt.xAxis[0]) {
    const xMin = opt.xAxis[0].min
    const xMax = opt.xAxis[0].max
    const yMin = opt.yAxis[0].min
    const yMax = opt.yAxis[0].max
    const xRange = (xMax - xMin) * 1.25
    const yRange = (yMax - yMin) * 1.25
    const xCenter = (xMin + xMax) / 2
    const yCenter = (yMin + yMax) / 2
    chart.setOption({
      xAxis: { min: xCenter - xRange / 2, max: xCenter + xRange / 2 },
      yAxis: { min: yCenter - yRange / 2, max: yCenter + yRange / 2 }
    })
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

watch(() => props.markers, updateChart, { deep: true })
</script>

<style scoped>
.vehicle-map-container {
  position: relative;
  height: 460px;
  background: #0f1f30;
  border-radius: 0 0 12px 12px;
  overflow: hidden;
}

.map {
  width: 100%;
  height: 100%;
}

.map-search {
  position: absolute;
  top: 14px;
  left: 14px;
  z-index: 30;
  display: flex;
  align-items: center;
  background: rgba(27, 40, 56, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 0 12px;
  height: 34px;
  gap: 8px;
}

.map-search-icon {
  color: #8899aa;
  font-size: 14px;
}

.map-search input {
  border: none;
  outline: none;
  font-size: 12px;
  color: #e0e0e0;
  width: 160px;
  background: transparent;
}

.map-search input::placeholder {
  color: rgba(136, 153, 170, 0.6);
}

.map-controls {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 30;
  display: flex;
  flex-direction: column;
  gap: 2px;
  background: rgba(27, 40, 56, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  overflow: hidden;
}

.map-controls button {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 16px;
  font-weight: 700;
  color: #8899aa;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s, color 0.15s;
}

.map-controls button:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #e0e0e0;
}

.map-controls button + button {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.map-legend {
  position: absolute;
  bottom: 14px;
  right: 14px;
  z-index: 30;
  background: rgba(27, 40, 56, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 10px 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.map-legend-title {
  font-size: 11px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 2px;
}

.map-legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #8899aa;
}

.legend-circle {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.legend-circle.green { background: #2ed573; }
.legend-circle.orange { background: #ffa502; }
.legend-circle.red { background: #ff4757; }
</style>
