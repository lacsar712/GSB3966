<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon blue">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">策略总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon green">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.published }}</div>
            <div class="stat-label">已发布</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon orange">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon purple">
            <el-icon><Edit /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.draft }}</div>
            <div class="stat-label">草稿</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>策略类型分布</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div v-for="(item, index) in typeStats" :key="index" class="type-item">
              <span class="type-name">{{ item.name }}</span>
              <el-progress :percentage="item.percentage" :color="item.color" />
              <span class="type-count">{{ item.count }}个</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近创建的策略</span>
              <el-button type="primary" link @click="$router.push('/strategy')">
                查看更多
              </el-button>
            </div>
          </template>
          <el-table :data="recentStrategies" stripe style="width: 100%">
            <el-table-column prop="strategyName" label="策略名称" show-overflow-tooltip />
            <el-table-column prop="strategyType" label="类型" width="100" />
            <el-table-column prop="statusText" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ row.statusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(notice, index) in notices"
              :key="index"
              :type="notice.type"
              :timestamp="notice.time"
            >
              {{ notice.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, CircleCheck, Timer, Edit } from '@element-plus/icons-vue'
import { getStrategyList } from '@/api/strategy'

const stats = ref({
  total: 0,
  published: 0,
  pending: 0,
  draft: 0
})

const typeStats = ref([
  { name: '趋势跟踪', count: 8, percentage: 40, color: '#409EFF' },
  { name: '均值回归', count: 5, percentage: 25, color: '#67C23A' },
  { name: '套利策略', count: 4, percentage: 20, color: '#E6A23C' },
  { name: '其他', count: 3, percentage: 15, color: '#909399' }
])

const recentStrategies = ref([])

const notices = ref([
  { content: '系统上线运行，欢迎使用交易策略管理系统', time: '2024-01-01', type: 'primary' },
  { content: '新增策略审核功能，管理员可对策略进行审核', time: '2024-01-02', type: 'success' },
  { content: '优化策略详情页面展示', time: '2024-01-03', type: 'info' }
])

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success', 'danger', 'info']
  return types[status] || 'info'
}

const fetchStats = async () => {
  try {
    const res = await getStrategyList({ pageNum: 1, pageSize: 1000 })
    const list = res.data.list || []
    stats.value.total = list.length
    stats.value.published = list.filter(s => s.status === 2).length
    stats.value.pending = list.filter(s => s.status === 1).length
    stats.value.draft = list.filter(s => s.status === 0).length
    recentStrategies.value = list.slice(0, 5)
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped lang="scss">
.dashboard {
  .mt-20 {
    margin-top: 20px;
  }
  
  .stat-card {
    display: flex;
    align-items: center;
    
    :deep(.el-card__body) {
      display: flex;
      align-items: center;
      width: 100%;
      padding: 20px;
    }
    
    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      
      .el-icon {
        font-size: 28px;
        color: #fff;
      }
      
      &.blue { background: linear-gradient(135deg, #409EFF, #64b5f6); }
      &.green { background: linear-gradient(135deg, #67C23A, #81c784); }
      &.orange { background: linear-gradient(135deg, #E6A23C, #ffb74d); }
      &.purple { background: linear-gradient(135deg, #9C27B0, #ce93d8); }
    }
    
    .stat-info {
      flex: 1;
      
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        line-height: 1;
      }
      
      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 8px;
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 500;
  }
  
  .chart-placeholder {
    padding: 20px 0;
    
    .type-item {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      
      .type-name {
        width: 80px;
        font-size: 14px;
        color: #606266;
      }
      
      .el-progress {
        flex: 1;
        margin: 0 16px;
      }
      
      .type-count {
        width: 60px;
        font-size: 14px;
        color: #909399;
        text-align: right;
      }
    }
  }
}
</style>
