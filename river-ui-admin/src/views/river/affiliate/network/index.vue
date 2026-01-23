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
      <el-form-item label="联盟编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入联盟编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="联盟名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入联盟名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
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
          v-hasPermi="['affiliate:network:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="warning"
          plain
          :loading="syncDealsLoading"
          @click="handleSyncData"
          v-hasPermi="['affiliate:network:sync']"
        >
          <Icon icon="ep:refresh" class="mr-5px" /> 同步数据
        </el-button>
        <el-button
          type="success"
          plain
          :loading="syncCouponsLoading"
          @click="handleSyncCoupons"
          v-hasPermi="['affiliate:network:sync']"
        >
          <Icon icon="ep:refresh" class="mr-5px" /> 同步优惠
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['affiliate:network:export']"
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
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="联盟编码" align="center" prop="code" />
      <el-table-column label="联盟名称" align="center" prop="name" />
      <el-table-column label="联盟类型" align="center" prop="type">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AFFILIATE_NETWORK_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="官网" align="center" prop="websiteUrl" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['affiliate:network:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['affiliate:network:delete']"
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
  <AffiliateNetworkForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { AffiliateNetworkApi, AffiliateNetworkVO } from '@/api/river/affiliate'
import AffiliateNetworkForm from './AffiliateNetworkForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 联盟网络 列表 */
defineOptions({ name: 'AffiliateNetwork' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<AffiliateNetworkVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  code: undefined,
  name: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const syncDealsLoading = ref(false) // 同步 Deal 的加载中
const syncCouponsLoading = ref(false) // 同步 Coupon 的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AffiliateNetworkApi.getAffiliateNetworkPage(queryParams)
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

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await AffiliateNetworkApi.deleteAffiliateNetwork(id)
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
    const data = await AffiliateNetworkApi.exportAffiliateNetwork(queryParams)
    download.excel(data, '联盟网络.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 同步数据按钮操作（Merchant + Offer） */
const handleSyncData = async () => {
  try {
    await message.confirm('确认要同步商家和Offer数据吗？此操作可能需要较长时间。')
    syncDealsLoading.value = true
    await AffiliateNetworkApi.syncData({ code: 'admitad' })
    message.success('商家和Offer同步任务已启动')
    await getList()
  } catch {
  } finally {
    syncDealsLoading.value = false
  }
}

/** 同步优惠按钮操作（Coupon + Deal） */
const handleSyncCoupons = async () => {
  try {
    await message.confirm('确认要同步优惠数据吗？此操作将同步Coupon和Deal，可能需要较长时间。')
    syncCouponsLoading.value = true
    await AffiliateNetworkApi.syncCoupons({ code: 'admitad' })
    message.success('优惠同步任务已启动')
    await getList()
  } catch {
  } finally {
    syncCouponsLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
