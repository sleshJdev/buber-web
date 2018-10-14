<template>
  <b-container id="app">
    <div class="mt-lg-5 mb-lg-5">
      <router-view/>
    </div>
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
