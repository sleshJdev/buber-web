<template>
  <ads-container :class="{'compact-ads': compact}"
                 :ads="ads" :compact="compact"
                 :onRemove="onRemove">
    <b-card v-for="ad in ads" :key="ad.id" tag="article">
      <b-card-body>
        <b-button block variant="secondary" :size="compact ? 'lg' : 'md'"
                  v-if="!shownPhones[ad.id]" @click.stop="showPhone(ad)">
          Show phone number...
        </b-button>
        <div class="text-center">
          <b-link v-if="shownPhones[ad.id]"
                  class="text-info text-monospace font-weight-bold"
                  :href="`tel:${ad.tel}`">
            Call {{ad.tel}}
          </b-link>
        </div>
      </b-card-body>
      <b-card-img @click="review(ad)"
                  :class="{'ad-card': !compact}"
                  :src="`/api/ads/${ad.id}/banner`" bottom></b-card-img>
      <b-card-footer>
        <small class="text-muted">
          {{ad.name || 'Anonymous'}} {{getAgeYears(ad.birthday)}}<br/>
          <span class="text-danger">Will be hide at {{endAdDate(ad.createdOn)}}</span>
        </small>
      </b-card-footer>
    </b-card>
  </ads-container>
</template>

<script>
  import Times from './utils/Times';
  import Http from './utils/Http';
  import AdsContainer from './AdsContainer';

  export default {
    name: 'ads',
    components: { AdsContainer },
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
    data() {
      return {
        shownPhones: Object.create(null),
      };
    },
    methods: {
      getAgeYears(birthdayUtcString) {
        return Times.computeAgeYears(birthdayUtcString);
      },
      onRemove() {
        const ad = this.ads.pop();
        setTimeout(() => this.ads.unshift(ad));
      },
      showPhone(ad) {
        if (!Http.isSignedIn()) {
          this.$root.$emit('bv::show::modal', 'unauthorizedModal');
          return;
        }

        this.$set(this.shownPhones, ad.id, true);
      },
      review(ad) {
        this.$router.push({
          name: 'ad-review',
          query: {
            id: ad.id,
          },
        });
      },
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
  .ad-card:hover {
    cursor: pointer;
    box-shadow: 0 0 10px 2px #ffc107;
  }

  .compact-ads .card {
    position: absolute;
    margin-bottom: 1em;
    margin-top: 1em;
  }

  @media (max-width: 576px) {
    .compact-ads .card {
      margin-right: 1em;
    }
  }

  @media (min-width: 576px) {
    .compact-ads .card {
      margin-right: 10%;
    }
  }

  @media (min-width: 768px) {
    .compact-ads .card {
      margin-right: 7em;
    }
  }
</style>
