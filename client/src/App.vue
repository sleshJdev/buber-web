<template>
  <b-container id="app">
    <div class="mt-md-3">
      <nav-bar></nav-bar>
    </div>
    <div class="mt-md-5 mb-md-5">
      <router-view/>
    </div>
  </b-container>
</template>

<script>
import NavBar from './components/NavBar';

export default {
  components: { NavBar },
  name: 'App',
};
</script>

<style>
</style>
