import { http } from '@/utils/request'
import type { AlertCenterData, AlertQueryParams } from '@/types/api'
import type { Alert, ApiResponse, PageResult } from '@/types'

// 获取预警中心数据
export function getAlertCenterData() {
  return http.get<AlertCenterData>('/alert/center-data')
}

// 获取告警列表
export function getAlertList(params: AlertQueryParams) {
  return http.get<PageResult<Alert>>('/alert/list', { params })
}

// 获取告警详情
export function getAlertDetail(id: string) {
  return http.get<Alert>(`/alert/${id}`)
}

// 获取告警统计概览
export function getAlertOverview() {
  return http.get<{
    totalAlerts: number
    pendingAlerts: number
    processingAlerts: number
    resolvedAlerts: number
  }>('/alert/overview')
}

// 获取告警等级分布
export function getAlertLevelDistribution() {
  return http.get<{
    name: string
    value: number
  }[]>('/alert/level-distribution')
}

// 获取告警趋势
export function getAlertTrend(days = 7) {
  return http.get<{
    date: string
    critical: number
    warning: number
    info: number
  }[]>(`/alert/trend?days=${days}`)
}

// 获取告警类型分布
export function getAlertTypeDistribution() {
  return http.get<{
    name: string
    value: number
  }[]>('/alert/type-distribution')
}

// 处理告警
export function processAlert(id: string, data: {
  action: 'process' | 'resolve' | 'ignore'
  remark?: string
}) {
  return http.post(`/alert/${id}/process`, data)
}

// 批量处理告警
export function batchProcessAlert(ids: string[], data: {
  action: 'process' | 'resolve' | 'ignore'
  remark?: string
}) {
  return http.post('/alert/batch-process', { ids, ...data })
}

// 获取告警类型列表
export function getAlertTypes() {
  return http.get<{
    code: string
    name: string
    description: string
  }[]>('/alert/types')
}
