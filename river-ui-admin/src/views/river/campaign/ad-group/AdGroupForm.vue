<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-form-item label="广告组名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入广告组名称" />
      </el-form-item>
      <el-form-item label="所属活动" prop="campaignId">
        <el-select v-model="formData.campaignId" placeholder="请选择营销活动" class="!w-full">
          <el-option
            v-for="campaign in campaignList"
            :key="campaign.id"
            :label="campaign.name"
            :value="campaign.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="出价策略" prop="bidStrategy">
        <el-select v-model="formData.bidStrategy" placeholder="请选择出价策略" clearable class="!w-full">
          <el-option label="最低成本" value="lowest_cost" />
          <el-option label="目标成本" value="target_cost" />
          <el-option label="最大化点击" value="max_clicks" />
          <el-option label="最大化转化" value="max_conversions" />
        </el-select>
      </el-form-item>
      <el-form-item label="定向条件" prop="targeting">
        <el-input
          v-model="formData.targeting"
          type="textarea"
          :rows="4"
          placeholder="请输入定向条件（JSON格式）"
        />
      </el-form-item>
      <el-form-item label="外部广告组ID" prop="externalAdGroupId">
        <el-input v-model="formData.externalAdGroupId" placeholder="请输入外部广告组ID（可选）" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :value="dict.value"
            >{{ dict.label }}</el-radio
          >
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
import { AdGroupApi, AdGroupVO } from '@/api/river/campaign'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 广告组表单 */
defineOptions({ name: 'AdGroupForm' })

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

const formData = ref({
  id: undefined,
  campaignId: undefined,
  name: '',
  targeting: '',
  bidStrategy: '',
  externalAdGroupId: '',
  status: 0
})
const formRules = reactive({
  name: [{ required: true, message: '广告组名称不能为空', trigger: 'blur' }],
  campaignId: [{ required: true, message: '所属活动不能为空', trigger: 'change' }],
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
      formData.value = await AdGroupApi.getAdGroup(id)
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
    const data = formData.value as unknown as AdGroupVO
    if (formType.value === 'create') {
      await AdGroupApi.createAdGroup(data)
      message.success(t('common.createSuccess'))
    } else {
      await AdGroupApi.updateAdGroup(data)
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
    name: '',
    targeting: '',
    bidStrategy: '',
    externalAdGroupId: '',
    status: 0
  }
  formRef.value?.resetFields()
}
</script>
