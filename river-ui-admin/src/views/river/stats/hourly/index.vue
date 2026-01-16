<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="维度类型" prop="dimensionType">
        <el-select
          v-model="queryParams.dimensionType"
          placeholder="请选择维度类型"
          clearable
          class="!w-180px"
        >
          <el-option label="Offer" :value="1" />
          <el-option label="Campaign" :value="2" />
          <el-option label="Traffic Source" :value="3" />
          <el-option label="Merchant" :value="4" />
          <el-option label="Category" :value="5" />
          <el-option label="Author" :value="6" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="queryParams.date"
          type="date"
          placeholder="选择日期"
          value-format="YYYY-MM-DD"
          class="!w-180px"
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

  <!-- 图表区域 -->
  <ContentWrap>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>小时趋势</span>
            </div>
          </template>
          <div ref="chartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="小时" prop="hour" width="160">
        <template #default="scope">
          {{ formatHour(scope.row.hour) }}
        </template>
      </el-table-column>
      <el-table-column label="维度" prop="dimensionType" width="120">
        <template #default="scope">
          {{ getDimensionTypeName(scope.row.dimensionType) }}
        </template>
      </el-table-column>
      <el-table-column label="维度ID" prop="dimensionId" width="100" />
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
      <el-table-column label="EPC" prop="epc" width="100" align="right">
        <template #default="scope">
          <span class="text-blue-600">${{ scope.row.epc?.toFixed(4) || '0.0000' }}</span>
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
import { HourlyStatsApi, HourlyStatsVO } from '@/api/river/stats'
import * as echarts from 'echarts'

defineOptions({ name: 'StatsHourly' })

const message = useMessage()

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const chartRef = ref<HTMLDivElement>() // 图表容器
const exportLoading = ref(false) // 导出的加载中

const queryParams = reactive({
  pageNo: 1,
  pageSize: 24,
  dimensionType: undefined,
  date: undefined
})
const queryFormRef = ref() // 搜索的表单

let chartInstance: echarts.ECharts | null = null

/** 维度类型名称映射 */
const dimensionTypeNames: Record<number, string> = {
  1: 'Offer',
  2: 'Campaign',
  3: 'Traffic Source',
  4: 'Merchant',
  5: 'Category',
  6: 'Author'
}

const getDimensionTypeName = (type: number) => {
  return dimensionTypeNames[type] || '-'
}

/** 格式化数字 */
const formatNumber = (num: number) => {
  return num?.toLocaleString() || '0'
}

/** 格式化金额 */
const formatMoney = (amount: number) => {
  return amount?.toFixed(2) || '0.00'
}

/** 格式化小时 */
const formatHour = (hour: string) => {
  if (!hour) return '-'
  const date = new Date(hour)
  return `${String(date.getHours()).padStart(2, '0')}:00`
}

/** 初始化图表 */
const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  window.addEventListener('resize', () => chartInstance?.resize())
}

/** 更新图表 */
const updateChart = () => {
  if (!chartInstance || !list.value.length) return

  const hours = list.value.map(item => {
    const date = new Date(item.hour)
    return `${String(date.getHours()).padStart(2, '0')}:00`
  })
  const clicks = list.value.map(item => item.clicks || 0)
  const conversions = list.value.map(item => item.conversions || 0)
  const revenue = list.value.map(item => item.revenue || 0)

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
      data: hours,
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

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await HourlyStatsApi.getHourlyStatsPage(queryParams)
    list.value = data.list
    total.value = data.total
    nextTick(() => updateChart())
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
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await HourlyStatsApi.exportHourlyStats(queryParams)
    download.excel(data, '小时统计.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  // 默认查询今天
  const today = new Date().toISOString().substring(0, 10)
  queryParams.date = today

  initChart()
  getList()
})

onUnmounted(() => {
  chartInstance?.dispose()
})
</script>

<style scoped>
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
</style>
