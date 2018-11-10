<template>
  <div>
    <nav-bar></nav-bar>
    <div class="my-3">
      <h4 class="text-info text-center">You ads</h4>
      <b-card-group columns>
        <b-card v-for="ad in ads" :key="ad.id" tag="article" no-body>
          <b-card-header>
            <span class="card-title">{{ad.name}}</span>
            <small>{{ad.createdOn}}</small>
          </b-card-header>
          <b-card-img :src="ad.avatar"
                      :alt="ad.title || ad.name"
                      img-top></b-card-img>
          <b-button-group size="sm" style="width: 100%">
            <b-btn style="width: 40%" variant="danger" size="sm" @click="remove(ad)">Remove</b-btn>
            <b-btn style="width: 60%" variant="warning" size="sm" @click="edit(ad)">Edit</b-btn>
          </b-button-group>
        </b-card>
      </b-card-group>
    </div>
  </div>
</template>

<script>
  import Http from './utils/Http';
  import AdForm from './AdForm';
  import NavBar from './NavBar';

  export default {
    components: { AdForm, NavBar },
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
