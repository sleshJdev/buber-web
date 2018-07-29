<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <ads :ads="ads"></ads>
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
        ads: [],
      };
    },
    mounted() {
      this.search({});
    },
    methods: {
      search(query) {
        return Http.fetchAds(query).then((ads) => {
          this.ads = ads;
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
