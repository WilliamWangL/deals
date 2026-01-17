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
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.DIMENSION_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker
          v-model="queryParams.date"
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

  <!-- 统计卡片 -->
  <ContentWrap>
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #ecf5ff">
              <Icon icon="ep:mouse" color="#409eff" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(summary.clicks) }}</div>
              <div class="stat-label">点击数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f0f9ff">
              <Icon icon="ep:check" color="#67c23a" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(summary.conversions) }}</div>
              <div class="stat-label">转化数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fdf6ec">
              <Icon icon="ep:coin" color="#e6a23c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-green-600">${{ formatMoney(summary.revenue) }}</div>
              <div class="stat-label">收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #fef0f0">
              <Icon icon="ep:trend-charts" color="#f56c6c" :size="24" />
            </div>
            <div class="stat-info">
              <div class="stat-value text-blue-600">${{ formatMoney(summary.profit) }}</div>
              <div class="stat-label">利润</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="日期" prop="date" width="120">
        <template #default="scope">
          {{ formatDate(scope.row.date) }}
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
      <el-table-column label="利润" prop="profit" width="120" align="right">
        <template #default="scope">
          <span :class="scope.row.profit >= 0 ? 'text-green-600' : 'text-red-600'">
            ${{ formatMoney(scope.row.profit) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="EPC" prop="epc" width="100" align="right">
        <template #default="scope">
          <span class="text-blue-600">${{ scope.row.epc?.toFixed(4) || '0.0000' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="CR" prop="cr" width="100" align="right">
        <template #default="scope">
          <span class="text-purple-600">{{ (scope.row.cr * 100)?.toFixed(2) || '0.00' }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="ROI" prop="roi" width="100" align="right">
        <template #default="scope">
          <span :class="scope.row.roi >= 1 ? 'text-green-600' : 'text-red-600'">
            {{ scope.row.roi?.toFixed(2) || '0.00' }}x
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
import { DailyStatsApi, DailyStatsVO } from '@/api/river/stats'
import { DICT_TYPE, getIntDictOptions, getDictLabel } from '@/utils/dict'

defineOptions({ name: 'StatsDaily' })

const message = useMessage()

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const summary = ref({ // 统计汇总
  clicks: 0,
  conversions: 0,
  revenue: 0,
  profit: 0
})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  dimensionType: undefined,
  date: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

const getDimensionTypeName = (type: number) => {
  return getDictLabel(DICT_TYPE.DIMENSION_TYPE, type) || '-'
}

/** 格式化数字 */
const formatNumber = (num: number) => {
  return num?.toLocaleString() || '0'
}

/** 格式化金额 */
const formatMoney = (amount: number) => {
  return amount?.toFixed(2) || '0.00'
}

/** 格式化日期 */
const formatDate = (date: string) => {
  if (!date) return '-'
  return date.substring(0, 10)
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DailyStatsApi.getDailyStatsPage(queryParams)
    list.value = data.list
    total.value = data.total
    // 计算汇总数据
    summary.value = {
      clicks: data.list.reduce((sum, item) => sum + (item.clicks || 0), 0),
      conversions: data.list.reduce((sum, item) => sum + (item.conversions || 0), 0),
      revenue: data.list.reduce((sum, item) => sum + (item.revenue || 0), 0),
      profit: data.list.reduce((sum, item) => sum + (item.profit || 0), 0)
    }
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
    const data = await DailyStatsApi.exportDailyStats(queryParams)
    download.excel(data, '日报统计.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  // 默认查询最近7天
  const end = new Date()
  const start = new Date()
  start.setDate(end.getDate() - 7)
  queryParams.date = [
    start.toISOString().substring(0, 10),
    end.toISOString().substring(0, 10)
  ]
  getList()
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
  font-size: 24px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
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
