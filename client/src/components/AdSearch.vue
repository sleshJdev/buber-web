<template>
  <b-form inline class="mb-3 pr-5 pl-5">
    <b-form-input type="text"
                  class="col-sm-6 mb-3"
                  v-model="query.name"
                  placeholder="Search by username...">
    </b-form-input>
    <b-form-input type="text"
                  class="col-sm-6 mb-3"
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
