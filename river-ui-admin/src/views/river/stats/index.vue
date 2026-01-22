<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="Offer" prop="offerId">
        <el-select
          v-model="queryParams.offerId"
          placeholder="全部"
          clearable
          filterable
          class="!w-160px"
        >
          <el-option
            v-for="item in offerOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Campaign" prop="campaignId">
        <el-select
          v-model="queryParams.campaignId"
          placeholder="全部"
          clearable
          filterable
          class="!w-160px"
        >
          <el-option
            v-for="item in campaignOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="流量来源" prop="trafficSourceId">
        <el-select
          v-model="queryParams.trafficSourceId"
          placeholder="全部"
          clearable
          class="!w-160px"
        >
          <el-option
            v-for="item in trafficSourceOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="维度类型" prop="dimensionType">
        <el-select
          v-model="queryParams.dimensionType"
          placeholder="请选择维度类型"
          clearable
          class="!w-160px"
          @change="handleDimensionTypeChange"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.DIMENSION_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="维度" prop="dimensionId" v-if="queryParams.dimensionType">
        <el-select
          v-model="queryParams.dimensionId"
          placeholder="请选择"
          clearable
          filterable
          class="!w-200px"
        >
          <el-option
            v-for="item in dimensionOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围" prop="dateRange">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- KPI 统计卡片 -->
  <ContentWrap>
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff">
              <Icon icon="ep:mouse" color="#409eff" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(summary.totalClicks) }}</div>
              <div class="stat-label">点击数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9eb">
              <Icon icon="ep:check" color="#67c23a" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(summary.totalConversions) }}</div>
              <div class="stat-label">转化数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec">
              <Icon icon="ep:coin" color="#e6a23c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-green-600">${{ formatMoney(summary.totalRevenue) }}</div>
              <div class="stat-label">收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0">
              <Icon icon="ep:money" color="#f56c6c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-red-600">${{ formatMoney(summary.totalCost) }}</div>
              <div class="stat-label">成本</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e8f4ff">
              <Icon icon="ep:trend-charts" color="#409eff" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value" :class="summary.totalProfit >= 0 ? 'text-green-600' : 'text-red-600'">
                ${{ formatMoney(summary.totalProfit) }}
              </div>
              <div class="stat-label">利润</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 新增：转化率 -->
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9eb">
              <Icon icon="ep:percentage" color="#67c23a" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ summary.totalClicks > 0 ? (summary.totalConversions / summary.totalClicks * 100).toFixed(2) + '%' : '0.00%' }}</div>
              <div class="stat-label">转化率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 新增：平均订单价值 -->
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff">
              <Icon icon="ep:shopping-cart" color="#409eff" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-blue-600">${{ summary.totalConversions > 0 ? (summary.totalRevenue / summary.totalConversions).toFixed(2) : '0.00' }}</div>
              <div class="stat-label">AOV</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <!-- 新增：每次点击收益 -->
      <el-col :xs="24" :sm="12" :md="4" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec">
              <Icon icon="ep:coin" color="#e6a23c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-orange-600">${{ summary.totalClicks > 0 ? (summary.totalRevenue / summary.totalClicks).toFixed(4) : '0.0000' }}</div>
              <div class="stat-label">EPC</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <!-- 趋势图 -->
  <ContentWrap>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>趋势分析</span>
        </div>
      </template>
      <div ref="chartRef" style="height: 300px"></div>
    </el-card>
  </ContentWrap>

  <!-- 数据列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="日期" prop="date" width="120">
        <template #default="scope">
          {{ formatDate(scope.row.date) }}
        </template>
      </el-table-column>
      <el-table-column label="维度类型" prop="dimensionType" width="100">
        <template #default="scope">
          <el-tag :type="getDimensionTypeColor(scope.row.dimensionType)" size="small">
            {{ getDimensionTypeName(scope.row.dimensionType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维度名称" prop="dimensionName" min-width="150" />
      <el-table-column label="点击数" prop="clicks" width="100" align="right">
        <template #default="scope">
          <span class="text-blue-600">{{ formatNumber(scope.row.clicks) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="转化数" prop="conversions" width="100" align="right">
        <template #default="scope">
          <span class="text-green-600">{{ formatNumber(scope.row.conversions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收入" prop="revenue" width="120" align="right">
        <template #default="scope">
          <span class="text-green-600">${{ formatMoney(scope.row.revenue) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本" prop="cost" width="120" align="right">
        <template #default="scope">
          <span class="text-red-600">${{ formatMoney(scope.row.cost) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="利润" prop="profit" width="120" align="right">
        <template #default="scope">
          <span :class="scope.row.profit >= 0 ? 'text-green-600' : 'text-red-600'">
            ${{ formatMoney(scope.row.profit) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="EPC" prop="epc" width="90" align="right">
        <template #default="scope">
          <span class="text-blue-600">${{ scope.row.epc?.toFixed(4) || '0.0000' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="CR" prop="cr" width="80" align="right">
        <template #default="scope">
          <span class="text-purple-600">{{ scope.row.cr?.toFixed(2) || '0.00' }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="ROI" prop="roi" width="80" align="right">
        <template #default="scope">
          <span :class="scope.row.roi >= 100 ? 'text-green-600' : 'text-red-600'">
            {{ scope.row.roi?.toFixed(0) || '0' }}%
          </span>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts">
import { DailyStatsApi, DimensionType } from '@/api/river/stats'
import { CampaignApi, TrafficSourceApi, LandingPageApi } from '@/api/river/campaign'
import { OfferApi, MerchantApi, CategoryApi } from '@/api/river/affiliate'
import * as echarts from 'echarts'
import { DICT_TYPE, getIntDictOptions, getDictLabel } from '@/utils/dict'
import download from '@/utils/download'

defineOptions({ name: 'StatsReport' })

const message = useMessage()

const loading = ref(true)
const total = ref(0)
const list = ref<any[]>([])
const dimensionOptions = ref<any[]>([])
const dateRange = ref<string[]>([])
const chartRef = ref<HTMLDivElement>()
const exportLoading = ref(false)
const offerOptions = ref<any[]>([])
const campaignOptions = ref<any[]>([])
const trafficSourceOptions = ref<any[]>([])

const summary = ref({
  totalClicks: 0,
  totalConversions: 0,
  totalRevenue: 0,
  totalCost: 0,
  totalProfit: 0,
  avgEpc: 0,
  avgCr: 0,
  avgRoi: 0
})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  dimensionType: undefined as number | undefined,
  dimensionId: undefined as number | undefined,
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined,
  offerId: undefined as number | undefined,
  campaignId: undefined as number | undefined,
  trafficSourceId: undefined as number | undefined
})
const queryFormRef = ref()

let chartInstance: echarts.ECharts | null = null

/** 维度类型颜色 */
const getDimensionTypeColor = (type: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined => {
  const colorMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined> = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger',
    6: undefined
  }
  return colorMap[type]
}

const getDimensionTypeName = (type: number) => {
  return getDictLabel(DICT_TYPE.DIMENSION_TYPE, type) || '-'
}

const formatNumber = (num: number) => {
  return num?.toLocaleString() || '0'
}

const formatMoney = (amount: number) => {
  return amount?.toFixed(2) || '0.00'
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return date.substring(0, 10)
}

/** 加载筛选选项 */
const loadFilterOptions = async () => {
  try {
    const [offerRes, campaignRes, trafficRes] = await Promise.all([
      OfferApi.getOfferList(),
      CampaignApi.getCampaignPage({ pageNo: 1, pageSize: 200 }),
      TrafficSourceApi.getTrafficSourceList()
    ])
    offerOptions.value = offerRes || []
    campaignOptions.value = (campaignRes?.list || []).map((item: any) => ({ id: item.id, name: item.name }))
    trafficSourceOptions.value = trafficRes || []
  } catch (error) {
    console.error('Failed to load filter options:', error)
  }
}

/** 维度类型切换 */
const handleDimensionTypeChange = async (type: number) => {
  queryParams.dimensionId = undefined
  dimensionOptions.value = []
  if (!type) return

  try {
    let data: any[] = []
    switch (type) {
      case DimensionType.CAMPAIGN:
        const campaignRes = await CampaignApi.getCampaignPage({ pageNo: 1, pageSize: 200 })
        data = (campaignRes?.list || []).map((item: any) => ({ id: item.id, name: item.name }))
        break
      case DimensionType.SOURCE:
        data = await TrafficSourceApi.getTrafficSourceList()
        break
      case DimensionType.OFFER:
        data = await OfferApi.getOfferList()
        break
      case DimensionType.LANDING_PAGE:
        data = await LandingPageApi.getLandingPageList()
        break
      case DimensionType.MERCHANT:
        data = await MerchantApi.getMerchantList()
        break
      case DimensionType.CATEGORY:
        data = await CategoryApi.getCategoryList()
        break
    }
    dimensionOptions.value = data || []
  } catch (error) {
    console.error('Failed to load dimension options:', error)
  }
}

/** 初始化图表 */
const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  window.addEventListener('resize', () => chartInstance?.resize())
}

/** 更新图表 */
const updateChart = (trendData: any[]) => {
  if (!chartInstance || !trendData?.length) {
    chartInstance?.setOption({ series: [] })
    return
  }

  const dates = trendData.map(item => item.date?.substring(0, 10) || '')
  const clicks = trendData.map(item => item.clicks || 0)
  const conversions = trendData.map(item => item.conversions || 0)
  const revenue = trendData.map(item => item.revenue || 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['点击数', '转化数', '收入']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left'
      },
      {
        type: 'value',
        name: '收入($)',
        position: 'right'
      }
    ],
    series: [
      {
        name: '点击数',
        type: 'line',
        data: clicks,
        smooth: true,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '转化数',
        type: 'line',
        data: conversions,
        smooth: true,
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '收入',
        type: 'bar',
        yAxisIndex: 1,
        data: revenue,
        itemStyle: { color: '#e6a23c' }
      }
    ]
  }

  chartInstance.setOption(option)
}

/** 构建查询参数 */
const buildQueryParams = () => {
  return {
    ...queryParams,
    startDate: dateRange.value?.[0],
    endDate: dateRange.value?.[1]
  }
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const params = buildQueryParams()

    // 并行请求分页数据、汇总数据、趋势数据
    const [pageData, summaryData, trendData] = await Promise.all([
      DailyStatsApi.getPage(params),
      DailyStatsApi.getSummary(params),
      DailyStatsApi.getTrend(params)
    ])

    list.value = pageData.list || []
    total.value = pageData.total || 0
    summary.value = summaryData || {
      totalClicks: 0,
      totalConversions: 0,
      totalRevenue: 0,
      totalCost: 0,
      totalProfit: 0,
      avgEpc: 0,
      avgCr: 0,
      avgRoi: 0
    }

    nextTick(() => updateChart(trendData || []))
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  dateRange.value = []
  queryParams.dimensionType = undefined
  queryParams.dimensionId = undefined
  dimensionOptions.value = []
  handleQuery()
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const params = buildQueryParams()
    const data = await DailyStatsApi.exportExcel(params)
    download.excel(data, '统计报表.xls')
  } catch {
    // ignore
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  // 默认查询最近7天
  const end = new Date()
  const start = new Date()
  start.setDate(end.getDate() - 7)
  dateRange.value = [
    start.toISOString().substring(0, 10),
    end.toISOString().substring(0, 10)
  ]

  initChart()
  loadFilterOptions()
  getList()
})

onUnmounted(() => {
  chartInstance?.dispose()
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 16px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-green-600 {
  color: #67c23a;
}

.text-red-600 {
  color: #f56c6c;
}

.text-blue-600 {
  color: #409eff;
}

.text-purple-600 {
  color: #9c27b0;
}
</style>
