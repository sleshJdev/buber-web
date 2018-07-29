<template>
  <b-modal id="unauthorizedModal" title="Need to sign in"
           size="sm" :lazy="true"
           ok-variant="success" cancel-variant="secondary"
           cancel-title="Dismiss" ok-title="Sign in"
           @ok="$router.push({name: 'sign-in'})">
    <p class="text-center">
      Please, sign in to review ads and see phone numbers
    </p>
  </b-modal>
</template>

<script>
  export default {
    name: 'modals',
  };
</script>

<style scoped>

</style>
