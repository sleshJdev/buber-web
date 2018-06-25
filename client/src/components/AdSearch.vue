<template>
  <b-form inline class="mb-md-3 pr-md-5 pl-md-5">
    <b-form-input type="text"
                  class="col-sm-6 mb-md-1"
                  v-model="query.name"
                  placeholder="Search by username...">
    </b-form-input>
    <b-form-input type="text"
                  class="col-sm-6 mb-md-1"
                  v-model="query.address"
                  placeholder="Type address where to search..."
                  required>
    </b-form-input>
  </b-form>
</template>

<script>
  export default {
    name: 'ad-search',
    props: ['onSearch'],
    data() {
      return {
        query: {
          name: null,
          address: null,
        },
      };
    },
    watch: {
      query: {
        deep: true,
        handler(query) {
          this.onSearch(query);
        },
      },
    },
  };
</script>

<style scoped>

</style>
