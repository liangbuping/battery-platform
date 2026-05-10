// 格式化工具函数

/**
 * 格式化日期时间
 * @param date 日期字符串或Date对象
 * @param format 格式模板，默认 'YYYY-MM-DD HH:mm:ss'
 */
export function formatDateTime(date: string | Date | number, format = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return '-'
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return '-'
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化日期
 * @param date 日期字符串或Date对象
 */
export function formatDate(date: string | Date | number): string {
  return formatDateTime(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间
 * @param date 日期字符串或Date对象
 */
export function formatTime(date: string | Date | number): string {
  return formatDateTime(date, 'HH:mm:ss')
}

/**
 * 格式化相对时间
 * @param date 日期字符串或Date对象
 */
export function formatRelativeTime(date: string | Date | number): string {
  if (!date) return '-'
  
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    return formatDateTime(date)
  }
}

/**
 * 格式化数字，添加千分位
 * @param num 数字
 */
export function formatNumber(num: number | string): string {
  if (num === null || num === undefined) return '-'
  const n = typeof num === 'string' ? parseFloat(num) : num
  if (isNaN(n)) return '-'
  return n.toLocaleString('zh-CN')
}

/**
 * 格式化百分比
 * @param value 数值
 * @param decimals 小数位数，默认 2
 */
export function formatPercent(value: number | string, decimals = 2): string {
  if (value === null || value === undefined) return '-'
  const v = typeof value === 'string' ? parseFloat(value) : value
  if (isNaN(v)) return '-'
  return `${(v * 100).toFixed(decimals)}%`
}

/**
 * 格式化文件大小
 * @param bytes 字节数
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const k = 1024
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${units[i]}`
}

/**
 * 格式化时长
 * @param seconds 秒数
 */
export function formatDuration(seconds: number): string {
  if (!seconds || seconds < 0) return '-'
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = Math.floor(seconds % 60)
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (minutes > 0) {
    return `${minutes}分钟${secs}秒`
  } else {
    return `${secs}秒`
  }
}

/**
 * 格式化手机号，隐藏中间四位
 * @param phone 手机号
 */
export function formatPhone(phone: string): string {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 格式化银行卡号，显示后四位
 * @param cardNo 银行卡号
 */
export function formatBankCard(cardNo: string): string {
  if (!cardNo) return '-'
  return `**** **** **** ${cardNo.slice(-4)}`
}

/**
 * 获取告警等级颜色
 * @param level 告警等级
 */
export function getAlertLevelColor(level: string): string {
  const colorMap: Record<string, string> = {
    critical: '#F56C6C',
    warning: '#E6A23C',
    info: '#409EFF'
  }
  return colorMap[level] || '#909399'
}

/**
 * 获取告警等级文本
 * @param level 告警等级
 */
export function getAlertLevelText(level: string): string {
  const textMap: Record<string, string> = {
    critical: '严重',
    warning: '警告',
    info: '提示'
  }
  return textMap[level] || level
}

/**
 * 获取工单状态文本
 * @param status 工单状态
 */
export function getWorkOrderStatusText(status: string): string {
  const textMap: Record<string, string> = {
    pending: '待处理',
    assigned: '已分配',
    processing: '处理中',
    completed: '已完成',
    closed: '已关闭'
  }
  return textMap[status] || status
}

/**
 * 获取工单类型文本
 * @param type 工单类型
 */
export function getWorkOrderTypeText(type: string): string {
  const textMap: Record<string, string> = {
    repair: '维修',
    maintenance: '保养',
    inspection: '巡检'
  }
  return textMap[type] || type
}
