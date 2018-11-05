<template>
  <div>
    <h4>You ads</h4>
    <b-card-group columns>
      <b-card v-for="ad in ads" :key="ad.id" tag="article">
        <b-card-img :src="ad.avatar"
                    :alt="ad.title || ad.name"
                    img-top></b-card-img>
        <b-card-footer>
          <b-button-group block>
            <b-button variant="warning" @click="edit(ad)">Edit</b-button>
            <b-button variant="danger" @click="remove(ad)">Remove</b-button>
          </b-button-group>
        </b-card-footer>
      </b-card>
    </b-card-group>
  </div>
</template>

<script>
  import Http from './utils/Http';
  import AdForm from './AdForm';

  export default {
    components: { AdForm },
    name: 'Profile',
    mounted() {
      const userInfo = Http.userInfo();
      Http.doGet(`/api/users/${userInfo.id}/ads`)
        .then((ads) => {
          this.ads = ads;
        });
    },
    data() {
      return {
        ads: null,
      };
    },
    methods: {
      edit(ad) {
        this.$router.push({
          name: 'ads-editor',
          query: {
            id: ad.id,
          },
        });
      },
      remove(ad) {
        const userInfo = Http.userInfo();
        return Http.doDelete(`/api/users/${userInfo.id}/ads/${ad.id}`)
          .then(() => {
            const index = this.ads.indexOf(ad);
            if (index >= 0) {
              this.ads.splice(index, 1);
            }
          });
      },
    },
  };
</script>

<style scoped>

</style>
