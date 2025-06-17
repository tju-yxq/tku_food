<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎和统计 -->
    <el-card shadow="hover" class="welcome-card">
      <el-row type="flex" justify="space-between" align="middle">
        <div class="welcome-section">
          <h2>您好, {{ name }}!</h2>
          <p>欢迎回到TjuFood管理系统，祝您工作愉快！</p>
          <p>您当前的角色是: 
            <el-tag 
              v-for="role in roles" 
              :key="role" 
              size="small" 
              effect="dark"
              style="margin-right: 5px;">
              {{ role }}
            </el-tag>
          </p>
        </div>
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-title">待办事项</div>
            <div class="stat-value">{{ pendingTasks }} / {{ totalTasks }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-title">项目总数</div>
            <div class="stat-value">{{ projectCount }}</div>
          </div>
        </div>
      </el-row>
    </el-card>

    <!-- 数据概览 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6" v-for="(card, index) in dataCards" :key="index">
        <el-card shadow="hover" class="data-card">
          <div class="data-card-inner">
            <div class="data-icon" :class="card.iconClass">
              <i :class="card.icon"></i>
            </div>
            <div class="data-info">
              <div class="data-title">{{ card.title }}</div>
              <div class="data-value">{{ card.value }}</div>
              <div class="data-trend">
                <span :class="card.trendClass">{{ card.trend }}</span> {{ card.trendText }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作和最近活动 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover" class="quick-access-card">
          <div slot="header">
            <span class="card-header-title">快捷操作</span>
          </div>
          <div class="quick-access">
            <el-button 
              v-for="action in quickActions" 
              :key="action.path"
              :type="action.type" 
              plain 
              @click="$router.push(action.path)">
              <i :class="action.icon"></i> {{ action.label }}
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="activity-card">
          <div slot="header">
            <span class="card-header-title">最近操作</span>
          </div>
          <el-timeline :reverse="false">
            <el-timeline-item
                v-for="(activity, index) in activities"
                :key="index"
                :timestamp="activity.timestamp"
                :color="activity.color">
              {{activity.content}}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 待办事项 -->
    <el-card shadow="hover" class="todo-card" style="margin-top: 20px;">
      <div slot="header">
        <span class="card-header-title">待办事项</span>
      </div>
      <el-table :data="todoList" style="width: 100%">
        <el-table-column prop="title" label="任务" min-width="180"></el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getTagType(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template slot-scope="scope">
            <el-tag 
              :type="getPriorityType(scope.row.priority)" 
              effect="dark" 
              size="small">
              {{ scope.row.priority }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="150"></el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleTodo(scope.row)">处理</el-button>
            <el-button type="text" size="small" @click="completeTodo(scope.row)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      pendingTasks: 3,
      totalTasks: 10,
      projectCount: 8,
      dataCards: [
        {
          title: '用户总数',
          value: '1,286',
          trend: '↑ 12%',
          trendText: '较上周',
          trendClass: 'trend-up',
          icon: 'el-icon-user',
          iconClass: 'user-icon'
        },
        {
          title: '博客总数',
          value: '526',
          trend: '↑ 8%',
          trendText: '较上周',
          trendClass: 'trend-up',
          icon: 'el-icon-document',
          iconClass: 'blog-icon'
        },
        {
          title: '评价总数',
          value: '3,128',
          trend: '↑ 15%',
          trendText: '较上周',
          trendClass: 'trend-up',
          icon: 'el-icon-chat-line-square',
          iconClass: 'review-icon'
        },
        {
          title: '菜品总数',
          value: '842',
          trend: '↑ 5%',
          trendText: '较上周',
          trendClass: 'trend-up',
          icon: 'el-icon-dish',
          iconClass: 'dish-icon'
        }
      ],
      quickActions: [
        { label: '食堂管理', icon: 'el-icon-office-building', path: '/content-mgr/canteen', type: 'primary' },
        { label: '审核中心', icon: 'el-icon-s-check', path: '/community-mgr/blog', type: 'success' },
        { label: '发布公告', icon: 'el-icon-bell', path: '/operation-mgr/notice', type: 'warning' },
        { label: '人员管理', icon: 'el-icon-user-solid', path: '/system-mgr/personnel', type: 'danger' },
        { label: '数据大屏', icon: 'el-icon-data-analysis', path: '/toolbox/statistics', type: 'info' }
      ],
      activities: [
        { content: '审核通过了用户 "TJU_Foodie" 的博客', timestamp: '2小时前', color: '#0bbd87' },
        { content: '更新了 "夏日特饮" 轮播图的上线时间', timestamp: '昨天', color: '' },
        { content: '禁用了违规用户 "小广告君"', timestamp: '2025-06-12', color: '' },
      ],
      todoList: [
        { title: '审核新发布的博客', type: '内容审核', priority: '高', deadline: '2025-06-18' },
        { title: '处理用户举报', type: '社区管理', priority: '中', deadline: '2025-06-19' },
        { title: '更新首页轮播图', type: '运营', priority: '低', deadline: '2025-06-20' },
        { title: '发布系统更新公告', type: '系统', priority: '高', deadline: '2025-06-18' }
      ]
    }
  },
  computed: {
    ...mapGetters(['name', 'roles'])
  },
  methods: {
    getTagType(type) {
      const types = {
        '内容审核': 'success',
        '社区管理': 'warning',
        '运营': 'info',
        '系统': 'danger'
      }
      return types[type] || 'primary'
    },
    getPriorityType(priority) {
      const types = {
        '高': 'danger',
        '中': 'warning',
        '低': 'info'
      }
      return types[priority] || 'primary'
    },
    handleTodo(todo) {
      this.$message.success(`开始处理任务: ${todo.title}`)
    },
    completeTodo(todo) {
      this.$message.success(`任务已完成: ${todo.title}`)
      // 实际应用中这里会调用API更新任务状态
      this.todoList = this.todoList.filter(item => item !== todo)
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container { 
  padding: 20px; 
  background-color: #f0f2f5;
  min-height: calc(100vh - 50px);
}

.welcome-card {
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  
  .welcome-section {
    h2 { 
      margin: 0; 
      font-size: 24px; 
      font-weight: 500; 
      color: #303133;
    }
    p { 
      margin-top: 10px; 
      color: #606266; 
      font-size: 14px;
    }
  }
  
  .stats-section { 
    display: flex; 
  }
  
  .stat-item {
    margin-left: 40px; 
    text-align: center;
    
    .stat-title { 
      color: #909399; 
      font-size: 14px; 
    }
    
    .stat-value { 
      font-size: 24px; 
      font-weight: bold; 
      color: #409EFF;
    }
  }
}

.data-card {
  border-radius: 8px;
  height: 120px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
  
  .data-card-inner {
    display: flex;
    height: 100%;
    padding: 15px;
  }
  
  .data-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 64px;
    height: 64px;
    border-radius: 16px;
    margin-right: 16px;
    
    i {
      font-size: 32px;
      color: white;
    }
    
    &.user-icon {
      background: linear-gradient(135deg, #409EFF, #007BFF);
    }
    
    &.blog-icon {
      background: linear-gradient(135deg, #67C23A, #4CAF50);
    }
    
    &.review-icon {
      background: linear-gradient(135deg, #E6A23C, #FF9800);
    }
    
    &.dish-icon {
      background: linear-gradient(135deg, #F56C6C, #FF5252);
    }
  }
  
  .data-info {
    display: flex;
    flex-direction: column;
    justify-content: center;
    
    .data-title {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }
    
    .data-value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .data-trend {
      font-size: 12px;
      color: #909399;
      
      .trend-up {
        color: #67C23A;
      }
      
      .trend-down {
        color: #F56C6C;
      }
    }
  }
}

.quick-access-card, .activity-card, .todo-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  
  .card-header-title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
  }
}

.quick-access {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  
  .el-button {
    margin: 0;
    min-width: 120px;
    
    i {
      margin-right: 4px;
    }
  }
}

.el-timeline {
  padding-left: 0;
  
  .el-timeline-item {
    padding-bottom: 16px;
    
    &:last-child {
      padding-bottom: 0;
    }
    
    .el-timeline-item__content {
      font-size: 14px;
      color: #606266;
    }
    
    .el-timeline-item__timestamp {
      font-size: 12px;
      color: #909399;
    }
  }
}

.todo-card {
  .el-table {
    border-radius: 4px;
    
    .el-table__row {
      cursor: pointer;
      
      &:hover {
        background-color: #f5f7fa;
      }
    }
  }
}
</style>