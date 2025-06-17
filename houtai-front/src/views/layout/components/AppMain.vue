<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <router-view :key="key" />
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    key() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.app-main {
  /* 50= navbar height */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;

  /* 【核心修改在这里】 */
  /* 我们把 padding 拆开写，给顶部留出 50px(导航栏) + 20px(间距) = 70px 的空间 */
  padding: 70px 20px 20px 20px;

  background-color: #f0f2f5;
}
</style>