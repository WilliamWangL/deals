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
          <el-form-item label="活动名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入活动名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="流量来源" prop="trafficSourceId">
            <el-select v-model="formData.trafficSourceId" placeholder="请选择流量来源" class="!w-full">
              <el-option
                v-for="source in trafficSourceList"
                :key="source.id"
                :label="source.name"
                :value="source.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择类型" class="!w-full">
              <el-option label="搜索" :value="1" />
              <el-option label="社交" :value="2" />
              <el-option label="展示" :value="3" />
              <el-option label="原生" :value="4" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="落地页" prop="landingPageId">
            <el-select v-model="formData.landingPageId" placeholder="请选择落地页" clearable class="!w-full">
              <el-option
                v-for="page in landingPageList"
                :key="page.id"
                :label="page.name"
                :value="page.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="日预算" prop="budgetDaily">
            <el-input-number v-model="formData.budgetDaily" :precision="2" :min="0" class="!w-full" />
            <span class="ml-2 text-gray-500">USD</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="总预算" prop="budgetTotal">
            <el-input-number v-model="formData.budgetTotal" :precision="2" :min="0" class="!w-full" />
            <span class="ml-2 text-gray-500">USD</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="关联Offer" prop="offerIds">
        <el-input v-model="formData.offerIds" placeholder="请输入关联的Offer ID，多个用逗号分隔" />
      </el-form-item>
      <el-form-item label="外部活动ID" prop="externalCampaignId">
        <el-input v-model="formData.externalCampaignId" placeholder="请输入外部活动ID（可选）" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">暂停</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { CampaignApi, CampaignVO } from '@/api/river/campaign'

/** 营销活动表单 */
defineOptions({ name: 'CampaignForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  trafficSourceList: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  landingPageList: {
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
  trafficSourceId: undefined,
  name: '',
  type: 1,
  offerIds: '',
  landingPageId: undefined,
  budgetDaily: undefined,
  budgetTotal: undefined,
  externalCampaignId: '',
  status: 1
})
const formRules = reactive({
  name: [{ required: true, message: '活动名称不能为空', trigger: 'blur' }],
  trafficSourceId: [{ required: true, message: '流量来源不能为空', trigger: 'change' }],
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
      formData.value = await CampaignApi.getCampaign(id)
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
    const data = formData.value as unknown as CampaignVO
    if (formType.value === 'create') {
      await CampaignApi.createCampaign(data)
      message.success(t('common.createSuccess'))
    } else {
      await CampaignApi.updateCampaign(data)
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
    trafficSourceId: undefined,
    name: '',
    type: 1,
    offerIds: '',
    landingPageId: undefined,
    budgetDaily: undefined,
    budgetTotal: undefined,
    externalCampaignId: '',
    status: 1
  }
  formRef.value?.resetFields()
}
</script>
