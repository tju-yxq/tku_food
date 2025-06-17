<template>
  <div class="dish-container">
    <el-card class="dish-card">
      <div slot="header" class="clearfix">
        <h2>菜品管理</h2>
      </div>

      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
          新增菜品
        </el-button>
      </div>

      <el-table :data="list" border fit highlight-current-row v-loading="listLoading">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="菜品名称"></el-table-column>
        <el-table-column prop="stallName" label="所属窗口"></el-table-column>
        <el-table-column prop="canteenName" label="所属食堂"></el-table-column>
        <el-table-column prop="price" label="价格(元)" width="120" align="center"></el-table-column>
        <el-table-column prop="liked" label="点赞数" width="100" align="center"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="{row}">
            <el-button type="primary" size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="query.current"
          :page-sizes="[10, 20, 50]"
          :page-size="query.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          style="margin-top: 20px; text-align: right;">
      </el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="600px">
      <el-form ref="dataForm" :model="dishForm" :rules="formRules" label-position="left" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜品名称" prop="name">
              <el-input v-model="dishForm.name" placeholder="请输入菜品名称" prefix-icon="el-icon-food"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属窗口" prop="stallId">
              <el-select v-model="dishForm.stallId" placeholder="请选择所属窗口" filterable style="width: 100%">
                <el-option v-for="item in stallList" :key="item.id" :label="item.name + ' (' + item.canteenName + ')'" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="dishForm.price" :precision="2" :step="0.1" :min="0" style="width: 100%">
                <template slot="append">元</template>
              </el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-input v-model="dishForm.category" placeholder="如：主食、小吃、饮品" prefix-icon="el-icon-menu"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="菜品描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="dishForm.description" placeholder="请输入菜品描述" />
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
  name: "DishManagement",
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      query: {
        current: 1,
        size: 10
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      dishForm: {
        id: undefined,
        name: '',
        stallId: '',
        price: 0.00,
        category: '',
        description: ''
      },
      stallList: [],
      formRules: {
        name: [
          { required: true, message: '请输入菜品名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        stallId: [
          { required: true, message: '请选择所属窗口', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' },
          { type: 'number', min: 0, message: '价格必须大于等于0', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // 查询参数模型
    queryModel() {
      return {
        current: this.query.current,
        size: this.query.size
      }
    },
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.dishForm.id,
        name: this.dishForm.name,
        stallId: this.dishForm.stallId,
        price: this.dishForm.price,
        category: this.dishForm.category,
        description: this.dishForm.description
      }
    }
  },
  created() {
    // 检查用户是否已登录，如果已登录则获取数据
    if (this.$store.getters.token) {
      this.fetchList()
      this.fetchSupportData()
    }
  },
  watch: {
    // 监听token变化，当用户登录后自动加载数据
    '$store.getters.token'(newToken) {
      if (newToken && this.list.length === 0) {
        this.fetchList()
        this.fetchSupportData()
      }
    }
  },
  methods: {
    fetchList() {
      this.listLoading = true
      request({
        url: '/admin/dishes/list',
        method: 'get',
        params: this.query
      }).then(response => {
        this.list = response.records
        this.total = response.total
        this.listLoading = false
      })
    },
    fetchSupportData() {
      request({ url: '/admin/stalls/list', method: 'get', params: { current: 1, size: 1000 } }).then(res => { this.stallList = res.records || [] })
    },
    handleSizeChange(val) {
      this.query.size = val;
      this.fetchList();
    },
    handleCurrentChange(val) {
      this.query.current = val;
      this.fetchList();
    },
    resetForm() {
      this.dishForm = {
        id: undefined,
        name: '',
        stallId: '',
        price: 0.00,
        category: '',
        description: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增菜品'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/dishes',
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
      this.dishForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑菜品'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/dishes',
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
      this.$confirm(`此操作将永久删除菜品 "${row.name}", 是否继续?`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/dishes/${row.id}`,
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
.dish-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.dish-card {
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