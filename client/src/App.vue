<template>
  <b-container id="app">
    <div class="mt-3">
      <nav-bar></nav-bar>
    </div>
    <div class="mt-5 mb-5">
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
