<template>
  <b-container id="app">
    <router-view/>
  </b-container>
</template>

<script>
  export default {
    name: 'App',
  };
</script>

<style scoped>
  @media (max-width: 576px) {
    .container {
      padding: 0;
    }
  }
</style>
