<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <ads-grid :ads="ads"></ads-grid>
  </div>
</template>

<script>
  import Http from './utils/Http';
  import AdSearch from './AdSearch';
  import AdsGrid from './AdsGrid';

  export default {
    components: {
      AdsGrid,
      AdSearch,
    },
    name: 'ads',
    mounted() {
      this.search({});
    },
    data() {
      return {
        ads: [],
      };
    },
    methods: {
      isSignedIn() {
        return Http.isSignedIn();
      },
      search(query) {
        return Http.fetchAds(query).then((ads) => {
          this.ads = ads;
        });
      },
    },
  };
</script>

<style scoped>
  .col-3 {
    padding: 20px;
  }

  img {
    width: 100%;
  }
</style>
