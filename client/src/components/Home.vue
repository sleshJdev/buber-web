<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <ads :ads="ads" :compact="compact"></ads>
    <modal></modal>
  </div>
</template>

<script>
  import Http from './utils/Http';
  import AdSearch from './AdSearch';
  import Ads from './Ads';
  import Modal from './Modal';

  export default {
    components: {
      Ads,
      AdSearch,
      Modal,
    },
    name: 'home',
    mounted() {
      this.search({});
      const compact = window.matchMedia('(max-width: 768px)');
      compact.addListener((it) => {
        this.compact = it.matches;
      });
      this.compact = compact.matches;
    },
    data() {
      return {
        ads: [],
        compact: false,
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
  img {
    width: 100%;
  }
</style>
