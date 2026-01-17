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
          <el-form-item label="标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入Deal标题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Slug" prop="slug">
            <el-input v-model="formData.slug" placeholder="请输入URL友好标识" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
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
        <el-col :span="8">
          <el-form-item label="原价" prop="originalPrice">
            <el-input-number v-model="formData.originalPrice" :precision="2" :min="0" class="!w-full" />
            <span class="ml-2 text-gray-500">$</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="优惠价" prop="dealPrice">
            <el-input-number v-model="formData.dealPrice" :precision="2" :min="0" class="!w-full" />
            <span class="ml-2 text-gray-500">$</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="折扣%" prop="discountPercent">
            <el-input-number v-model="formData.discountPercent" :min="0" :max="100" class="!w-full" />
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
          <el-form-item label="库存限制" prop="stockLimit">
            <el-input-number v-model="formData.stockLimit" :min="0" class="!w-full" />
            <span class="ml-2 text-gray-500">件</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="热度分数" prop="hotScore">
            <el-input-number v-model="formData.hotScore" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="推荐">
            <el-switch v-model="formData.featured" />
            <span class="ml-2 text-gray-500">设为推荐后将在首页展示</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="图片URL" prop="imageUrl">
        <el-input v-model="formData.imageUrl" placeholder="请输入图片URL" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入Deal描述"
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
import { DealApi, DealVO } from '@/api/river/coupon'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** Deal 表单 */
defineOptions({ name: 'DealForm' })

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

const formData = ref({
  id: undefined,
  merchantId: undefined,
  offerId: undefined,
  title: '',
  slug: '',
  description: '',
  originalPrice: 0,
  dealPrice: 0,
  discountPercent: 0,
  startTime: undefined,
  endTime: undefined,
  stockLimit: undefined,
  imageUrl: '',
  hotScore: 0,
  featured: false,
  status: 1
})
const formRules = reactive({
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  merchantId: [{ required: true, message: '商家不能为空', trigger: 'change' }],
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
      formData.value = await DealApi.getDeal(id)
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
    const data = formData.value as unknown as DealVO
    if (formType.value === 'create') {
      await DealApi.createDeal(data)
      message.success(t('common.createSuccess'))
    } else {
      await DealApi.updateDeal(data)
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
    title: '',
    slug: '',
    description: '',
    originalPrice: 0,
    dealPrice: 0,
    discountPercent: 0,
    startTime: undefined,
    endTime: undefined,
    stockLimit: undefined,
    imageUrl: '',
    hotScore: 0,
    featured: false,
    status: 0
  }
  formRef.value?.resetFields()
}
</script>
