import { http } from '@/utils/request'
import type { DashboardData } from '@/types/api'
import type { ApiResponse, PageResult, Vehicle } from '@/types'

// 获取仪表盘数据
export function getDashboardData() {
  return http.get<DashboardData>('/dashboard/data')
}

// 获取KPI数据
export function getKpiData() {
  return http.get<{
    totalVehicles: number
    onlineVehicles: number
    warningVehicles: number
    todayAlerts: number
    totalVehiclesTrend: number
    onlineVehiclesTrend: number
    warningVehiclesTrend: number
    todayAlertsTrend: number
  }>('/dashboard/kpi')
}

// 获取地图数据
export function getMapData() {
  return http.get<{
    id: string
    lng: number
    lat: number
    status: string
    info: {
      vin: string
      plateNumber: string
      batteryLevel: number
    }
  }[]>('/dashboard/map')
}

// 获取预警分类统计
export function getAlertCategoryStats() {
  return http.get<{
    name: string
    value: number
  }[]>('/dashboard/alert-category')
}

// 获取实时告警列表
export function getRecentAlerts(limit = 10) {
  return http.get<{
    id: string
    vin: string
    plateNumber: string
    level: string
    typeName: string
    description: string
    createTime: string
  }[]>(`/dashboard/recent-alerts?limit=${limit}`)
}

// 获取风险车辆TOP5
export function getRiskTop5() {
  return http.get<{
    vehicleId: string
    vin: string
    plateNumber: string
    riskScore: number
    alertCount: number
  }[]>('/dashboard/risk-top5')
}

// 获取车辆列表
export function getVehicleList(params: {
  page?: number
  pageSize?: number
  status?: string
}) {
  return http.get<PageResult<Vehicle>>('/dashboard/vehicles', { params })
}
