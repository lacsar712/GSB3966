<template>
  <div class="strategy-edit">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑策略' : '创建策略' }}</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-form-item label="策略名称" prop="strategyName">
          <el-input v-model="form.strategyName" placeholder="请输入策略名称" maxlength="50" show-word-limit />
        </el-form-item>
        
        <el-form-item label="策略类型" prop="strategyType">
          <el-select v-model="form.strategyType" placeholder="请选择策略类型" style="width: 100%">
            <el-option label="趋势跟踪" value="趋势跟踪" />
            <el-option label="均值回归" value="均值回归" />
            <el-option label="套利策略" value="套利策略" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="交易标的" prop="tradingSymbol">
          <el-input v-model="form.tradingSymbol" placeholder="如：沪深300、BTC/USDT" />
        </el-form-item>
        
        <el-form-item label="策略描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述策略逻辑、适用场景等"
          />
        </el-form-item>
        
        <el-form-item label="策略参数">
          <div class="json-editor">
            <el-input
              v-model="parametersJson"
              type="textarea"
              :rows="6"
              placeholder='{"maPeriod": 20, "stdMultiplier": 2}'
            />
            <div class="json-hint">JSON格式，定义策略可调参数</div>
          </div>
        </el-form-item>
        
        <el-form-item label="风险控制">
          <div class="json-editor">
            <el-input
              v-model="riskControlJson"
              type="textarea"
              :rows="6"
              placeholder='{"stopLoss": 0.05, "takeProfit": 0.1, "maxPosition": 0.3}'
            />
            <div class="json-hint">JSON格式，定义止损、止盈、仓位控制等</div>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createStrategy, updateStrategy, getStrategyDetail } from '@/api/strategy'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  strategyName: '',
  strategyType: '',
  tradingSymbol: '',
  description: '',
  parameters: {},
  riskControl: {}
})

const parametersJson = ref('{}')
const riskControlJson = ref('{}')

watch(parametersJson, (val) => {
  try {
    form.parameters = JSON.parse(val || '{}')
  } catch (e) {
    // ignore
  }
})

watch(riskControlJson, (val) => {
  try {
    form.riskControl = JSON.parse(val || '{}')
  } catch (e) {
    // ignore
  }
})

const rules = {
  strategyName: [
    { required: true, message: '请输入策略名称', trigger: 'blur' },
    { max: 50, message: '最多50个字符', trigger: 'blur' }
  ],
  strategyType: [{ required: true, message: '请选择策略类型', trigger: 'change' }]
}

const fetchDetail = async () => {
  if (!isEdit.value) return
  try {
    const res = await getStrategyDetail(route.params.id)
    Object.assign(form, res.data)
    parametersJson.value = JSON.stringify(res.data.parameters || {}, null, 2)
    riskControlJson.value = JSON.stringify(res.data.riskControl || {}, null, 2)
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      JSON.parse(parametersJson.value || '{}')
      JSON.parse(riskControlJson.value || '{}')
    } catch (e) {
      ElMessage.error('JSON格式错误，请检查')
      return
    }
    
    submitting.value = true
    try {
      const data = {
        ...form,
        parameters: parametersJson.value,
        riskControl: riskControlJson.value
      }
      
      if (isEdit.value) {
        await updateStrategy(route.params.id, data)
        ElMessage.success('更新成功')
      } else {
        await createStrategy(data)
        ElMessage.success('创建成功')
      }
      router.push('/strategy')
    } catch (error) {
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped lang="scss">
.strategy-edit {
  .card-header {
    font-size: 16px;
    font-weight: 500;
  }
  
  .json-editor {
    width: 100%;
    
    .json-hint {
      font-size: 12px;
      color: #909399;
      margin-top: 4px;
    }
  }
}
</style>
