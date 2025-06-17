<template>
  <div class="api-doc-container">
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
      <!-- 接口文档标签页 -->
      <el-tab-pane label="接口文档" name="docs">
        <!-- 工具栏 -->
        <el-card class="toolbar-card">
          <div slot="header" class="clearfix">
            <h2>API接口文档</h2>
          </div>

      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-select v-model="selectedEnvironment" placeholder="选择环境" @change="handleEnvironmentChange">
              <el-option
                v-for="env in environments"
                :key="env.value"
                :label="env.label"
                :value="env.value">
                <span style="float: left">{{ env.label }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ env.url }}</span>
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-input
              v-model="currentUrl"
              placeholder="API文档地址"
              prefix-icon="el-icon-link"
              readonly>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" icon="el-icon-refresh" @click="refreshDoc">刷新文档</el-button>
            <el-button type="success" icon="el-icon-view" @click="openInNewTab">新窗口打开</el-button>
            <el-button type="info" icon="el-icon-download" @click="downloadApiSpec">下载规范</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 状态信息 -->
      <div class="status-info">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="status-item">
              <i class="el-icon-connection" :class="connectionStatus.class"></i>
              <span>连接状态：{{ connectionStatus.text }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="status-item">
              <i class="el-icon-time"></i>
              <span>最后更新：{{ lastUpdateTime }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="status-item">
              <i class="el-icon-document"></i>
              <span>API版本：{{ apiVersion }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="status-item">
              <i class="el-icon-user"></i>
              <span>当前用户：{{ currentUser }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 文档内容 -->
    <el-card class="doc-card">
      <div v-loading="loading" class="doc-content">
        <!-- 如果iframe加载失败，显示提示信息 -->
        <div v-if="iframeError" class="iframe-error">
          <div class="error-content">
            <i class="el-icon-warning" style="font-size: 48px; color: #E6A23C;"></i>
            <h3>无法在iframe中加载API文档</h3>
            <p>由于浏览器安全策略限制，无法在当前页面中嵌入显示API文档。</p>
            <div class="error-actions">
              <el-button type="primary" size="large" @click="openInNewTab">
                <i class="el-icon-view"></i>
                在新窗口中打开API文档
              </el-button>
              <el-button type="success" size="large" @click="retryIframe">
                <i class="el-icon-refresh"></i>
                重试加载
              </el-button>
            </div>
            <div class="direct-link">
              <p>或直接访问：<a :href="currentUrl" target="_blank">{{ currentUrl }}</a></p>
            </div>
          </div>
        </div>

        <!-- iframe内容 -->
        <iframe
          v-show="!iframeError"
          ref="docFrame"
          :src="currentUrl"
          class="doc-iframe"
          @load="handleFrameLoad"
          @error="handleFrameError">
        </iframe>
      </div>
    </el-card>

    <!-- 快捷操作面板 -->
    <el-card class="quick-actions-card">
      <div slot="header">
        <span>快捷操作</span>
      </div>
      <div class="quick-actions">
        <el-button-group>
          <el-button size="small" icon="el-icon-s-home" @click="goToHome">首页</el-button>
          <el-button size="small" icon="el-icon-menu" @click="toggleSidebar">侧边栏</el-button>
          <el-button size="small" icon="el-icon-search" @click="openSearch">搜索</el-button>
          <el-button size="small" icon="el-icon-setting" @click="openSettings">设置</el-button>
        </el-button-group>
      </div>
    </el-card>
      </el-tab-pane>

      <!-- API文档标签页 -->
      <el-tab-pane label="API文档" name="management">
        <!-- 概览卡片 -->
        <el-row :gutter="20" class="overview-cards">
          <el-col :span="6">
            <el-card class="overview-card">
              <div class="card-content">
                <div class="card-icon">
                  <i class="el-icon-document" style="color: #409EFF;"></i>
                </div>
                <div class="card-info">
                  <div class="card-title">总接口数</div>
                  <div class="card-value">{{ statistics.totalEndpoints || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="overview-card">
              <div class="card-content">
                <div class="card-icon">
                  <i class="el-icon-menu" style="color: #67C23A;"></i>
                </div>
                <div class="card-info">
                  <div class="card-title">控制器数</div>
                  <div class="card-value">{{ statistics.totalControllers || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="overview-card">
              <div class="card-content">
                <div class="card-icon">
                  <i class="el-icon-files" style="color: #E6A23C;"></i>
                </div>
                <div class="card-info">
                  <div class="card-title">数据模型</div>
                  <div class="card-value">{{ statistics.totalModels || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="overview-card">
              <div class="card-content">
                <div class="card-icon">
                  <i class="el-icon-success" style="color: #F56C6C;"></i>
                </div>
                <div class="card-info">
                  <div class="card-title">文档覆盖率</div>
                  <div class="card-value">{{ statistics.documentationCoverage || '0%' }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 模块统计 -->
        <el-card class="module-stats-card">
          <div slot="header">
            <h3>模块接口统计</h3>
          </div>
          <el-table :data="moduleStatistics" border>
            <el-table-column prop="name" label="模块名称" width="200"></el-table-column>
            <el-table-column prop="endpoints" label="接口数量" width="120" align="center"></el-table-column>
            <el-table-column label="文档覆盖率" width="150" align="center">
              <template slot-scope="{row}">
                <el-progress :percentage="parseInt(row.coverage)" :color="getProgressColor(row.coverage)"></el-progress>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template slot-scope="{row}">
                <el-button size="mini" type="text" @click="viewModuleDoc(row.name)">查看文档</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- API性能监控 -->
        <el-card class="performance-card">
          <div slot="header">
            <h3>API性能监控</h3>
          </div>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="performance-item">
                <div class="performance-title">平均响应时间</div>
                <div class="performance-value">{{ performanceData.avgResponseTime }}ms</div>
                <div class="performance-trend" :class="performanceData.responseTrend">
                  <i :class="performanceData.responseTrend === 'up' ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ performanceData.responseChange }}%
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="performance-item">
                <div class="performance-title">请求成功率</div>
                <div class="performance-value">{{ performanceData.successRate }}%</div>
                <div class="performance-trend" :class="performanceData.successTrend">
                  <i :class="performanceData.successTrend === 'up' ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ performanceData.successChange }}%
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="performance-item">
                <div class="performance-title">今日调用量</div>
                <div class="performance-value">{{ performanceData.todayRequests }}</div>
                <div class="performance-trend" :class="performanceData.requestsTrend">
                  <i :class="performanceData.requestsTrend === 'up' ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ performanceData.requestsChange }}%
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- API版本管理 -->
        <el-card class="version-card">
          <div slot="header">
            <h3>API版本管理</h3>
          </div>
          <el-table :data="apiVersions" border>
            <el-table-column prop="version" label="版本号" width="120"></el-table-column>
            <el-table-column prop="releaseDate" label="发布日期" width="150"></el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template slot-scope="{row}">
                <el-tag :type="getVersionStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="版本说明" min-width="200"></el-table-column>
            <el-table-column prop="usage" label="使用率" width="120" align="center">
              <template slot-scope="{row}">
                <el-progress :percentage="row.usage" :color="getUsageColor(row.usage)"></el-progress>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template slot-scope="{row}">
                <el-button size="mini" type="text" @click="viewVersionDoc(row.version)">查看文档</el-button>
                <el-button size="mini" type="text" @click="compareVersion(row.version)">版本对比</el-button>
                <el-button v-if="row.status === '已废弃'" size="mini" type="text" style="color: #f56c6c;" @click="removeVersion(row.version)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 热门API排行 -->
        <el-card class="popular-apis-card">
          <div slot="header">
            <h3>热门API排行</h3>
          </div>
          <el-table :data="popularApis" border>
            <el-table-column type="index" label="排名" width="80" align="center">
              <template slot-scope="{$index}">
                <span class="rank-badge" :class="getRankClass($index)">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="path" label="API路径" min-width="200"></el-table-column>
            <el-table-column prop="method" label="请求方法" width="100" align="center">
              <template slot-scope="{row}">
                <el-tag :type="getMethodType(row.method)" size="mini">{{ row.method }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="calls" label="调用次数" width="120" align="center"></el-table-column>
            <el-table-column prop="avgResponseTime" label="平均响应时间" width="150" align="center">
              <template slot-scope="{row}">
                <span :class="getResponseTimeClass(row.avgResponseTime)">{{ row.avgResponseTime }}ms</span>
              </template>
            </el-table-column>
            <el-table-column prop="errorRate" label="错误率" width="100" align="center">
              <template slot-scope="{row}">
                <span :class="getErrorRateClass(row.errorRate)">{{ row.errorRate }}%</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ApiDoc',
  data() {
    return {
      activeTab: 'docs',
      loading: true,
      iframeError: false,
      selectedEnvironment: 'local',
      currentUrl: '',
      lastUpdateTime: '',
      apiVersion: 'v1.0.0',
      currentUser: '',
      // API管理相关数据
      statistics: {},
      moduleStatistics: [],
      performanceData: {
        avgResponseTime: 125,
        responseTrend: 'down',
        responseChange: 8.5,
        successRate: 99.2,
        successTrend: 'up',
        successChange: 0.3,
        todayRequests: 15420,
        requestsTrend: 'up',
        requestsChange: 12.8
      },
      apiVersions: [],
      popularApis: [],
      environments: [
        {
          label: '本地开发环境',
          value: 'local',
          url: 'http://127.0.0.1:8090/api/admin/doc-proxy/embedded'
        },
        {
          label: '测试环境',
          value: 'test',
          url: 'http://test-api.tjufood.com/doc.html#/home'
        },
        {
          label: '预发布环境',
          value: 'staging',
          url: 'http://staging-api.tjufood.com/doc.html#/home'
        },
        {
          label: '生产环境',
          value: 'production',
          url: 'http://api.tjufood.com/doc.html#/home'
        }
      ],
      connectionStatus: {
        text: '检查中...',
        class: 'status-checking'
      }
    }
  },
  computed: {
    // 当前环境配置
    currentEnvironment() {
      return this.environments.find(env => env.value === this.selectedEnvironment)
    }
  },
  created() {
    this.initializeDoc()
    this.fetchStatistics()
    this.fetchApiVersions()
    this.fetchPopularApis()
  },
  mounted() {
    this.currentUser = this.$store.getters.name || '管理员'
    this.updateTime()
    // 每分钟更新一次时间
    this.timeInterval = setInterval(this.updateTime, 60000)
  },
  beforeDestroy() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
  },
  methods: {
    // 初始化文档
    initializeDoc() {
      this.handleEnvironmentChange()
      this.checkApiStatus()
    },

    // 环境切换
    handleEnvironmentChange() {
      if (this.currentEnvironment) {
        this.currentUrl = this.currentEnvironment.url
        this.loading = true
        this.iframeError = false
        this.checkApiStatus()

        // 设置iframe加载超时检测
        setTimeout(() => {
          if (this.loading) {
            this.handleFrameError()
          }
        }, 10000) // 10秒超时
      }
    },

    // 检查API状态
    async checkApiStatus() {
      try {
        this.connectionStatus = {
          text: '检查中...',
          class: 'status-checking'
        }

        // 这里可以添加实际的API健康检查
        // const response = await this.$http.get('/api/health')

        // 模拟检查
        setTimeout(() => {
          this.connectionStatus = {
            text: '连接正常',
            class: 'status-success'
          }
        }, 1000)

      } catch (error) {
        this.connectionStatus = {
          text: '连接失败',
          class: 'status-error'
        }
        this.$message.error('API服务连接失败')
      }
    },

    // 刷新文档
    refreshDoc() {
      this.loading = true
      this.$refs.docFrame.src = this.currentUrl + '?t=' + Date.now()
      this.$message.success('文档刷新中...')
    },

    // 新窗口打开
    openInNewTab() {
      window.open(this.currentUrl, '_blank')
      this.$message.success('已在新窗口打开API文档')
    },

    // 下载API规范
    downloadApiSpec() {
      // 这里可以实现下载OpenAPI规范文件的功能
      const specUrl = this.currentUrl.replace('/doc.html#/home', '/v3/api-docs')
      window.open(specUrl, '_blank')
      this.$message.success('正在下载API规范文件...')
    },

    // iframe加载完成
    handleFrameLoad() {
      this.loading = false
      this.iframeError = false
      this.updateTime()
      this.$message.success('API文档加载完成')
    },

    // iframe加载错误
    handleFrameError() {
      this.loading = false
      this.iframeError = true
      this.connectionStatus = {
        text: '加载失败',
        class: 'status-error'
      }
      this.$message.error('API文档加载失败，建议在新窗口中打开')
    },

    // 重试iframe加载
    retryIframe() {
      this.iframeError = false
      this.loading = true
      this.refreshDoc()
    },

    // 更新时间
    updateTime() {
      this.lastUpdateTime = new Date().toLocaleString()
    },

    // 快捷操作
    goToHome() {
      this.$refs.docFrame.contentWindow.postMessage({ action: 'goHome' }, '*')
    },

    toggleSidebar() {
      this.$refs.docFrame.contentWindow.postMessage({ action: 'toggleSidebar' }, '*')
    },

    openSearch() {
      this.$refs.docFrame.contentWindow.postMessage({ action: 'openSearch' }, '*')
    },

    openSettings() {
      this.$message.info('文档设置功能开发中...')
    },

    // API管理相关方法
    handleTabClick(tab) {
      if (tab.name === 'management') {
        this.fetchStatistics()
        this.fetchApiVersions()
        this.fetchPopularApis()
      }
    },

    async fetchStatistics() {
      try {
        const response = await request({
          url: '/admin/api-docs/statistics',
          method: 'get'
        })
        this.statistics = response
        this.moduleStatistics = response.moduleStatistics || []
      } catch (error) {
        console.error('获取API统计失败', error)
      }
    },

    async fetchApiVersions() {
      try {
        const response = await request({
          url: '/admin/api-docs/versions',
          method: 'get'
        })
        this.apiVersions = response || []
      } catch (error) {
        console.error('获取API版本失败', error)
        // 使用模拟数据
        this.apiVersions = [
          {
            version: 'v1.2.0',
            releaseDate: '2025-06-15',
            status: '当前版本',
            description: '新增API文档管理功能，优化性能监控',
            usage: 85
          },
          {
            version: 'v1.1.0',
            releaseDate: '2025-05-20',
            status: '稳定版本',
            description: '增加操作日志功能，修复已知问题',
            usage: 12
          },
          {
            version: 'v1.0.0',
            releaseDate: '2025-04-10',
            status: '已废弃',
            description: '初始版本，基础功能实现',
            usage: 3
          }
        ]
      }
    },

    async fetchPopularApis() {
      try {
        const response = await request({
          url: '/admin/api-docs/popular',
          method: 'get'
        })
        this.popularApis = response || []
      } catch (error) {
        console.error('获取热门API失败', error)
        // 使用模拟数据
        this.popularApis = [
          {
            path: '/api/user/login',
            method: 'POST',
            calls: 8520,
            avgResponseTime: 95,
            errorRate: 0.2
          },
          {
            path: '/api/canteen/list',
            method: 'GET',
            calls: 6340,
            avgResponseTime: 120,
            errorRate: 0.1
          },
          {
            path: '/api/dish/search',
            method: 'GET',
            calls: 5890,
            avgResponseTime: 180,
            errorRate: 0.5
          },
          {
            path: '/api/user/profile',
            method: 'GET',
            calls: 4720,
            avgResponseTime: 85,
            errorRate: 0.0
          },
          {
            path: '/api/blog/list',
            method: 'GET',
            calls: 3650,
            avgResponseTime: 200,
            errorRate: 1.2
          }
        ]
      }
    },

    viewModuleDoc(moduleName) {
      this.activeTab = 'docs'
      this.$message.success(`切换到接口文档查看 ${moduleName} 模块`)
    },

    getProgressColor(coverage) {
      const percent = parseInt(coverage)
      if (percent >= 95) return '#67C23A'
      if (percent >= 80) return '#E6A23C'
      return '#F56C6C'
    },

    // 版本管理相关方法
    getVersionStatusType(status) {
      const statusMap = {
        '当前版本': 'success',
        '稳定版本': 'info',
        '已废弃': 'danger'
      }
      return statusMap[status] || 'info'
    },

    getUsageColor(usage) {
      if (usage >= 70) return '#67C23A'
      if (usage >= 30) return '#E6A23C'
      return '#F56C6C'
    },

    viewVersionDoc(version) {
      this.$message.info(`查看 ${version} 版本文档`)
    },

    compareVersion(version) {
      this.$message.info(`版本对比功能开发中... (${version})`)
    },

    removeVersion(version) {
      this.$confirm(`确定要移除版本 ${version} 吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success(`版本 ${version} 已移除`)
      })
    },

    // 热门API相关方法
    getRankClass(index) {
      if (index === 0) return 'rank-gold'
      if (index === 1) return 'rank-silver'
      if (index === 2) return 'rank-bronze'
      return 'rank-normal'
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

    getResponseTimeClass(time) {
      if (time <= 100) return 'response-fast'
      if (time <= 200) return 'response-normal'
      return 'response-slow'
    },

    getErrorRateClass(rate) {
      if (rate <= 0.5) return 'error-low'
      if (rate <= 2) return 'error-medium'
      return 'error-high'
    }
  }
}
</script>

<style scoped>
.api-doc-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.toolbar-card, .doc-card, .quick-actions-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.clearfix h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
  text-align: center;
}

.toolbar {
  margin-bottom: 20px;
}

.status-info {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.status-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.status-item i {
  margin-right: 8px;
  font-size: 16px;
}

.status-success {
  color: #67C23A;
}

.status-error {
  color: #F56C6C;
}

.status-checking {
  color: #E6A23C;
}

.doc-content {
  position: relative;
  min-height: 600px;
}

.doc-iframe {
  width: 100%;
  height: calc(100vh - 300px);
  min-height: 600px;
  border: none;
  border-radius: 6px;
  background-color: #fff;
}

.quick-actions-card {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 200px;
  z-index: 1000;
}

.quick-actions {
  text-align: center;
}

.el-select {
  width: 100%;
}

.el-input {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .api-doc-container {
    padding: 10px;
  }

  .quick-actions-card {
    position: relative;
    bottom: auto;
    right: auto;
    width: 100%;
  }

  .doc-iframe {
    height: calc(100vh - 400px);
    min-height: 400px;
  }
}

/* 加载动画 */
.el-loading-mask {
  border-radius: 6px;
}

/* 工具栏按钮样式 */
.toolbar .el-button {
  margin-right: 10px;
}

.toolbar .el-button:last-child {
  margin-right: 0;
}

/* 状态指示器动画 */
.status-checking {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

/* iframe错误页面样式 */
.iframe-error {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600px;
  background-color: #fafafa;
  border-radius: 6px;
}

.error-content {
  text-align: center;
  max-width: 500px;
  padding: 40px;
}

.error-content h3 {
  margin: 20px 0;
  color: #303133;
  font-size: 20px;
}

.error-content p {
  margin: 15px 0;
  color: #606266;
  line-height: 1.6;
}

.error-actions {
  margin: 30px 0;
}

.error-actions .el-button {
  margin: 0 10px;
}

.direct-link {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.direct-link a {
  color: #409EFF;
  text-decoration: none;
}

.direct-link a:hover {
  text-decoration: underline;
}

/* API管理页面样式 */
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
}

.module-stats-card, .performance-card, .version-card, .popular-apis-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.module-stats-card h3, .performance-card h3, .version-card h3, .popular-apis-card h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

/* 性能监控样式 */
.performance-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.performance-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.performance-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.performance-trend {
  font-size: 12px;
  font-weight: bold;
}

.performance-trend.up {
  color: #67C23A;
}

.performance-trend.down {
  color: #F56C6C;
}

/* 排名徽章样式 */
.rank-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  border-radius: 50%;
  color: white;
  font-weight: bold;
  font-size: 12px;
}

.rank-gold {
  background: linear-gradient(45deg, #FFD700, #FFA500);
}

.rank-silver {
  background: linear-gradient(45deg, #C0C0C0, #A9A9A9);
}

.rank-bronze {
  background: linear-gradient(45deg, #CD7F32, #B87333);
}

.rank-normal {
  background: #909399;
}

/* 响应时间样式 */
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

/* 错误率样式 */
.error-low {
  color: #67C23A;
  font-weight: bold;
}

.error-medium {
  color: #E6A23C;
}

.error-high {
  color: #F56C6C;
  font-weight: bold;
}
</style>