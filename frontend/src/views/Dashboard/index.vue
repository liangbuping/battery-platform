<template>
  <div class="dashboard-page">
    <KpiCards :data="kpiData" />

    <div class="main-content">
      <div class="panel-col map-col">
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">车辆实时位置</div>
            <span class="panel-badge">实时定位</span>
          </div>
          <VehicleMap :markers="mapData" />
        </div>
      </div>
      <div class="panel-col stats-col">
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">预警分类统计</div>
            <span class="panel-badge">今日</span>
          </div>
          <AlertStatistics :data="alertCategory" />
        </div>
      </div>
    </div>

    <div class="bottom-content">
      <div class="panel-col half-col">
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">实时告警</div>
            <span class="alert-count-badge">{{ recentAlerts.length }}</span>
          </div>
          <AlertList :alerts="recentAlerts" @click="handleAlertClick" />
        </div>
      </div>
      <div class="panel-col half-col">
        <div class="panel">
          <div class="panel-header">
            <div class="panel-title">风险车辆 TOP5</div>
            <span class="panel-badge danger-badge">需重点关注</span>
          </div>
          <RiskTop5 :data="riskTop5" @click="handleRiskClick" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDashboardStore } from '@/stores/dashboard'
import { storeToRefs } from 'pinia'

import KpiCards from '@/components/KpiCards.vue'
import VehicleMap from '@/components/VehicleMap.vue'
import AlertList from '@/components/AlertList.vue'
import RiskTop5 from '@/components/RiskTop5.vue'
import AlertStatistics from '@/components/AlertStatistics.vue'

const router = useRouter()
const dashboardStore = useDashboardStore()
const { kpiData, mapData, alertCategory, recentAlerts, riskTop5 } = storeToRefs(dashboardStore)

const handleAlertClick = (alert: any) => {
  router.push(`/alert?id=${alert.id}`)
}

const handleRiskClick = (vehicle: any) => {
  console.log('查看风险车辆:', vehicle)
}

onMounted(() => {
  dashboardStore.fetchDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  padding: 20px 24px 24px;
  background: #0d1b2a;
  min-height: calc(100vh - 60px);
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.bottom-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.panel {
  background: #1b2838;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  height: 100%;
}

.panel-header {
  padding: 16px 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-title::before {
  content: '';
  width: 3px;
  height: 16px;
  background: #00d4ff;
  border-radius: 2px;
}

.panel-badge {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 10px;
  background: rgba(0, 212, 255, 0.12);
  color: #00d4ff;
  font-weight: 500;
}

.danger-badge {
  background: rgba(255, 71, 87, 0.12);
  color: #ff4757;
}

.alert-count-badge {
  background: #ff4757;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 1px 8px;
  border-radius: 10px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}
</style>
