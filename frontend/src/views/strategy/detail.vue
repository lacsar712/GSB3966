<template>
  <div class="strategy-detail">
    <el-page-header @back="router.back()" title="返回列表" />
    
    <el-card shadow="hover" v-loading="loading" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">{{ strategy?.strategyName }}</span>
            <el-tag :type="getStatusType(strategy?.status)" style="margin-left: 12px">
              {{ strategy?.statusText }}
            </el-tag>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="handleEdit" v-if="canEdit">编辑</el-button>
          </div>
        </div>
      </template>
      
      <el-descriptions :column="2" border v-if="strategy">
        <el-descriptions-item label="策略代码">{{ strategy.strategyCode }}</el-descriptions-item>
        <el-descriptions-item label="策略类型">{{ strategy.strategyType }}</el-descriptions-item>
        <el-descriptions-item label="交易标的">{{ strategy.tradingSymbol || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ strategy.creatorName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ strategy.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ strategy.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="审核人" v-if="strategy.reviewerName">
          {{ strategy.reviewerName }}
        </el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="strategy.reviewTime">
          {{ strategy.reviewTime }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div class="section" v-if="strategy?.description">
        <h3>策略描述</h3>
        <div class="content">{{ strategy.description }}</div>
      </div>
      
      <div class="section" v-if="strategy?.parameters">
        <h3>策略参数</h3>
        <pre class="json-content">{{ formatJson(strategy.parameters) }}</pre>
      </div>
      
      <div class="section" v-if="strategy?.riskControl">
        <h3>风险控制</h3>
        <pre class="json-content">{{ formatJson(strategy.riskControl) }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStrategyDetail } from '@/api/strategy'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const strategy = ref(null)

const canEdit = computed(() => {
  if (!strategy.value) return false
  return (userStore.isAdmin || 
    (userStore.isTrader && strategy.value.creatorId === userStore.userInfo?.userId)) 
    && strategy.value.status === 0
})

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success', 'danger', 'info']
  return types[status] || 'info'
}

const formatJson = (json) => {
  try {
    if (typeof json === 'string') {
      return JSON.stringify(JSON.parse(json), null, 2)
    }
    return JSON.stringify(json, null, 2)
  } catch (e) {
    return json
  }
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getStrategyDetail(route.params.id)
    strategy.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  router.push(`/strategy/edit/${strategy.value.id}`)
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="scss">
.strategy-detail {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-left {
      display: flex;
      align-items: center;
      
      .title {
        font-size: 18px;
        font-weight: 500;
      }
    }
  }
  
  .section {
    margin-top: 24px;
    
    h3 {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #ebeef5;
    }
    
    .content {
      color: #606266;
      line-height: 1.8;
      white-space: pre-wrap;
    }
    
    .json-content {
      background: #f5f7fa;
      padding: 16px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 13px;
      color: #606266;
      overflow-x: auto;
    }
  }
}
</style>
