<template>
  <div class="sensitive-word-container">
    <el-card class="sensitive-word-card">
      <div slot="header" class="clearfix">
        <h2>敏感词管理</h2>
      </div>

      <!-- 搜索和新增区域 -->
      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.word"
              placeholder="请输入敏感词"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-input
              v-model="searchForm.category"
              placeholder="请输入分类"
              prefix-icon="el-icon-menu"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增敏感词</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="100" align="center"></el-table-column>
        <el-table-column prop="word" label="敏感词" min-width="150"></el-table-column>
        <el-table-column prop="category" label="分类" width="120"></el-table-column>
        <el-table-column prop="level" label="敏感等级" width="120" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getLevelType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="adminId" label="添加人ID" width="120" align="center"></el-table-column>
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
      <el-form ref="dataForm" :model="wordForm" :rules="formRules" label-position="left" label-width="100px">
        <el-form-item label="敏感词" prop="word">
          <el-input v-model="wordForm.word" placeholder="请输入敏感词" prefix-icon="el-icon-warning"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="wordForm.category" placeholder="请选择或输入分类" filterable allow-create style="width: 100%">
            <el-option label="广告" value="广告"></el-option>
            <el-option label="政治" value="政治"></el-option>
            <el-option label="辱骂" value="辱骂"></el-option>
            <el-option label="色情" value="色情"></el-option>
            <el-option label="暴力" value="暴力"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="敏感等级" prop="level">
          <el-select v-model="wordForm.level" placeholder="请选择敏感等级" style="width: 100%">
            <el-option label="1级 (轻微)" :value="1"></el-option>
            <el-option label="2级 (一般)" :value="2"></el-option>
            <el-option label="3级 (中等)" :value="3"></el-option>
            <el-option label="4级 (严重)" :value="4"></el-option>
            <el-option label="5级 (极严重)" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="wordForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
  name: 'SensitiveWordsManagement',
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
        word: '',
        category: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      wordForm: {
        id: undefined,
        word: '',
        category: '',
        level: 1,
        status: 1
      },
      formRules: {
        word: [
          { required: true, message: '请输入敏感词', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        level: [
          { required: true, message: '请选择敏感等级', trigger: 'change' }
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
        word: this.searchForm.word || undefined,
        category: this.searchForm.category || undefined
      }
    },
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.wordForm.id,
        word: this.wordForm.word,
        category: this.wordForm.category,
        level: this.wordForm.level,
        status: this.wordForm.status
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
        url: '/admin/sensitive-words/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取敏感词列表失败', error)
        this.$message.error('获取敏感词列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    handleSearch() {
      this.query.current = 1
      this.fetchList()
    },
    resetSearch() {
      this.searchForm = {
        word: '',
        category: ''
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
      this.wordForm = {
        id: undefined,
        word: '',
        category: '',
        level: 1,
        status: 1
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增敏感词'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/sensitive-words',
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
      this.wordForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑敏感词'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/sensitive-words',
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
      this.$confirm(`确定要删除敏感词 "${row.word}" 吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/sensitive-words/${row.id}`,
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
    getLevelType(level) {
      const types = {
        1: 'success',
        2: 'primary',
        3: 'warning',
        4: 'danger',
        5: 'danger'
      }
      return types[level] || 'info'
    },
    getLevelText(level) {
      const texts = {
        1: '1级',
        2: '2级',
        3: '3级',
        4: '4级',
        5: '5级'
      }
      return texts[level] || '未知'
    }
  }
}
</script>

<style scoped>
.sensitive-word-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.sensitive-word-card {
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