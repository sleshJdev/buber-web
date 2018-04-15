<template>
  <post-ad-button/>
</template>

<script>
  import PostAdButton from './PostAdButton';

  export default {
    components: { PostAdButton },
    name: 'Home',
    data() {
      return {
        msg: 'Welcome to Your Vue.js App',
      };
    },
  };
</script>

<style scoped>
</style>
