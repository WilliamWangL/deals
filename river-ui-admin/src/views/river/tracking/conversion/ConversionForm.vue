<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="140px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="网络代码" prop="networkCode">
            <el-input v-model="formData.networkCode" placeholder="请输入网络代码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="外部转化ID" prop="externalConversionId">
            <el-input v-model="formData.externalConversionId" placeholder="请输入外部转化ID" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="转化类型" prop="conversionType">
            <el-select v-model="formData.conversionType" placeholder="请选择转化类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CONVERSION_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.CONVERSION_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="佣金" prop="commission">
            <el-input-number v-model="formData.commission" :precision="4" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="货币" prop="currency">
            <el-select v-model="formData.currency" placeholder="请选择货币" class="!w-full">
              <el-option label="USD" value="USD" />
              <el-option label="EUR" value="EUR" />
              <el-option label="GBP" value="GBP" />
              <el-option label="CNY" value="CNY" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="点击ID" prop="clickId">
        <el-input v-model="formData.clickId" placeholder="请输入关联的点击ID（可选）" />
      </el-form-item>
      <el-form-item label="转化时间" prop="conversionTime">
        <el-date-picker
          v-model="formData.conversionTime"
          type="datetime"
          placeholder="选择转化时间"
          value-format="x"
          class="!w-full"
        />
      </el-form-item>
      <el-form-item label="网络负载数据" prop="networkPayload">
        <el-input
          v-model="formData.networkPayload"
          type="textarea"
          :rows="4"
          placeholder="请输入网络负载数据（JSON格式）"
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
import { ConversionApi, ConversionVO } from '@/api/river/tracking'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 转化记录表单 */
defineOptions({ name: 'ConversionForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改

const formData = ref({
  id: undefined,
  clickId: '',
  networkCode: '',
  externalConversionId: '',
  conversionType: 2,
  commission: 0,
  currency: 'USD',
  status: 0,
  networkPayload: '',
  conversionTime: undefined
})
const formRules = reactive({
  networkCode: [{ required: true, message: '网络代码不能为空', trigger: 'blur' }],
  externalConversionId: [{ required: true, message: '外部转化ID不能为空', trigger: 'blur' }],
  conversionType: [{ required: true, message: '转化类型不能为空', trigger: 'change' }],
  commission: [{ required: true, message: '佣金不能为空', trigger: 'change' }],
  currency: [{ required: true, message: '货币不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }],
  conversionTime: [{ required: true, message: '转化时间不能为空', trigger: 'change' }]
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
      formData.value = await ConversionApi.getConversion(id)
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
    const data = formData.value as unknown as ConversionVO
    if (formType.value === 'create') {
      await ConversionApi.createConversion(data)
      message.success(t('common.createSuccess'))
    } else {
      await ConversionApi.updateConversion(data)
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
    clickId: '',
    networkCode: '',
    externalConversionId: '',
    conversionType: 2,
    commission: 0,
    currency: 'USD',
    status: 0,
    networkPayload: '',
    conversionTime: undefined
  }
  formRef.value?.resetFields()
}
</script>
