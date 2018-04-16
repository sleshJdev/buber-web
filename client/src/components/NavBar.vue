<template>
  <b-nav fill tabs>
    <b-nav-item href="#/">
      <b-button :block="true" variant="primary">Ads</b-button>
    </b-nav-item>
    <b-nav-item href="#/ads">
      <b-button :block="true" variant="success">Post Ad</b-button>
    </b-nav-item>
  </b-nav>
</template>

<script>
  export default {
    name: 'nav-bar',
  };
</script>

<style scoped>

</style>
