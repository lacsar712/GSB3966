<template>
  <div class="strategy-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">策略列表</span>
          <el-button type="primary" @click="handleCreate" v-if="userStore.isTrader || userStore.isAdmin">
            <el-icon><Plus /></el-icon>创建策略
          </el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索策略名称/代码"
          clearable
          style="width: 220px"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="searchForm.strategyType" placeholder="策略类型" clearable style="width: 140px">
          <el-option label="趋势跟踪" value="趋势跟踪" />
          <el-option label="均值回归" value="均值回归" />
          <el-option label="套利策略" value="套利策略" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="草稿" :value="0" />
          <el-option label="待审核" :value="1" />
          <el-option label="已发布" :value="2" />
          <el-option label="已暂停" :value="3" />
          <el-option label="已归档" :value="4" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      
      <el-table :data="strategyList" stripe v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="strategyCode" label="策略代码" width="140" />
        <el-table-column prop="strategyName" label="策略名称" show-overflow-tooltip />
        <el-table-column prop="strategyType" label="类型" width="100" />
        <el-table-column prop="tradingSymbol" label="交易标的" width="120" show-overflow-tooltip />
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="statusText" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)" 
              v-if="canEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleSubmit(row)" 
              v-if="canSubmit(row)">提交审核</el-button>
            <el-button type="success" link size="small" @click="handleReview(row)" 
              v-if="canReview(row)">审核</el-button>
            <el-button type="success" link size="small" @click="handleStart(row)" 
              v-if="canStart(row)">启动</el-button>
            <el-button type="danger" link size="small" @click="handlePause(row)" 
              v-if="canPause(row)">暂停</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" 
              v-if="canDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <el-dialog v-model="reviewDialogVisible" title="策略审核" width="400px">
      <p style="margin-bottom: 16px;">策略名称：{{ currentRow?.strategyName }}</p>
      <el-radio-group v-model="reviewResult">
        <el-radio :label="true">通过</el-radio>
        <el-radio :label="false">拒绝</el-radio>
      </el-radio-group>
      <el-input
        v-model="reviewRemark"
        type="textarea"
        placeholder="审核备注"
        rows="3"
        style="margin-top: 16px"
      />
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReview" :loading="reviewing">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getStrategyList, deleteStrategy, submitForReview, reviewStrategy, startStrategy, pauseStrategy } from '@/api/strategy'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const strategyList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  strategyType: '',
  status: null
})

const reviewDialogVisible = ref(false)
const reviewResult = ref(true)
const reviewRemark = ref('')
const currentRow = ref(null)
const reviewing = ref(false)

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success', 'danger', 'info']
  return types[status] || 'info'
}

const canEdit = (row) => {
  return (userStore.isAdmin || (userStore.isTrader && row.creatorId === userStore.userInfo?.userId)) && row.status === 0
}

const canSubmit = (row) => {
  return row.creatorId === userStore.userInfo?.userId && row.status === 0
}

const canReview = (row) => {
  return userStore.isManager && row.status === 1
}

const canStart = (row) => {
  return userStore.isManager && (row.status === 2 || row.status === 3)
}

const canPause = (row) => {
  return userStore.isManager && row.status === 2
}

const canDelete = (row) => {
  return userStore.isAdmin || (row.creatorId === userStore.userInfo?.userId && row.status === 0)
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStrategyList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword,
      strategyType: searchForm.strategyType,
      status: searchForm.status
    })
    strategyList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.strategyType = ''
  searchForm.status = null
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handlePageChange = (val) => {
  pageNum.value = val
  fetchData()
}

const handleCreate = () => {
  router.push('/strategy/create')
}

const handleView = (row) => {
  router.push(`/strategy/detail/${row.id}`)
}

const handleEdit = (row) => {
  router.push(`/strategy/edit/${row.id}`)
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定提交该策略进行审核吗？', '提示', { type: 'warning' })
    await submitForReview(row.id)
    ElMessage.success('提交成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleReview = (row) => {
  currentRow.value = row
  reviewResult.value = true
  reviewRemark.value = ''
  reviewDialogVisible.value = true
}

const confirmReview = async () => {
  reviewing.value = true
  try {
    await reviewStrategy(currentRow.value.id, {
      approved: reviewResult.value,
      remark: reviewRemark.value
    })
    ElMessage.success('审核完成')
    reviewDialogVisible.value = false
    fetchData()
  } finally {
    reviewing.value = false
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm('确定启动该策略吗？', '提示', { type: 'warning' })
    await startStrategy(row.id)
    ElMessage.success('启动成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handlePause = async (row) => {
  try {
    await ElMessageBox.confirm('确定暂停该策略吗？', '提示', { type: 'warning' })
    await pauseStrategy(row.id)
    ElMessage.success('暂停成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该策略吗？此操作不可恢复！', '警告', { type: 'error' })
    await deleteStrategy(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.strategy-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 16px;
      font-weight: 500;
    }
  }
  
  .search-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 8px;
  }
  
  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
