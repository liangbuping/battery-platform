import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getWorkOrderList,
  getWorkOrderDetail,
  createWorkOrder,
  assignWorkOrder,
  processWorkOrder,
  completeWorkOrder,
  closeWorkOrder,
  getWorkOrderStats,
  getClosedLoopData
} from '@/api/workOrder'
import type { WorkOrder, PageResult, WorkOrderStep } from '@/types'
import type { WorkOrderQueryParams } from '@/types/api'

export const useWorkOrderStore = defineStore('workOrder', () => {
  // State
  const workOrderList = ref<PageResult<WorkOrder> | null>(null)
  const currentWorkOrder = ref<WorkOrder | null>(null)
  const closedLoopSteps = ref<WorkOrderStep[]>([])
  const stats = ref({
    total: 0,
    pending: 0,
    processing: 0,
    completed: 0,
    closed: 0
  })
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 查询参数
  const queryParams = ref<WorkOrderQueryParams>({
    page: 1,
    pageSize: 20
  })

  // Getters
  const workOrders = computed(() => workOrderList.value?.list || [])
  const total = computed(() => workOrderList.value?.total || 0)
  const page = computed(() => workOrderList.value?.page || 1)
  const pageSize = computed(() => workOrderList.value?.pageSize || 20)

  const pendingCount = computed(() => stats.value.pending)
  const processingCount = computed(() => stats.value.processing)
  const completedCount = computed(() => stats.value.completed)

  // Actions
  async function fetchWorkOrderList(params?: WorkOrderQueryParams) {
    loading.value = true
    error.value = null
    try {
      const mergedParams = { ...queryParams.value, ...params }
      const data = await getWorkOrderList(mergedParams)
      workOrderList.value = data
      queryParams.value = mergedParams
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取工单列表失败'
      console.error('获取工单列表失败:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchWorkOrderDetail(id: string) {
    loading.value = true
    error.value = null
    try {
      const data = await getWorkOrderDetail(id)
      currentWorkOrder.value = data
    } catch (err) {
      error.value = err instanceof Error ? err.message : '获取工单详情失败'
      console.error('获取工单详情失败:', err)
    } finally {
      loading.value = false
    }
  }

  async function fetchClosedLoopData(workOrderId: string) {
    try {
      const data = await getClosedLoopData(workOrderId)
      closedLoopSteps.value = data.steps
    } catch (err) {
      console.error('获取闭环流程数据失败:', err)
    }
  }

  async function fetchStats() {
    try {
      const data = await getWorkOrderStats()
      stats.value = data
    } catch (err) {
      console.error('获取工单统计失败:', err)
    }
  }

  async function handleCreateWorkOrder(data: Partial<WorkOrder>) {
    loading.value = true
    try {
      const result = await createWorkOrder(data)
      // 刷新列表
      await fetchWorkOrderList()
      return { success: true, data: result }
    } catch (err) {
      console.error('创建工单失败:', err)
      return { success: false, error: err instanceof Error ? err.message : '创建工单失败' }
    } finally {
      loading.value = false
    }
  }

  async function handleAssignWorkOrder(id: string, assignee: string) {
    try {
      await assignWorkOrder(id, assignee)
      // 刷新当前工单
      await fetchWorkOrderDetail(id)
      return true
    } catch (err) {
      console.error('分配工单失败:', err)
      return false
    }
  }

  async function handleProcess(id: string, stepId: string, action: 'start' | 'complete', remark?: string) {
    try {
      await processWorkOrder(id, { stepId, action, remark })
      // 刷新当前工单和闭环数据
      await fetchWorkOrderDetail(id)
      await fetchClosedLoopData(id)
      return true
    } catch (err) {
      console.error('处理工单失败:', err)
      return false
    }
  }

  async function handleComplete(id: string, result: string, remark?: string) {
    try {
      await completeWorkOrder(id, { result, remark })
      // 刷新当前工单
      await fetchWorkOrderDetail(id)
      return true
    } catch (err) {
      console.error('完成工单失败:', err)
      return false
    }
  }

  async function handleClose(id: string, reason: string) {
    try {
      await closeWorkOrder(id, { reason })
      // 刷新当前工单
      await fetchWorkOrderDetail(id)
      return true
    } catch (err) {
      console.error('关闭工单失败:', err)
      return false
    }
  }

  // 更新查询参数
  function updateQueryParams(params: Partial<WorkOrderQueryParams>) {
    queryParams.value = { ...queryParams.value, ...params }
  }

  // 重置查询参数
  function resetQueryParams() {
    queryParams.value = {
      page: 1,
      pageSize: 20
    }
  }

  // 设置当前工单
  function setCurrentWorkOrder(workOrder: WorkOrder | null) {
    currentWorkOrder.value = workOrder
  }

  return {
    // State
    workOrderList,
    currentWorkOrder,
    closedLoopSteps,
    stats,
    loading,
    error,
    queryParams,
    // Getters
    workOrders,
    total,
    page,
    pageSize,
    pendingCount,
    processingCount,
    completedCount,
    // Actions
    fetchWorkOrderList,
    fetchWorkOrderDetail,
    fetchClosedLoopData,
    fetchStats,
    handleCreateWorkOrder,
    handleAssignWorkOrder,
    handleProcess,
    handleComplete,
    handleClose,
    updateQueryParams,
    resetQueryParams,
    setCurrentWorkOrder
  }
})
