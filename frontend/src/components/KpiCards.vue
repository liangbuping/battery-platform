<template>
  <div class="kpi-row">
    <div class="kpi-card">
      <div class="kpi-header">
        <span class="kpi-label">总车辆</span>
        <div class="kpi-icon blue">&#128653;</div>
      </div>
      <div class="kpi-value-row">
        <span class="kpi-value">{{ formatNumber(data.totalVehicles) }}</span>
        <span class="kpi-unit">辆</span>
      </div>
      <div class="kpi-indicator green">
        &#9650; 较上月 +{{ Math.abs(data.totalVehiclesTrend || 0) }}
      </div>
    </div>
    <div class="kpi-card">
      <div class="kpi-header">
        <span class="kpi-label">在线车辆</span>
        <div class="kpi-icon green">&#9679;</div>
      </div>
      <div class="kpi-value-row">
        <span class="kpi-value">{{ formatNumber(data.onlineVehicles) }}</span>
        <span class="kpi-unit">辆</span>
      </div>
      <div class="kpi-indicator green">
        在线率 {{ data.totalVehicles > 0 ? ((data.onlineVehicles / data.totalVehicles) * 100).toFixed(1) : 0 }}%
      </div>
    </div>
    <div class="kpi-card">
      <div class="kpi-header">
        <span class="kpi-label">预警车辆</span>
        <div class="kpi-icon orange">&#9888;</div>
      </div>
      <div class="kpi-value-row">
        <span class="kpi-value warning-pulse">{{ formatNumber(data.warningVehicles) }}</span>
        <span class="kpi-unit">辆</span>
      </div>
      <div class="kpi-indicator orange">
        <span class="dot-orange"></span>
        较昨日 +{{ Math.abs(data.warningVehiclesTrend || 0) }}
      </div>
    </div>
    <div class="kpi-card">
      <div class="kpi-header">
        <span class="kpi-label">今日告警</span>
        <div class="kpi-icon red">&#128680;</div>
      </div>
      <div class="kpi-value-row">
        <span class="kpi-value alert-pulse">{{ formatNumber(data.todayAlerts) }}</span>
        <span class="kpi-unit">条</span>
      </div>
      <div class="kpi-indicator red">
        <span class="dot-red"></span>
        需立即处理
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { formatNumber } from '@/utils/format'

interface KpiData {
  totalVehicles: number
  onlineVehicles: number
  warningVehicles: number
  todayAlerts: number
  totalVehiclesTrend: number
  onlineVehiclesTrend: number
  warningVehiclesTrend: number
  todayAlertsTrend: number
}

withDefaults(defineProps<{
  data: KpiData
}>(), {
  data: () => ({
    totalVehicles: 0,
    onlineVehicles: 0,
    warningVehicles: 0,
    todayAlerts: 0,
    totalVehiclesTrend: 0,
    onlineVehiclesTrend: 0,
    warningVehiclesTrend: 0,
    todayAlertsTrend: 0
  })
})
</script>

<style scoped>
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.kpi-card {
  background: linear-gradient(135deg, #1b2838 0%, #1e3450 100%);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
  animation: fadeInUp 0.6s ease both;
}

.kpi-card:nth-child(1) { animation-delay: 0.05s; }
.kpi-card:nth-child(2) { animation-delay: 0.1s; }
.kpi-card:nth-child(3) { animation-delay: 0.15s; }
.kpi-card:nth-child(4) { animation-delay: 0.2s; }

.kpi-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.kpi-card::before {
  content: '';
  position: absolute;
  top: -30px;
  right: -30px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
}

.kpi-card::after {
  content: '';
  position: absolute;
  bottom: -20px;
  left: -20px;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
}

.kpi-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.kpi-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.65);
  font-weight: 400;
}

.kpi-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.kpi-icon.blue { background: rgba(0, 212, 255, 0.2); }
.kpi-icon.green { background: rgba(46, 213, 115, 0.2); }
.kpi-icon.orange { background: rgba(255, 165, 2, 0.2); }
.kpi-icon.red { background: rgba(255, 71, 87, 0.2); }

.kpi-value-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.kpi-value {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -1px;
  font-variant-numeric: tabular-nums;
}

.kpi-unit {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.kpi-indicator {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 6px;
  padding: 2px 8px;
  border-radius: 10px;
}

.kpi-indicator.green { background: rgba(46, 213, 115, 0.15); color: #2ed573; }
.kpi-indicator.orange { background: rgba(255, 165, 2, 0.15); color: #ffa502; }
.kpi-indicator.red { background: rgba(255, 71, 87, 0.15); color: #ff4757; }

.warning-pulse {
  animation: pulse 2s infinite;
}

.alert-pulse {
  animation: pulseShadow 1.5s infinite;
}

.dot-orange {
  width: 6px;
  height: 6px;
  background: #ffa502;
  border-radius: 50%;
  display: inline-block;
  animation: pulseOrange 1.5s infinite;
}

.dot-red {
  width: 6px;
  height: 6px;
  background: #ff4757;
  border-radius: 50%;
  display: inline-block;
  animation: pulseRed 1.5s infinite;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(18px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}

@keyframes pulseShadow {
  0%, 100% { box-shadow: 0 0 0 0 rgba(255, 71, 87, 0.45); }
  50% { box-shadow: 0 0 0 8px rgba(255, 71, 87, 0); }
}

@keyframes pulseOrange {
  0%, 100% { box-shadow: 0 0 0 0 rgba(255, 165, 2, 0.5); }
  50% { box-shadow: 0 0 0 8px rgba(255, 165, 2, 0); }
}

@keyframes pulseRed {
  0%, 100% { box-shadow: 0 0 0 0 rgba(255, 71, 87, 0.6); }
  50% { box-shadow: 0 0 0 14px rgba(255, 71, 87, 0); }
}
</style>
