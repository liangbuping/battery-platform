import { http } from '@/utils/request'
import type { VehicleQueryParams } from '@/types/api'
import type { Vehicle, ApiResponse, PageResult } from '@/types'

// 获取车辆列表
export function getVehicleList(params: VehicleQueryParams) {
  return http.get<PageResult<Vehicle>>('/vehicle/list', { params })
}

// 获取车辆详情
export function getVehicleDetail(id: string) {
  return http.get<Vehicle>(`/vehicle/${id}`)
}

// 根据VIN获取车辆
export function getVehicleByVin(vin: string) {
  return http.get<Vehicle>(`/vehicle/vin/${vin}`)
}

// 获取车辆实时数据
export function getVehicleRealtimeData(id: string) {
  return http.get<{
    vehicleId: string
    vin: string
    batteryLevel: number
    voltage: number
    current: number
    temperature: number
    speed: number
    mileage: number
    gps: {
      lng: number
      lat: number
    }
    status: string
    updateTime: string
  }>(`/vehicle/${id}/realtime`)
}

// 获取车辆电池数据
export function getVehicleBatteryData(id: string) {
  return http.get<{
    vehicleId: string
    vin: string
    totalVoltage: number
    totalCurrent: number
    soc: number
    soh: number
    cellVoltages: number[]
    cellTemperatures: number[]
    maxTemp: number
    minTemp: number
    maxVoltageDiff: number
    updateTime: string
  }>(`/vehicle/${id}/battery`)
}

// 获取车辆历史轨迹
export function getVehicleTrack(id: string, params: {
  startTime: string
  endTime: string
}) {
  return http.get<{
    points: {
      lng: number
      lat: number
      time: string
      speed: number
    }[]
  }>(`/vehicle/${id}/track`, { params })
}

// 获取车辆告警历史
export function getVehicleAlertHistory(id: string, params: {
  page?: number
  pageSize?: number
  startTime?: string
  endTime?: string
}) {
  return http.get<PageResult<{
    id: string
    level: string
    type: string
    typeName: string
    description: string
    status: string
    createTime: string
  }>>(`/vehicle/${id}/alerts`, { params })
}

// 获取车辆统计信息
export function getVehicleStats() {
  return http.get<{
    total: number
    online: number
    offline: number
    warning: number
    error: number
  }>('/vehicle/stats')
}

// 获取车辆型号列表
export function getVehicleModels() {
  return http.get<{
    code: string
    name: string
    manufacturer: string
  }[]>('/vehicle/models')
}
