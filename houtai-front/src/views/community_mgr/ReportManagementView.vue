<template>
  <div class="report-container">
    <el-card class="report-card">
      <div slot="header" class="clearfix">
        <h2>举报管理</h2>
      </div>

      <!-- 搜索区域 -->
      <div class="search-container">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.reporterId"
              placeholder="请输入举报人ID"
              prefix-icon="el-icon-user"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.targetType" placeholder="请选择举报对象类型" clearable style="width: 100%">
              <el-option label="博客" :value="1"></el-option>
              <el-option label="评论" :value="2"></el-option>
              <el-option label="用户" :value="3"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.status" placeholder="请选择处理状态" clearable style="width: 100%">
              <el-option label="待处理" :value="0"></el-option>
              <el-option label="已处理" :value="1"></el-option>
              <el-option label="已驳回" :value="2"></el-option>
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="举报ID" width="100" align="center"></el-table-column>
        <el-table-column prop="reporterId" label="举报人ID" width="120" align="center"></el-table-column>
        <el-table-column prop="targetId" label="被举报对象ID" width="140" align="center"></el-table-column>
        <el-table-column prop="targetType" label="对象类型" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getTargetTypeColor(row.targetType)">
              {{ getTargetTypeText(row.targetType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="举报原因" min-width="150"></el-table-column>
        <el-table-column prop="description" label="详细描述" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="处理状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="举报时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button v-if="row.status === 0" size="mini" type="success" @click="handleProcess(row)">处理</el-button>
            <el-button size="mini" type="info" @click="handleView(row)">详情</el-button>
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

    <!-- 处理举报对话框 -->
    <el-dialog title="处理举报" :visible.sync="processDialogVisible" width="600px">
      <div v-if="currentReport">
        <el-row :gutter="20">
          <el-col :span="12">
            <p><strong>举报ID：</strong>{{ currentReport.id }}</p>
            <p><strong>举报人ID：</strong>{{ currentReport.reporterId }}</p>
            <p><strong>被举报对象ID：</strong>{{ currentReport.targetId }}</p>
            <p><strong>对象类型：</strong>{{ getTargetTypeText(currentReport.targetType) }}</p>
          </el-col>
          <el-col :span="12">
            <p><strong>举报原因：</strong>{{ currentReport.reason }}</p>
            <p><strong>举报时间：</strong>{{ currentReport.createTime | formatDate }}</p>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <p><strong>详细描述：</strong></p>
            <div style="border: 1px solid #dcdfe6; padding: 15px; border-radius: 4px; background-color: #fafafa;">
              {{ currentReport.description }}
            </div>
          </el-col>
        </el-row>

        <el-form ref="processForm" :model="processForm" :rules="processRules" label-width="100px" style="margin-top: 20px;">
          <el-form-item label="处理结果" prop="status">
            <el-radio-group v-model="processForm.status">
              <el-radio :label="1">处理通过（举报有效）</el-radio>
              <el-radio :label="2">驳回举报（举报无效）</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="处理说明" prop="result">
            <el-input type="textarea" :rows="4" v-model="processForm.result" placeholder="请输入处理说明"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">确认处理</el-button>
      </div>
    </el-dialog>

    <!-- 举报详情对话框 -->
    <el-dialog title="举报详情" :visible.sync="detailDialogVisible" width="700px">
      <div v-if="currentReport">
        <el-row :gutter="20">
          <el-col :span="12">
            <p><strong>举报ID：</strong>{{ currentReport.id }}</p>
            <p><strong>举报人ID：</strong>{{ currentReport.reporterId }}</p>
            <p><strong>被举报对象ID：</strong>{{ currentReport.targetId }}</p>
            <p><strong>对象类型：</strong>{{ getTargetTypeText(currentReport.targetType) }}</p>
            <p><strong>举报原因：</strong>{{ currentReport.reason }}</p>
          </el-col>
          <el-col :span="12">
            <p><strong>处理状态：</strong>
              <el-tag :type="getStatusType(currentReport.status)">
                {{ getStatusText(currentReport.status) }}
              </el-tag>
            </p>
            <p><strong>举报时间：</strong>{{ currentReport.createTime | formatDate }}</p>
            <p v-if="currentReport.handleTime"><strong>处理时间：</strong>{{ currentReport.handleTime | formatDate }}</p>
            <p v-if="currentReport.handlerId"><strong>处理人ID：</strong>{{ currentReport.handlerId }}</p>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <p><strong>详细描述：</strong></p>
            <div style="border: 1px solid #dcdfe6; padding: 15px; border-radius: 4px; background-color: #fafafa; margin-bottom: 15px;">
              {{ currentReport.description }}
            </div>
            <p v-if="currentReport.result"><strong>处理结果：</strong></p>
            <div v-if="currentReport.result" style="border: 1px solid #dcdfe6; padding: 15px; border-radius: 4px; background-color: #f0f9ff;">
              {{ currentReport.result }}
            </div>
          </el-col>
        </el-row>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'ReportManagement',
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
        reporterId: '',
        targetType: '',
        status: ''
      },
      processDialogVisible: false,
      detailDialogVisible: false,
      currentReport: null,
      processForm: {
        status: 1,
        result: ''
      },
      processRules: {
        status: [
          { required: true, message: '请选择处理结果', trigger: 'change' }
        ],
        result: [
          { required: true, message: '请输入处理说明', trigger: 'blur' }
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
        reporterId: this.searchForm.reporterId || undefined,
        targetType: this.searchForm.targetType !== '' ? this.searchForm.targetType : undefined,
        status: this.searchForm.status !== '' ? this.searchForm.status : undefined
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
        url: '/admin/reports/list',
        method: 'get',
        params: this.queryModel
      }).then(response => {
        this.list = response.records || response
        this.total = response.total || 0
        this.listLoading = false
      }).catch(error => {
        console.error('获取举报列表失败', error)
        this.$message.error('获取举报列表失败：' + (error.message || '请稍后重试'))
        this.listLoading = false
      })
    },
    handleSearch() {
      this.query.current = 1
      this.fetchList()
    },
    resetSearch() {
      this.searchForm = {
        reporterId: '',
        targetType: '',
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
    handleProcess(row) {
      this.currentReport = row
      this.processForm = {
        status: 1,
        result: ''
      }
      this.processDialogVisible = true
    },
    handleView(row) {
      this.currentReport = row
      this.detailDialogVisible = true
    },
    submitProcess() {
      this.$refs.processForm.validate((valid) => {
        if (valid) {
          request({
            url: `/admin/reports/${this.currentReport.id}/handle`,
            method: 'put',
            data: this.processForm
          }).then(() => {
            this.processDialogVisible = false
            this.$message.success('处理成功！')
            this.fetchList()
          }).catch(error => {
            this.$message.error('处理失败：' + (error.message || '请稍后重试'))
          })
        } else {
          return false
        }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定要删除举报记录 "${row.id}" 吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        request({
          url: `/admin/reports/${row.id}`,
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
    getTargetTypeText(type) {
      const texts = {
        1: '博客',
        2: '评论',
        3: '用户'
      }
      return texts[type] || '未知'
    },
    getTargetTypeColor(type) {
      const colors = {
        1: 'primary',
        2: 'success',
        3: 'warning'
      }
      return colors[type] || 'info'
    },
    getStatusType(status) {
      const types = {
        0: 'warning',  // 待处理
        1: 'success',  // 已处理
        2: 'danger'    // 已驳回
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        0: '待处理',
        1: '已处理',
        2: '已驳回'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.report-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.report-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.clearfix h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
  text-align: center;
}

.search-container {
  margin-bottom: 20px;
}

.data-table {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>