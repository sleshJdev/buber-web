<template>
  <div v-if="!!ad">
    <b-card>
      <b-media>
        <h5 class="mt-0">Ad Details</h5>
        <p>
          <span class="text-info">Title</span> : {{ad.name || 'Anonymous'}}
          <span class="text-info">Created By</span> : {{ad.ownerName || 'Anonymous'}}
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
    <p class="text-info mt-3" v-if="adsRes.ads.length">
      More ads from this user
      ({{`${adsRes.count} of ${adsRes.total}`}}):
    </p>
    <ads-grid :ads="adsRes.ads" :compact="true"></ads-grid>
  </div>
</template>

<script>
  import Times from './utils/Times';
  import Http from './utils/Http';
  import AdsGrid from './AdsGrid';

  export default {
    components: { AdsGrid },
    name: 'ad-review',
    data() {
      return {
        ad: null,
        adsRes: {
          ads: [],
          count: 0,
          total: 0,
        },
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
        return Http.doGet(`/api/ads/${adId}`)
          .then((ad) => {
            this.ad = ad;
            return Http.doGet(`/api/ads?ownerId=${ad.ownerId}`);
          })
          .then((response) => {
            this.adsRes = {
              ads: response.content.filter(ad => ad.id !== this.ad.id),
              count: response.numberOfElements - 1,
              total: response.totalElements - 1,
            };
          });
      },
    },
  };
</script>

<style scoped>

</style>
