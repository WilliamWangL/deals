<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="营销活动" prop="campaignId">
            <el-select v-model="formData.campaignId" placeholder="请选择营销活动" class="!w-full" @change="onCampaignChange">
              <el-option
                v-for="campaign in campaignList"
                :key="campaign.id"
                :label="campaign.name"
                :value="campaign.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="广告组" prop="adGroupId">
            <el-select v-model="formData.adGroupId" placeholder="请选择广告组" clearable class="!w-full">
              <el-option
                v-for="group in filteredAdGroups"
                :key="group.id"
                :label="group.name"
                :value="group.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="日期" prop="date">
            <el-date-picker
              v-model="formData.date"
              type="date"
              placeholder="选择日期"
              value-format="x"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="货币" prop="currency">
            <el-select v-model="formData.currency" placeholder="请选择货币" class="!w-full">
              <el-option label="USD" value="USD" />
              <el-option label="EUR" value="EUR" />
              <el-option label="GBP" value="GBP" />
              <el-option label="CNY" value="CNY" />
              <el-option label="JPY" value="JPY" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="展示量" prop="impressions">
            <el-input-number v-model="formData.impressions" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="点击量" prop="clicks">
            <el-input-number v-model="formData.clicks" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="成本" prop="cost">
            <el-input-number v-model="formData.cost" :precision="4" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="来源" prop="source">
        <el-radio-group v-model="formData.source">
          <el-radio :value="1">API</el-radio>
          <el-radio :value="2">手动录入</el-radio>
          <el-radio :value="3">导入</el-radio>
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
import { CostRecordApi, CostRecordVO, AdGroupApi } from '@/api/river/campaign'

/** 成本记录表单 */
defineOptions({ name: 'CostRecordForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  campaignList: {
    type: Array as PropType<any[]>,
    default: () => []
  }
})

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改

const adGroupList = ref([]) // 广告组列表

const formData = ref({
  id: undefined,
  campaignId: undefined,
  adGroupId: undefined,
  date: undefined,
  impressions: 0,
  clicks: 0,
  cost: 0,
  currency: 'USD',
  source: 2
})

// 根据选中的活动筛选广告组
const filteredAdGroups = computed(() => {
  if (!formData.value.campaignId) return []
  return adGroupList.value.filter((g) => g.campaignId === formData.value.campaignId)
})

const formRules = reactive({
  campaignId: [{ required: true, message: '营销活动不能为空', trigger: 'change' }],
  date: [{ required: true, message: '日期不能为空', trigger: 'change' }],
  currency: [{ required: true, message: '货币不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 获取广告组列表 */
const getAdGroupList = async () => {
  const data = await AdGroupApi.getAdGroupPage({ pageNo: 1, pageSize: 1000 })
  adGroupList.value = data.list
}

/** 活动变化时清空广告组 */
const onCampaignChange = () => {
  formData.value.adGroupId = undefined
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  await getAdGroupList()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await CostRecordApi.getCostRecord(id)
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
    const data = formData.value as unknown as CostRecordVO
    if (formType.value === 'create') {
      await CostRecordApi.createCostRecord(data)
      message.success(t('common.createSuccess'))
    } else {
      await CostRecordApi.updateCostRecord(data)
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
    campaignId: undefined,
    adGroupId: undefined,
    date: undefined,
    impressions: 0,
    clicks: 0,
    cost: 0,
    currency: 'USD',
    source: 2
  }
  formRef.value?.resetFields()
}
</script>
