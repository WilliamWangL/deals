<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="600">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item label="商家ID" prop="merchantId">
        <el-input-number v-model="formData.merchantId" placeholder="请输入商家ID" :min="1" />
      </el-form-item>
      <el-form-item label="优惠码" prop="code">
        <el-input v-model="formData.code" placeholder="请输入优惠码" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="formData.description" placeholder="请输入描述" type="textarea" />
      </el-form-item>
      <el-form-item label="折扣类型" prop="discountType">
        <el-input-number v-model="formData.discountType" placeholder="请输入折扣类型" :min="0" />
      </el-form-item>
      <el-form-item label="折扣值" prop="discountValue">
        <el-input-number v-model="formData.discountValue" placeholder="请输入折扣值" :precision="2" :min="0" />
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
import * as CouponApi from '@/api/river/coupon'

defineOptions({ name: 'CouponForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  merchantId: undefined,
  code: '',
  description: '',
  discountType: 0,
  discountValue: 0,
  status: CommonStatusEnum.ENABLE
})
const formRules = reactive({
  merchantId: [{ required: true, message: '商家ID不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '优惠码不能为空', trigger: 'blur' }],
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
      formData.value = await CouponApi.getCoupon(id)
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
    const data = formData.value as unknown as CouponApi.CouponVO
    if (formType.value === 'create') {
      await CouponApi.createCoupon(data)
      message.success(t('common.createSuccess'))
    } else {
      await CouponApi.updateCoupon(data)
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
    merchantId: undefined,
    code: '',
    description: '',
    discountType: 0,
    discountValue: 0,
    status: CommonStatusEnum.ENABLE
  }
  formRef.value?.resetFields()
}
</script>
