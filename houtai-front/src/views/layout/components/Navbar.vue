<template>
  <div class="navbar">
    <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <breadcrumb class="breadcrumb-container" />
    <div class="right-menu">
      <header-search class="right-menu-item" />
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <span class="user-name">{{ name }}</span>
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/">
            <el-dropdown-item>首页</el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import HeaderSearch from '@/components/HeaderSearch'

export default {
  components: { Breadcrumb, Hamburger, HeaderSearch },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'name'])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      try {
        await this.$store.dispatch('user/logout')
        this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      } catch (error) {
        console.error('登出失败:', error)
        this.$message.error('登出失败，请稍后再试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  
  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    
    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }
  
  .breadcrumb-container {
    float: left;
  }
  
  .right-menu {
    float: right;
    height: 100%;
    display: flex;
    align-items: center;
    
    .right-menu-item {
      display: inline-block;
      padding: 0 12px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: middle;
      
      &.hover-effect {
        cursor: pointer;
        transition: background .3s;
        
        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }
    
    .avatar-container {
      margin-right: 30px;
      
      .avatar-wrapper {
        display: flex;
        align-items: center;
        position: relative;
        
        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
          margin-right: 8px;
        }
        
        .user-name {
          font-size: 14px;
          color: #606266;
          margin-right: 5px;
        }
        
        .el-icon-caret-bottom {
          cursor: pointer;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>