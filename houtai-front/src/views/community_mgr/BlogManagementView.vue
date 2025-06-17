<template>
  <div class="blog-container">
    <el-card class="blog-card">
      <div slot="header" class="clearfix">
        <h2>博客管理</h2>
      </div>

      <!-- 搜索区域 -->
      <div class="search-container">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.title"
              placeholder="请输入博客标题"
              prefix-icon="el-icon-document"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-input
              v-model="searchForm.userId"
              placeholder="请输入作者ID"
              prefix-icon="el-icon-user"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100%">
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="审核通过" :value="1"></el-option>
              <el-option label="审核拒绝" :value="2"></el-option>
              <el-option label="用户隐藏" :value="3"></el-option>
              <el-option label="管理员删除" :value="4"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="博客ID" width="100" align="center"></el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template slot-scope="{row}">
            <el-button type="text" @click="handleView(row)">{{ row.title }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="作者ID" width="100" align="center"></el-table-column>
        <el-table-column prop="liked" label="点赞数" width="100" align="center"></el-table-column>
        <el-table-column prop="comments" label="评论数" width="100" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="置顶" width="80" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.isTop === 1 ? 'warning' : 'info'" size="mini">
              {{ row.isTop === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isQuality" label="加精" width="80" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.isQuality === 1 ? 'success' : 'info'" size="mini">
              {{ row.isQuality === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="info" @click="handleView(row)">详情</el-button>
            <el-dropdown @command="handleCommand" trigger="click">
              <el-button size="mini" type="primary">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-if="row.status === 0" :command="{action: 'approve', row}">审核通过</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 0" :command="{action: 'reject', row}">审核拒绝</el-dropdown-item>
                <el-dropdown-item :command="{action: 'toggleTop', row}">
                  {{ row.isTop === 1 ? '取消置顶' : '设为置顶' }}
                </el-dropdown-item>
                <el-dropdown-item :command="{action: 'toggleQuality', row}">
                  {{ row.isQuality === 1 ? '取消加精' : '设为加精' }}
                </el-dropdown-item>
                <el-dropdown-item :command="{action: 'delete', row}" divided>删除博客</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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

    <!-- 博客详情对话框 -->
    <el-dialog title="博客详情" :visible.sync="dialogVisible" width="800px">
      <div v-if="currentBlog">
        <el-row :gutter="20">
          <el-col :span="12">
            <p><strong>博客ID：</strong>{{ currentBlog.id }}</p>
            <p><strong>标题：</strong>{{ currentBlog.title }}</p>
            <p><strong>作者ID：</strong>{{ currentBlog.userId }}</p>
            <p><strong>状态：</strong>
              <el-tag :type="getStatusType(currentBlog.status)">
                {{ getStatusText(currentBlog.status) }}
              </el-tag>
            </p>
          </el-col>
          <el-col :span="12">
            <p><strong>点赞数：</strong>{{ currentBlog.liked }}</p>
            <p><strong>评论数：</strong>{{ currentBlog.comments }}</p>
            <p><strong>置顶：</strong>{{ currentBlog.isTop === 1 ? '是' : '否' }}</p>
            <p><strong>加精：</strong>{{ currentBlog.isQuality === 1 ? '是' : '否' }}</p>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <p><strong>发布时间：</strong>{{ currentBlog.createTime | formatDate }}</p>
            <p><strong>更新时间：</strong>{{ currentBlog.updateTime | formatDate }}</p>
            <div style="margin-top: 20px;">
              <strong>博客内容：</strong>
              <div style="border: 1px solid #dcdfe6; padding: 15px; margin-top: 10px; border-radius: 4px; background-color: #fafafa;">
                {{ currentBlog.content }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'BlogManagement',
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
        title: '',
        userId: '',
        status: ''
      },
      dialogVisible: false,
      currentBlog: null
    }
  },
  computed: {
    // 查询参数模型
    queryModel() {
      return {
        current: this.query.current,
        size: this.query.size,
        title: this.searchForm.title || undefined,
        userId: this.searchForm.userId || undefined,
        status: this.searchForm.status !== '' ? this.searchForm.status : undefined
      }
    }
  },
  created() {
    // 检查用户是否已登录，如果已登录则获取数据
    if (this.$store.getters.token) {
      this.fetchList()
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
        url: '/admin/blogs/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取博客列表失败', error)
        this.$message.error('获取博客列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    handleSearch() {
      this.query.current = 1
      this.fetchList()
    },
    resetSearch() {
      this.searchForm = {
        title: '',
        userId: '',
        status: ''
      }
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
      this.currentBlog = row
      this.dialogVisible = true
    },
    handleCommand(command) {
      const { action, row } = command
      switch (action) {
        case 'approve':
          this.handleAudit(row, 1, '通过')
          break
        case 'reject':
          this.handleAudit(row, 2, '拒绝')
          break
        case 'toggleTop':
          this.handleToggleSpecial(row, 'isTop', row.isTop === 1 ? 0 : 1)
          break
        case 'toggleQuality':
          this.handleToggleSpecial(row, 'isQuality', row.isQuality === 1 ? 0 : 1)
          break
        case 'delete':
          this.handleDelete(row)
          break
      }
    },
    handleAudit(row, status, action) {
      this.$confirm(`确定要${action}博客 "${row.title}" 吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/blogs/${row.id}/audit`,
          method: 'put',
          data: { status }
        }).then(() => {
          this.$message.success(`${action}成功！`)
          this.fetchList()
        }).catch(error => {
          this.$message.error(`${action}失败：` + (error.message || '请稍后重试'))
        })
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    handleToggleSpecial(row, field, value) {
      const action = value === 1 ? '设置' : '取消'
      const type = field === 'isTop' ? '置顶' : '加精'

      this.$confirm(`确定要${action}${type}博客 "${row.title}" 吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        const data = {}
        data[field] = value === 1

        request({
          url: `/admin/blogs/${row.id}/specials`,
          method: 'put',
          data
        }).then(() => {
          this.$message.success(`${action}${type}成功！`)
          this.fetchList()
        }).catch(error => {
          this.$message.error(`${action}${type}失败：` + (error.message || '请稍后重试'))
        })
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },
    handleDelete(row) {
      this.$confirm(`确定要删除博客 "${row.title}" 吗？此操作不可恢复！`, '警告', {
        type: 'error'
      }).then(() => {
        request({
          url: `/admin/blogs/${row.id}`,
          method: 'delete'
        }).then(() => {
          this.$message.success('删除成功！')
          this.fetchList()
        }).catch(error => {
          this.$message.error('删除失败：' + (error.message || '请稍后重试'))
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    getStatusType(status) {
      const types = {
        0: 'warning',  // 待审核
        1: 'success',  // 审核通过
        2: 'danger',   // 审核拒绝
        3: 'info',     // 用户隐藏
        4: 'danger'    // 管理员删除
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '待审核',
        1: '审核通过',
        2: '审核拒绝',
        3: '用户隐藏',
        4: '管理员删除'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.blog-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.blog-card {
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
