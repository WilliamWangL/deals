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
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入Deal标题"
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
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['coupon:deal:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['coupon:deal:export']"
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
      <el-table-column label="标题" align="center" prop="title" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="商家" align="center" prop="merchantId">
        <template #default="scope">
          {{ getMerchantName(scope.row.merchantId) }}
        </template>
      </el-table-column>
      <el-table-column label="原价" align="center" prop="originalPrice">
        <template #default="scope">
          <span class="line-through text-gray-400">${{ scope.row.originalPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠价" align="center" prop="dealPrice">
        <template #default="scope">
          <span class="text-red-500 font-bold">${{ scope.row.dealPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="折扣" align="center" prop="discountPercent" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.discountPercent" type="danger">{{ scope.row.discountPercent }}% OFF</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="库存限制" align="center" prop="stockLimit" width="100">
        <template #default="scope">
          {{ scope.row.stockLimit || '不限' }}
        </template>
      </el-table-column>
      <el-table-column label="有效期至" align="center" prop="endTime" width="120">
        <template #default="scope">
          {{ scope.row.endTime ? formatDate(scope.row.endTime) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="推荐" align="center" prop="featured" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.featured" type="warning">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="热度" align="center" prop="hotScore" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" width="150">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['coupon:deal:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['coupon:deal:delete']"
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
  <DealForm ref="formRef" @success="getList" :merchant-list="merchantList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { DealApi, DealVO } from '@/api/river/coupon'
import { MerchantApi } from '@/api/river/affiliate'
import DealForm from './DealForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** Deal 列表 */
defineOptions({ name: 'Deal' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<DealVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const merchantList = ref<any[]>([]) // 商家列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  title: undefined,
  merchantId: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DealApi.getDealPage(queryParams)
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

/** 获取商家名称 */
const getMerchantName = (merchantId: number) => {
  const merchant = merchantList.value.find((m) => m.id === merchantId)
  return merchant ? merchant.name : '-'
}

/** 格式化日期 */
const formatDate = (date: Date) => {
  if (!date) return '-'
  return dateFormatter(new Date(), new Date(date), 'YYYY-MM-DD')
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
    await DealApi.deleteDeal(id)
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
    const data = await DealApi.exportDeal(queryParams)
    download.excel(data, 'Deal.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getMerchantList()
  getList()
})
</script>
