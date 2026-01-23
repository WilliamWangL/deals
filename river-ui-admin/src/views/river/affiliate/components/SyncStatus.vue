<template>
  <el-card class="sync-status-card">
    <template #header>
      <div class="card-header">
        <span>同步状态</span>
        <el-button-group>
          <el-button :icon="Refresh" :loading="loading" @click="refreshStats">
            刷新
          </el-button>
          <el-button type="primary" :icon="Download" :loading="syncing" @click="triggerSync">
            立即同步
          </el-button>
        </el-button-group>
      </div>
    </template>

    <el-row :gutter="20">
      <el-col :span="8">
        <div class="stat-item">
          <div class="stat-label">上次同步</div>
          <div class="stat-value">{{ lastSyncTime || '从未同步' }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-item">
          <div class="stat-label">商家</div>
          <div class="stat-value">{{ stats.merchants || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-item">
          <div class="stat-label">优惠</div>
          <div class="stat-value">{{ stats.coupons || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <el-divider />

    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-item small">
          <div class="stat-label">商家</div>
          <div class="stat-value">{{ stats.merchants || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-item small">
          <div class="stat-label">优惠</div>
          <div class="stat-value">{{ stats.coupons || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-item small">
          <div class="stat-label">Deals</div>
          <div class="stat-value">{{ stats.deals || 0 }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-item small">
          <div class="stat-label">失败</div>
          <div class="stat-value text-danger">{{ stats.failed || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <el-tag :type="statusType" class="status-tag">
      {{ statusText }}
    </el-tag>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Download } from '@element-plus/icons-vue'
import request from '@/utils/request'

interface SyncStats {
  merchants: number
  offers: number
  coupons: number
  deals: number
  failed: number
  lastSyncTime: string
}

const loading = ref(false)
const syncing = ref(false)
const stats = ref<SyncStats>({
  merchants: 0,
  offers: 0,
  coupons: 0,
  deals: 0,
  failed: 0,
  lastSyncTime: ''
})

const statusType = computed(() => {
  if (syncing.value) return 'info'
  if (stats.value.failed > 0) return 'danger'
  return 'success'
})

const statusText = computed(() => {
  if (syncing.value) return '同步中...'
  if (stats.value.lastSyncTime) return '已同步'
  return '未同步'
})

const lastSyncTime = computed(() => {
  if (!stats.value.lastSyncTime) return null
  return new Date(stats.value.lastSyncTime).toLocaleString('zh-CN')
})

async function refreshStats() {
  loading.value = true
  try {
    // TODO: Replace with actual API call
    // const { data } = await request.get('/admin-api/affiliate/sync/stats')
    // stats.value = data
    
    // Mock data for now
    await new Promise(resolve => setTimeout(resolve, 500))
    stats.value = {
      merchants: 10,
      offers: 50,
      coupons: 30,
      deals: 20,
      failed: 0,
      lastSyncTime: new Date().toISOString()
    }
  } catch (error) {
    ElMessage.error('获取同步状态失败')
  } finally {
    loading.value = false
  }
}

async function triggerSync() {
  syncing.value = true
  try {
    // TODO: Replace with actual API call
    // const { data } = await request.post('/admin-api/affiliate/sync-all?code=admitad')
    // if (data.success) {
    //   ElMessage.success(data.message || '同步成功')
    //   await refreshStats()
    // }
    
    // Mock sync
    await new Promise(resolve => setTimeout(resolve, 2000))
    stats.value = {
      merchants: 10,
      offers: 50,
      coupons: 30,
      deals: 20,
      failed: 0,
      lastSyncTime: new Date().toISOString()
    }
    ElMessage.success('同步成功：10 商家, 50 优惠, 30 Deals')
  } catch (error) {
    ElMessage.error('同步失败')
  } finally {
    syncing.value = false
  }
}

onMounted(() => {
  refreshStats()
})
</script>

<style scoped>
.sync-status-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-item.small .stat-value {
  font-size: 16px;
}

.text-danger {
  color: #F56C6C;
}

.status-tag {
  margin-top: 10px;
}
</style>
