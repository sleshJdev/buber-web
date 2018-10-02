<template>
  <ads-container :ads="ads" :compact="compact" :onThrowOut="swapCard">
    <b-card v-for="ad in ads" :key="ad.id" tag="article">
      <b-card-body>
        <b-button block variant="dark" :size="compact ? 'lg' : 'sm'"
                  v-if="!shownPhones[ad.id]" @click.stop="showPhone(ad)">
          Show phone number
        </b-button>
        <div class="text-center phone-number">
          <b-link v-if="shownPhones[ad.id]"
                  class="text-info text-monospace font-weight-bold"
                  :href="`tel:${ad.tel}`">
            Call {{ad.tel}}
          </b-link>
        </div>
      </b-card-body>
      <b-card-img @click="review(ad)"
                  :class="{'ad-card': !compact}"
                  :src="ad.avatar" bottom></b-card-img>
      <b-card-footer>
        <small class="text-muted">
          {{ad.name || 'Anonymous'}} {{ad.age}}<br/>
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
    },
    mounted() {
      const compact = window.matchMedia('(max-width: 576px)');
      compact.addListener((it) => {
        this.compact = it.matches;
      });
      this.compact = compact.matches;
    },
    data() {
      return {
        shownPhones: Object.create(null),
        compact: false,
      };
    },
    methods: {
      getAgeYears(birthdayUtcString) {
        return Times.computeAgeYears(birthdayUtcString);
      },
      swapCard() {
        const topAd = this.ads.pop();
        this.ads.unshift(topAd);
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

  @media (max-width: 992px) {
    .phone-number {
      padding: 0.5em;
    }

    .card-body .btn {
      border-radius: 0;
    }

    .card-body {
      padding: 0;
    }
  }

  @media (max-width: 576px) {

  }

  @media (max-width: 576px) {
    .card:nth-child(3) {
      /*top: 2px;*/
      transform: translate(2px, 2px) rotate(0.4deg);
    }

    .card:nth-child(2) {
      /*top: 4px;*/
      transform: translate(-4px, -2px) rotate(-1deg);
    }

    .card {
      position: absolute;
      margin-top: 1rem;
    }
  }
</style>
