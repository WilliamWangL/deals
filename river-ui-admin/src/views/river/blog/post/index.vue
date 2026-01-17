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
          placeholder="请输入文章标题"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="作者" prop="authorId">
        <el-select
          v-model="queryParams.authorId"
          placeholder="请选择作者"
          clearable
          filterable
          class="!w-240px"
        >
          <el-option
            v-for="author in authorList"
            :key="author.id"
            :label="author.name"
            :value="author.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable class="!w-240px">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BLOG_POST_TYPE)"
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
            v-for="dict in getIntDictOptions(DICT_TYPE.BLOG_POST_STATUS)"
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
          v-hasPermi="['blog:post:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
          v-hasPermi="['blog:post:export']"
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
      <el-table-column label="封面" prop="coverImage" width="100">
        <template #default="scope">
          <el-image
            v-if="scope.row.coverImage"
            :src="scope.row.coverImage"
            fit="cover"
            style="width: 60px; height: 40px; border-radius: 4px"
          />
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip />
      <el-table-column label="作者" prop="authorId" width="120">
        <template #default="scope">
          {{ getAuthorName(scope.row.authorId) }}
        </template>
      </el-table-column>
      <el-table-column label="类型" prop="type" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BLOG_POST_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BLOG_POST_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="推荐" prop="featured" width="80" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.featured" type="danger" size="small">推荐</el-tag>
          <span v-else class="text-gray-400">-</span>
        </template>
      </el-table-column>
      <el-table-column label="浏览" prop="viewCount" width="80" align="center">
        <template #default="scope">
          <span class="text-gray-600">{{ scope.row.viewCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="发布时间"
        prop="publishedAt"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column
        label="创建时间"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['blog:post:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['blog:post:delete']"
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
  <PostForm ref="formRef" @success="getList" :author-list="authorList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import { PostApi, PostVO, AuthorApi } from '@/api/river/blog'
import PostForm from './PostForm.vue'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

defineOptions({ name: 'BlogPost' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const authorList = ref([]) // 作者列表

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  title: undefined,
  authorId: undefined,
  type: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 获取作者名称 */
const getAuthorName = (authorId: number) => {
  const author = authorList.value.find((a) => a.id === authorId)
  return author?.name || '-'
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PostApi.getPostPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取作者列表 */
const getAuthorList = async () => {
  authorList.value = await AuthorApi.getAuthorList()
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
    await PostApi.deletePost(id)
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
    const data = await PostApi.exportPost(queryParams)
    download.excel(data, '文章列表.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
  getAuthorList()
})
</script>
