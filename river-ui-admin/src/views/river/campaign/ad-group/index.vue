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
          placeholder="请输入广告组名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
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
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
          v-hasPermi="['campaign:ad-group:create']"
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
      <el-table-column label="名称" prop="name" min-width="200" />
      <el-table-column label="所属活动" prop="campaignId" width="200">
        <template #default="scope">
          {{ getCampaignName(scope.row.campaignId) }}
        </template>
      </el-table-column>
      <el-table-column label="出价策略" prop="bidStrategy" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.bidStrategy" type="info" size="small">
            {{ scope.row.bidStrategy }}
          </el-tag>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="外部ID" prop="externalAdGroupId" width="150" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
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
            v-hasPermi="['campaign:ad-group:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['campaign:ad-group:delete']"
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
  <AdGroupForm ref="formRef" @success="getList" :campaign-list="campaignList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { AdGroupApi, AdGroupVO, CampaignApi } from '@/api/river/campaign'
import AdGroupForm from './AdGroupForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

defineOptions({ name: 'CampaignAdGroup' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const campaignList = ref([]) // 营销活动列表

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  campaignId: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 获取活动名称 */
const getCampaignName = (id: number) => {
  const campaign = campaignList.value.find((c) => c.id === id)
  return campaign?.name || '-'
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AdGroupApi.getAdGroupPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取活动列表 */
const getCampaignList = async () => {
  const data = await CampaignApi.getCampaignPage({ pageNo: 1, pageSize: 200 })
  campaignList.value = data.list
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
    await AdGroupApi.deleteAdGroup(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
  getCampaignList()
})
</script>
