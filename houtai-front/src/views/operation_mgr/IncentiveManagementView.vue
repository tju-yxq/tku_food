<template>
  <div class="incentive-container">
    <el-card class="incentive-card">
      <div slot="header" class="clearfix">
        <h2>激励管理</h2>
      </div>

      <!-- 搜索和新增区域 -->
      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.ruleName"
              placeholder="请输入规则名称"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增激励规则</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="ruleName" label="规则名称" min-width="150"></el-table-column>
        <el-table-column prop="actionType" label="行为类型" width="120" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getActionTypeColor(row.actionType)">
              {{ getActionTypeText(row.actionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="credits" label="积分奖励" width="100" align="center">
          <template slot-scope="{row}">
            <span style="color: #67C23A;">+{{ row.credits }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="dailyLimit" label="每日限制" width="100" align="center">
          <template slot-scope="{row}">
            <span>{{ row.dailyLimit === -1 ? '无限制' : row.dailyLimit + '次' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDate }}</span>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="500px">
      <el-form ref="dataForm" :model="ruleForm" :rules="formRules" label-position="left" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称" prefix-icon="el-icon-edit"></el-input>
        </el-form-item>
        <el-form-item label="行为类型" prop="actionType">
          <el-select v-model="ruleForm.actionType" placeholder="请选择行为类型" style="width: 100%">
            <el-option label="用户签到" value="SIGN_IN"></el-option>
            <el-option label="发布博客" value="POST_BLOG"></el-option>
            <el-option label="发表评论" value="POST_COMMENT"></el-option>
            <el-option label="点赞博客" value="LIKE_BLOG"></el-option>
            <el-option label="分享内容" value="SHARE_CONTENT"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="积分奖励" prop="credits">
          <el-input-number v-model="ruleForm.credits" :min="1" :max="1000" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="每日限制" prop="dailyLimit">
          <el-input-number v-model="ruleForm.dailyLimit" :min="-1" style="width: 100%">
            <template slot="append">次（-1表示无限制）</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="ruleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="规则描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="ruleForm.description" placeholder="请输入规则描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'IncentiveManagement',
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
        ruleName: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      ruleForm: {
        id: undefined,
        ruleName: '',
        actionType: '',
        credits: 1,
        dailyLimit: -1,
        status: 1,
        description: ''
      },
      formRules: {
        ruleName: [
          { required: true, message: '请输入规则名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        actionType: [
          { required: true, message: '请选择行为类型', trigger: 'change' }
        ],
        credits: [
          { required: true, message: '请输入积分奖励', trigger: 'blur' }
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
        ruleName: this.searchForm.ruleName || undefined
      }
    },
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.ruleForm.id,
        ruleName: this.ruleForm.ruleName,
        actionType: this.ruleForm.actionType,
        credits: this.ruleForm.credits,
        dailyLimit: this.ruleForm.dailyLimit,
        status: this.ruleForm.status,
        description: this.ruleForm.description
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
          ruleName: '每日签到奖励',
          actionType: 'SIGN_IN',
          credits: 5,
          dailyLimit: 1,
          status: 1,
          description: '用户每日首次签到可获得积分奖励',
          createTime: '2025-06-01 10:00:00'
        },
        {
          id: 2,
          ruleName: '发布博客奖励',
          actionType: 'POST_BLOG',
          credits: 10,
          dailyLimit: 3,
          status: 1,
          description: '用户发布博客可获得积分奖励，每日最多3次',
          createTime: '2025-06-01 10:00:00'
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
        url: '/admin/incentive-rules/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取激励规则列表失败', error)
        this.$message.error('获取激励规则列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    handleSearch() {
      this.query.current = 1
      this.fetchList()
    },
    resetSearch() {
      this.searchForm = {
        ruleName: ''
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
      this.ruleForm = {
        id: undefined,
        ruleName: '',
        actionType: '',
        credits: 1,
        dailyLimit: -1,
        status: 1,
        description: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增激励规则'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/incentive-rules',
            method: 'post',
            data: this.submitModel
          }).then(() => {
            this.dialogFormVisible = false
            this.$message.success('新增成功！')
            this.query.current = 1
            this.fetchList()
          }).catch(error => {
            this.$message.error('新增失败：' + (error.message || '请稍后重试'))
          })
        } else {
          return false
        }
      })
    },
    handleEdit(row) {
      this.ruleForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑激励规则'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/incentive-rules',
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
    handleDelete(row) {
      this.$confirm(`确定要删除激励规则 "${row.ruleName}" 吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/incentive-rules/${row.id}`,
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
    getActionTypeText(type) {
      const texts = {
        'SIGN_IN': '用户签到',
        'POST_BLOG': '发布博客',
        'POST_COMMENT': '发表评论',
        'LIKE_BLOG': '点赞博客',
        'SHARE_CONTENT': '分享内容'
      }
      return texts[type] || '未知'
    },
    getActionTypeColor(type) {
      const colors = {
        'SIGN_IN': 'success',
        'POST_BLOG': 'primary',
        'POST_COMMENT': 'warning',
        'LIKE_BLOG': 'danger',
        'SHARE_CONTENT': 'info'
      }
      return colors[type] || 'info'
    }
  }
}
</script>

<style scoped>
.incentive-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.incentive-card {
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
