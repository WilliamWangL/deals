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
          <el-form-item label="优惠码" prop="code">
            <el-input v-model="formData.code" placeholder="请输入优惠码，如: SAVE20" uppercase />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商家" prop="merchantId">
            <el-select v-model="formData.merchantId" placeholder="请选择商家" filterable class="!w-full">
              <el-option
                v-for="merchant in merchantList"
                :key="merchant.id"
                :label="merchant.name"
                :value="merchant.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="折扣类型" prop="discountType">
            <el-select v-model="formData.discountType" placeholder="请选择折扣类型" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.COUPON_DISCOUNT_TYPE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="折扣值" prop="discountValue">
            <el-input-number
              v-model="formData.discountValue"
              :precision="2"
              :min="0"
              :max="discountType === 1 ? 100 : 9999"
              class="!w-full"
            />
            <span class="ml-2 text-gray-500">{{ discountType === 1 ? '%' : '$' }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="最低消费" prop="minPurchase">
            <el-input-number v-model="formData.minPurchase" :precision="2" :min="0" class="!w-full" />
            <span class="ml-2 text-gray-500">$</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
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
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="formData.startTime"
              type="datetime"
              placeholder="选择开始时间"
              value-format="x"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="formData.endTime"
              type="datetime"
              placeholder="选择结束时间"
              value-format="x"
              class="!w-full"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="来源" prop="source">
            <el-select v-model="formData.source" placeholder="请选择来源" class="!w-full">
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.COUPON_SOURCE)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="热度分数" prop="hotScore">
            <el-input-number v-model="formData.hotScore" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="已验证">
        <el-switch v-model="formData.verified" />
        <span class="ml-2 text-gray-500">标记为已验证的优惠码</span>
      </el-form-item>
      <el-form-item label="使用条款" prop="terms">
        <el-input
          v-model="formData.terms"
          type="textarea"
          :rows="3"
          placeholder="请输入使用条款和限制条件"
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
import { CouponApi, CouponVO } from '@/api/river/coupon'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 优惠券 表单 */
defineOptions({ name: 'CouponForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  merchantList: {
    type: Array as PropType<any[]>,
    default: () => []
  }
})

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const discountType = ref(1)

const formData = ref({
  id: undefined,
  merchantId: undefined,
  offerId: undefined,
  code: '',
  discountType: 1,
  discountValue: 0,
  minPurchase: 0,
  startTime: undefined,
  endTime: undefined,
  terms: '',
  source: 2,
  verified: false,
  hotScore: 0,
  status: 1
})
const formRules = reactive({
  code: [{ required: true, message: '优惠码不能为空', trigger: 'blur' }],
  merchantId: [{ required: true, message: '商家不能为空', trigger: 'change' }],
  discountType: [{ required: true, message: '折扣类型不能为空', trigger: 'change' }],
  discountValue: [{ required: true, message: '折扣值不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

watch(() => formData.value.discountType, (val) => {
  discountType.value = val
})

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
      formData.value = await CouponApi.getCoupon(id)
      discountType.value = formData.value.discountType
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
    const data = formData.value as unknown as CouponVO
    if (formType.value === 'create') {
      await CouponApi.createCoupon(data)
      message.success(t('common.createSuccess'))
    } else {
      await CouponApi.updateCoupon(data)
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
    merchantId: undefined,
    offerId: undefined,
    code: '',
    discountType: 1,
    discountValue: 0,
    minPurchase: 0,
    startTime: undefined,
    endTime: undefined,
    terms: '',
    source: 2,
    verified: false,
    hotScore: 0,
    status: 0
  }
  formRef.value?.resetFields()
}
</script>
