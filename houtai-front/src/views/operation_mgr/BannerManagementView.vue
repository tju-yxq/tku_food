<template>
  <div class="banner-container">
    <el-card class="banner-card">
      <div slot="header" class="clearfix">
        <h2>轮播图管理</h2>
      </div>

      <!-- 搜索和新增区域 -->
      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.title"
              placeholder="请输入轮播图标题"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100%">
              <el-option label="启用" :value="1"></el-option>
              <el-option label="禁用" :value="0"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增轮播图</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column label="预览" width="120" align="center">
          <template slot-scope="{row}">
            <el-image
              style="width: 100px; height: 50px; border-radius: 4px;"
              :src="row.imageUrl"
              fit="cover"
              :preview-src-list="[row.imageUrl]"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="150"></el-table-column>
        <el-table-column prop="linkUrl" label="跳转链接" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.startTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.endTime | formatDate }}</span>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible" width="600px">
      <el-form ref="dataForm" :model="bannerForm" :rules="formRules" label-position="left" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="bannerForm.title" placeholder="请输入轮播图标题" prefix-icon="el-icon-edit"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="bannerForm.sort" :min="0" placeholder="数字越小，排序越靠前" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="图片URL" prop="imageUrl">
          <el-input v-model="bannerForm.imageUrl" placeholder="请输入图片URL" prefix-icon="el-icon-picture">
            <el-button slot="append" @click="previewImage">预览</el-button>
          </el-input>
        </el-form-item>

        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="bannerForm.linkUrl" placeholder="请输入跳转链接" prefix-icon="el-icon-link"></el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="bannerForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="bannerForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="bannerForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="bannerForm.description" placeholder="请输入轮播图描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">确认</el-button>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog title="图片预览" :visible.sync="previewDialogVisible" width="500px">
      <div style="text-align: center;">
        <el-image
          style="max-width: 100%; max-height: 400px;"
          :src="bannerForm.imageUrl"
          fit="contain"
        >
          <div slot="error" class="image-slot">
            <i class="el-icon-picture-outline"></i>
            <p>图片加载失败</p>
          </div>
        </el-image>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'BannerManagement',
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
      previewDialogVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      bannerForm: {
        id: undefined,
        title: '',
        imageUrl: '',
        linkUrl: '',
        sort: 0,
        status: 1,
        startTime: '',
        endTime: '',
        description: ''
      },
      formRules: {
        title: [
          { required: true, message: '请输入轮播图标题', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        imageUrl: [
          { required: true, message: '请输入图片URL', trigger: 'blur' }
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
        id: this.bannerForm.id,
        title: this.bannerForm.title,
        imageUrl: this.bannerForm.imageUrl,
        linkUrl: this.bannerForm.linkUrl,
        sort: this.bannerForm.sort,
        status: this.bannerForm.status,
        startTime: this.bannerForm.startTime,
        endTime: this.bannerForm.endTime,
        description: this.bannerForm.description
      }
    }
  },
  created() {
    // 检查用户是否已登录，如果已登录则获取数据
    if (this.$store.getters.token) {
      this.fetchList()
    } else {
      // 添加测试数据用于预览功能演示
      this.list = [
        {
          id: 1,
          title: '夏日特饮推荐',
          imageUrl: 'https://gitee.com/Yu-xinqiang0413/images/raw/master/images/OIP-C.jpg',
          linkUrl: '/promotion/summer-drinks',
          sort: 1,
          status: 1,
          startTime: '2025-06-01 00:00:00',
          endTime: '2025-08-31 23:59:59',
          description: '夏日清爽特饮，限时优惠'
        },
        {
          id: 2,
          title: '新品上市',
          imageUrl: 'https://gitee.com/Yu-xinqiang0413/images/raw/master/images/OIP-C.jpg',
          linkUrl: '/products/new',
          sort: 2,
          status: 1,
          startTime: '2025-06-15 00:00:00',
          endTime: '2025-07-15 23:59:59',
          description: '全新产品震撼上市'
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
        url: '/admin/banners/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取轮播图列表失败', error)
        this.$message.error('获取轮播图列表失败：' + (error.message || '请稍后重试'))
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
      this.bannerForm = {
        id: undefined,
        title: '',
        imageUrl: '',
        linkUrl: '',
        sort: 0,
        status: 1,
        startTime: '',
        endTime: '',
        description: ''
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogTitle = '新增轮播图'
      this.dialogFormVisible = true
    },
    createData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/banners',
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
      this.bannerForm = Object.assign({}, row)
      this.dialogStatus = 'edit'
      this.dialogTitle = '编辑轮播图'
      this.dialogFormVisible = true
    },
    updateData() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          request({
            url: '/admin/banners',
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
      this.$confirm(`确定要删除轮播图 "${row.title}" 吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/banners/${row.id}`,
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
    previewImage() {
      if (!this.bannerForm.imageUrl) {
        this.$message.warning('请先输入图片URL')
        return
      }
      this.previewDialogVisible = true
    }
  }
}
</script>

<style scoped>
.banner-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.banner-card {
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

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;
  flex-direction: column;
}
</style>
