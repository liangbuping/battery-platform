import { http } from '@/utils/request'
import type { WorkOrderQueryParams } from '@/types/api'
import type { WorkOrder, ApiResponse, PageResult } from '@/types'

// 获取工单列表
export function getWorkOrderList(params: WorkOrderQueryParams) {
  return http.get<PageResult<WorkOrder>>('/work-order/list', { params })
}

// 获取工单详情
export function getWorkOrderDetail(id: string) {
  return http.get<WorkOrder>(`/work-order/${id}`)
}

// 创建工单
export function createWorkOrder(data: Partial<WorkOrder>) {
  return http.post<WorkOrder>('/work-order', data)
}

// 更新工单
export function updateWorkOrder(id: string, data: Partial<WorkOrder>) {
  return http.put<WorkOrder>(`/work-order/${id}`, data)
}

// 分配工单
export function assignWorkOrder(id: string, assignee: string) {
  return http.post(`/work-order/${id}/assign`, { assignee })
}

// 处理工单
export function processWorkOrder(id: string, data: {
  stepId: string
  action: 'start' | 'complete'
  remark?: string
}) {
  return http.post(`/work-order/${id}/process`, data)
}

// 完成工单
export function completeWorkOrder(id: string, data: {
  result: string
  remark?: string
}) {
  return http.post(`/work-order/${id}/complete`, data)
}

// 关闭工单
export function closeWorkOrder(id: string, data: {
  reason: string
}) {
  return http.post(`/work-order/${id}/close`, data)
}

// 获取工单统计
export function getWorkOrderStats() {
  return http.get<{
    total: number
    pending: number
    processing: number
    completed: number
    closed: number
  }>('/work-order/stats')
}

// 获取工单类型列表
export function getWorkOrderTypes() {
  return http.get<{
    code: string
    name: string
  }[]>('/work-order/types')
}

// 获取闭环流程数据
export function getClosedLoopData(workOrderId: string) {
  return http.get<{
    steps: {
      id: string
      name: string
      status: 'pending' | 'processing' | 'completed'
      operator?: string
      operatorName?: string
      startTime?: string
      endTime?: string
      remark?: string
    }[]
  }>(`/work-order/${workOrderId}/closed-loop`)
}
