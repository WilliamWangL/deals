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
        <el-col :span="16">
          <el-form-item label="Offer" prop="offerId">
            <el-select v-model="formData.offerId" placeholder="请选择Offer" filterable class="!w-full">
              <el-option
                v-for="offer in offerList"
                :key="offer.id"
                :label="offer.name"
                :value="offer.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="Slug" prop="slug">
            <el-input v-model="formData.slug" placeholder="短链接标识" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-divider content-position="left">预设Sub参数</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Sub1" prop="presetSub1">
            <el-input v-model="formData.presetSub1" placeholder="预设Sub1值" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Sub2" prop="presetSub2">
            <el-input v-model="formData.presetSub2" placeholder="预设Sub2值" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Sub3" prop="presetSub3">
            <el-input v-model="formData.presetSub3" placeholder="预设Sub3值" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Sub4" prop="presetSub4">
            <el-input v-model="formData.presetSub4" placeholder="预设Sub4值" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Sub5" prop="presetSub5">
            <el-input v-model="formData.presetSub5" placeholder="预设Sub5值" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-divider content-position="left">UTM参数（可选）</el-divider>
      <el-form-item label="UTM参数" prop="utmParams">
        <el-input
          v-model="formData.utmParams"
          type="textarea"
          :rows="4"
          placeholder="请输入UTM参数（JSON格式），如: {&quot;utm_source&quot;: &quot;google&quot;, &quot;utm_medium&quot;: &quot;cpc&quot;}"
        />
      </el-form-item>
      <el-alert
        title="生成的链接格式: https://yourdomain.com/click/{slug}?sub1={sub1}&sub2={sub2}..."
        type="info"
        :closable="false"
      />
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { TrackingLinkApi, TrackingLinkVO } from '@/api/river/tracking'

/** 追踪链接表单 */
defineOptions({ name: 'TrackingLinkForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps({
  offerList: {
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
  offerId: undefined,
  slug: '',
  presetSub1: '',
  presetSub2: '',
  presetSub3: '',
  presetSub4: '',
  presetSub5: '',
  utmParams: '',
  status: 1
})
const formRules = reactive({
  offerId: [{ required: true, message: 'Offer不能为空', trigger: 'change' }],
  slug: [{ required: true, message: 'Slug不能为空', trigger: 'blur' }],
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
      formData.value = await TrackingLinkApi.getTrackingLink(id)
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
    const data = formData.value as unknown as TrackingLinkVO
    if (formType.value === 'create') {
      await TrackingLinkApi.createTrackingLink(data)
      message.success(t('common.createSuccess'))
    } else {
      await TrackingLinkApi.updateTrackingLink(data)
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
    offerId: undefined,
    slug: '',
    presetSub1: '',
    presetSub2: '',
    presetSub3: '',
    presetSub4: '',
    presetSub5: '',
    utmParams: '',
    status: 1
  }
  formRef.value?.resetFields()
}
</script>
