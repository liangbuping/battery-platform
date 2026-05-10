<template>
  <div class="alert-list">
    <div class="alert-content">
      <div
        v-for="alert in displayAlerts"
        :key="alert.id"
        class="alert-item"
        @click="handleClick(alert)"
      >
        <span class="alert-time">{{ formatAlertTime(alert.createTime) }}</span>
        <div class="alert-content-inner">
          <span class="alert-vehicle">{{ alert.plateNumber }}</span>
          <span class="alert-type">{{ alert.typeName }} - {{ alert.description }}</span>
        </div>
        <span class="alert-badge" :class="getLevelClass(alert.level)">
          {{ getLevelText(alert.level) }}
        </span>
      </div>

      <div v-if="alerts.length === 0" class="alert-empty">暂无告警</div>
    </div>

    <div class="alert-view-all" v-if="alerts.length > 0">
      <a href="javascript:void(0);" @click="handleViewAll">查看全部 &rarr;</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

interface AlertItem {
  id: string
  vin: string
  plateNumber: string
  level: string
  typeName: string
  description: string
  createTime: string
}

const props = defineProps<{
  alerts: AlertItem[]
}>()

const emit = defineEmits<{
  click: [alert: AlertItem]
}>()

const router = useRouter()

const displayAlerts = computed(() => {
  return props.alerts.slice(0, 8)
})

const formatAlertTime = (time: string) => {
  if (!time) return ''
  const d = new Date(time)
  const h = String(d.getHours()).padStart(2, '0')
  const m = String(d.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

const getLevelClass = (level: string) => {
  const map: Record<string, string> = {
    critical: 'high',
    warning: 'medium',
    info: 'low'
  }
  return map[level] || 'low'
}

const getLevelText = (level: string) => {
  const map: Record<string, string> = {
    critical: '严重',
    warning: '一般',
    info: '提示'
  }
  return map[level] || level
}

const handleClick = (alert: AlertItem) => {
  emit('click', alert)
}

const handleViewAll = () => {
  router.push('/alert')
}
</script>

<style scoped>
.alert-list {
  padding: 20px;
}

.alert-content {
  display: flex;
  flex-direction: column;
  gap: 0;
  overflow-y: auto;
  max-height: 380px;
}

.alert-content::-webkit-scrollbar {
  width: 4px;
}

.alert-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.alert-item {
  display: grid;
  grid-template-columns: 58px 1fr auto;
  align-items: center;
  gap: 10px;
  padding: 11px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  animation: slideInRight 0.4s ease both;
  transition: background 0.15s;
  cursor: pointer;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-item:hover {
  background: rgba(255, 255, 255, 0.04);
  margin: 0 -10px;
  padding-left: 10px;
  padding-right: 10px;
  border-radius: 6px;
}

.alert-item:nth-child(1) { animation-delay: 0.05s; }
.alert-item:nth-child(2) { animation-delay: 0.1s; }
.alert-item:nth-child(3) { animation-delay: 0.15s; }
.alert-item:nth-child(4) { animation-delay: 0.2s; }
.alert-item:nth-child(5) { animation-delay: 0.25s; }
.alert-item:nth-child(6) { animation-delay: 0.3s; }
.alert-item:nth-child(7) { animation-delay: 0.35s; }
.alert-item:nth-child(8) { animation-delay: 0.4s; }

.alert-time {
  font-size: 11px;
  color: #8899aa;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.alert-content-inner {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.alert-vehicle {
  font-size: 13px;
  font-weight: 600;
  color: #e0e0e0;
}

.alert-type {
  font-size: 11px;
  color: #8899aa;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.alert-badge {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 10px;
  font-weight: 600;
  white-space: nowrap;
}

.alert-badge.high {
  background: rgba(255, 71, 87, 0.15);
  color: #ff4757;
}

.alert-badge.medium {
  background: rgba(255, 165, 2, 0.15);
  color: #ffa502;
}

.alert-badge.low {
  background: rgba(0, 212, 255, 0.15);
  color: #00d4ff;
}

.alert-empty {
  text-align: center;
  color: #8899aa;
  padding: 40px 0;
  font-size: 13px;
}

.alert-view-all {
  text-align: center;
  padding: 10px 0 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  margin-top: 4px;
}

.alert-view-all a {
  font-size: 12px;
  color: #00d4ff;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.15s;
}

.alert-view-all a:hover {
  color: #33ddff;
  text-decoration: underline;
}

@keyframes slideInRight {
  from { opacity: 0; transform: translateX(24px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>
