<template>
  <div class="notice-container">
    <el-card class="notice-card">
      <div slot="header" class="clearfix">
        <h2>公告管理</h2>
      </div>

      <!-- 搜索和新增区域 -->
      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.title"
              placeholder="请输入公告标题"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100%">
              <el-option label="草稿" :value="0"></el-option>
              <el-option label="已发布" :value="1"></el-option>
              <el-option label="已下线" :value="2"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-plus" @click="handleCreate">发布公告</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="title" label="公告标题" min-width="200">
          <template slot-scope="{row}">
            <el-button type="text" @click="handleView(row)">{{ row.title }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="公告类型" width="120" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getTypeColor(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getPriorityColor(row.priority)" size="mini">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
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
        <el-table-column prop="publishTime" label="发布时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.publishTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="700px">
      <el-form ref="dataForm" :model="noticeForm" :rules="formRules" label-position="left" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公告标题" prop="title">
              <el-input v-model="noticeForm.title" placeholder="请输入公告标题" prefix-icon="el-icon-edit"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="noticeForm.type" placeholder="请选择公告类型" style="width: 100%">
                <el-option label="系统通知" :value="1"></el-option>
                <el-option label="活动公告" :value="2"></el-option>
                <el-option label="维护通知" :value="3"></el-option>
                <el-option label="其他" :value="4"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="noticeForm.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="低" :value="1"></el-option>
                <el-option label="中" :value="2"></el-option>
                <el-option label="高" :value="3"></el-option>
                <el-option label="紧急" :value="4"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker
                v-model="noticeForm.publishTime"
                type="datetime"
                placeholder="选择发布时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否置顶" prop="isTop">
              <el-radio-group v-model="noticeForm.isTop">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="noticeForm.status">
                <el-radio :label="0">草稿</el-radio>
                <el-radio :label="1">发布</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公告内容" prop="content">
          <el-input type="textarea" :rows="6" v-model="noticeForm.content" placeholder="请输入公告内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">确认</el-button>
      </div>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog title="公告详情" :visible.sync="detailDialogVisible" width="600px">
      <div v-if="currentNotice">
        <el-row :gutter="20">
          <el-col :span="12">
            <p><strong>公告ID：</strong>{{ currentNotice.id }}</p>
            <p><strong>标题：</strong>{{ currentNotice.title }}</p>
            <p><strong>类型：</strong>{{ getTypeText(currentNotice.type) }}</p>
            <p><strong>优先级：</strong>{{ getPriorityText(currentNotice.priority) }}</p>
          </el-col>
          <el-col :span="12">
            <p><strong>状态：</strong>
              <el-tag :type="getStatusType(currentNotice.status)">
                {{ getStatusText(currentNotice.status) }}
              </el-tag>
            </p>
            <p><strong>置顶：</strong>{{ currentNotice.isTop === 1 ? '是' : '否' }}</p>
            <p><strong>发布时间：</strong>{{ currentNotice.publishTime | formatDate }}</p>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <div style="margin-top: 20px;">
              <strong>公告内容：</strong>
              <div style="border: 1px solid #dcdfe6; padding: 15px; margin-top: 10px; border-radius: 4px; background-color: #fafafa; white-space: pre-wrap;">{{ currentNotice.content }}</div>
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
  name: 'NoticeManagement',
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
        status: ''
      },
      dialogFormVisible: false,
      detailDialogVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      currentNotice: null,
      noticeForm: {
        id: undefined,
        title: '',
        content: '',
        type: 1,
        priority: 2,
        status: 0,
        isTop: 0,
        publishTime: ''
      },
      formRules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择公告类型', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    // 查询参数模型
    queryModel() {
      return {
        current: this.query.current,
        size: this.query.size,
        title: this.searchForm.title || undefined,
        status: this.searchForm.status !== '' ? this.searchForm.status : undefined
      }
    },
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.noticeForm.id,
        title: this.noticeForm.title,
        content: this.noticeForm.content,
        type: this.noticeForm.type,
        priority: this.noticeForm.priority,
        status: this.noticeForm.status,
        isTop: this.noticeForm.isTop,
        publishTime: this.noticeForm.publishTime
      }
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
          title: '系统维护通知',
          content: '系统将于今晚22:00-24:00进行维护升级，期间可能影响正常使用，请提前做好准备。',
          type: 3,
          priority: 3,
          status: 1,
          isTop: 1,
          publishTime: '2025-06-15 14:30:00'
        },
        {
          id: 2,
          title: '夏日美食节活动开始',
          content: '夏日美食节正式开始！各种优惠活动等你来参与，详情请查看活动页面。',
          type: 2,
          priority: 2,
          status: 1,
          isTop: 0,
          publishTime: '2025-06-10 09:00:00'
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
        url: '/admin/notices/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取公告列表失败', error)
        this.$message.error('获取公告列表失败：' + (error.message || '请稍后重试'))
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
    resetForm() {
      this.noticeForm = {
        id: undefined,
        title: '',
        content: '',
        type: 1,
        priority: 2,
        status: 0,
        isTop: 0,
        publishTime: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '发布公告'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/notices',
            method: 'post',
            data: this.submitModel
          }).then(() => {
            this.dialogFormVisible = false
            this.$message.success('发布成功！')
            this.query.current = 1
            this.fetchList()
          }).catch(error => {
            this.$message.error('发布失败：' + (error.message || '请稍后重试'))
          })
        } else {
          return false
        }
      })
    },
    handleEdit(row) {
      this.noticeForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑公告'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/notices',
            method: 'put',
            data: this.submitModel
          }).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新成功！')
            this.fetchList()
          }).catch(error => {
            this.$message.error('更新失败：' + (error.message || '请稍后重试'))
          })
        } else {
          return false
        }
      })
    },
    handleView(row) {
      this.currentNotice = row
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确定要删除公告 "${row.title}" 吗？此操作不可恢复！`, '警告', {
        type: 'error'
      }).then(() => {
        request({
          url: `/admin/notices/${row.id}`,
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
    getTypeText(type) {
      const texts = {
        1: '系统通知',
        2: '活动公告',
        3: '维护通知',
        4: '其他'
      }
      return texts[type] || '未知'
    },
    getTypeColor(type) {
      const colors = {
        1: 'primary',
        2: 'success',
        3: 'warning',
        4: 'info'
      }
      return colors[type] || 'info'
    },
    getPriorityText(priority) {
      const texts = {
        1: '低',
        2: '中',
        3: '高',
        4: '紧急'
      }
      return texts[priority] || '未知'
    },
    getPriorityColor(priority) {
      const colors = {
        1: 'info',
        2: 'primary',
        3: 'warning',
        4: 'danger'
      }
      return colors[priority] || 'info'
    },
    getStatusType(status) {
      const types = {
        0: 'info',     // 草稿
        1: 'success',  // 已发布
        2: 'danger'    // 已下线
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '草稿',
        1: '已发布',
        2: '已下线'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.notice-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.notice-card {
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

.data-table {
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
