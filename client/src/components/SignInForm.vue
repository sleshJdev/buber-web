<template>
  <div>
    <h2 class="text-center text-info">{{label}}</h2>
    <p class="text-center" v-if="message">
      <small v-bind:class="[success ? 'text-success' : 'text-danger']">
        {{message}}
      </small>
    </p>
    <div class="sign-in-form row justify-content-center">
      <b-form class="col-lg-4 col-md-4" @submit.prevent="onSubmit">
        <b-input-group class="mb-2">
          <b-input placeholder="Username or email" v-model="credentials.username"></b-input>
        </b-input-group>
        <b-input-group class="mb-2">
          <b-input type="password" placeholder="Password" v-model="credentials.password"></b-input>
        </b-input-group>
        <p v-if="signIn">
          Don't have an account, then
          <span class="anchor text-info" @click="signIn = false">Sign Up!</span>
        </p>
        <p v-else-if="!signIn">
          Have an account, then
          <span class="anchor text-info" @click="signIn = true">Sign In!</span>
        </p>
        <b-button type="submit" class="float-sm-right" variant="primary">{{label}}</b-button>
      </b-form>
    </div>
  </div>
</template>

<script>
  import Http from './utils/Http';

  export default {
    name: 'sign-in-form',
    data() {
      return {
        success: false,
        message: false,
        signIn: true,
        credentials: {
          username: '',
          password: '',
        },
      };
    },
    computed: {
      label() {
        return this.signIn ? 'Sign In' : 'Sign Up';
      },
    },
    methods: {
      onSubmit() {
        if (this.signIn) {
          Http.signIn(this.credentials)
            .then(() => {
              this.success = true;
              this.message = 'Signed in successfully!';
              setTimeout(() => {
                this.$router.push('/');
              }, 1500);
            })
            .catch(() => {
              this.success = false;
              this.message = 'Wrong credentials. Please, try again.';
            });
        } else {
          Http.signUp(this.credentials)
            .then(() => {
              this.credentials.password = '';
              this.success = true;
              this.signIn = true;
              this.message = 'Your account was created successfully! Enter a password to login.';
            })
            .catch((response) => {
              this.success = false;
              this.message = response.status === 400
                ? 'Such user already exists'
                : 'Could\'t create an account. Please, try again.';
            });
        }
      },
    },
  };
</script>

<style scoped>
  .anchor {
    cursor: pointer;
  }

  @media (max-width: 576px) {
    .sign-in-form {
      padding: 1em;
    }

    .sign-in-form button[type=submit] {
      display: block;
      width: 100%;
    }
  }

</style>
