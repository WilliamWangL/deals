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
      <el-form-item label="转化ID" prop="conversionId">
        <el-input
          v-model="queryParams.conversionId"
          placeholder="请输入转化ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-180px"
        />
      </el-form-item>
      <el-form-item label="点击ID" prop="clickId">
        <el-input
          v-model="queryParams.clickId"
          placeholder="请输入点击ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-180px"
        />
      </el-form-item>
      <el-form-item label="归因类型" prop="attributionType">
        <el-select
          v-model="queryParams.attributionType"
          placeholder="请选择归因类型"
          clearable
          class="!w-180px"
        >
          <el-option label="最后点击" :value="1" />
          <el-option label="首次点击" :value="2" />
          <el-option label="线性归因" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="转化ID" prop="conversionId" width="100" />
      <el-table-column label="点击ID" prop="clickId" width="260">
        <template #default="scope">
          <span class="font-mono text-blue-600 text-xs">{{ scope.row.clickId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="归因类型" prop="attributionType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.attributionType === 1" type="primary" size="small">最后点击</el-tag>
          <el-tag v-else-if="scope.row.attributionType === 2" type="success" size="small">首次点击</el-tag>
          <el-tag v-else-if="scope.row.attributionType === 3" type="info" size="small">线性归因</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="置信度" prop="confidenceScore" width="100" align="center">
        <template #default="scope">
          <el-progress
            :percentage="scope.row.confidenceScore"
            :color="getConfidenceColor(scope.row.confidenceScore)"
            :stroke-width="8"
          />
        </template>
      </el-table-column>
      <el-table-column label="归因窗口" prop="attributionWindow" width="120" align="center">
        <template #default="scope">
          <span v-if="scope.row.attributionWindow">
            {{ formatWindow(scope.row.attributionWindow) }}
          </span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
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
import { dateFormatter } from '@/utils/formatTime'
import { AttributionApi } from '@/api/river/tracking'

defineOptions({ name: 'TrackingAttribution' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  conversionId: undefined,
  clickId: undefined,
  attributionType: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 获取置信度颜色 */
const getConfidenceColor = (score: number) => {
  if (score >= 80) return '#67c23a'
  if (score >= 50) return '#e6a23c'
  return '#f56c6c'
}

/** 格式化归因窗口（毫秒转天/小时） */
const formatWindow = (ms: number) => {
  if (!ms) return '-'
  const hours = ms / (1000 * 60 * 60)
  if (hours >= 24) {
    return `${(hours / 24).toFixed(1)} 天`
  }
  return `${hours.toFixed(1)} 小时`
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AttributionApi.getAttributionPage(queryParams)
    list.value = data.list || []
    total.value = data.total || 0
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

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
