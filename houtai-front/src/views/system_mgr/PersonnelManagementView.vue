<template>
  <div class="personnel-container">
    <el-card class="personnel-card">
      <div slot="header" class="clearfix">
        <h2>人员管理</h2>
      </div>

      <!-- 搜索和新增区域 -->
      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入管理员账号"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入姓名"
              prefix-icon="el-icon-user"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
          <el-col :span="6" style="text-align: right;">
            <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增管理员</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="list" 
        border 
        fit 
        highlight-current-row 
        v-loading="listLoading" 
        class="data-table"
        row-key="id"
        stripe>
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="username" label="管理员账号" min-width="150"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column label="角色" width="150">
          <template slot-scope="{row}">
            <el-tag size="medium" effect="plain" type="primary">
              {{ row.roleName || '未分配角色' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" effect="dark">
              {{ row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="warning" icon="el-icon-key" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="600px">
      <el-form ref="dataForm" :model="adminForm" :rules="formRules" label-position="right" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="管理员账号" prop="username">
              <el-input v-model="adminForm.username" placeholder="请输入管理员账号" prefix-icon="el-icon-user" :disabled="dialogStatus==='edit'"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="adminForm.name" placeholder="请输入姓名" prefix-icon="el-icon-edit"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="dialogStatus==='create'">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="adminForm.password" type="password" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="adminForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="el-icon-lock" show-password></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="角色" prop="roleId">
          <el-select v-model="adminForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.name"
              :value="role.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="adminForm.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">禁用</el-radio>
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
  name: 'PersonnelManagement',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.adminForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      list: [],
      total: 0,
      listLoading: true,
      query: {
        current: 1,
        size: 10
      },
      searchForm: {
        username: '',
        name: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      roleOptions: [],
      adminForm: {
        id: undefined,
        username: '',
        name: '',
        password: '',
        confirmPassword: '',
        roleId: undefined,
        status: 0  // 0=启用，1=禁用
      },
      formRules: {
        username: [
          { required: true, message: '请输入管理员账号', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        roleId: [
          { required: true, message: '请选择角色', trigger: 'change' }
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
        username: this.searchForm.username || undefined,
        name: this.searchForm.name || undefined
      }
    },
    // 创建/编辑数据模型
    submitModel() {
      const model = {
        id: this.adminForm.id,
        username: this.adminForm.username,
        name: this.adminForm.name,
        roleId: this.adminForm.roleId,
        status: this.adminForm.status
      }
      
      // 只有在创建时才添加密码字段
      if (this.dialogStatus === 'create') {
        model.password = this.adminForm.password
      }
      
      return model
    }
  },
  created() {
    this.fetchRoleOptions()
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
        url: '/admin/admins/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取管理员列表失败', error)
        this.$message.error('获取管理员列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    fetchRoleOptions() {
      request({
        url: '/admin/roles/list',
        method: 'get',
        params: { current: 1, size: 100 }
      }).then(response => {
        this.roleOptions = response.records || response || []
      }).catch(error => {
        console.error('获取角色列表失败', error)
        // 添加默认角色选项
        this.roleOptions = [
          { id: 1, name: '超级管理员' },
          { id: 2, name: '内容管理员' },
          { id: 3, name: '运营管理员' }
        ]
      })
    },
    handleSearch() {
      this.query.current = 1
      this.fetchList()
    },
    resetSearch() {
      this.searchForm = {
        username: '',
        name: ''
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
      this.adminForm = {
        id: undefined,
        username: '',
        name: '',
        password: '',
        confirmPassword: '',
        roleId: undefined,
        status: 0  // 0=启用，1=禁用
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增管理员'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/admins',
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
      this.adminForm = Object.assign({}, row)
      this.adminForm.roleId = row.roleId
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑管理员'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/admins',
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
    handleResetPassword(row) {
      this.$confirm(`确定要重置管理员 "${row.username}" 的密码吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/admins/${row.id}/reset-password`,
          method: 'put'
        }).then((response) => {
          this.$message.success(response || '密码重置成功！新密码为：123456')
        }).catch(error => {
          this.$message.error('密码重置失败：' + (error.message || '请稍后重试'))
        })
      }).catch(() => {
        this.$message.info('已取消重置')
      })
    },
    handleDelete(row) {
      this.$confirm(`确定要删除管理员 "${row.username}" 吗？此操作不可恢复！`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        request({
          url: `/admin/admins/${row.id}`,
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
    }
  }
}
</script>

<style lang="scss" scoped>
.personnel-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.personnel-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  
  .clearfix h2 {
    margin: 0;
    font-size: 20px;
    color: #303133;
    text-align: center;
  }
}

.toolbar {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.data-table {
  margin-bottom: 20px;
  
  .el-button {
    padding: 7px 12px;
    
    & + .el-button {
      margin-left: 5px;
    }
  }
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>