<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <ads :ads="adsResponse && adsResponse.content || []"></ads>
    <modals></modals>
  </div>
</template>

<script>
  import Http from './utils/Http';
  import AdSearch from './AdSearch';
  import Ads from './Ads';
  import Modals from './Modals';

  export default {
    name: 'home',
    components: {
      Ads,
      AdSearch,
      Modals,
    },
    data() {
      return {
        adsResponse: null,
      };
    },
    mounted() {
      this.search({});
    },
    methods: {
      search(query) {
        return Http.fetchAds(query)
          .then((adsResponse) => {
            this.adsResponse = adsResponse;
          });
      },
    },
  };
</script>

<style scoped>
  img {
    width: 100%;
  }
</style>
