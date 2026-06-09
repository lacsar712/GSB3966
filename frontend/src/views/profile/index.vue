<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="user-card">
            <el-avatar :size="80" :icon="UserFilled" />
            <h3>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h3>
            <p>{{ userStore.userInfo?.roleName }}</p>
          </div>
          <el-divider />
          <div class="user-info">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userStore.userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色：</span>
              <span class="value">{{ userStore.userInfo?.roleName }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>基本信息</span>
          </template>
          <el-form label-width="100px" style="max-width: 500px">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="userInfo.realName" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="userInfo.phone" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getUserDetail } from '@/api/user'

const userStore = useUserStore()
const userInfo = ref({})

const fetchUserInfo = async () => {
  try {
    const res = await getUserDetail(userStore.userInfo?.userId)
    userInfo.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleSave = () => {
  ElMessage.success('保存成功')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped lang="scss">
.profile-page {
  .user-card {
    text-align: center;
    padding: 20px 0;
    
    h3 {
      margin: 16px 0 8px;
      font-size: 20px;
    }
    
    p {
      color: #909399;
      margin: 0;
    }
  }
  
  .user-info {
    .info-item {
      display: flex;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .label {
        width: 80px;
        color: #909399;
      }
      
      .value {
        flex: 1;
        color: #606266;
      }
    }
  }
}
</style>
