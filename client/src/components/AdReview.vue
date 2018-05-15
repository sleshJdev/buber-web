<template>
  <div v-if="!!ad">
    <b-card>
      <b-media>
        <h5 class="mt-0">Ad Details</h5>
        <p>
          <span class="text-info">Created By</span> : {{ad.name || 'Anonymous'}}
          <span class="text-info">Created On</span> : {{formatDate(ad.createdOn)}}
          <span class="text-info">Tel</span> : {{ad.tel}}
          <span class="text-info">Location</span> : {{ad.location.address}}
        </p>
        <p>
        </p>
        <p>
          <span class="text-info">Description</span> {{ad.description}}
        </p>
        <b-img :src="`/api/ads/${ad.id}/banner`" fluid-grow></b-img>
      </b-media>
    </b-card>
  </div>
</template>

<script>
  import Times from './utils/Times';

  export default {
    name: 'ad-review',
    data() {
      return {
        ad: null,
      };
    },
    created() {
      this.fetchAd();
    },
    methods: {
      formatDate(date) {
        return Times.format(date);
      },
      fetchAd() {
        const adId = this.$route.query.id;
        return fetch(`/api/ads/${adId}`)
          .then(response => response.json())
          .then((data) => {
            this.ad = data;
          });
      },
    },
  };
</script>

<style scoped>

</style>
