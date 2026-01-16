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
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入活动名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="流量来源" prop="trafficSourceId">
        <el-select
          v-model="queryParams.trafficSourceId"
          placeholder="请选择流量来源"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option
            v-for="source in trafficSourceList"
            :key="source.id"
            :label="source.name"
            :value="source.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable class="!w-240px">
          <el-option label="搜索" :value="1" />
          <el-option label="社交" :value="2" />
          <el-option label="展示" :value="3" />
          <el-option label="原生" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option label="启用" :value="1" />
          <el-option label="暂停" :value="0" />
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
          v-hasPermi="['campaign:campaign:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['campaign:campaign:export']"
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
      <el-table-column label="名称" prop="name" min-width="200" />
      <el-table-column label="流量来源" prop="trafficSourceId" width="150">
        <template #default="scope">
          {{ getTrafficSourceName(scope.row.trafficSourceId) }}
        </template>
      </el-table-column>
      <el-table-column label="类型" prop="type" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 1" type="primary" size="small">搜索</el-tag>
          <el-tag v-else-if="scope.row.type === 2" type="success" size="small">社交</el-tag>
          <el-tag v-else-if="scope.row.type === 3" type="warning" size="small">展示</el-tag>
          <el-tag v-else-if="scope.row.type === 4" type="info" size="small">原生</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="日预算" prop="budgetDaily" width="120" align="right">
        <template #default="scope">
          <span v-if="scope.row.budgetDaily" class="text-green-600">
            ${{ scope.row.budgetDaily.toFixed(2) }}
          </span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="总预算" prop="budgetTotal" width="120" align="right">
        <template #default="scope">
          <span v-if="scope.row.budgetTotal" class="text-blue-600">
            ${{ scope.row.budgetTotal.toFixed(2) }}
          </span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="外部ID" prop="externalCampaignId" width="150" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="info">暂停</el-tag>
        </template>
      </el-table-column>
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
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['campaign:campaign:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['campaign:campaign:delete']"
          >
            删除
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
  <CampaignForm ref="formRef" @success="getList" :traffic-source-list="trafficSourceList" :landing-page-list="landingPageList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { CampaignApi, CampaignVO, TrafficSourceApi, LandingPageApi } from '@/api/river/campaign'
import CampaignForm from './CampaignForm.vue'

defineOptions({ name: 'CampaignManagement' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const trafficSourceList = ref([]) // 流量来源列表
const landingPageList = ref([]) // 落地页列表

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  trafficSourceId: undefined,
  type: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 获取流量来源名称 */
const getTrafficSourceName = (id: number) => {
  const source = trafficSourceList.value.find((s) => s.id === id)
  return source?.name || '-'
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CampaignApi.getCampaignPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取流量来源列表 */
const getTrafficSourceList = async () => {
  trafficSourceList.value = await TrafficSourceApi.getTrafficSourceList()
}

/** 获取落地页列表 */
const getLandingPageList = async () => {
  landingPageList.value = await LandingPageApi.getLandingPageList()
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

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await CampaignApi.deleteCampaign(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await CampaignApi.exportCampaign(queryParams)
    download.excel(data, '营销活动列表.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
  getTrafficSourceList()
  getLandingPageList()
})
</script>
