<template>
  <div>
    <h2 class="text-center text-info">
      Sign In
    </h2>
    <div class="row justify-content-center">
      <b-form class="col-3" @submit="signIn">
        <b-input-group class="mb-2">
          <b-input placeholder="Username or email" v-model="credentials.username"/>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-input placeholder="Password" v-model="credentials.password"/>
        </b-input-group>
        <div class="row justify-content-end pr-3">
          <b-button type="submit" variant="primary">Sign In</b-button>
        </div>
      </b-form>
    </div>
  </div>
</template>

<script>
  import Auth from './utils/Http';

  export default {
    name: 'sign-in-form',
    data() {
      return {
        credentials: {
          username: '',
          password: '',
        },
      };
    },
    methods: {
      signIn() {
        Auth.signIn(this.credentials).then(() => {
          this.$router.push('/');
        });
      },
    },
  };
</script>

<style scoped>

</style>
