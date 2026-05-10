// 通用类型定义

// 车辆信息
export interface Vehicle {
  id: string
  vin: string
  plateNumber: string
  model: string
  status: 'online' | 'offline' | 'warning' | 'error'
  batteryLevel: number
  mileage: number
  lastUpdateTime: string
  location?: {
    lng: number
    lat: number
    address: string
  }
}

// 告警信息
export interface Alert {
  id: string
  vehicleId: string
  vin: string
  plateNumber: string
  level: 'critical' | 'warning' | 'info'
  type: string
  typeName: string
  description: string
  status: 'pending' | 'processing' | 'resolved'
  createTime: string
  updateTime?: string
  location?: string
}

// 工单信息
export interface WorkOrder {
  id: string
  alertId?: string
  vehicleId: string
  vin: string
  plateNumber: string
  type: 'repair' | 'maintenance' | 'inspection'
  status: 'pending' | 'assigned' | 'processing' | 'completed' | 'closed'
  priority: 'high' | 'medium' | 'low'
  description: string
  assignee?: string
  assigneeName?: string
  createTime: string
  deadline: string
  completeTime?: string
  steps?: WorkOrderStep[]
}

// 工单步骤
export interface WorkOrderStep {
  id: string
  name: string
  status: 'pending' | 'processing' | 'completed'
  operator?: string
  operatorName?: string
  startTime?: string
  endTime?: string
  remark?: string
}

// KPI数据
export interface KpiData {
  totalVehicles: number
  onlineVehicles: number
  warningVehicles: number
  todayAlerts: number
  totalVehiclesTrend: number
  onlineVehiclesTrend: number
  warningVehiclesTrend: number
  todayAlertsTrend: number
}

// 统计数据
export interface StatisticsData {
  alertLevelDistribution: {
    name: string
    value: number
  }[]
  alertTypeDistribution: {
    name: string
    value: number
  }[]
  alertTrend: {
    date: string
    critical: number
    warning: number
    info: number
  }[]
  riskVehicles: {
    vehicleId: string
    vin: string
    plateNumber: string
    riskScore: number
    alertCount: number
  }[]
}

// 地图标记
export interface MapMarker {
  id: string
  lng: number
  lat: number
  status: 'online' | 'offline' | 'warning' | 'error'
  info: Vehicle
}

// 分页响应
export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

// 通用响应
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}
