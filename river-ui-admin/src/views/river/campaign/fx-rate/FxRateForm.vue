<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="源货币" prop="fromCurrency">
            <el-select v-model="formData.fromCurrency" placeholder="请选择源货币" class="!w-full">
              <el-option
                v-for="currency in currencyList"
                :key="currency.code"
                :label="`${currency.code} - ${currency.name}`"
                :value="currency.code"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="目标货币" prop="toCurrency">
            <el-select v-model="formData.toCurrency" placeholder="请选择目标货币" class="!w-full">
              <el-option
                v-for="currency in currencyList"
                :key="currency.code"
                :label="`${currency.code} - ${currency.name}`"
                :value="currency.code"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="汇率" prop="rate">
            <el-input-number v-model="formData.rate" :precision="6" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="生效日期" prop="effectiveDate">
            <el-date-picker
              v-model="formData.effectiveDate"
              type="date"
              placeholder="选择生效日期"
              value-format="x"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-alert
        v-if="formData.fromCurrency && formData.toCurrency"
        :title="`1 ${formData.fromCurrency} = ${formData.rate || 0} ${formData.toCurrency}`"
        type="info"
        :closable="false"
        class="mb-4"
      />
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { FxRateApi, FxRateVO } from '@/api/river/campaign'

/** 汇率表单 */
defineOptions({ name: 'FxRateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  currencyList: {
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
  fromCurrency: '',
  toCurrency: '',
  rate: 0,
  effectiveDate: undefined
})
const formRules = reactive({
  fromCurrency: [{ required: true, message: '源货币不能为空', trigger: 'change' }],
  toCurrency: [{ required: true, message: '目标货币不能为空', trigger: 'change' }],
  rate: [{ required: true, message: '汇率不能为空', trigger: 'change' }],
  effectiveDate: [{ required: true, message: '生效日期不能为空', trigger: 'change' }]
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
      formData.value = await FxRateApi.getFxRate(id)
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
    const data = formData.value as unknown as FxRateVO
    if (formType.value === 'create') {
      await FxRateApi.createFxRate(data)
      message.success(t('common.createSuccess'))
    } else {
      await FxRateApi.updateFxRate(data)
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
    fromCurrency: '',
    toCurrency: '',
    rate: 0,
    effectiveDate: undefined
  }
  formRef.value?.resetFields()
}
</script>
