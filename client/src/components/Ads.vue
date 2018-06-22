<template>
  <div>
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
            Location: {{ad.location.address || 'Undefined'}}<br/>
            <span class="text-danger">Will be hide at {{endAdDate(ad.createdOn)}}</span>
          </small>
        </b-card-footer>
      </b-card>
    </ads-container>
    <b-modal ref="unauthorizedModal" title="Need to sign in" size="sm"
             ok-variant="success" cancel-variant="secondary"
             cancel-title="Dismiss" ok-title="Sign in"
             @ok="$router.push({name: 'sign-in'})">
      <p class="text-center">
        Please, sign in to review ads and see phone numbers
      </p>
    </b-modal>
  </div>
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
      onRemove() {
        const ad = this.ads.pop();
        setTimeout(() => this.ads.unshift(ad));
      },
      showPhone(ad) {
        if (!Http.isSignedIn()) {
          this.$refs.unauthorizedModal.show();
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

  .compact-ads {
    margin-bottom: 1em;
  }

  .compact-ads .card {
    position: absolute;
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
