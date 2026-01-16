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
      <el-form-item label="网络代码" prop="networkCode">
        <el-input
          v-model="queryParams.networkCode"
          placeholder="请输入网络代码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-180px"
        />
      </el-form-item>
      <el-form-item label="外部ID" prop="externalConversionId">
        <el-input
          v-model="queryParams.externalConversionId"
          placeholder="请输入外部转化ID"
          clearable
          @keyup.enter="handleQuery"
          class="!w-180px"
        />
      </el-form-item>
      <el-form-item label="转化类型" prop="conversionType">
        <el-select
          v-model="queryParams.conversionType"
          placeholder="请选择转化类型"
          clearable
          class="!w-180px"
        >
          <el-option label="Lead" :value="1" />
          <el-option label="Sale" :value="2" />
          <el-option label="Install" :value="3" />
          <el-option label="Signup" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-180px"
        >
          <el-option label="待确认" :value="0" />
          <el-option label="已确认" :value="1" />
          <el-option label="已拒绝" :value="2" />
          <el-option label="已撤销" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['tracking:conversion:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['tracking:conversion:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="网络代码" prop="networkCode" width="120">
        <template #default="scope">
          <el-tag type="primary" size="small">{{ scope.row.networkCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="外部转化ID" prop="externalConversionId" width="180" show-overflow-tooltip />
      <el-table-column label="点击ID" prop="clickId" width="200" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.clickId" class="font-mono text-blue-600">
            {{ scope.row.clickId }}
          </span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="转化类型" prop="conversionType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.conversionType === 1" type="info" size="small">Lead</el-tag>
          <el-tag v-else-if="scope.row.conversionType === 2" type="success" size="small">Sale</el-tag>
          <el-tag v-else-if="scope.row.conversionType === 3" type="warning" size="small">Install</el-tag>
          <el-tag v-else-if="scope.row.conversionType === 4" type="primary" size="small">Signup</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="佣金" prop="commission" width="120" align="right">
        <template #default="scope">
          <span class="text-green-600 font-medium">
            {{ scope.row.currency }} {{ scope.row.commission?.toFixed(4) || '0.0000' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info" size="small">待确认</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success" size="small">已确认</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger" size="small">已拒绝</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="warning" size="small">已撤销</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="转化时间"
        prop="conversionTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column
        label="创建时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleView(scope.row)"
          >
            详情
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['tracking:conversion:update']"
          >
            编辑
          </el-button>
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

  <!-- 表单弹窗：添加/修改 -->
  <ConversionForm ref="formRef" @success="getList" />

  <!-- 详情弹窗 -->
  <el-dialog v-model="detailVisible" title="转化详情" width="800px">
    <el-descriptions :column="2" border v-if="currentDetail">
      <el-descriptions-item label="ID">{{ currentDetail.id }}</el-descriptions-item>
      <el-descriptions-item label="网络代码">{{ currentDetail.networkCode }}</el-descriptions-item>
      <el-descriptions-item label="外部转化ID" :span="2">{{ currentDetail.externalConversionId }}</el-descriptions-item>
      <el-descriptions-item label="点击ID" :span="2">
        <span v-if="currentDetail.clickId" class="font-mono">{{ currentDetail.clickId }}</span>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="转化类型">
        <el-tag v-if="currentDetail.conversionType === 1" type="info" size="small">Lead</el-tag>
        <el-tag v-else-if="currentDetail.conversionType === 2" type="success" size="small">Sale</el-tag>
        <el-tag v-else-if="currentDetail.conversionType === 3" type="warning" size="small">Install</el-tag>
        <el-tag v-else-if="currentDetail.conversionType === 4" type="primary" size="small">Signup</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag v-if="currentDetail.status === 0" type="info" size="small">待确认</el-tag>
        <el-tag v-else-if="currentDetail.status === 1" type="success" size="small">已确认</el-tag>
        <el-tag v-else-if="currentDetail.status === 2" type="danger" size="small">已拒绝</el-tag>
        <el-tag v-else-if="currentDetail.status === 3" type="warning" size="small">已撤销</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="佣金">
        {{ currentDetail.currency }} {{ currentDetail.commission?.toFixed(4) || '0.0000' }}
      </el-descriptions-item>
      <el-descriptions-item label="转化时间">
        {{ formatDate(currentDetail.conversionTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="网络负载" :span="2">
        <pre class="bg-gray-50 p-2 rounded text-xs overflow-auto max-h-40">{{ currentDetail.networkPayload || '-' }}</pre>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关 闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { ConversionApi, ConversionVO } from '@/api/river/tracking'
import ConversionForm from './ConversionForm.vue'

defineOptions({ name: 'TrackingConversion' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const detailVisible = ref(false) // 详情弹窗
const currentDetail = ref<ConversionVO | null>(null) // 当前详情数据

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  networkCode: undefined,
  externalConversionId: undefined,
  conversionType: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 格式化日期 */
const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ConversionApi.getConversionPage(queryParams)
    list.value = data.list
    total.value = data.total
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 查看详情 */
const handleView = async (row: ConversionVO) => {
  currentDetail.value = row
  detailVisible.value = true
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ConversionApi.exportConversion(queryParams)
    download.excel(data, '转化记录.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
