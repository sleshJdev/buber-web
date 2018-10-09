<template>
  <b-form inline class="mb-sm-3 pr-md-5 pl-md-5">
    <b-form-input type="text"
                  class="col-sm-6 mb-md-1"
                  v-model="query.name"
                  placeholder="Search by name, description...">
    </b-form-input>
    <b-form-select class="col-sm-6 mb-md-1"
                   v-model="query.city"
                   :options="cities"
                   placeholder="Search by name, description...">
    </b-form-select>
  </b-form>
</template>

<script>
  import Http from './utils/Http';

  export default {
    name: 'ad-search',
    props: ['onSearch'],
    data() {
      return {
        cities: [],
        query: {
          name: null,
          city: null,
        },
      };
    },
    mounted() {
      return Http.doGet('/api/ads/cities')
        .then((cities) => {
          this.cities = cities
            .map(it => ({ text: it, value: it }))
            .sort();
          this.cities.unshift({ text: 'Any city', value: null });
        });
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
