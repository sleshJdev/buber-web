<template>
  <b-form inline>
    <div class="row">
      <b-form-input type="text"
                    v-model="query.name"
                    placeholder="Search by name...">
      </b-form-input>
      <b-form-select v-model="query.city"
                     :options="cities">
      </b-form-select>
    </div>
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
