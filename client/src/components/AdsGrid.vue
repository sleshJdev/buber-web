<template>
  <b-card-group columns>
    <b-card v-for="ad in ads" :key="ad.id" tag="article" no-body>
      <b-card-body>
        <b-link :to="{name: 'ad-review', query: {id: ad.id}}"
                class="card-link" v-if="!compact">
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
</template>

<script>
  import Times from './utils/Times';

  export default {
    name: 'ads-grid',
    props: {
      ads: {
        type: Array,
        required: true,
      },
      compact: {
        type: Boolean,
        default: false,
      },
    },
    methods: {
      formatDate(date) {
        return Times.format(date);
      },
      endAdDate(createdOn) {
        const endDateUtc = Times.endAdDate(createdOn);
        return Times.format(endDateUtc);
      },
    },
  };
</script>

<style scoped>

</style>
