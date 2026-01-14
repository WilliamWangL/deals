<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="800">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item label="文章标题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入文章标题" />
      </el-form-item>
      <el-form-item label="Slug" prop="slug">
        <el-input v-model="formData.slug" placeholder="请输入 Slug" />
      </el-form-item>
      <el-form-item label="文章内容" prop="content">
        <Editor v-model="formData.content" height="300px" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input-number v-model="formData.type" placeholder="请输入类型" :min="0" />
      </el-form-item>
      <el-form-item label="发布时间" prop="publishedAt">
        <el-date-picker
          v-model="formData.publishedAt"
          type="datetime"
          placeholder="请选择发布时间"
          value-format="x"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" clearable placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="Number(dict.value)"
            :label="dict.label"
            :value="Number(dict.value)"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { CommonStatusEnum } from '@/utils/constants'
import * as PostApi from '@/api/river/blog'

defineOptions({ name: 'PostForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref<Partial<PostApi.PostVO>>({
  id: undefined,
  title: '',
  slug: '',
  content: '',
  type: 0,
  status: CommonStatusEnum.ENABLE,
  publishedAt: undefined
})
const formRules = reactive({
  title: [{ required: true, message: '文章标题不能为空', trigger: 'blur' }],
  slug: [{ required: true, message: 'Slug 不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '文章内容不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref()

const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  if (id) {
    formLoading.value = true
    try {
      formData.value = await PostApi.getPost(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])
const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return
  formLoading.value = true
  try {
    const data = formData.value as PostApi.PostVO
    if (formType.value === 'create') {
      await PostApi.createPost(data)
      message.success(t('common.createSuccess'))
    } else {
      await PostApi.updatePost(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = () => {
  formData.value = {
    id: undefined,
    title: '',
    slug: '',
    content: '',
    type: 0,
    status: CommonStatusEnum.ENABLE,
    publishedAt: undefined
  }
  formRef.value?.resetFields()
}
</script>
