<template>
  <div class="log-container">
    <el-card class="log-card">
      <div slot="header" class="clearfix">
        <h2>日志管理</h2>
      </div>

      <!-- 搜索区域 -->
      <div class="search-container">
        <el-row :gutter="20">
          <el-col :span="4">
            <el-input
              v-model="searchForm.operatorName"
              placeholder="请输入操作人"
              prefix-icon="el-icon-user"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="4">
            <el-input
              v-model="searchForm.module"
              placeholder="请输入模块"
              prefix-icon="el-icon-menu"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="4">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100%">
              <el-option label="成功" :value="1"></el-option>
              <el-option label="失败" :value="0"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 100%">
            </el-date-picker>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
            <el-button type="danger" icon="el-icon-delete" @click="handleCleanup">清理日志</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="100" align="center"></el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="120"></el-table-column>
        <el-table-column prop="module" label="模块" width="120"></el-table-column>
        <el-table-column prop="operation" label="操作" width="120"></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="mini">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="耗时" width="80" align="center">
          <template slot-scope="{row}">
            <span>{{ row.executeTime }}ms</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP地址" width="120"></el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="info" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="query.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="query.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog title="日志详情" :visible.sync="detailDialogVisible" width="700px">
      <div v-if="currentLog">
        <el-row :gutter="20">
          <el-col :span="12">
            <p><strong>日志ID：</strong>{{ currentLog.id }}</p>
            <p><strong>操作人：</strong>{{ currentLog.operatorName }}</p>
            <p><strong>模块：</strong>{{ currentLog.module }}</p>
            <p><strong>操作：</strong>{{ currentLog.operation }}</p>
            <p><strong>状态：</strong>
              <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'" size="mini">
                {{ currentLog.status === 1 ? '成功' : '失败' }}
              </el-tag>
            </p>
          </el-col>
          <el-col :span="12">
            <p><strong>请求方法：</strong>{{ currentLog.method }}</p>
            <p><strong>执行时间：</strong>{{ currentLog.executeTime }}ms</p>
            <p><strong>IP地址：</strong>{{ currentLog.ip }}</p>
            <p><strong>操作时间：</strong>{{ currentLog.createTime | formatDate }}</p>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <p><strong>操作描述：</strong></p>
            <div style="border: 1px solid #dcdfe6; padding: 15px; border-radius: 4px; background-color: #fafafa; margin-bottom: 15px;">
              {{ currentLog.description }}
            </div>
            <p v-if="currentLog.errorMsg"><strong>错误信息：</strong></p>
            <div v-if="currentLog.errorMsg" style="border: 1px solid #f56c6c; padding: 15px; border-radius: 4px; background-color: #fef0f0; color: #f56c6c;">
              {{ currentLog.errorMsg }}
            </div>
          </el-col>
        </el-row>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'LogManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      query: {
        current: 1,
        size: 10
      },
      searchForm: {
        operatorName: '',
        module: '',
        status: ''
      },
      dateRange: [],
      detailDialogVisible: false,
      currentLog: null
    }
  },
  computed: {
    // 查询参数模型
    queryModel() {
      const params = {
        current: this.query.current,
        size: this.query.size,
        operatorName: this.searchForm.operatorName || undefined,
        module: this.searchForm.module || undefined,
        status: this.searchForm.status !== '' ? this.searchForm.status : undefined
      }

      if (this.dateRange && this.dateRange.length === 2) {
        params.startTime = this.formatDateTime(this.dateRange[0])
        params.endTime = this.formatDateTime(this.dateRange[1])
      }

      return params
    }
  },
  created() {
    // 检查用户是否已登录，如果已登录则获取数据
    if (this.$store.getters.token) {
      this.fetchList()
    } else {
      // 添加测试数据
      this.list = [
        {
          id: 1,
          operatorName: 'admin',
          module: '用户管理',
          operation: '新增用户',
          description: '管理员新增了用户：test@example.com',
          method: 'POST',
          status: 1,
          executeTime: 156,
          ip: '192.168.1.100',
          createTime: '2025-06-17 14:30:00'
        },
        {
          id: 2,
          operatorName: 'admin',
          module: '博客管理',
          operation: '删除博客',
          description: '管理员删除了博客：ID=123',
          method: 'DELETE',
          status: 0,
          executeTime: 89,
          ip: '192.168.1.100',
          errorMsg: '博客不存在或已被删除',
          createTime: '2025-06-17 14:25:00'
        }
      ]
      this.total = this.list.length
      this.listLoading = false
    }
  },
  watch: {
    // 监听token变化，当用户登录后自动加载数据
    '$store.getters.token'(newToken) {
      if (newToken && this.list.length === 0) {
        this.fetchList()
      }
    }
  },
  filters: {
    formatDate(value) {
      if (!value) return ''
      return new Date(value).toLocaleString()
    }
  },
  methods: {
    fetchList() {
      this.listLoading = true
      request({
        url: '/admin/logs/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取日志列表失败', error)
        this.$message.error('获取日志列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    handleSearch() {
      this.query.current = 1
      this.fetchList()
    },
    resetSearch() {
      this.searchForm = {
        operatorName: '',
        module: '',
        status: ''
      }
      this.dateRange = []
      this.query.current = 1
      this.fetchList()
    },
    handleSizeChange(val) {
      this.query.size = val
      this.fetchList()
    },
    handleCurrentChange(val) {
      this.query.current = val
      this.fetchList()
    },
    handleView(row) {
      this.currentLog = row
      this.detailDialogVisible = true
    },
    handleCleanup() {
      this.$prompt('请输入要保留的天数（默认30天）', '清理日志', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: '30',
        inputValidator: (value) => {
          if (!value || isNaN(value) || parseInt(value) < 1) {
            return '请输入有效的天数'
          }
          return true
        }
      }).then(({ value }) => {
        request({
          url: '/admin/logs/cleanup',
          method: 'delete',
          params: { days: parseInt(value) }
        }).then(() => {
          this.$message.success('清理完成！')
          this.fetchList()
        }).catch(error => {
          this.$message.error('清理失败：' + (error.message || '请稍后重试'))
        })
      }).catch(() => {
        this.$message.info('已取消清理')
      })
    },
    formatDateTime(date) {
      if (!date) return ''
      const d = new Date(date)
      return d.getFullYear() + '-' +
             String(d.getMonth() + 1).padStart(2, '0') + '-' +
             String(d.getDate()).padStart(2, '0') + ' ' +
             String(d.getHours()).padStart(2, '0') + ':' +
             String(d.getMinutes()).padStart(2, '0') + ':' +
             String(d.getSeconds()).padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.log-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.log-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.clearfix h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
  text-align: center;
}

.search-container {
  margin-bottom: 20px;
}

.data-table {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>