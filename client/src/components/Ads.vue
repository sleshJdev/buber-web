<template>
  <b-card-group columns>
    <b-card v-for="ad in ads" :key="ad.id" tag="article" no-body>
      <b-card-body>
        <b-link :to="{name: 'ad-review', query: {href: ad._links.self.href}}"
                class="card-link">
          Review Ad
        </b-link>
      </b-card-body>
      <b-card-footer>
        <small class="text-muted">
          Location: {{ad.location.formattedAddress || 'Undefined'}}
        </small>
      </b-card-footer>
      <b-card-img :src="ad._links.banner.href" bottom></b-card-img>
    </b-card>
  </b-card-group>

</template>

<script>
  import NavBar from './NavBar';
  import Times from './utils/Times';

  export default {
    components: { NavBar },
    name: 'ads',
    created() {
      this.fetchAds();
    },
    data() {
      return {
        ads: [],
      };
    },
    methods: {
      formatDate(date) {
        return Times.format(date);
      },
      fetchAds() {
        return fetch('/api/ads?size=1000&sort=createdOn,desc')
          .then(response => response.json())
          .then((data) => {
            // eslint-disable-next-line
            this.ads = data._embedded.ads;
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
