<template>
  <div class="campus-container">
    <el-card class="campus-card">
      <div slot="header" class="clearfix">
        <h2>校区管理</h2>
      </div>

      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
          新增校区
        </el-button>
      </div>

      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="100" align="center"></el-table-column>
        <el-table-column prop="name" label="校区名称" min-width="150"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="200"></el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button type="primary" size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="500px">
      <el-form ref="dataForm" :model="campusForm" :rules="formRules" label-position="left" label-width="100px">
        <el-form-item label="校区名称" prop="name">
          <el-input v-model="campusForm.name" placeholder="请输入校区名称" prefix-icon="el-icon-school"></el-input>
        </el-form-item>
        <el-form-item label="校区地址" prop="address">
          <el-input v-model="campusForm.address" placeholder="请输入校区地址" prefix-icon="el-icon-location"></el-input>
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
// 引入请求工具
import request from '@/utils/request'

export default {
  name: "CampusManagement",
  data() {
    return {
      list: [],
      listLoading: true,
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      campusForm: {
        id: undefined,
        name: '',
        address: ''
      },
      formRules: {
        name: [
          { required: true, message: '请输入校区名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入校区地址', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.campusForm.id,
        name: this.campusForm.name,
        address: this.campusForm.address
      }
    }
  },
  // 在组件创建后立即获取数据
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
  methods: {
    // 1. 获取校区列表 (Read)
    fetchList() {
      this.listLoading = true
      // 使用 request 工具请求后端接口
      request({
        url: '/admin/campuses/list',
        method: 'get'
      }).then(response => {
        this.list = response
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },

    resetForm() {
      this.campusForm = {
        id: undefined,
        name: '',
        address: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增校区'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/campuses',
            method: 'post',
            data: this.submitModel
          }).then(() => {
            this.dialogFormVisible = false
            this.$message.success('新增成功！')
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
      this.campusForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑校区'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/campuses',
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

    // 4. 处理删除 (Delete)
    handleDelete(row) {
      this.$confirm('此操作将永久删除该校区, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/campuses/${row.id}`,
          method: 'delete'
        }).then(() => {
          this.$message({
            type: 'success',
            message: '删除成功!'
          });
          this.fetchList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  }
}
</script>

<style scoped>
.campus-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.campus-card {
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
</style>