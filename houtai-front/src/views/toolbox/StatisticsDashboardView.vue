<template>
  <div class="statistics-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-people">
            <i class="el-icon-user-solid card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总用户数</div>
            <span class="card-panel-num">{{ statistics.totalUsers || 0 }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-message">
            <i class="el-icon-s-comment card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总博客数</div>
            <span class="card-panel-num">{{ statistics.totalBlogs || 0 }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-money">
            <i class="el-icon-food card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">菜品总数</div>
            <span class="card-panel-num">{{ statistics.totalDishes || 0 }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-shopping">
            <i class="el-icon-s-shop card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">食堂总数</div>
            <span class="card-panel-num">{{ statistics.totalCanteens || 0 }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-card class="chart-card">
      <div slot="header">
        <h3>用户增长趋势</h3>
      </div>
      <div ref="lineChart" style="height: 400px; width: 100%;"></div>
    </el-card>

    <!-- 排行榜和分布图 -->
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <div slot="header">
            <h3>热门菜品排行榜</h3>
          </div>
          <el-table :data="popularDishes" height="450">
            <el-table-column prop="rank" label="排名" width="60" align="center"></el-table-column>
            <el-table-column prop="name" label="菜品名称"></el-table-column>
            <el-table-column prop="canteen" label="所属食堂"></el-table-column>
            <el-table-column prop="score" label="评分" width="80" align="center">
              <template slot-scope="{row}">
                <span style="color: #f39c12;">{{ row.score }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card>
          <div slot="header">
            <h3>食堂分布统计</h3>
          </div>
          <div ref="pieChart" style="height: 450px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import * as echarts from 'echarts'
import request from '@/utils/request'

export default {
  name: 'StatisticsDashboard',
  data() {
    return {
      lineChart: null,
      pieChart: null,
      statistics: {
        totalUsers: 0,
        totalBlogs: 0,
        totalCanteens: 0,
        totalDishes: 0
      },
      popularDishes: [],
      userGrowthData: null,
      canteenDistribution: []
    }
  },
  created() {
    this.fetchStatistics()
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    if (this.lineChart) {
      this.lineChart.dispose()
      this.lineChart = null
    }
    if (this.pieChart) {
      this.pieChart.dispose()
      this.pieChart = null
    }
  },
  methods: {
    async fetchStatistics() {
      try {
        // 获取概览统计
        const overviewRes = await request({
          url: '/admin/statistics/overview',
          method: 'get'
        })
        this.statistics = overviewRes

        // 获取用户增长趋势
        const growthRes = await request({
          url: '/admin/statistics/user-growth',
          method: 'get'
        })
        this.userGrowthData = growthRes

        // 获取热门菜品
        const dishesRes = await request({
          url: '/admin/statistics/popular-dishes',
          method: 'get'
        })
        this.popularDishes = dishesRes

        // 获取食堂分布
        const distributionRes = await request({
          url: '/admin/statistics/canteen-distribution',
          method: 'get'
        })
        this.canteenDistribution = distributionRes

        // 重新初始化图表
        this.$nextTick(() => {
          this.initCharts()
        })
      } catch (error) {
        console.error('获取统计数据失败', error)
        // 使用默认数据
        this.useDefaultData()
      }
    },
    useDefaultData() {
      this.statistics = {
        totalUsers: 1290,
        totalBlogs: 540,
        totalCanteens: 15,
        totalDishes: 2800
      }
      this.popularDishes = [
        { rank: 1, name: '油泼面', canteen: '学一食堂', score: 4.9 },
        { rank: 2, name: '自选香锅', canteen: '学一食堂', score: 4.6 },
        { rank: 3, name: '黄焖鸡米饭', canteen: '学二食堂', score: 4.5 },
        { rank: 4, name: '牛肉拉面', canteen: '清真食堂', score: 4.5 },
        { rank: 5, name: '猪脚饭', canteen: '留学生食堂', score: 4.4 }
      ]
      this.userGrowthData = {
        dates: ['2025-06-11', '2025-06-12', '2025-06-13', '2025-06-14', '2025-06-15', '2025-06-16', '2025-06-17'],
        userCounts: [1200, 1215, 1230, 1245, 1260, 1275, 1290],
        blogCounts: [450, 465, 480, 495, 510, 525, 540]
      }
      this.canteenDistribution = [
        { name: '北洋园校区', value: 8, itemStyle: { color: '#409EFF' } },
        { name: '卫津路校区', value: 5, itemStyle: { color: '#67C23A' } },
        { name: '其他校区', value: 2, itemStyle: { color: '#E6A23C' } }
      ]
    },
    initCharts() {
      this.initLineChart()
      this.initPieChart()
    },
    initLineChart() {
      if (!this.$refs.lineChart) return

      this.lineChart = echarts.init(this.$refs.lineChart)

      const option = {
        title: {
          text: '用户和博客增长趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['用户数', '博客数'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.userGrowthData ? this.userGrowthData.dates : []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '用户数',
            type: 'line',
            smooth: true,
            data: this.userGrowthData ? this.userGrowthData.userCounts : [],
            itemStyle: { color: '#409EFF' }
          },
          {
            name: '博客数',
            type: 'line',
            smooth: true,
            data: this.userGrowthData ? this.userGrowthData.blogCounts : [],
            itemStyle: { color: '#67C23A' }
          }
        ]
      }

      this.lineChart.setOption(option)
    },
    initPieChart() {
      if (!this.$refs.pieChart) return

      this.pieChart = echarts.init(this.$refs.pieChart)

      const option = {
        title: {
          text: '食堂分布统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'middle'
        },
        series: [
          {
            name: '食堂数量',
            type: 'pie',
            radius: '65%',
            center: ['60%', '60%'],
            data: this.canteenDistribution,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      this.pieChart.setOption(option)
    }
  }
}
</script>
<style lang="scss" scoped>
.statistics-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.panel-group {
  margin-bottom: 20px;

  .card-panel-col {
    margin-bottom: 20px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px 0 rgba(0,0,0,0.15);
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;

      &.icon-people {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.icon-message {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.icon-money {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.icon-shopping {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
      color: #fff;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 24px;
        color: #303133;
        font-weight: 600;
      }
    }
  }
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);

  h3 {
    margin: 0;
    font-size: 18px;
    color: #303133;
    text-align: center;
  }
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);

  h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
    text-align: center;
  }
}
</style>
