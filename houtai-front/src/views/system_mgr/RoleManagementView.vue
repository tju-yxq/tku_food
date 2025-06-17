<template>
  <div class="role-container">
    <el-card class="role-card">
      <div slot="header" class="clearfix">
        <h2>角色管理</h2>
      </div>

      <!-- 搜索和新增区域 -->
      <div class="toolbar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.roleName"
              placeholder="请输入角色名称"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增角色</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <el-table :data="list" border fit highlight-current-row v-loading="listLoading" class="data-table">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="角色名称" min-width="150"></el-table-column>
        <el-table-column prop="description" label="描述" min-width="200"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" type="primary" @click="handleEditPermissions(row)">权限配置</el-button>
            <el-button size="mini" type="warning" @click="handleEdit(row)">编辑</el-button>
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
      <el-form ref="dataForm" :model="roleForm" :rules="formRules" label-position="left" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" prefix-icon="el-icon-key" :disabled="dialogStatus==='edit'"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="roleForm.description" placeholder="请输入角色描述"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
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

    <!-- 权限配置对话框 -->
    <el-dialog :title="'为角色 [' + (currentRole ? currentRole.roleName : '') + '] 分配权限'" :visible.sync="permissionDialogVisible" width="500px" @close="resetChecked">
      <el-tree
        ref="permissionTree"
        :data="permissionTree"
        :props="{ children: 'children', label: 'label' }"
        show-checkbox
        node-key="id"
        highlight-current
        check-strictly
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 假设你已经在api目录下创建了对应的请求方法
// import { listRoles, getRolePermissions, assignPermissions, listAllPermissions } from '@/api/system'

// 为方便演示，我们先用axios模拟请求
import request from '@/utils/request'

export default {
  name: 'RoleManagement',
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
        roleName: ''
      },
      dialogFormVisible: false,
      permissionDialogVisible: false,
      dialogStatus: '',
      dialogTitle: '',
      currentRole: null,
      roleForm: {
        id: undefined,
        roleName: '',
        roleCode: '',
        description: '',
        status: 0  // 0=启用，1=禁用
      },
      permissionTree: [],
      formRules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        roleCode: [
          { required: true, message: '请输入角色编码', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
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
        roleName: this.searchForm.roleName || undefined
      }
    },
    // 创建/编辑数据模型
    submitModel() {
      return {
        id: this.roleForm.id,
        roleName: this.roleForm.roleName,
        roleCode: this.roleForm.roleCode,
        description: this.roleForm.description,
        status: this.roleForm.status
      }
    }
  },
  created() {
    this.fetchPermissionTree()
    // 检查用户是否已登录，如果已登录则获取数据
    if (this.$store.getters.token) {
      this.fetchList()
    } else {
      // 添加测试数据
      this.list = [
        {
          id: 1,
          roleName: '超级管理员',
          roleCode: 'SUPER_ADMIN',
          description: '系统超级管理员，拥有所有权限',
          status: 0,  // 0=启用
          createTime: '2025-01-01 00:00:00'
        },
        {
          id: 2,
          roleName: '内容管理员',
          roleCode: 'CONTENT_ADMIN',
          description: '负责内容管理相关功能',
          status: 0,  // 0=启用
          createTime: '2025-03-15 10:30:00'
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
    // 获取角色列表
    async fetchList() {
      this.listLoading = true
      try {
        const params = {
          current: this.query.current,
          size: 100  // 角色数量不多，一次性加载
        }
        const response = await request({
          url: '/admin/roles/list',
          method: 'get',
          params: params
        })

        if (response.success) {
          // 处理字段映射：后端返回name，前端使用roleName
          this.list = response.data.records.map(item => ({
            id: item.id,
            roleName: item.name,  // 后端name字段映射到前端roleName
            roleCode: item.name,  // 暂时用name作为roleCode
            description: item.description,
            status: item.status,
            createTime: item.createTime
          }))
          this.total = response.data.total
        } else {
          this.$message.error('获取角色列表失败')
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
        this.$message.error('获取角色列表失败')
      } finally {
        this.listLoading = false
      }
    },

    // 获取所有角色
    async fetchRoles() {
      // 你的接口是 /api/admin/roles/list
      const res = await request({ url: '/admin/roles/list', method: 'get' });
      this.rolesList = res; // 后端返回的是分页数据，这里简化为直接赋值
    },
    // 获取权限树
    async fetchPermissionTree() {
      try {
        // 暂时使用模拟数据，后续可以接入真实权限接口
        this.permissionTree = [
          {
            id: 1,
            label: '系统管理',
            children: [
              { id: 11, label: '用户管理' },
              { id: 12, label: '角色管理' },
              { id: 13, label: '权限管理' }
            ]
          },
          {
            id: 2,
            label: '内容管理',
            children: [
              { id: 21, label: '食堂管理' },
              { id: 22, label: '菜品管理' },
              { id: 23, label: '博客管理' }
            ]
          }
        ]
      } catch (error) {
        console.error('获取权限树失败:', error)
      }
    },

    // 获取所有权限
    async fetchAllPermissions() {
      // 请求我们刚刚在后端新增的接口
      const res = await request({ url: '/admin/permissions/list', method: 'get' });
      // 这里可以进一步处理成树形结构，但为简化，先直接使用列表
      this.permissionTree = res;
    },
    // 点击“编辑权限”按钮
    async handleEdit(row) {
      this.currentRole = row;
      this.dialogVisible = true;

      // 获取该角色已有的权限
      const ownedPermissions = await request({ url: `/admin/roles/${row.id}/permissions`, method: 'get' });

      // 在 nextTick 中设置，确保DOM渲染完成
      this.$nextTick(() => {
        this.$refs.tree.setCheckedKeys(ownedPermissions);
      });
    },
    // 保存权限分配
    async handleSavePermissions() {
      const checkedKeys = this.$refs.tree.getCheckedKeys();
      const payload = {
        roleId: this.currentRole.id,
        permissionIds: checkedKeys
      };

      // 调用分配权限的接口
      await request({ url: '/admin/roles/permissions', method: 'post', data: payload });

      this.$message.success('权限分配成功！');
      this.dialogVisible = false;
    },
    // 关闭对话框时，清空树的选中状态
    resetChecked() {
      if(this.$refs.tree) {
        this.$refs.tree.setCheckedKeys([]);
      }
    }
  }
}
</script>

<style scoped>
.role-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.role-card {
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