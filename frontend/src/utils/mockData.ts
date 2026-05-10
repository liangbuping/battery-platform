const mockData = {
  dashboard: {
    kpi: {
      totalVehicles: 10,
      onlineVehicles: 8,
      alertVehicles: 3,
      todayAlerts: 6
    },
    vehicles: [
      { id: 1, vin: 'LSVAG2180E2100001', plateNumber: '京A12345', model: 'Model S', status: 1, soc: 85, soh: 95, temperature: 25.5, riskLevel: 0, longitude: 116.4074, latitude: 39.9042 },
      { id: 2, vin: 'LSVAG2180E2100002', plateNumber: '京A12346', model: 'Model 3', status: 1, soc: 72, soh: 88, temperature: 32.8, riskLevel: 2, longitude: 116.3974, latitude: 39.9142 },
      { id: 3, vin: 'LSVAG2180E2100003', plateNumber: '京A12347', model: 'Model X', status: 2, soc: 45, soh: 92, temperature: 28.3, riskLevel: 1, longitude: 116.4174, latitude: 39.9242 },
      { id: 4, vin: 'LSVAG2180E2100004', plateNumber: '京A12348', model: 'Model Y', status: 1, soc: 90, soh: 97, temperature: 24.1, riskLevel: 0, longitude: 116.4274, latitude: 39.9342 },
      { id: 5, vin: 'LSVAG2180E2100005', plateNumber: '京A12349', model: 'Model S', status: 3, soc: 65, soh: 85, temperature: 35.6, riskLevel: 3, longitude: 116.4374, latitude: 39.9442 },
      { id: 6, vin: 'LSVAG2180E2100006', plateNumber: '沪B56789', model: 'Model 3', status: 1, soc: 78, soh: 93, temperature: 26.7, riskLevel: 0, longitude: 121.4737, latitude: 31.2304 },
      { id: 7, vin: 'LSVAG2180E2100007', plateNumber: '沪B56790', model: 'Model X', status: 0, soc: 0, soh: 89, temperature: 22.0, riskLevel: 1, longitude: 121.4837, latitude: 31.2404 },
      { id: 8, vin: 'LSVAG2180E2100008', plateNumber: '沪B56791', model: 'Model Y', status: 1, soc: 82, soh: 94, temperature: 27.5, riskLevel: 0, longitude: 121.4937, latitude: 31.2504 },
      { id: 9, vin: 'LSVAG2180E2100009', plateNumber: '沪B56792', model: 'Model S', status: 2, soc: 38, soh: 87, temperature: 31.2, riskLevel: 2, longitude: 121.5037, latitude: 31.2604 },
      { id: 10, vin: 'LSVAG2180E2100010', plateNumber: '沪B56793', model: 'Model 3', status: 1, soc: 91, soh: 96, temperature: 23.8, riskLevel: 0, longitude: 121.5137, latitude: 31.2704 }
    ],
    alertTypeStats: [
      { type: 1, count: 3, label: '温度异常' },
      { type: 2, count: 2, label: '电压异常' },
      { type: 3, count: 1, label: 'SOC异常' },
      { type: 4, count: 1, label: '绝缘故障' },
      { type: 5, count: 0, label: '其他' }
    ],
    alertLevelStats: [
      { level: 1, count: 2, label: '一般' },
      { level: 2, count: 2, label: '严重' },
      { level: 3, count: 1, label: '紧急' }
    ],
    riskTop5: [
      { vehicleId: 5, vin: 'LSVAG2180E2100005', plateNumber: '京A12349', riskLevel: 3, temperature: 35.6 },
      { vehicleId: 2, vin: 'LSVAG2180E2100002', plateNumber: '京A12346', riskLevel: 2, temperature: 32.8 },
      { vehicleId: 9, vin: 'LSVAG2180E2100009', plateNumber: '沪B56792', riskLevel: 2, temperature: 31.2 },
      { vehicleId: 3, vin: 'LSVAG2180E2100003', plateNumber: '京A12347', riskLevel: 1, temperature: 28.3 },
      { vehicleId: 7, vin: 'LSVAG2180E2100007', plateNumber: '沪B56790', riskLevel: 1, temperature: 22.0 }
    ]
  },
  alerts: {
    list: [
      { id: 1, alertNo: 'ALT202405010001', vin: 'LSVAG2180E2100002', plateNumber: '京A12346', alertType: 1, alertLevel: 2, description: '电池温度过高，当前温度32.8℃', status: 0, alertTime: new Date().toISOString() },
      { id: 2, alertNo: 'ALT202405010002', vin: 'LSVAG2180E2100005', plateNumber: '京A12349', alertType: 1, alertLevel: 3, description: '电池温度严重超标，当前温度35.6℃', status: 0, alertTime: new Date().toISOString() },
      { id: 3, alertNo: 'ALT202405010003', vin: 'LSVAG2180E2100003', plateNumber: '京A12347', alertType: 3, alertLevel: 1, description: 'SOC偏低，当前电量45%', status: 1, alertTime: new Date(Date.now() - 3600000).toISOString() },
      { id: 4, alertNo: 'ALT202405010004', vin: 'LSVAG2180E2100009', plateNumber: '沪B56792', alertType: 2, alertLevel: 2, description: '电压异常波动', status: 0, alertTime: new Date(Date.now() - 1800000).toISOString() },
      { id: 5, alertNo: 'ALT202405010005', vin: 'LSVAG2180E2100007', plateNumber: '沪B56790', alertType: 4, alertLevel: 1, description: '绝缘电阻偏低', status: 2, alertTime: new Date(Date.now() - 7200000).toISOString() },
      { id: 6, alertNo: 'ALT202405010006', vin: 'LSVAG2180E2100002', plateNumber: '京A12346', alertType: 2, alertLevel: 1, description: '单体电压不均衡', status: 0, alertTime: new Date(Date.now() - 900000).toISOString() }
    ],
    total: 6
  },
  workOrders: {
    list: [
      { id: 1, orderNo: 'WO202405010001', title: '电池温度异常处理', vehicleId: 2, vin: 'LSVAG2180E2100002', plateNumber: '京A12346', status: 1, priority: 3, assignee: '张三', createTime: new Date().toISOString() },
      { id: 2, orderNo: 'WO202405010002', title: '紧急温度故障处理', vehicleId: 5, vin: 'LSVAG2180E2100005', plateNumber: '京A12349', status: 0, priority: 4, assignee: '李四', createTime: new Date().toISOString() },
      { id: 3, orderNo: 'WO202405010003', title: '定期保养', vehicleId: 3, vin: 'LSVAG2180E2100003', plateNumber: '京A12347', status: 1, priority: 2, assignee: '王五', createTime: new Date().toISOString() },
      { id: 4, orderNo: 'WO202405010004', title: '电压异常检修', vehicleId: 9, vin: 'LSVAG2180E2100009', plateNumber: '沪B56792', status: 0, priority: 3, assignee: '赵六', createTime: new Date().toISOString() },
      { id: 5, orderNo: 'WO202405010005', title: '电池组更换', vehicleId: 7, vin: 'LSVAG2180E2100007', plateNumber: '沪B56790', status: 2, priority: 2, assignee: '钱七', createTime: new Date().toISOString() }
    ],
    total: 5
  }
};

export const getMockData = (key: string) => {
  const keys = key.split('.');
  let data: any = mockData;
  for (const k of keys) {
    data = data[k];
  }
  return data;
};

export const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const mockRequest = async (key: string) => {
  await delay(300);
  return {
    code: 200,
    msg: 'success',
    data: getMockData(key)
  };
};
