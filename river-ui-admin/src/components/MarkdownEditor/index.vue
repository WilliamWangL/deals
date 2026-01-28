<template>
  <div class="markdown-editor-wrapper">
    <MdEditor
      v-model="content"
      :language="language"
      :theme="isDark ? 'dark' : 'light'"
      :preview="preview"
      :toolbars="toolbars"
      :style="{ height }"
      @on-upload-img="handleUploadImg"
      @on-change="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { MdEditor, ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { getAccessToken, getTenantId } from '@/utils/auth'

defineOptions({ name: 'MarkdownEditor' })

const message = useMessage() // 消息弹窗

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '500px'
  },
  preview: {
    type: Boolean,
    default: true
  },
  language: {
    type: String as PropType<'zh-CN' | 'en-US'>,
    default: 'zh-CN'
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 双向绑定
const content = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 主题
const isDark = computed(() => {
  return document.documentElement.classList.contains('dark')
})

// 工具栏配置
const toolbars: ToolbarNames[] = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  '-',
  'revoke',
  'next',
  '=',
  'preview',
  'fullscreen'
]

// 图片上传处理
const handleUploadImg = async (
  files: File[],
  callback: (urls: string[]) => void
) => {
  const results: string[] = []

  for (const file of files) {
    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/infra/file/upload`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${getAccessToken()}`,
            'tenant-id': String(getTenantId())
          },
          body: formData
        }
      )

      const data = await response.json()
      if (data.code === 0 && data.data) {
        results.push(data.data)
      } else {
        console.error('Upload API error:', data.msg || data.message)
        message.error(`图片上传失败: ${data.msg || '未知错误'}`)
      }
    } catch (error) {
      console.error('Upload failed:', error)
      message.error('图片上传失败，请重试')
    }
  }

  callback(results)
}

// 内容变化回调
const handleChange = (val: string) => {
  emit('change', val)
}
</script>

<style scoped>
.markdown-editor-wrapper {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
}

.markdown-editor-wrapper :deep(.md-editor) {
  --md-bk-color: var(--el-bg-color);
}
</style>
