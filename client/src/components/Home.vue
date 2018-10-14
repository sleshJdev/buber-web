<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <infinity-scroll :compact="compact" :on-ending="loadMoreAds">
      <ads :compact="compact" :ads="loadedAds"></ads>
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
        compact: false,
        loading: false,
      };
    },
    mounted() {
      this.search({});
      const compact = window.matchMedia('(max-width: 576px)');
      compact.addListener((it) => {
        this.compact = it.matches;
      });
      this.compact = compact.matches;
    },
    watch: {
      loadedAds(remainingAds) {
        if (remainingAds.length < 5 && !this.loading) {
          this.loading = true;
          this.loadMoreAds().finally(() => {
            this.loading = false;
          });
        }
      },
      compact(newcompact) {
        // if user switched back from mobile view to desktop,
        // reload first page of ads with user filters
        if (!newcompact) {
          const query = {
            ...this.query,
            page: 0,
          };
          this.search(query);
        }
      },
    },
    methods: {
      loadMoreAds() {
        if (!this.adsResponse.last) {
          const query = {
            ...this.query,
            page: this.adsResponse.number + 1,
          };
          return this.enhanceSearch(query, true);
        }
        return Promise.resolve();
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
