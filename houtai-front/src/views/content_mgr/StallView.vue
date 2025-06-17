<template>
  <div class="stall-container">
    <el-card class="stall-card">
      <div slot="header" class="clearfix">
        <h2>窗口管理</h2>
      </div>

      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
          新增窗口
        </el-button>
      </div>

      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="窗口名称" min-width="120"></el-table-column>
        <el-table-column prop="canteenName" label="所属食堂" min-width="120"></el-table-column>
        <el-table-column prop="typeName" label="窗口类型" width="120"></el-table-column>
        <el-table-column prop="location" label="位置" width="100"></el-table-column>
        <el-table-column prop="openHours" label="营业时间" width="120"></el-table-column>
        <el-table-column prop="avgPrice" label="人均消费" width="100" align="center">
          <template slot-scope="{row}">
            <span>{{ row.avgPrice ? '¥' + row.avgPrice : '暂无数据' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="综合评分" width="100" align="center">
          <template slot-scope="{row}">
            <span>{{ row.score ? row.score.toFixed(1) + '分' : '暂无评分' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
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
      <el-form ref="dataForm" :model="stallForm" :rules="formRules" label-position="left" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="窗口名称" prop="name">
              <el-input v-model="stallForm.name" placeholder="请输入窗口名称" prefix-icon="el-icon-shop"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属食堂" prop="canteenId">
              <el-select v-model="stallForm.canteenId" placeholder="请选择食堂" filterable style="width: 100%">
                <el-option v-for="item in canteenList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="窗口类型" prop="typeId">
              <el-select v-model="stallForm.typeId" placeholder="请选择窗口类型" style="width: 100%">
                <el-option v-for="item in stallTypeList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="窗口位置" prop="location">
              <el-input v-model="stallForm.location" placeholder="如：二楼E05" prefix-icon="el-icon-location"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="营业时间" prop="openHours">
          <el-input v-model="stallForm.openHours" placeholder="如：10:00-20:00" prefix-icon="el-icon-time"></el-input>
        </el-form-item>

        <el-form-item label="窗口介绍" prop="introduction">
          <el-input type="textarea" :rows="3" v-model="stallForm.introduction" placeholder="请输入窗口介绍" />
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
  name: "StallManagement",
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
      stallForm: {
        id: undefined,
        name: '',
        canteenId: '',
        typeId: '',
        location: '',
        openHours: '',
        introduction: ''
      },
      canteenList: [],
      stallTypeList: [],
      formRules: {
        name: [
          { required: true, message: '请输入窗口名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        canteenId: [
          { required: true, message: '请选择所属食堂', trigger: 'change' }
        ],
        typeId: [
          { required: true, message: '请选择窗口类型', trigger: 'change' }
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
        id: this.stallForm.id,
        name: this.stallForm.name,
        canteenId: this.stallForm.canteenId,
        typeId: this.stallForm.typeId,
        location: this.stallForm.location,
        openHours: this.stallForm.openHours,
        introduction: this.stallForm.introduction
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
        url: '/admin/stalls/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取窗口列表失败', error)
        this.$message.error('获取窗口列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    fetchSupportData() {
      // 获取食堂列表用于下拉框 (获取所有，所以设置一个较大的size)
      request({ url: '/admin/canteens/list', method: 'get', params: { current: 1, size: 1000 } }).then(res => { this.canteenList = res.records || [] })
      // 获取窗口类型列表用于下拉框
      request({ url: '/admin/stall-types/list', method: 'get' }).then(res => { this.stallTypeList = res || [] })
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
      this.stallForm = {
        id: undefined,
        name: '',
        canteenId: '',
        typeId: '',
        location: '',
        openHours: '',
        introduction: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增窗口'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/stalls',
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
      this.stallForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑窗口'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/stalls',
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
      this.$confirm(`此操作将永久删除窗口 "${row.name}", 是否继续?`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/stalls/${row.id}`,
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
.stall-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.stall-card {
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