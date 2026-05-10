// API 相关类型定义

// 登录请求
export interface LoginRequest {
  username: string
  password: string
  captcha?: string
}

// 登录响应
export interface LoginResponse {
  token: string
  userInfo: {
    id: string
    username: string
    realName: string
    avatar?: string
    roles: string[]
    permissions: string[]
  }
}

// 分页请求参数
export interface PageParams {
  page?: number
  pageSize?: number
}

// 告警查询参数
export interface AlertQueryParams extends PageParams {
  level?: string
  type?: string
  status?: string
  startTime?: string
  endTime?: string
  vehicleId?: string
  vin?: string
}

// 工单查询参数
export interface WorkOrderQueryParams extends PageParams {
  type?: string
  status?: string
  priority?: string
  assignee?: string
  startTime?: string
  endTime?: string
}

// 车辆查询参数
export interface VehicleQueryParams extends PageParams {
  status?: string
  model?: string
  vin?: string
  plateNumber?: string
}

// 仪表盘数据
export interface DashboardData {
  kpi: {
    totalVehicles: number
    onlineVehicles: number
    warningVehicles: number
    todayAlerts: number
    totalVehiclesTrend: number
    onlineVehiclesTrend: number
    warningVehiclesTrend: number
    todayAlertsTrend: number
  }
  mapData: {
    id: string
    lng: number
    lat: number
    status: string
    info: {
      vin: string
      plateNumber: string
      batteryLevel: number
    }
  }[]
  alertCategory: {
    name: string
    value: number
  }[]
  recentAlerts: {
    id: string
    vin: string
    plateNumber: string
    level: string
    typeName: string
    description: string
    createTime: string
  }[]
  riskTop5: {
    vehicleId: string
    vin: string
    plateNumber: string
    riskScore: number
    alertCount: number
  }[]
}

// 预警中心数据
export interface AlertCenterData {
  overview: {
    totalAlerts: number
    pendingAlerts: number
    processingAlerts: number
    resolvedAlerts: number
  }
  levelDistribution: {
    name: string
    value: number
  }[]
  trendData: {
    date: string
    critical: number
    warning: number
    info: number
  }[]
  typeDistribution: {
    name: string
    value: number
  }[]
}
