<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联盟编码" prop="code">
            <el-input v-model="formData.code" placeholder="请输入联盟编码，如: admitad" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联盟名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入联盟名称" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联盟类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择联盟类型" class="!w-full">
              <el-option label="CPA 网络" :value="1" />
              <el-option label="联盟营销" :value="2" />
              <el-option label="其他" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态" class="!w-full">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="API 基础地址" prop="apiBaseUrl">
        <el-input v-model="formData.apiBaseUrl" placeholder="请输入 API 基础地址，如: https://api.admitad.com" />
      </el-form-item>
      <el-form-item label="联盟官网" prop="websiteUrl">
        <el-input v-model="formData.websiteUrl" placeholder="请输入联盟官网" />
      </el-form-item>
      <el-form-item label="Logo URL" prop="logoUrl">
        <el-input v-model="formData.logoUrl" placeholder="请输入 Logo URL" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入联盟描述"
        />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="2"
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { AffiliateNetworkApi, AffiliateNetworkVO } from '@/api/river/affiliate'

/** 联盟网络 表单 */
defineOptions({ name: 'AffiliateNetworkForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  code: '',
  name: '',
  type: 1,
  apiBaseUrl: '',
  status: 1,
  websiteUrl: '',
  logoUrl: '',
  description: '',
  remark: ''
})
const formRules = reactive({
  code: [{ required: true, message: '联盟编码不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '联盟名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '联盟类型不能为空', trigger: 'change' }],
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
      formData.value = await AffiliateNetworkApi.getAffiliateNetwork(id)
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
    const data = formData.value as unknown as AffiliateNetworkVO
    if (formType.value === 'create') {
      await AffiliateNetworkApi.createAffiliateNetwork(data)
      message.success(t('common.createSuccess'))
    } else {
      await AffiliateNetworkApi.updateAffiliateNetwork(data)
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
    code: '',
    name: '',
    type: 1,
    apiBaseUrl: '',
    status: 1,
    websiteUrl: '',
    logoUrl: '',
    description: '',
    remark: ''
  }
  formRef.value?.resetFields()
}
</script>
