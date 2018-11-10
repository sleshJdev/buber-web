<template>
  <ads-container :compact="compact" :onThrowOut="swapCard">
    <b-card v-for="ad in ads" :key="ad.id" tag="article" no-body>
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
      <crop-card-img :url="ad.avatar">
        <b-card-img @click="review(ad)"
                    :class="{'ad-card': !compact}"
                    :src="ad.avatar" bottom></b-card-img>
      </crop-card-img>
      <b-card-footer>
        <small class="text-muted">
          {{ad.name || 'Anonymous'}} {{Times.changeYears(-ad.birthyear)}},
          {{!!ad.price ? ad.price + '$' : 'Not specified'}}<br/>
          <span class="text-danger">
            Will be hide at {{Times.format(Times.addDays(ad.createdOn, 5))}}
          </span>
        </small>
      </b-card-footer>
    </b-card>
  </ads-container>
</template>

<script>
  import Times from './utils/Times';
  import AdsContainer from './AdsContainer';
  import CropCardImg from './CropCardImg';

  export default {
    name: 'ads',
    components: { CropCardImg, AdsContainer },
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
        Times,
        now: null,
        scrollHeight: 0,
        shownPhones: Object.create(null),
      };
    },
    methods: {
      swapCard() {
        this.ads.pop();
      },
      showPhone(ad) {
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
    .card:nth-child(even) {
      /*top: 2px;*/
      transform: translate(2px, 4px) rotate(2deg);
    }

    .card:nth-child(odd) {
      /*top: 4px;*/
      transform: translate(-2px, -4px) rotate(-2deg);
    }

    .card {
      position: absolute;
    }

    .card img {
      max-height: 350px;
    }
  }
</style>
