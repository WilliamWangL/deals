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
      <el-form-item label="活动" prop="campaignId">
        <el-select
          v-model="queryParams.campaignId"
          placeholder="请选择营销活动"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option
            v-for="campaign in campaignList"
            :key="campaign.id"
            :label="campaign.name"
            :value="campaign.id"
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
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['campaign:cost-record:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="日期" prop="date" width="120" :formatter="dateFormatter" />
      <el-table-column label="营销活动" prop="campaignId" width="200">
        <template #default="scope">
          {{ getCampaignName(scope.row.campaignId) }}
        </template>
      </el-table-column>
      <el-table-column label="广告组" prop="adGroupId" width="150">
        <template #default="scope">
          {{ getAdGroupName(scope.row.adGroupId) }}
        </template>
      </el-table-column>
      <el-table-column label="展示量" prop="impressions" width="100" align="right">
        <template #default="scope">
          <span class="text-gray-600">{{ formatNumber(scope.row.impressions) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="点击量" prop="clicks" width="100" align="right">
        <template #default="scope">
          <span class="text-blue-600">{{ formatNumber(scope.row.clicks) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="成本" prop="cost" width="120" align="right">
        <template #default="scope">
          <span class="text-red-600 font-medium">
            {{ scope.row.currency }} {{ scope.row.cost?.toFixed(4) || '0.0000' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="来源" prop="source" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COST_RECORD_SOURCE" :value="scope.row.source" />
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
            v-hasPermi="['campaign:cost-record:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['campaign:cost-record:delete']"
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
  <CostRecordForm ref="formRef" @success="getList" :campaign-list="campaignList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { CostRecordApi, CostRecordVO, CampaignApi, AdGroupApi } from '@/api/river/campaign'
import CostRecordForm from './CostRecordForm.vue'
import { DICT_TYPE } from '@/utils/dict'

defineOptions({ name: 'CampaignCostRecord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const campaignList = ref([]) // 营销活动列表
const adGroupList = ref([]) // 广告组列表

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  campaignId: undefined,
  date: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 格式化数字 */
const formatNumber = (num: number) => {
  return num?.toLocaleString() || '0'
}

/** 获取活动名称 */
const getCampaignName = (id: number) => {
  const campaign = campaignList.value.find((c) => c.id === id)
  return campaign?.name || '-'
}

/** 获取广告组名称 */
const getAdGroupName = (id: number) => {
  if (!id) return '-'
  const group = adGroupList.value.find((g) => g.id === id)
  return group?.name || `ID: ${id}`
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CostRecordApi.getCostRecordPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取活动列表 */
const getCampaignList = async () => {
  const data = await CampaignApi.getCampaignPage({ pageNo: 1, pageSize: 1000 })
  campaignList.value = data.list
}

/** 获取广告组列表 */
const getAdGroupList = async () => {
  const data = await AdGroupApi.getAdGroupPage({ pageNo: 1, pageSize: 1000 })
  adGroupList.value = data.list
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
    await CostRecordApi.deleteCostRecord(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
  getCampaignList()
  getAdGroupList()
})
</script>
