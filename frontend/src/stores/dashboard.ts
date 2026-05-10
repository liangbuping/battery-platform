import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getDashboardData,
  getKpiData,
  getMapData,
  getAlertCategoryStats,
  getRecentAlerts,
  getRiskTop5
} from '@/api/dashboard'
import type { DashboardData } from '@/types/api'

export const useDashboardStore = defineStore('dashboard', () => {
  // State
  const dashboardData = ref<DashboardData | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const kpiData = computed(() => dashboardData.value?.kpi || {
    totalVehicles: 0,
    onlineVehicles: 0,
    warningVehicles: 0,
    todayAlerts: 0,
    totalVehiclesTrend: 0,
    onlineVehiclesTrend: 0,
    warningVehiclesTrend: 0,
    todayAlertsTrend: 0
  })

  const mapData = computed(() => dashboardData.value?.mapData || [])
  const alertCategory = computed(() => dashboardData.value?.alertCategory || [])
  const recentAlerts = computed(() => dashboardData.value?.recentAlerts || [])
  const riskTop5 = computed(() => dashboardData.value?.riskTop5 || [])

  // Actions
  async function fetchDashboardData() {
    loading.value = true
    error.value = null
    try {
      const data = await getDashboardData()
      dashboardData.value = data
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取数据失败'
      console.error('获取仪表盘数据失败:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchKpiData() {
    try {
      const data = await getKpiData()
      if (dashboardData.value) {
        dashboardData.value.kpi = data
      }
    } catch (err) {
      console.error('获取KPI数据失败:', err)
    }
  }

  async function fetchMapData() {
    try {
      const data = await getMapData()
      if (dashboardData.value) {
        dashboardData.value.mapData = data
      }
    } catch (err) {
      console.error('获取地图数据失败:', err)
    }
  }

  async function fetchAlertCategory() {
    try {
      const data = await getAlertCategoryStats()
      if (dashboardData.value) {
        dashboardData.value.alertCategory = data
      }
    } catch (err) {
      console.error('获取预警分类失败:', err)
    }
  }

  async function fetchRecentAlerts(limit = 10) {
    try {
      const data = await getRecentAlerts(limit)
      if (dashboardData.value) {
        dashboardData.value.recentAlerts = data
      }
    } catch (err) {
      console.error('获取实时告警失败:', err)
    }
  }

  async function fetchRiskTop5() {
    try {
      const data = await getRiskTop5()
      if (dashboardData.value) {
        dashboardData.value.riskTop5 = data
      }
    } catch (err) {
      console.error('获取风险车辆失败:', err)
    }
  }

  // 刷新所有数据
  async function refreshAll() {
    await fetchDashboardData()
  }

  return {
    // State
    dashboardData,
    loading,
    error,
    // Getters
    kpiData,
    mapData,
    alertCategory,
    recentAlerts,
    riskTop5,
    // Actions
    fetchDashboardData,
    fetchKpiData,
    fetchMapData,
    fetchAlertCategory,
    fetchRecentAlerts,
    fetchRiskTop5,
    refreshAll
  }
})
