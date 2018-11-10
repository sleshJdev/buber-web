<template>
  <div v-if="!!ad">
    <nav-bar></nav-bar>
    <b-card>
      <b-media>
        <h5 class="mt-0 mb-2"><span class="text-info">{{ad.name || 'Anonymous'}}</span></h5>
        <p>
          {{ad.tagline}}
        </p>
        <p>
          <span class="text-info">Age</span> : {{Times.changeYears(-ad.birthyear)}} years
          <span class="text-info">Price</span> : {{!!ad.price ? ad.price + '$' : 'Not specified'}}
          <span class="text-info">Tel</span> :
          <b-link :href="`tel:${ad.tel}`">{{ad.tel}}</b-link>
        </p>
        <p>
          <span class="text-info">Created By</span> : {{ad.name || 'Anonymous'}}
          <span v-if="ad.createdOn" class="text-info">Created On</span>
          : {{Times.format(ad.createdOn)}}
          <span class="text-info">Location</span> : {{ad.city}}
        </p>
        <p>
          <span class="text-info">Description</span> {{ad.description}}
        </p>
        <b-img :src="ad.avatar" fluid-grow></b-img>
      </b-media>
    </b-card>
    <div v-if="ad.photos && ad.photos.length">
      <p class="text-info mt-3">
        Gallery
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
  import NavBar from './NavBar';

  export default {
    components: { Ads, NavBar },
    name: 'ad-review',
    data() {
      return {
        Times,
        ad: null,
      };
    },
    created() {
      this.fetchAd();
    },
    methods: {
      fetchAd() {
        const adId = this.$route.query.id;
        return Http.doGet(`/api/ads/${adId}`)
          .then((ad) => {
            this.ad = ad;
          });
      },
    },
  };
</script>

<style scoped>
</style>
