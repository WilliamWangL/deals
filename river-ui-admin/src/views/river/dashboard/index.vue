<template>
  <div v-loading="loading" class="p-4">
    <!-- Stats Cards -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="flex items-center justify-between">
              <span>今日点击</span>
              <el-icon><Pointer /></el-icon>
            </div>
          </template>
          <div class="text-3xl font-bold text-blue-600">{{ stats.todayClicks }}</div>
          <div class="text-sm text-gray-500">较昨日 {{ stats.clicksChange }}%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="flex items-center justify-between">
              <span>今日转化</span>
              <el-icon><ShoppingCart /></el-icon>
            </div>
          </template>
          <div class="text-3xl font-bold text-green-600">{{ stats.todayConversions }}</div>
          <div class="text-sm text-gray-500">转化率 {{ stats.conversionRate }}%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="flex items-center justify-between">
              <span>今日收入</span>
              <el-icon><Money /></el-icon>
            </div>
          </template>
          <div class="text-3xl font-bold text-orange-600">${{ stats.todayRevenue }}</div>
          <div class="text-sm text-gray-500">EPC: ${{ stats.epc }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="flex items-center justify-between">
              <span>ROI</span>
              <el-icon><TrendCharts /></el-icon>
            </div>
          </template>
          <div class="text-3xl font-bold" :class="stats.roi >= 0 ? 'text-green-600' : 'text-red-600'">
            {{ stats.roi }}%
          </div>
          <div class="text-sm text-gray-500">成本: ${{ stats.todayCost }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts -->
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>近7日趋势</span>
          </template>
          <div ref="chartRef" class="h-80"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>Top 5 Offers</span>
          </template>
          <el-table :data="topOffers" stripe>
            <el-table-column prop="name" label="Offer" />
            <el-table-column prop="clicks" label="点击" width="80" />
            <el-table-column prop="revenue" label="收入" width="80">
              <template #default="{ row }">
                ${{ row.revenue }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Pointer, ShoppingCart, Money, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getDashboardSummary,
  getDashboardTrend,
  DashboardTrendVO
} from '@/api/river/stats'

const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | null = null
let resizeHandler: (() => void) | null = null

const loading = ref(false)

const stats = ref({
  todayClicks: 0,
  clicksChange: 0,
  todayConversions: 0,
  conversionRate: 0,
  todayRevenue: 0,
  epc: 0,
  todayCost: 0,
  roi: 0
})

const topOffers = ref([
  { name: 'Amazon Prime', clicks: 456, revenue: 234.50 },
  { name: 'Nike Summer Sale', clicks: 321, revenue: 156.78 },
  { name: 'eBay Electronics', clicks: 234, revenue: 98.45 },
  { name: 'Walmart Groceries', clicks: 189, revenue: 67.23 },
  { name: 'Target Home', clicks: 145, revenue: 45.67 }
])

const trendData = ref<DashboardTrendVO[]>([])

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const [summaryRes, trendRes] = await Promise.all([
      getDashboardSummary(),
      getDashboardTrend({})
    ])

    // Map summary data to stats
    stats.value = {
      todayClicks: summaryRes.totalClicks,
      clicksChange: 0,
      todayConversions: summaryRes.totalConversions,
      conversionRate: summaryRes.avgCr,
      todayRevenue: summaryRes.totalRevenue,
      epc: summaryRes.avgEpc,
      todayCost: summaryRes.totalCost,
      roi: summaryRes.avgRoi
    }

    // Store trend data for chart
    trendData.value = trendRes
  } finally {
    loading.value = false
  }
}

const initChart = () => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  updateChart()
  resizeHandler = () => chart?.resize()
  window.addEventListener('resize', resizeHandler)
}

const updateChart = () => {
  if (!chart) return

  const dates = trendData.value.map((item) => item.date)
  const clicks = trendData.value.map((item) => item.clicks)
  const conversions = trendData.value.map((item) => item.conversions)
  const revenue = trendData.value.map((item) => item.revenue)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['点击', '转化', '收入($)'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dates.length > 0 ? dates : ['暂无数据']
    },
    yAxis: [
      { type: 'value', name: '数量' },
      { type: 'value', name: '收入($)', position: 'right' }
    ],
    series: [
      { name: '点击', type: 'bar', data: clicks },
      { name: '转化', type: 'bar', data: conversions },
      { name: '收入($)', type: 'line', yAxisIndex: 1, data: revenue }
    ]
  }

  chart.setOption(option)
}

onMounted(async () => {
  await fetchDashboardData()
  initChart()
})

onUnmounted(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
  }
  if (chart) {
    chart.dispose()
    chart = null
  }
})
</script>

<style scoped>
.flex {
  display: flex;
}
.items-center {
  align-items: center;
}
.justify-between {
  justify-content: space-between;
}
</style>
