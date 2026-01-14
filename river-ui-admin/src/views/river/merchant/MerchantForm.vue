<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="600">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item label="商家名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入商家名称" />
      </el-form-item>
      <el-form-item label="Slug" prop="slug">
        <el-input v-model="formData.slug" placeholder="请输入 Slug" />
      </el-form-item>
      <el-form-item label="域名" prop="domain">
        <el-input v-model="formData.domain" placeholder="请输入域名" />
      </el-form-item>
      <el-form-item label="Logo" prop="logo">
        <el-input v-model="formData.logo" placeholder="请输入 Logo URL" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="formData.description" placeholder="请输入描述" type="textarea" />
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
import * as MerchantApi from '@/api/river/merchant'

defineOptions({ name: 'MerchantForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref<Partial<MerchantApi.MerchantVO>>({
  id: undefined,
  name: '',
  slug: '',
  domain: '',
  logo: '',
  description: '',
  status: CommonStatusEnum.ENABLE
})
const formRules = reactive({
  name: [{ required: true, message: '商家名称不能为空', trigger: 'blur' }],
  slug: [
    { required: true, message: '请输入URL标识', trigger: 'blur' },
    {
      pattern: /^[a-z0-9-]+$/,
      message: 'URL标识只能包含小写字母、数字和连字符',
      trigger: 'blur'
    },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  domain: [
    { required: true, message: '请输入域名', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9][a-zA-Z0-9-]*\.[a-zA-Z]{2,}$/,
      message: '请输入有效域名格式（如 example.com）',
      trigger: 'blur'
    }
  ],
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
      formData.value = await MerchantApi.getMerchant(id)
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
    const data = formData.value as MerchantApi.MerchantVO
    if (formType.value === 'create') {
      await MerchantApi.createMerchant(data)
      message.success(t('common.createSuccess'))
    } else {
      await MerchantApi.updateMerchant(data)
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
    name: '',
    slug: '',
    domain: '',
    logo: '',
    description: '',
    status: CommonStatusEnum.ENABLE
  }
  formRef.value?.resetFields()
}
</script>
