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
      <el-form-item label="优惠码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入优惠码"
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
      <el-form-item label="折扣类型" prop="discountType">
        <el-select
          v-model="queryParams.discountType"
          placeholder="请选择折扣类型"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COUPON_DISCOUNT_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
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
          v-hasPermi="['coupon:coupon:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['coupon:coupon:export']"
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
      <el-table-column label="编号" align="center" prop="id" width="80">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="handleDetail(scope.row)">
            {{ scope.row.id }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="优惠码" align="center" prop="code" width="150">
        <template #default="scope">
          <el-tag class="cursor-copy" @click="copyCode(scope.row.code)">{{ scope.row.code }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="商家" align="center" prop="merchantId">
        <template #default="scope">
          {{ getMerchantName(scope.row.merchantId) }}
        </template>
      </el-table-column>
      <el-table-column label="折扣类型" align="center" prop="discountType" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COUPON_DISCOUNT_TYPE" :value="scope.row.discountType" />
        </template>
      </el-table-column>
      <el-table-column label="折扣值" align="center" prop="discountValue">
        <template #default="scope">
          <span v-if="scope.row.discountType === 1">{{ scope.row.discountValue }}%</span>
          <span v-else-if="scope.row.discountType === 2">${{ scope.row.discountValue }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="最低消费" align="center" prop="minPurchase">
        <template #default="scope">
          {{ scope.row.minPurchase ? '$' + scope.row.minPurchase : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="有效期" align="center" width="180">
        <template #default="scope">
          <div v-if="scope.row.startTime || scope.row.endTime" class="text-xs">
            {{ formatDateStr(scope.row.startTime) }} 至<br/>{{ formatDateStr(scope.row.endTime) }}
          </div>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="验证状态" align="center" prop="verified" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.verified" type="success">已验证</el-tag>
          <el-tag v-else type="warning">未验证</el-tag>
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
            v-hasPermi="['coupon:coupon:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['coupon:coupon:delete']"
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
  <CouponForm ref="formRef" @success="getList" :merchant-list="merchantList" />

  <!-- 详情弹窗 -->
  <el-dialog v-model="detailVisible" title="优惠券详情" width="600px">
    <el-descriptions :column="2" border v-if="currentDetail.id">
      <el-descriptions-item label="ID">{{ currentDetail.id }}</el-descriptions-item>
      <el-descriptions-item label="名称">{{ currentDetail.name || '-' }}</el-descriptions-item>
      <el-descriptions-item label="商家">{{ getMerchantName(currentDetail.merchantId) }}</el-descriptions-item>
      <el-descriptions-item label="类型">
        <dict-tag :type="DICT_TYPE.COUPON_TYPE" :value="currentDetail.type" />
      </el-descriptions-item>
      <el-descriptions-item label="代码">
        <el-link v-if="currentDetail.code" :underline="false" type="primary" @click="copyCode(currentDetail.code)">
          {{ currentDetail.code }}
        </el-link>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="折扣">
        <template v-if="currentDetail.discountType === 1">{{ currentDetail.discountValue }}%</template>
        <template v-else-if="currentDetail.discountType === 2">${{ currentDetail.discountValue }}</template>
        <template v-else>-</template>
      </el-descriptions-item>
      <el-descriptions-item label="有效期">
        {{ currentDetail.startTime && currentDetail.endTime ? formatDateStr(currentDetail.startTime) + ' 至 ' + formatDateStr(currentDetail.endTime) : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="currentDetail.status" />
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ currentDetail.createTime ? dateFormatter(currentDetail.createTime) : '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关 闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import { CouponApi, CouponVO } from '@/api/river/coupon'
import { MerchantApi } from '@/api/river/affiliate'
import CouponForm from './CouponForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 优惠券 列表 */
defineOptions({ name: 'Coupon' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<CouponVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const merchantList = ref<any[]>([]) // 商家列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  code: undefined,
  merchantId: undefined,
  discountType: undefined,
  verified: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CouponApi.getCouponPage(queryParams)
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
const formatDateStr = (date: Date) => {
  if (!date) return '-'
  return formatDate(new Date(date), 'YYYY-MM-DD')
}

/** 复制优惠码 */
const copyCode = async (code: string) => {
  try {
    await navigator.clipboard.writeText(code)
    message.success('优惠码已复制到剪贴板')
  } catch {
    message.error('复制失败')
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 详情操作 */
const detailVisible = ref(false)
const currentDetail = ref({} as CouponVO)
const handleDetail = (row: CouponVO) => {
  currentDetail.value = row
  detailVisible.value = true
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
    await CouponApi.deleteCoupon(id)
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
    const data = await CouponApi.exportCoupon(queryParams)
    download.excel(data, '优惠券.xls')
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
