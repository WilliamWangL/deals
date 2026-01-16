<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="900px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form-item label="标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入文章标题" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择类型" class="!w-full">
              <el-option label="文章" :value="1" />
              <el-option label="页面" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Slug" prop="slug">
            <el-input v-model="formData.slug" placeholder="请输入URL友好标识" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="作者" prop="authorId">
            <el-select v-model="formData.authorId" placeholder="请选择作者" filterable class="!w-full">
              <el-option
                v-for="author in authorList"
                :key="author.id"
                :label="author.name"
                :value="author.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态" class="!w-full">
              <el-option label="草稿" :value="0" />
              <el-option label="已发布" :value="1" />
              <el-option label="已下线" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发布时间" prop="publishedAt">
            <el-date-picker
              v-model="formData.publishedAt"
              type="datetime"
              placeholder="选择发布时间"
              value-format="x"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="封面图片" prop="coverImage">
        <el-input v-model="formData.coverImage" placeholder="请输入封面图片URL" />
      </el-form-item>
      <el-form-item label="摘要" prop="excerpt">
        <el-input
          v-model="formData.excerpt"
          type="textarea"
          :rows="3"
          placeholder="请输入文章摘要"
        />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          v-model="formData.content"
          type="textarea"
          :rows="10"
          placeholder="请输入文章内容（支持Markdown）"
        />
      </el-form-item>
      <el-divider content-position="left">SEO设置</el-divider>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="SEO标题" prop="metaTitle">
            <el-input v-model="formData.metaTitle" placeholder="请输入SEO标题（可选）" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="SEO描述" prop="metaDescription">
            <el-input
              v-model="formData.metaDescription"
              type="textarea"
              :rows="2"
              placeholder="请输入SEO描述（可选）"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="规范URL" prop="canonicalUrl">
            <el-input v-model="formData.canonicalUrl" placeholder="请输入规范URL（可选）" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="推荐">
        <el-switch v-model="formData.featured" />
        <span class="ml-2 text-gray-500">设为推荐后将在首页展示</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { PostApi, PostVO } from '@/api/river/blog'

/** 文章表单 */
defineOptions({ name: 'PostForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  authorList: {
    type: Array as PropType<any[]>,
    default: () => []
  }
})

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改

const formData = ref({
  id: undefined,
  authorId: undefined,
  title: '',
  slug: '',
  content: '',
  excerpt: '',
  coverImage: '',
  type: 1,
  status: 0,
  publishedAt: undefined,
  metaTitle: '',
  metaDescription: '',
  canonicalUrl: '',
  viewCount: 0,
  featured: false
})
const formRules = reactive({
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  authorId: [{ required: true, message: '作者不能为空', trigger: 'change' }],
  type: [{ required: true, message: '类型不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await PostApi.getPost(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as PostVO
    if (formType.value === 'create') {
      await PostApi.createPost(data)
      message.success(t('common.createSuccess'))
    } else {
      await PostApi.updatePost(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    authorId: undefined,
    title: '',
    slug: '',
    content: '',
    excerpt: '',
    coverImage: '',
    type: 1,
    status: 0,
    publishedAt: undefined,
    metaTitle: '',
    metaDescription: '',
    canonicalUrl: '',
    viewCount: 0,
    featured: false
  }
  formRef.value?.resetFields()
}
</script>
