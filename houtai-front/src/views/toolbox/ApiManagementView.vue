<template>
  <div class="api-management-container">
    <!-- 概览统计 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-data-line" style="color: #409EFF;"></i>
            </div>
            <div class="card-info">
              <div class="card-title">今日调用</div>
              <div class="card-value">{{ statistics.todayCalls || 0 }}</div>
              <div class="card-trend up">
                <i class="el-icon-top"></i>
                +12.5%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-time" style="color: #67C23A;"></i>
            </div>
            <div class="card-info">
              <div class="card-title">平均响应时间</div>
              <div class="card-value">{{ statistics.avgResponseTime || 0 }}ms</div>
              <div class="card-trend down">
                <i class="el-icon-bottom"></i>
                -8.3%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-success" style="color: #E6A23C;"></i>
            </div>
            <div class="card-info">
              <div class="card-title">成功率</div>
              <div class="card-value">{{ statistics.successRate || 0 }}%</div>
              <div class="card-trend up">
                <i class="el-icon-top"></i>
                +0.2%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon">
              <i class="el-icon-warning" style="color: #F56C6C;"></i>
            </div>
            <div class="card-info">
              <div class="card-title">错误数</div>
              <div class="card-value">{{ statistics.errorCount || 0 }}</div>
              <div class="card-trend down">
                <i class="el-icon-bottom"></i>
                -15.6%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
      <!-- API访问日志 -->
      <el-tab-pane label="访问日志" name="access">
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
              <el-form-item label="API路径">
                <el-input v-model="searchForm.apiPath" placeholder="请输入API路径" clearable></el-input>
              </el-form-item>
              <el-form-item label="状态码">
                <el-select v-model="searchForm.status" placeholder="请选择" clearable>
                  <el-option label="200 成功" value="200"></el-option>
                  <el-option label="400 请求错误" value="400"></el-option>
                  <el-option label="401 未授权" value="401"></el-option>
                  <el-option label="403 禁止访问" value="403"></el-option>
                  <el-option label="404 未找到" value="404"></el-option>
                  <el-option label="500 服务器错误" value="500"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="searchAccessLogs">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
                <el-button type="success" @click="exportLogs">导出</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 访问日志表格 -->
          <el-table :data="accessLogs" border v-loading="loading" stripe>
            <el-table-column prop="apiPath" label="API路径" min-width="200" show-overflow-tooltip></el-table-column>
            <el-table-column prop="httpMethod" label="方法" width="80" align="center">
              <template slot-scope="{row}">
                <el-tag :type="getMethodType(row.httpMethod)" size="mini">{{ row.httpMethod }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="callerName" label="调用者" width="120"></el-table-column>
            <el-table-column prop="responseStatus" label="状态码" width="80" align="center">
              <template slot-scope="{row}">
                <el-tag :type="getStatusType(row.responseStatus)" size="mini">{{ row.responseStatus }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="responseTime" label="响应时间" width="100" align="center">
              <template slot-scope="{row}">
                <span :class="getResponseTimeClass(row.responseTime)">{{ row.responseTime }}ms</span>
              </template>
            </el-table-column>
            <el-table-column prop="clientIp" label="客户端IP" width="130"></el-table-column>
            <el-table-column prop="createTime" label="访问时间" width="180" align="center"></el-table-column>
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

      <!-- 错误日志 -->
      <el-tab-pane label="错误日志" name="error">
        <el-card>
          <!-- 错误统计 -->
          <div class="error-stats">
            <h3>错误统计</h3>
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="error-item">
                  <div class="error-title">4xx 客户端错误</div>
                  <div class="error-count">{{ errorStats.client4xx || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="error-item">
                  <div class="error-title">5xx 服务器错误</div>
                  <div class="error-count">{{ errorStats.server5xx || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="error-item">
                  <div class="error-title">超时错误</div>
                  <div class="error-count">{{ errorStats.timeout || 0 }}</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 性能监控 -->
      <el-tab-pane label="性能监控" name="performance">
        <el-card>
          <!-- 性能指标 -->
          <div class="performance-metrics">
            <h3>性能指标</h3>
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="metric-item">
                  <div class="metric-title">热门API Top 10</div>
                  <el-table :data="popularApis" size="small">
                    <el-table-column prop="apiPath" label="API路径" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="callCount" label="调用次数" width="100" align="center"></el-table-column>
                    <el-table-column prop="avgResponseTime" label="平均响应时间" width="120" align="center">
                      <template slot-scope="{row}">{{ row.avgResponseTime }}ms</template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="metric-item">
                  <div class="metric-title">慢查询API Top 10</div>
                  <el-table :data="slowApis" size="small">
                    <el-table-column prop="apiPath" label="API路径" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="avgResponseTime" label="平均响应时间" width="120" align="center">
                      <template slot-scope="{row}">
                        <span class="slow-time">{{ row.avgResponseTime }}ms</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="callCount" label="调用次数" width="100" align="center"></el-table-column>
                  </el-table>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 详情对话框 -->
    <el-dialog title="访问详情" :visible.sync="detailVisible" width="70%">
      <div v-if="currentDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="API路径">{{ currentDetail.apiPath }}</el-descriptions-item>
          <el-descriptions-item label="HTTP方法">{{ currentDetail.httpMethod }}</el-descriptions-item>
          <el-descriptions-item label="调用者">{{ currentDetail.callerName }}</el-descriptions-item>
          <el-descriptions-item label="客户端IP">{{ currentDetail.clientIp }}</el-descriptions-item>
          <el-descriptions-item label="状态码">{{ currentDetail.responseStatus }}</el-descriptions-item>
          <el-descriptions-item label="响应时间">{{ currentDetail.responseTime }}ms</el-descriptions-item>
          <el-descriptions-item label="访问时间">{{ currentDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="追踪ID">{{ currentDetail.traceId }}</el-descriptions-item>
          <el-descriptions-item label="请求参数" :span="2">
            <pre>{{ currentDetail.requestParams }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="响应数据" :span="2">
            <pre>{{ currentDetail.responseData }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ApiManagement',
  data() {
    return {
      activeTab: 'access',
      loading: false,
      statistics: {},
      accessLogs: [],
      errorStats: {},
      popularApis: [],
      slowApis: [],
      total: 0,
      query: {
        current: 1,
        size: 10
      },
      searchForm: {
        timeRange: [],
        apiPath: '',
        status: ''
      },
      detailVisible: false,
      currentDetail: null
    }
  },
  created() {
    this.fetchStatistics()
    this.fetchAccessLogs()
    this.fetchPopularApis()
    this.fetchSlowApis()
  },
  methods: {
    async fetchStatistics() {
      try {
        const response = await request({
          url: '/admin/api-management/statistics',
          method: 'get'
        })
        this.statistics = response
      } catch (error) {
        console.error('获取统计信息失败', error)
        // 使用模拟数据
        this.statistics = {
          todayCalls: 15420,
          avgResponseTime: 125,
          successRate: 99.2,
          errorCount: 23
        }
      }
    },

    async fetchAccessLogs() {
      this.loading = true
      try {
        const params = {
          ...this.query,
          ...this.searchForm
        }
        if (this.searchForm.timeRange && this.searchForm.timeRange.length === 2) {
          params.startTime = this.searchForm.timeRange[0]
          params.endTime = this.searchForm.timeRange[1]
        }
        
        const response = await request({
          url: '/admin/api-management/access-logs',
          method: 'get',
          params
        })
        this.accessLogs = response.records || []
        this.total = response.total || 0
      } catch (error) {
        console.error('获取访问日志失败', error)
        // 使用模拟数据
        this.accessLogs = [
          {
            apiPath: '/api/user/login',
            httpMethod: 'POST',
            callerName: 'user123',
            responseStatus: 200,
            responseTime: 95,
            clientIp: '192.168.1.100',
            createTime: '2025-06-17 14:30:25'
          }
        ]
      } finally {
        this.loading = false
      }
    },

    async fetchPopularApis() {
      try {
        const response = await request({
          url: '/admin/api-management/popular-apis',
          method: 'get'
        })
        this.popularApis = response || []
      } catch (error) {
        console.error('获取热门API失败', error)
      }
    },

    async fetchSlowApis() {
      try {
        const response = await request({
          url: '/admin/api-management/slow-apis',
          method: 'get'
        })
        this.slowApis = response || []
      } catch (error) {
        console.error('获取慢查询API失败', error)
      }
    },

    handleTabClick(tab) {
      if (tab.name === 'access') {
        this.fetchAccessLogs()
      } else if (tab.name === 'error') {
        this.fetchErrorStats()
      }
    },

    searchAccessLogs() {
      this.query.current = 1
      this.fetchAccessLogs()
    },

    resetSearch() {
      this.searchForm = {
        timeRange: [],
        apiPath: '',
        status: ''
      }
      this.searchAccessLogs()
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
      this.fetchAccessLogs()
    },

    getMethodType(method) {
      const methodMap = {
        'GET': 'success',
        'POST': 'primary',
        'PUT': 'warning',
        'DELETE': 'danger'
      }
      return methodMap[method] || 'info'
    },

    getStatusType(status) {
      if (status >= 200 && status < 300) return 'success'
      if (status >= 400 && status < 500) return 'warning'
      if (status >= 500) return 'danger'
      return 'info'
    },

    getResponseTimeClass(time) {
      if (time <= 100) return 'response-fast'
      if (time <= 500) return 'response-normal'
      return 'response-slow'
    }
  }
}
</script>

<style scoped>
.api-management-container {
  padding: 20px;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  font-size: 40px;
  margin-right: 20px;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-trend {
  font-size: 12px;
  font-weight: bold;
}

.card-trend.up {
  color: #67C23A;
}

.card-trend.down {
  color: #F56C6C;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 6px;
}

.response-fast {
  color: #67C23A;
  font-weight: bold;
}

.response-normal {
  color: #E6A23C;
}

.response-slow {
  color: #F56C6C;
  font-weight: bold;
}

.slow-time {
  color: #F56C6C;
  font-weight: bold;
}

.error-stats, .performance-metrics {
  padding: 20px;
}

.error-item, .metric-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #f9f9f9;
}

.error-title, .metric-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.error-count {
  font-size: 28px;
  font-weight: bold;
  color: #F56C6C;
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
