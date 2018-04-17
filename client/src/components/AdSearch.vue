<template>
  <b-form inline class="mb-3">
    <b-form-input type="text"
                  class="mr-3 col-md-3"
                  v-model="query.name"
                  placeholder="Search by username...">
    </b-form-input>
    <b-form-input type="text"
                  class="col-md-4"
                  v-model="query.location"
                  placeholder="Type location where to search..."
                  required>
    </b-form-input>
  </b-form>
</template>

<script>
  export default {
    name: 'ad-search',
    props: ['onSearch', 'locations'],
    data() {
      return {
        query: {
          name: null,
          location: null,
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
