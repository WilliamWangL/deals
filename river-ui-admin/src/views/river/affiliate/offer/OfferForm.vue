<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible" width="900px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Offer名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入Offer名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联盟网络" prop="networkId">
            <el-select v-model="formData.networkId" placeholder="请选择联盟网络" class="!w-full" @change="handleNetworkChange">
              <el-option
                v-for="network in networkList"
                :key="network.id"
                :label="network.name"
                :value="network.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商家" prop="merchantId">
            <el-select v-model="formData.merchantId" placeholder="请选择商家" filterable class="!w-full">
              <el-option
                v-for="merchant in filteredMerchantList"
                :key="merchant.id"
                :label="merchant.name"
                :value="merchant.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联盟侧ID" prop="externalId">
            <el-input v-model="formData.externalId" placeholder="请输入联盟侧Offer ID" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="佣金类型" prop="commissionType">
            <el-select v-model="formData.commissionType" placeholder="请选择佣金类型" class="!w-full">
              <el-option label="CPA (按行动)" :value="1" />
              <el-option label="CPC (按点击)" :value="2" />
              <el-option label="CPS (按销售)" :value="3" />
              <el-option label="CPL (按潜在客户)" :value="4" />
              <el-option label="CPM (按千次展示)" :value="5" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="佣金数值" prop="commissionValue">
            <el-input-number v-model="formData.commissionValue" :precision="4" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="佣金货币" prop="currency">
            <el-select v-model="formData.currency" placeholder="请选择货币" class="!w-full">
              <el-option label="USD - 美元" value="USD" />
              <el-option label="EUR - 欧元" value="EUR" />
              <el-option label="GBP - 英镑" value="GBP" />
              <el-option label="CNY - 人民币" value="CNY" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Cookie有效期" prop="cookieDays">
            <el-input-number v-model="formData.cookieDays" :min="0" :max="365" class="!w-full" />
            <span class="ml-2">天</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="EPC" prop="epc">
            <el-input-number v-model="formData.epc" :precision="4" :min="0" class="!w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="转化率" prop="conversionRate">
            <el-input-number v-model="formData.conversionRate" :precision="4" :min="0" :max="1" :step="0.01" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="热度分数" prop="hotScore">
            <el-input-number v-model="formData.hotScore" :min="0" class="!w-full" />
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
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="推荐">
            <el-switch v-model="formData.featured" />
            <span class="ml-2 text-gray-500">设为推荐后将在首页展示</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="追踪链接模板" prop="trackingUrlTemplate">
        <el-input
          v-model="formData.trackingUrlTemplate"
          type="textarea"
          :rows="2"
          placeholder="请输入追踪链接模板，支持 {affiliate_id}、{offer_id} 等变量"
        />
      </el-form-item>
      <el-form-item label="落地页URL" prop="landingUrl">
        <el-input v-model="formData.landingUrl" placeholder="请输入落地页URL" />
      </el-form-item>
      <el-form-item label="图片URL" prop="imageUrl">
        <el-input v-model="formData.imageUrl" placeholder="请输入图片URL" />
      </el-form-item>
      <el-form-item label="Offer描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入Offer描述"
        />
      </el-form-item>
      <el-form-item label="支持地区" prop="regions">
        <el-input
          v-model="formData.regions"
          type="textarea"
          :rows="2"
          placeholder='JSON 数组格式，如: ["US", "UK", "CA"]'
        />
      </el-form-item>
      <el-form-item label="分类ID" prop="categoryIds">
        <el-input
          v-model="formData.categoryIds"
          type="textarea"
          :rows="2"
          placeholder='JSON 数组格式，如: [1, 2, 3]'
        />
      </el-form-item>
      <el-form-item label="标签" prop="tags">
        <el-input
          v-model="formData.tags"
          type="textarea"
          :rows="2"
          placeholder='JSON 数组格式，如: ["hot", "new", "mobile"]'
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
import { OfferApi, OfferVO, MerchantApi, AffiliateNetworkApi } from '@/api/river/affiliate'

/** Offer 表单 */
defineOptions({ name: 'OfferForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  merchantList: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  networkList: {
    type: Array as PropType<any[]>,
    default: () => []
  }
})

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const filteredMerchantList = ref<any[]>([]) // 根据网络筛选的商家列表

const formData = ref({
  id: undefined,
  merchantId: undefined,
  networkId: undefined,
  externalId: '',
  name: '',
  description: '',
  commissionType: 1,
  commissionValue: 0,
  currency: 'USD',
  cookieDays: 30,
  trackingUrlTemplate: '',
  landingUrl: '',
  status: 1,
  regions: '',
  categoryIds: '',
  tags: '',
  imageUrl: '',
  epc: 0,
  conversionRate: 0,
  featured: false,
  hotScore: 0
})
const formRules = reactive({
  name: [{ required: true, message: 'Offer名称不能为空', trigger: 'blur' }],
  merchantId: [{ required: true, message: '商家不能为空', trigger: 'change' }],
  networkId: [{ required: true, message: '联盟网络不能为空', trigger: 'change' }],
  commissionType: [{ required: true, message: '佣金类型不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 网络改变时筛选商家 */
const handleNetworkChange = (networkId: number) => {
  formData.value.merchantId = undefined
  if (networkId) {
    filteredMerchantList.value = props.merchantList.filter((m: any) => m.networkId === networkId)
  } else {
    filteredMerchantList.value = props.merchantList
  }
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  filteredMerchantList.value = props.merchantList
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await OfferApi.getOffer(id)
      // 根据网络筛选商家
      if (formData.value.networkId) {
        filteredMerchantList.value = props.merchantList.filter((m: any) => m.networkId === formData.value.networkId)
      }
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
    const data = formData.value as unknown as OfferVO
    if (formType.value === 'create') {
      await OfferApi.createOffer(data)
      message.success(t('common.createSuccess'))
    } else {
      await OfferApi.updateOffer(data)
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
    networkId: undefined,
    externalId: '',
    name: '',
    description: '',
    commissionType: 1,
    commissionValue: 0,
    currency: 'USD',
    cookieDays: 30,
    trackingUrlTemplate: '',
    landingUrl: '',
    status: 1,
    regions: '',
    categoryIds: '',
    tags: '',
    imageUrl: '',
    epc: 0,
    conversionRate: 0,
    featured: false,
    hotScore: 0
  }
  formRef.value?.resetFields()
}
</script>
