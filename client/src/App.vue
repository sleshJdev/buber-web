<template>
  <b-container id="app">
    <router-view/>
  </b-container>
</template>

<script>
  import Http from './components/utils/Http';

  export default {
    name: 'App',
    created() {
      Http.relogin();
    },
  };
</script>

<style scoped>
  @media (max-width: 576px) {
    .container {
      padding: 0;
    }
  }
</style>
