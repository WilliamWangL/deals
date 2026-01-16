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
      <el-form-item label="源货币" prop="fromCurrency">
        <el-select
          v-model="queryParams.fromCurrency"
          placeholder="请选择源货币"
          clearable
          class="!w-180px"
        >
          <el-option
            v-for="currency in currencyList"
            :key="currency.code"
            :label="currency.code"
            :value="currency.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="目标货币" prop="toCurrency">
        <el-select
          v-model="queryParams.toCurrency"
          placeholder="请选择目标货币"
          clearable
          class="!w-180px"
        >
          <el-option
            v-for="currency in currencyList"
            :key="currency.code"
            :label="currency.code"
            :value="currency.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="生效日期" prop="effectiveDate">
        <el-date-picker
          v-model="queryParams.effectiveDate"
          type="date"
          placeholder="选择生效日期"
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
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['campaign:fx-rate:create']"
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
      <el-table-column label="源货币" prop="fromCurrency" width="120">
        <template #default="scope">
          <el-tag type="primary" size="small">{{ scope.row.fromCurrency }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标货币" prop="toCurrency" width="120">
        <template #default="scope">
          <el-tag type="success" size="small">{{ scope.row.toCurrency }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="汇率" prop="rate" width="150" align="right">
        <template #default="scope">
          <span class="text-blue-600 font-medium text-lg">
            {{ scope.row.rate?.toFixed(6) || '0.000000' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="生效日期" prop="effectiveDate" width="120">
        <template #default="scope">
          {{ formatDate(scope.row.effectiveDate) }}
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
            v-hasPermi="['campaign:fx-rate:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['campaign:fx-rate:delete']"
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
  <FxRateForm ref="formRef" @success="getList" :currency-list="currencyList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { FxRateApi, FxRateVO, CurrencyApi } from '@/api/river/campaign'
import FxRateForm from './FxRateForm.vue'

defineOptions({ name: 'CampaignFxRate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const currencyList = ref([]) // 货币列表

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  fromCurrency: undefined,
  toCurrency: undefined,
  effectiveDate: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 格式化日期 */
const formatDate = (date: string) => {
  if (!date) return '-'
  return date.substring(0, 10)
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FxRateApi.getFxRatePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取货币列表 */
const getCurrencyList = async () => {
  currencyList.value = await CurrencyApi.getCurrencyList()
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
    await FxRateApi.deleteFxRate(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
  getCurrencyList()
})
</script>
