<template>
  <div>
    <ad-search :on-search="search"></ad-search>
    <b-card-group columns>
      <b-card v-for="ad in ads" :key="ad.id" tag="article" no-body>
        <b-card-body>
          <b-link :to="{name: 'ad-review', query: {id: ad.id}}"
                  class="card-link">
            Review Ad
          </b-link>
        </b-card-body>
        <b-card-footer>
          <small class="text-muted">
            Location: {{ad.location.address || 'Undefined'}}<br/>
            <span class="text-danger">Will be hide at {{endAdDate(ad.createdOn)}}</span>
          </small>
        </b-card-footer>
        <b-card-img :src="`/api/ads/${ad.id}/banner`" bottom></b-card-img>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
  import Times from './utils/Times';
  import Http from './utils/Http';
  import AdSearch from './AdSearch';

  export default {
    components: { AdSearch },
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
              const endDateString = this.endAdDate(it.createdOn);
              const endDate = new Date(endDateString);
              return endDate >= now;
            });
          });
      },
      formatDate(date) {
        return Times.format(date);
      },
      endAdDate(createdOn) {
        const endDateUtc = Times.addDays(createdOn, 5);
        return Times.format(endDateUtc);
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
