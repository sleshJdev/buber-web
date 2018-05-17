<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <ads-grid :ads="ads"></ads-grid>
  </div>
</template>

<script>
  import Times from './utils/Times';
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
        return Http.doGet(`/api/ads?name=${query.name || ''}&address=${query.address || ''}`)
          .then((data) => {
            // eslint-disable-next-line
            const now = new Date();
            // eslint-disable-next-line
            this.ads = data.content.filter((it) => {
              const endDate = Times.endAdDate(it.createdOn);
              return endDate >= now;
            });
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
