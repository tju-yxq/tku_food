<template>
  <div class="audit-log-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-document" style="color: #409EFF;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">总日志数</div>
              <div class="stat-value">{{ statistics.totalLogs || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-user" style="color: #67C23A;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">今日登录</div>
              <div class="stat-value">{{ statistics.todayLogins || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-setting" style="color: #E6A23C;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">今日操作</div>
              <div class="stat-value">{{ statistics.todayOperations || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-warning" style="color: #F56C6C;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-title">风险事件</div>
              <div class="stat-value">{{ statistics.riskEvents || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
      <!-- 操作日志 -->
      <el-tab-pane label="操作日志" name="operation">
        <el-card>
          <!-- 搜索条件 -->
          <div class="search-form">
            <el-form :inline="true" :model="searchForm" size="small">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="searchForm.timeRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="yyyy-MM-dd HH:mm:ss"
                  value-format="yyyy-MM-dd HH:mm:ss">
                </el-date-picker>
              </el-form-item>
              <el-form-item label="操作模块">
                <el-select v-model="searchForm.module" placeholder="请选择" clearable>
                  <el-option label="用户管理" value="用户管理"></el-option>
                  <el-option label="内容管理" value="内容管理"></el-option>
                  <el-option label="社区管理" value="社区管理"></el-option>
                  <el-option label="运营管理" value="运营管理"></el-option>
                  <el-option label="系统管理" value="系统管理"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="关键词">
                <el-input v-model="searchForm.keyword" placeholder="请输入关键词" clearable></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="searchOperationLogs">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
                <el-button type="success" @click="exportLogs">导出</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 操作日志表格 -->
          <el-table :data="operationLogs" border v-loading="loading" stripe>
            <el-table-column prop="operatorName" label="操作人" width="120"></el-table-column>
            <el-table-column prop="module" label="操作模块" width="120"></el-table-column>
            <el-table-column prop="operation" label="操作类型" width="150"></el-table-column>
            <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip></el-table-column>
            <el-table-column prop="ip" label="IP地址" width="130"></el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template slot-scope="{row}">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="mini">
                  {{ row.status === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="操作时间" width="180" align="center"></el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="{row}">
                <el-button size="mini" type="text" @click="viewDetails(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="query.current"
            :page-size="query.size"
            layout="total, prev, pager, next"
            :total="total">
          </el-pagination>
        </el-card>
      </el-tab-pane>

      <!-- 登录日志 -->
      <el-tab-pane label="登录日志" name="login">
        <el-card>
          <!-- 登录日志内容 -->
          <div class="login-stats">
            <h3>登录统计</h3>
            <!-- 这里可以添加登录统计图表 -->
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 安全日志 -->
      <el-tab-pane label="安全日志" name="security">
        <el-card>
          <!-- 安全日志内容 -->
          <div class="security-events">
            <h3>安全事件</h3>
            <!-- 这里可以添加安全事件列表 -->
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 详情对话框 -->
    <el-dialog title="操作详情" :visible.sync="detailVisible" width="60%">
      <div v-if="currentDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="操作人">{{ currentDetail.operatorName }}</el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ currentDetail.module }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ currentDetail.operation }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentDetail.ip }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ currentDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="执行时间">{{ currentDetail.executeTime }}ms</el-descriptions-item>
          <el-descriptions-item label="操作描述" :span="2">{{ currentDetail.description }}</el-descriptions-item>
          <el-descriptions-item label="请求参数" :span="2">
            <pre>{{ currentDetail.params }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="返回结果" :span="2">
            <pre>{{ currentDetail.result }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AuditLog',
  data() {
    return {
      activeTab: 'operation',
      loading: false,
      statistics: {},
      operationLogs: [],
      total: 0,
      query: {
        current: 1,
        size: 10
      },
      searchForm: {
        timeRange: [],
        module: '',
        keyword: ''
      },
      detailVisible: false,
      currentDetail: null
    }
  },
  created() {
    this.fetchStatistics()
    this.fetchOperationLogs()
  },
  methods: {
    async fetchStatistics() {
      try {
        const response = await request({
          url: '/admin/audit-logs/statistics',
          method: 'get'
        })
        this.statistics = response
      } catch (error) {
        console.error('获取统计信息失败', error)
      }
    },

    async fetchOperationLogs() {
      this.loading = true
      try {
        const params = {
          ...this.query,
          logType: 1, // 操作日志
          ...this.searchForm
        }
        if (this.searchForm.timeRange && this.searchForm.timeRange.length === 2) {
          params.startTime = this.searchForm.timeRange[0]
          params.endTime = this.searchForm.timeRange[1]
        }
        
        const response = await request({
          url: '/admin/audit-logs/list',
          method: 'get',
          params
        })
        this.operationLogs = response.records || []
        this.total = response.total || 0
      } catch (error) {
        console.error('获取操作日志失败', error)
      } finally {
        this.loading = false
      }
    },

    handleTabClick(tab) {
      if (tab.name === 'operation') {
        this.fetchOperationLogs()
      }
    },

    searchOperationLogs() {
      this.query.current = 1
      this.fetchOperationLogs()
    },

    resetSearch() {
      this.searchForm = {
        timeRange: [],
        module: '',
        keyword: ''
      }
      this.searchOperationLogs()
    },

    exportLogs() {
      this.$message.info('导出功能开发中...')
    },

    viewDetails(row) {
      this.currentDetail = row
      this.detailVisible = true
    },

    handleCurrentChange(page) {
      this.query.current = page
      this.fetchOperationLogs()
    }
  }
}
</script>

<style scoped>
.audit-log-container {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 6px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

pre {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}
</style>
