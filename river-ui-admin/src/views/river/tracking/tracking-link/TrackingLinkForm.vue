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
          <el-form-item label="目标类型" prop="targetType">
            <el-select v-model="formData.targetType" placeholder="请选择目标类型" class="!w-full" @change="handleTargetTypeChange">
              <el-option
                v-for="dict in targetTypeOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="目标ID" prop="targetId">
            <el-input-number v-model="formData.targetId" placeholder="请输入目标ID" :min="1" class="!w-full" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form-item label="Slug" prop="slug">
            <el-input v-model="formData.slug" placeholder="短链接标识" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
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
      </el-row>
      <el-divider content-position="left">UTM参数（可选）</el-divider>
      <el-form-item label="UTM参数" prop="utmParams">
        <el-input
          v-model="formData.utmParams"
          type="textarea"
          :rows="4"
          placeholder="请输入UTM参数（JSON格式），如: {&quotutm_source&quot;: &quotgoogle&quot, &quotutm_medium&quot;: &quotcpc&quot}"
        />
      </el-form-item>
      <el-alert
        title="目标类型说明：1=商家, 2=Offer, 3=Deal, 4=优惠券"
        type="info"
        :closable="false"
        class="mb-10px"
      />
      <el-alert
        title="生成的链接格式: https://yourdomain.com/go/{slug}?sub1={sub1}&sub2={sub2}..."
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
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

/** 追踪链接表单 */
defineOptions({ name: 'TrackingLinkForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

// 目标类型选项
const targetTypeOptions = [
  { value: 1, label: '商家' },
  { value: 2, label: 'Offer' },
  { value: 3, label: 'Deal' },
  { value: 4, label: '优惠券' }
]

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const formType = ref('') // 表单的类型：create - 新增；update - 修改

const formData = ref({
  id: undefined,
  targetType: 2 as number,  // 默认 Offer
  targetId: undefined as number | undefined,
  slug: '',
  presetSub1: '',
  presetSub2: '',
  presetSub3: '',
  presetSub4: '',
  presetSub5: '',
  utmParams: '',
  status: 0
})
const formRules = reactive({
  targetType: [{ required: true, message: '目标类型不能为空', trigger: 'change' }],
  targetId: [{ required: true, message: '目标ID不能为空', trigger: 'blur' }],
  slug: [{ required: true, message: 'Slug不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'change' }]
})
const formRef = ref() // 表单 Ref

/** 目标类型变化时清空目标ID */
const handleTargetTypeChange = () => {
  formData.value.targetId = undefined
}

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
    targetType: 2,  // 默认 Offer
    targetId: undefined,
    slug: '',
    presetSub1: '',
    presetSub2: '',
    presetSub3: '',
    presetSub4: '',
    presetSub5: '',
    utmParams: '',
    status: 0
  }
  formRef.value?.resetFields()
}
</script>
