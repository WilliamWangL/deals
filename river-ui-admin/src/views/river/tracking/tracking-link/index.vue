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
      <el-form-item label="Offer" prop="offerId">
        <el-select
          v-model="queryParams.offerId"
          placeholder="请选择Offer"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option
            v-for="offer in offerList"
            :key="offer.id"
            :label="offer.name"
            :value="offer.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Slug" prop="slug">
        <el-input
          v-model="queryParams.slug"
          placeholder="请输入Slug"
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
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
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
          v-hasPermi="['tracking:link:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['tracking:link:export']"
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
      <el-table-column label="Slug" prop="slug" width="150">
        <template #default="scope">
          <el-tag type="primary" size="small">{{ scope.row.slug }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Offer" prop="offerId" width="200">
        <template #default="scope">
          {{ getOfferName(scope.row.offerId) }}
        </template>
      </el-table-column>
      <el-table-column label="预设Sub1" prop="presetSub1" width="120" show-overflow-tooltip />
      <el-table-column label="预设Sub2" prop="presetSub2" width="120" show-overflow-tooltip />
      <el-table-column label="预设Sub3" prop="presetSub3" width="120" show-overflow-tooltip />
      <el-table-column label="预设Sub4" prop="presetSub4" width="120" show-overflow-tooltip />
      <el-table-column label="预设Sub5" prop="presetSub5" width="120" show-overflow-tooltip />
      <el-table-column label="UTM参数" prop="utmParams" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.utmParams" class="text-gray-500">{{ truncateUtm(scope.row.utmParams) }}</span>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
          <el-tag v-else type="info">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="copyLink(scope.row)"
          >
            复制链接
          </el-button>
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['tracking:link:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['tracking:link:delete']"
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
  <TrackingLinkForm ref="formRef" @success="getList" :offer-list="offerList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { TrackingLinkApi, TrackingLinkVO } from '@/api/river/tracking'
import TrackingLinkForm from './TrackingLinkForm.vue'

defineOptions({ name: 'TrackingLink' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const offerList = ref([]) // Offer列表

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  offerId: undefined,
  slug: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 获取Offer名称 */
const getOfferName = (id: number) => {
  const offer = offerList.value.find((o) => o.id === id)
  return offer?.name || `ID: ${id}`
}

/** 截断UTM参数显示 */
const truncateUtm = (utm: string) => {
  if (!utm || utm.length <= 30) return utm
  return utm.substring(0, 30) + '...'
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await TrackingLinkApi.getTrackingLinkPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取Offer列表 */
const getOfferList = async () => {
  // 这里需要调用 Offer API，暂时使用空数组
  // 实际应该从 affiliate 模块获取
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

/** 复制链接 */
const copyLink = async (row: TrackingLinkVO) => {
  const link = `${window.location.origin}/click/${row.slug}`
  try {
    await navigator.clipboard.writeText(link)
    message.success('链接已复制到剪贴板: ' + link)
  } catch {
    message.error('复制失败')
  }
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await TrackingLinkApi.deleteTrackingLink(id)
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
    const data = await TrackingLinkApi.exportTrackingLink(queryParams)
    download.excel(data, '追踪链接.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
  getOfferList()
})
</script>
