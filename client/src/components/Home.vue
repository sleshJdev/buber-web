<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <infinity-scroll :on-ending="loadMoreAds">
      <ads :ads="loadedAds"></ads>
    </infinity-scroll>
    <modals></modals>
  </div>
</template>

<script>
  import Http from './utils/Http';
  import AdSearch from './AdSearch';
  import Ads from './Ads';
  import Modals from './Modals';
  import InfinityScroll from './InfinityScroll';

  export default {
    name: 'home',
    components: {
      InfinityScroll,
      AdSearch,
      Ads,
      Modals,
    },
    data() {
      return {
        query: {},
        loadedAds: [],
        adsResponse: {},
      };
    },
    mounted() {
      this.search({});
    },
    methods: {
      loadMoreAds() {
        if (!this.adsResponse.last) {
          const query = {
            ...this.query,
            page: this.adsResponse.number + 1,
          };
          this.enhanceSearch(query, true);
        }
      },
      search(query) {
        return this.enhanceSearch(query);
      },
      enhanceSearch(query, append) {
        this.query = query;
        return Http.fetchAds(query).then((adsResponse) => {
          const ads = adsResponse.content;
          if (append === true) {
            ads.forEach(ad => this.loadedAds.push(ad));
          } else {
            this.loadedAds = ads;
          }
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
