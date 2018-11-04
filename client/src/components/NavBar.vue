<template>
  <b-navbar toggleable="sm" variant="faded" type="light">

    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-navbar-brand href="#/">
      <b-img src="static/logo.png" class="d-inline-block align-top" width="100"/>
    </b-navbar-brand>

    <b-collapse is-nav id="nav_collapse">

      <b-navbar-nav>
        <ad-search :on-search="onSearch"></ad-search>
      </b-navbar-nav>

      <!-- Right aligned nav items -->
      <b-navbar-nav class="ml-auto">
        <b-nav-item href="#/ads/new" v-if="signedIn">New Ad</b-nav-item>
        <b-nav-item href="#/sign-in" right v-if="!signedIn">Sign In</b-nav-item>
        <b-nav-item-dropdown right v-if="signedIn">
          <!-- Using button-content slot -->
          <template slot="button-content">
            Hi, <em>{{username}}</em>!
          </template>
          <b-dropdown-item href="#">Profile</b-dropdown-item>
          <b-dropdown-item href="#" @click="signOut">SignOut</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

    </b-collapse>
  </b-navbar>
</template>

<script>
  import Http from './utils/Http';
  import AdSearch from './AdSearch';

  let intervalId = null;
  export default {
    name: 'nav-bar',
    props: ['onSearch'],
    components: {
      AdSearch,
    },
    data() {
      return {
        username: '',
        signedIn: false,
      };
    },
    created() {
      clearInterval(intervalId);
      intervalId = setInterval(() => {
        const userInfo = Http.userInfo();
        this.username = userInfo ? userInfo.username : '';
        this.signedIn = Http.isSignedIn();
      }, 200);
    },
    methods: {
      signOut() {
        Http.signOut();
        this.$router.push({ name: 'sign-in' });
      },
    },
  };
</script>

<style scoped>

</style>
