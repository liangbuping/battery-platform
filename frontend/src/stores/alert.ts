import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getAlertCenterData,
  getAlertList,
  getAlertDetail,
  getAlertOverview,
  getAlertLevelDistribution,
  getAlertTrend,
  getAlertTypeDistribution,
  processAlert,
  batchProcessAlert
} from '@/api/alert'
import type { Alert, PageResult } from '@/types'
import type { AlertCenterData, AlertQueryParams } from '@/types/api'

export const useAlertStore = defineStore('alert', () => {
  // State
  const centerData = ref<AlertCenterData | null>(null)
  const alertList = ref<PageResult<Alert> | null>(null)
  const currentAlert = ref<Alert | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 查询参数
  const queryParams = ref<AlertQueryParams>({
    page: 1,
    pageSize: 20
  })

  // Getters
  const overview = computed(() => centerData.value?.overview || {
    totalAlerts: 0,
    pendingAlerts: 0,
    processingAlerts: 0,
    resolvedAlerts: 0
  })

  const levelDistribution = computed(() => centerData.value?.levelDistribution || [])
  const trendData = computed(() => centerData.value?.trendData || [])
  const typeDistribution = computed(() => centerData.value?.typeDistribution || [])

  const alerts = computed(() => alertList.value?.list || [])
  const total = computed(() => alertList.value?.total || 0)
  const page = computed(() => alertList.value?.page || 1)
  const pageSize = computed(() => alertList.value?.pageSize || 20)

  // Actions
  async function fetchCenterData() {
    loading.value = true
    error.value = null
    try {
      const data = await getAlertCenterData()
      centerData.value = data
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取数据失败'
      console.error('获取预警中心数据失败:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchAlertList(params?: AlertQueryParams) {
    loading.value = true
    error.value = null
    try {
      const mergedParams = { ...queryParams.value, ...params }
      const data = await getAlertList(mergedParams)
      alertList.value = data
      queryParams.value = mergedParams
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取告警列表失败'
      console.error('获取告警列表失败:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchAlertDetail(id: string) {
    loading.value = true
    error.value = null
    try {
      const data = await getAlertDetail(id)
      currentAlert.value = data
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取告警详情失败'
      console.error('获取告警详情失败:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchOverview() {
    try {
      const data = await getAlertOverview()
      if (centerData.value) {
        centerData.value.overview = data
      }
    } catch (err) {
      console.error('获取告警概览失败:', err)
    }
  }

  async function fetchLevelDistribution() {
    try {
      const data = await getAlertLevelDistribution()
      if (centerData.value) {
        centerData.value.levelDistribution = data
      }
    } catch (err) {
      console.error('获取告警等级分布失败:', err)
    }
  }

  async function fetchTrendData(days = 7) {
    try {
      const data = await getAlertTrend(days)
      if (centerData.value) {
        centerData.value.trendData = data
      }
    } catch (err) {
      console.error('获取告警趋势失败:', err)
    }
  }

  async function fetchTypeDistribution() {
    try {
      const data = await getAlertTypeDistribution()
      if (centerData.value) {
        centerData.value.typeDistribution = data
      }
    } catch (err) {
      console.error('获取告警类型分布失败:', err)
    }
  }

  async function handleProcessAlert(id: string, action: 'process' | 'resolve' | 'ignore', remark?: string) {
    try {
      await processAlert(id, { action, remark })
      // 刷新列表
      await fetchAlertList()
      return true
    } catch (err) {
      console.error('处理告警失败:', err)
      return false
    }
  }

  async function handleBatchProcess(ids: string[], action: 'process' | 'resolve' | 'ignore', remark?: string) {
    try {
      await batchProcessAlert(ids, { action, remark })
      // 刷新列表
      await fetchAlertList()
      return true
    } catch (err) {
      console.error('批量处理告警失败:', err)
      return false
    }
  }

  // 更新查询参数
  function updateQueryParams(params: Partial<AlertQueryParams>) {
    queryParams.value = { ...queryParams.value, ...params }
  }

  // 重置查询参数
  function resetQueryParams() {
    queryParams.value = {
      page: 1,
      pageSize: 20
    }
  }

  return {
    // State
    centerData,
    alertList,
    currentAlert,
    loading,
    error,
    queryParams,
    // Getters
    overview,
    levelDistribution,
    trendData,
    typeDistribution,
    alerts,
    total,
    page,
    pageSize,
    // Actions
    fetchCenterData,
    fetchAlertList,
    fetchAlertDetail,
    fetchOverview,
    fetchLevelDistribution,
    fetchTrendData,
    fetchTypeDistribution,
    handleProcessAlert,
    handleBatchProcess,
    updateQueryParams,
    resetQueryParams
  }
})
