<template>
  <div class="mobile-alert-page">
    <div class="mobile-header">
      <h1>告警推送</h1>
      <el-badge :value="unreadCount" v-if="unreadCount > 0" />
    </div>
    
    <div class="alert-list">
      <div
        v-for="alert in alertList"
        :key="alert.id"
        class="alert-card"
        :class="{ unread: !alert.read }"
        @click="handleClick(alert)"
      >
        <div class="alert-header">
          <el-tag :type="getLevelType(alert.level)" size="small">
            {{ getLevelText(alert.level) }}
          </el-tag>
          <span class="alert-time">{{ formatRelativeTime(alert.createTime) }}</span>
        </div>
        <div class="alert-body">
          <div class="vehicle-info">
            <span class="plate">{{ alert.plateNumber }}</span>
            <span class="vin">{{ alert.vin }}</span>
          </div>
          <div class="alert-type">{{ alert.typeName }}</div>
          <div class="alert-desc">{{ alert.description }}</div>
        </div>
        <div class="alert-footer">
          <el-button type="primary" size="small" @click.stop="handleProcess(alert)">
            立即处理
          </el-button>
          <el-button size="small" @click.stop="handleIgnore(alert)">
            忽略
          </el-button>
        </div>
      </div>
      
      <el-empty v-if="alertList.length === 0" description="暂无告警" />
    </div>
    
    <div class="load-more" v-if="hasMore">
      <el-button link @click="loadMore">加载更多</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { formatRelativeTime, getAlertLevelText } from '@/utils/format'
import { ElMessage } from 'element-plus'

interface AlertItem {
  id: string
  vin: string
  plateNumber: string
  level: 'critical' | 'warning' | 'info'
  typeName: string
  description: string
  createTime: string
  read: boolean
}

// 模拟数据
const alertList = ref<AlertItem[]>([
  {
    id: 'ALT001',
    vin: 'LSVAG2180E2100001',
    plateNumber: '京A12345',
    level: 'critical',
    typeName: '温度异常',
    description: '电池包温度超过阈值，当前温度65°C',
    createTime: new Date(Date.now() - 1000 * 60 * 5).toISOString(),
    read: false
  },
  {
    id: 'ALT002',
    vin: 'LSVAG2180E2100002',
    plateNumber: '京A12346',
    level: 'warning',
    typeName: '电压异常',
    description: '单体电压差超过0.1V',
    createTime: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    read: false
  },
  {
    id: 'ALT003',
    vin: 'LSVAG2180E2100003',
    plateNumber: '京A12347',
    level: 'info',
    typeName: 'SOC过低',
    description: '电池SOC低于20%，请及时充电',
    createTime: new Date(Date.now() - 1000 * 60 * 60).toISOString(),
    read: true
  }
])

const hasMore = ref(false)

const unreadCount = computed(() => alertList.value.filter(a => !a.read).length)

const getLevelType = (level: string) => {
  const types: Record<string, 'danger' | 'warning' | 'info'> = {
    critical: 'danger',
    warning: 'warning',
    info: 'info'
  }
  return types[level] || 'info'
}

const getLevelText = (level: string) => {
  return getAlertLevelText(level)
}

const handleClick = (alert: AlertItem) => {
  alert.read = true
  console.log('查看告警详情:', alert)
}

const handleProcess = (alert: AlertItem) => {
  alert.read = true
  ElMessage.success(`开始处理告警: ${alert.id}`)
}

const handleIgnore = (alert: AlertItem) => {
  alert.read = true
  ElMessage.info(`已忽略告警: ${alert.id}`)
}

const loadMore = () => {
  ElMessage.info('加载更多...')
}
</script>

<style scoped>
.mobile-alert-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 20px;
}

.mobile-header {
  background: #fff;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.mobile-header h1 {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.alert-list {
  padding: 12px;
}

.alert-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.alert-card.unread {
  border-left: 3px solid #f5222d;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.alert-time {
  font-size: 12px;
  color: #8c8c8c;
}

.alert-body {
  margin-bottom: 12px;
}

.vehicle-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.plate {
  font-weight: 600;
  color: #262626;
  font-size: 15px;
}

.vin {
  font-size: 12px;
  color: #8c8c8c;
}

.alert-type {
  font-size: 14px;
  color: #595959;
  margin-bottom: 6px;
}

.alert-desc {
  font-size: 13px;
  color: #8c8c8c;
  line-height: 1.5;
}

.alert-footer {
  display: flex;
  gap: 8px;
}

.load-more {
  text-align: center;
  padding: 16px;
}
</style>
