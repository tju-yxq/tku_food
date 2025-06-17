<template>
  <div class="stall-type-container">
    <el-card class="stall-type-card">
      <div slot="header" class="clearfix">
        <h2>窗口类型管理</h2>
      </div>

      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
          新增窗口类型
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="80px" style="width: 400px; margin-left:50px;">
        <el-form-item label="类型名称">
          <el-input v-model="temp.name" placeholder="请输入窗口类型名称" />
        </el-form-item>
        <el-form-item label="图标URL">
          <el-input v-model="temp.icon" placeholder="请输入图标的URL" />
        </el-form-item>
        <el-form-item label="排序值">
          <el-input v-model.number="temp.sort" placeholder="数字越小，排序越靠前" />
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
  name: "StallTypeManagement",
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
        url: '/admin/stall-types/list',
        method: 'get'
      }).then(response => {
        this.list = response
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        icon: '',
        sort: 0
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增窗口类型'
      this.dialogFormVisible = true
    },
    createData() {
      request({
        url: '/admin/stall-types',
        method: 'post',
        data: this.temp
      }).then(() => {
        this.dialogFormVisible = false
        this.$message.success('新增成功！')
        this.fetchList()
      })
    },
    handleEdit(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑窗口类型'
      this.dialogFormVisible = true
    },
    updateData() {
      request({
        url: '/admin/stall-types',
        method: 'put',
        data: this.temp
      }).then(() => {
        this.dialogFormVisible = false
        this.$message.success('更新成功！')
        this.fetchList()
      })
    },
    handleDelete(row) {
      this.$confirm(`此操作将永久删除窗口类型 "${row.name}", 是否继续?`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/stall-types/${row.id}`,
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
.app-container {
  padding: 20px;
}
</style>
