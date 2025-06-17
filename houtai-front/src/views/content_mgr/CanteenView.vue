<template>
  <div class="canteen-container">
    <el-card class="canteen-card">
      <div slot="header">
        <span>食堂管理</span>
      </div>

      <div class="toolbar">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
          新增食堂
        </el-button>
      </div>

      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="食堂名称" min-width="120"></el-table-column>
        <el-table-column prop="campusName" label="所属校区" width="120"></el-table-column>
        <el-table-column prop="typeName" label="食堂类型" width="120"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="150"></el-table-column>
        <el-table-column prop="floor" label="楼层" width="100"></el-table-column>
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
      <el-form ref="dataForm" :model="canteenForm" :rules="formRules" label-position="left" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="食堂名称" prop="name">
              <el-input v-model="canteenForm.name" placeholder="请输入食堂名称" prefix-icon="el-icon-office-building"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属校区" prop="campusId">
              <el-select v-model="canteenForm.campusId" placeholder="请选择校区" style="width: 100%">
                <el-option v-for="item in campusList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="食堂类型" prop="typeId">
          <el-select v-model="canteenForm.typeId" placeholder="请选择食堂类型" style="width: 100%">
            <el-option v-for="item in canteenTypeList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层信息" prop="floor">
              <el-input v-model="canteenForm.floor" placeholder="如：共三层" prefix-icon="el-icon-office-building"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="营业时间" prop="openHours">
              <el-input v-model="canteenForm.openHours" placeholder="如：07:00-21:00" prefix-icon="el-icon-time"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="address">
          <el-input v-model="canteenForm.address" placeholder="请输入详细地址" prefix-icon="el-icon-location"></el-input>
        </el-form-item>

        <el-form-item label="食堂介绍" prop="introduction">
          <el-input type="textarea" :rows="3" v-model="canteenForm.introduction" placeholder="请输入食堂介绍" />
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
  name: "CanteenManagement",
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
      canteenForm: {
        id: undefined,
        name: '',
        campusId: '',
        typeId: '',
        address: '',
        floor: '',
        openHours: '',
        introduction: ''
      },
      campusList: [],
      canteenTypeList: [],
      formRules: {
        name: [
          { required: true, message: '请输入食堂名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        campusId: [
          { required: true, message: '请选择所属校区', trigger: 'change' }
        ],
        typeId: [
          { required: true, message: '请选择食堂类型', trigger: 'change' }
        ],
        address: [
          { required: true, message: '请输入详细地址', trigger: 'blur' }
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
        id: this.canteenForm.id,
        name: this.canteenForm.name,
        campusId: this.canteenForm.campusId,
        typeId: this.canteenForm.typeId,
        address: this.canteenForm.address,
        floor: this.canteenForm.floor,
        openHours: this.canteenForm.openHours,
        introduction: this.canteenForm.introduction
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
        url: '/admin/canteens/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取食堂列表失败', error)
        this.$message.error('获取食堂列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    fetchSupportData() {
      // 获取校区列表
      request({
        url: '/admin/campuses/list',
        method: 'get'
      }).then(response => {
        this.campusList = response || []
      }).catch(error => {
        console.error('获取校区列表失败', error)
      })

      // 获取食堂类型列表
      request({
        url: '/admin/canteen-types/list',
        method: 'get'
      }).then(response => {
        this.canteenTypeList = response || []
      }).catch(error => {
        console.error('获取食堂类型列表失败', error)
      })
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
      this.canteenForm = {
        id: undefined,
        name: '',
        campusId: '',
        typeId: '',
        address: '',
        floor: '',
        openHours: '',
        introduction: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增食堂'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/canteens',
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
      this.canteenForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑食堂'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/canteens',
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
      this.$confirm(`此操作将永久删除食堂 "${row.name}", 是否继续?`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/canteens/${row.id}`,
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
.canteen-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.canteen-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
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