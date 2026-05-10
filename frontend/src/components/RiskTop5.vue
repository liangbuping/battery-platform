<template>
  <div class="risk-top5">
    <div class="risk-table-wrapper">
      <table class="risk-table">
        <thead>
          <tr>
            <th style="width: 60px;">排名</th>
            <th>车辆编号</th>
            <th>电池SOH</th>
            <th>风险等级</th>
            <th>检测日期</th>
            <th>建议措施</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(item, index) in displayData"
            :key="item.vehicleId"
            @click="handleClick(item)"
            class="risk-row"
          >
            <td>
              <span class="risk-rank" :class="`rank-${index + 1}`">{{ index + 1 }}</span>
            </td>
            <td class="plate-cell">{{ item.plateNumber }}</td>
            <td>
              <div class="soh-bar-cell">
                <div class="soh-bar-bg">
                  <div
                    class="soh-bar-fill"
                    :class="getSohClass(item.riskScore)"
                    :style="{ width: getSohWidth(item.riskScore) }"
                  ></div>
                </div>
                <span class="soh-value" :style="{ color: getSohColor(item.riskScore) }">
                  {{ getSohPercent(item.riskScore) }}
                </span>
              </div>
            </td>
            <td>
              <span class="risk-badge" :class="getRiskBadgeClass(item.riskScore)">
                {{ getRiskLabel(item.riskScore) }}
              </span>
            </td>
            <td class="date-cell">{{ getRecentDate() }}</td>
            <td class="action-cell" :style="{ color: getSohColor(item.riskScore) }">
              {{ getActionText(item.riskScore) }}
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="data.length === 0" class="risk-empty">暂无数据</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface RiskVehicle {
  vehicleId: string
  vin: string
  plateNumber: string
  riskScore: number
  alertCount: number
}

const props = defineProps<{
  data: RiskVehicle[]
}>()

const emit = defineEmits<{
  click: [vehicle: RiskVehicle]
}>()

const displayData = computed(() => {
  return props.data.slice(0, 5)
})

// Map riskScore (0-100) to simulated SOH (60-80%)
const riskScoreToSoh = (score: number): number => {
  // Higher risk score = lower SOH
  const soh = 80 - (score / 100) * 20
  return Math.round(soh * 10) / 10
}

const getSohPercent = (score: number): string => {
  return riskScoreToSoh(score).toFixed(1) + '%'
}

const getSohWidth = (score: number): string => {
  return riskScoreToSoh(score) + '%'
}

const getSohClass = (score: number): string => {
  if (score >= 80) return 'critical'
  if (score >= 60) return 'warning'
  return 'caution'
}

const getSohColor = (score: number): string => {
  if (score >= 80) return '#ff4757'
  if (score >= 60) return '#ffa502'
  return '#f1c40f'
}

const getRiskBadgeClass = (score: number): string => {
  if (score >= 80) return 'critical'
  if (score >= 60) return 'high'
  if (score >= 40) return 'medium'
  return 'low'
}

const getRiskLabel = (score: number): string => {
  if (score >= 80) return '极高'
  if (score >= 60) return '高'
  if (score >= 40) return '中'
  return '低'
}

const getRecentDate = (): string => {
  const dates = ['2026-05-08', '2026-05-07', '2026-05-06', '2026-05-05', '2026-05-04']
  return dates[Math.floor(Math.random() * dates.length)]
}

const getActionText = (score: number): string => {
  if (score >= 80) return '建议立即停运检测'
  if (score >= 60) return '限制运营并安排检测'
  return '加强监测频次'
}

const handleClick = (vehicle: RiskVehicle) => {
  emit('click', vehicle)
}
</script>

<style scoped>
.risk-top5 {
  padding: 0;
}

.risk-table-wrapper {
  overflow-x: auto;
}

.risk-table {
  width: 100%;
  border-collapse: collapse;
}

.risk-table thead th {
  padding: 12px 18px;
  text-align: left;
  font-size: 12px;
  font-weight: 600;
  color: #8899aa;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: rgba(255, 255, 255, 0.03);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  white-space: nowrap;
}

.risk-table thead th:first-child {
  border-radius: 8px 0 0 0;
}

.risk-table thead th:last-child {
  border-radius: 0 8px 0 0;
}

.risk-row {
  transition: background 0.15s;
  cursor: pointer;
}

.risk-row:hover {
  background: rgba(255, 255, 255, 0.04);
}

.risk-table tbody td {
  padding: 14px 18px;
  font-size: 13px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  color: #e0e0e0;
}

.risk-table tbody tr:last-child td {
  border-bottom: none;
}

.risk-rank {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
}

.risk-rank.rank-1 { background: rgba(255, 71, 87, 0.15); color: #ff4757; }
.risk-rank.rank-2 { background: rgba(255, 165, 2, 0.15); color: #ffa502; }
.risk-rank.rank-3 { background: rgba(241, 196, 15, 0.15); color: #f1c40f; }
.risk-rank.rank-4 { background: rgba(0, 212, 255, 0.15); color: #00d4ff; }
.risk-rank.rank-5 { background: rgba(0, 212, 255, 0.15); color: #00d4ff; }

.plate-cell {
  font-weight: 600;
  white-space: nowrap;
}

.soh-bar-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.soh-bar-bg {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 4px;
  overflow: hidden;
  max-width: 120px;
}

.soh-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.8s ease;
  animation: barGrow 1s ease both;
}

.soh-bar-fill.critical { background: linear-gradient(90deg, #ff4757, #c0392b); }
.soh-bar-fill.warning { background: linear-gradient(90deg, #ffa502, #e67e22); }
.soh-bar-fill.caution { background: linear-gradient(90deg, #f1c40f, #f39c12); }

.soh-value {
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  min-width: 45px;
  font-size: 12px;
}

.risk-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 10px;
  font-weight: 600;
  white-space: nowrap;
}

.risk-badge.critical { background: rgba(255, 71, 87, 0.15); color: #ff4757; }
.risk-badge.high { background: rgba(255, 165, 2, 0.15); color: #ffa502; }
.risk-badge.medium { background: rgba(241, 196, 15, 0.15); color: #f1c40f; }
.risk-badge.low { background: rgba(46, 213, 115, 0.15); color: #2ed573; }

.date-cell {
  white-space: nowrap;
  color: #8899aa;
}

.action-cell {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.risk-empty {
  text-align: center;
  color: #8899aa;
  padding: 40px 0;
  font-size: 13px;
}

@keyframes barGrow {
  from { width: 0; }
}
</style>
