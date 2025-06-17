<template>
  <div class="canteen-type-container">
    <el-card class="canteen-type-card">
      <div slot="header" class="clearfix">
        <h2>食堂类型管理</h2>
      </div>

      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
          新增食堂类型
        </el-button>
      </div>

      <el-table :data="list" border fit highlight-current-row v-loading="listLoading">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="类型名称"></el-table-column>
        <el-table-column prop="icon" label="图标URL"></el-table-column>
        <el-table-column prop="sort" label="排序值" width="100" align="center"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="{row}">
            <el-button type="primary" size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="500px">
      <el-form ref="dataForm" :model="typeForm" :rules="formRules" label-position="left" label-width="100px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="typeForm.name" placeholder="请输入食堂类型名称" prefix-icon="el-icon-menu"></el-input>
        </el-form-item>
        <el-form-item label="图标URL" prop="icon">
          <el-input v-model="typeForm.icon" placeholder="请输入图标的URL" prefix-icon="el-icon-picture"></el-input>
        </el-form-item>
        <el-form-item label="排序值" prop="sort">
          <el-input-number v-model="typeForm.sort" :min="0" placeholder="数字越小，排序越靠前" style="width: 100%"></el-input-number>
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
  name: "CanteenTypeManagement",
  data() {
    return {
      list: [],
      listLoading: true,
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      typeForm: {
        id: undefined,
        name: '',
        icon: '',
        sort: 0
      },
      formRules: {
        name: [
          { required: true, message: '请输入类型名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.typeForm.id,
        name: this.typeForm.name,
        icon: this.typeForm.icon,
        sort: this.typeForm.sort
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
  methods: {
    fetchList() {
      this.listLoading = true
      request({
        url: '/admin/canteen-types/list',
        method: 'get'
      }).then(response => {
        this.list = response
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    resetForm() {
      this.typeForm = {
        id: undefined,
        name: '',
        icon: '',
        sort: 0
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增食堂类型'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/canteen-types',
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
      this.typeForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑食堂类型'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/canteen-types',
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
      this.$confirm(`此操作将永久删除食堂类型 "${row.name}", 是否继续?`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/canteen-types/${row.id}`,
          method: 'delete'
        }).then(() => {
          this.$message.success('删除成功!')
          this.fetchList()
        })
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    }
  }
}
</script>

<style scoped>
.canteen-type-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.canteen-type-card {
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

.el-form-item {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
