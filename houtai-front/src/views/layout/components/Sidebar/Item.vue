<template>
  <div>
    <i v-if="icon" :class="icon" style="margin-right: 5px;" />
    <span v-if="title">{{ title }}</span>
  </div>
</template>

<script>
export default {
  name: 'MenuItem',
  // 使用 props 接收父组件传来的图标和标题
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  }
}
</script>