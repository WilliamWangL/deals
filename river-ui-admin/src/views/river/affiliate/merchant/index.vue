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
      <el-form-item label="商家名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入商家名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="联盟网络" prop="networkId">
        <el-select
          v-model="queryParams.networkId"
          placeholder="请选择联盟网络"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="network in networkList"
            :key="network.id"
            :label="network.name"
            :value="network.id"
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
          v-hasPermi="['affiliate:merchant:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['affiliate:merchant:export']"
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
      <el-table-column label="商家名称" align="center" prop="name" />
      <el-table-column label="联盟网络" align="center" prop="networkId">
        <template #default="scope">
          {{ getNetworkName(scope.row.networkId) }}
        </template>
      </el-table-column>
      <el-table-column label="域名" align="center" prop="domain" :show-overflow-tooltip="true" />
      <el-table-column label="评级" align="center" prop="rating">
        <template #default="scope">
          <el-rate v-model="scope.row.rating" disabled show-score score-template="{value}" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['affiliate:merchant:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['affiliate:merchant:delete']"
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
  <MerchantForm ref="formRef" @success="getList" :network-list="networkList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { MerchantApi, MerchantVO, AffiliateNetworkApi } from '@/api/river/affiliate'
import MerchantForm from './MerchantForm.vue'

/** 商家 列表 */
defineOptions({ name: 'Merchant' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<MerchantVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const networkList = ref<any[]>([]) // 联盟网络列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  networkId: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await MerchantApi.getMerchantPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取联盟网络列表 */
const getNetworkList = async () => {
  networkList.value = await AffiliateNetworkApi.getAffiliateNetworkList()
}

/** 获取网络名称 */
const getNetworkName = (networkId: number) => {
  const network = networkList.value.find((n) => n.id === networkId)
  return network ? network.name : '-'
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
    await MerchantApi.deleteMerchant(id)
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
    const data = await MerchantApi.exportMerchant(queryParams)
    download.excel(data, '商家.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getNetworkList()
  getList()
})
</script>
