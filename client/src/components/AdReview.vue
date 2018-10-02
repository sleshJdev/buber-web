<template>
  <div v-if="!!ad">
    <b-card>
      <b-media>
        <h5 class="mt-0"><span class="text-info">{{ad.name || 'Anonymous'}}</span> profile</h5>
        <p>
          <span class="text-info">Age</span> : {{ad.age}} years
          <span class="text-info">Price</span> : {{ad.price}}
          <span class="text-info">Tel</span> :
          <b-link :href="`tel:${ad.tel}`">{{ad.tel}}</b-link>
        </p>
        <p>
          <span class="text-info">Created By</span> : {{ad.name || 'Anonymous'}}
          <span class="text-info">Created On</span> : {{formatDate(ad.createdOn)}}
          <span class="text-info">Location</span> : {{ad.city}}
        </p>
        <p>
          <span class="text-info">Description</span> {{ad.description}}
        </p>
        <b-img :src="ad.avatar" fluid-grow></b-img>
      </b-media>
    </b-card>
    <div v-if="ad.photos.length">
      <p class="text-info mt-3">
        More photos from this user
      </p>
      <b-card-group columns>
        <b-card-img v-for="photo in ad.photos" :key="photo"
                    :src="`${photo.replace('/ths/', '/main/')}`">
        </b-card-img>
      </b-card-group>
    </div>
  </div>
</template>

<script>
  import Times from './utils/Times';
  import Http from './utils/Http';
  import Ads from './Ads';

  export default {
    components: { Ads },
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
      getAgeYears(birthdayUtcString) {
        return Times.computeAgeYears(birthdayUtcString);
      },
      formatDate(date) {
        return Times.format(date);
      },
      fetchAd() {
        const adId = this.$route.query.id;
        return Http.doGet(`/api/ads/${adId}`)
          .then((ad) => this.ad = ad);
      },
    },
  };
</script>

<style scoped>
</style>
