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
      <el-form-item label="Offer名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入Offer名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="商家" prop="merchantId">
        <el-select
          v-model="queryParams.merchantId"
          placeholder="请选择商家"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option
            v-for="merchant in merchantList"
            :key="merchant.id"
            :label="merchant.name"
            :value="merchant.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="佣金类型" prop="commissionType">
        <el-select
          v-model="queryParams.commissionType"
          placeholder="请选择佣金类型"
          clearable
          class="!w-240px"
        >
          <el-option label="CPA" :value="1" />
          <el-option label="CPC" :value="2" />
          <el-option label="CPS" :value="3" />
          <el-option label="CPL" :value="4" />
          <el-option label="CPM" :value="5" />
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
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['affiliate:offer:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['affiliate:offer:export']"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      row-key="id"
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
    >
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="Offer名称" align="center" prop="name" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="商家" align="center" prop="merchantId">
        <template #default="scope">
          {{ getMerchantName(scope.row.merchantId) }}
        </template>
      </el-table-column>
      <el-table-column label="佣金类型" align="center" prop="commissionType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.commissionType === 1" type="primary">CPA</el-tag>
          <el-tag v-else-if="scope.row.commissionType === 2" type="success">CPC</el-tag>
          <el-tag v-else-if="scope.row.commissionType === 3" type="warning">CPS</el-tag>
          <el-tag v-else-if="scope.row.commissionType === 4" type="info">CPL</el-tag>
          <el-tag v-else type="danger">CPM</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="佣金数值" align="center" prop="commissionValue">
        <template #default="scope">
          {{ scope.row.commissionValue }} {{ scope.row.currency || 'USD' }}
        </template>
      </el-table-column>
      <el-table-column label="Cookie期(天)" align="center" prop="cookieDays" width="100" />
      <el-table-column label="EPC" align="center" prop="epc" width="100" />
      <el-table-column label="转化率" align="center" prop="conversionRate" width="100">
        <template #default="scope">
          {{ (scope.row.conversionRate * 100).toFixed(2) }}%
        </template>
      </el-table-column>
      <el-table-column label="推荐" align="center" prop="featured" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.featured" type="warning">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="150">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['affiliate:offer:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['affiliate:offer:delete']"
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
  <OfferForm ref="formRef" @success="getList" :merchant-list="merchantList" :network-list="networkList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { OfferApi, OfferVO, MerchantApi, AffiliateNetworkApi } from '@/api/river/affiliate'
import OfferForm from './OfferForm.vue'

/** Offer 列表 */
defineOptions({ name: 'Offer' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<OfferVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const merchantList = ref<any[]>([]) // 商家列表
const networkList = ref<any[]>([]) // 联盟网络列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  merchantId: undefined,
  commissionType: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OfferApi.getOfferPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取商家列表 */
const getMerchantList = async () => {
  merchantList.value = await MerchantApi.getMerchantList()
}

/** 获取联盟网络列表 */
const getNetworkList = async () => {
  networkList.value = await AffiliateNetworkApi.getAffiliateNetworkList()
}

/** 获取商家名称 */
const getMerchantName = (merchantId: number) => {
  const merchant = merchantList.value.find((m) => m.id === merchantId)
  return merchant ? merchant.name : '-'
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
    await OfferApi.deleteOffer(id)
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
    const data = await OfferApi.exportOffer(queryParams)
    download.excel(data, 'Offer.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getMerchantList()
  getNetworkList()
  getList()
})
</script>
