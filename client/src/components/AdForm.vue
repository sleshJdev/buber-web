<template>
  <div>
    <nav-bar></nav-bar>
    <h2 class="text-center text-info">
      {{isedit ? 'Editing ad' : 'New Ad'}}
    </h2>
    <p class="text-center">
      <small class="text-warning">Fill out fields before post your ad</small>
    </p>
    <b-form @submit.prevent="onSubmit" @reset="onReset">
      <div class="row justify-content-center">
        <b-button-group class="my-2">
          <b-button type="reset" variant="danger">Reset</b-button>
          <b-button type="submit" variant="primary">Submit</b-button>
        </b-button-group>
      </div>
      <div class="row">
        <div class="col-md-6">
          <div class="row">
            <div class="col-md-6">
              <b-form-group id="form-group-name"
                            label="Your Name:"
                            label-for="form-name"
                            description="How you would like another users addressed to you">
                <b-form-input id="form-name"
                              size="sm"
                              type="text"
                              v-model="form.name"
                              required
                              placeholder="Enter your phone number">
                </b-form-input>
              </b-form-group>
            </div>
            <div class="col-md-6">
              <b-form-group id="form-group-tel"
                            label="Phone number:"
                            label-for="form-tel"
                            description="Your phone number will be visible only in your pfofile.">
                <b-form-input id="form-tel"
                              type="tel"
                              size="sm"
                              v-model="form.tel"
                              required
                              placeholder="Enter your phone number">
                </b-form-input>
              </b-form-group>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6">
              <b-form-group id="form-group-price"
                            label="Price $/h:"
                            label-for="form-price"
                            description="Your dollar price per hour.">
                <b-form-input id="form-price"
                              type="number"
                              size="sm"
                              v-model="form.price"
                              required
                              placeholder="Enter your price per hour, $">
                </b-form-input>
              </b-form-group>
            </div>
            <div class="col-md-6">
              <b-form-group id="form-group-birthyear"
                            label="Birthyear:"
                            label-for="form-group-birthyear"
                            description="When you born?">
                <b-form-input id="form-birthyear"
                              size="sm"
                              type="number"
                              step="1"
                              :min="Times.changeYears(-100)"
                              :max="Times.changeYears(0)"
                              v-model="form.birthyear"
                              required
                              placeholder="You birth year">
                </b-form-input>
              </b-form-group>
            </div>
          </div>
          <b-form-group id="form-group-location"
                        label="Your Location:"
                        label-for="form-location">
            <vue-google-autocomplete
              id="map"
              size="sm"
              types="(cities)"
              country="ca"
              ref="location"
              :enable-geolocation="true"
              classname="form-control form-control-sm"
              placeholder="Start typing"
              required>
            </vue-google-autocomplete>
          </b-form-group>
          <b-form-group id="form-goup-description"
                        label="Ad description"
                        label-for="form-description">
            <b-form-textarea id="form-description"
                             v-model="form.description"
                             size="sm"
                             placeholder="Enter ad description"
                             :rows="3"
                             :max-rows="6">
            </b-form-textarea>
          </b-form-group>
        </div>
        <div class="col-md-6">
          <b-form-group id="form-group-banner"
                        label="Avatar(touch to edit)"
                        label-for="form-banner-0">
            <p3 :src="form.avatar.src" style="min-width: 100px; min-height: 100px;"
                @change="(fd) => {form.avatar.src = fd.src; form.avatar.file = fd.file;}"></p3>
          </b-form-group>
        </div>
      </div>
      <h3 class="text-center text-info">Gallery</h3>
      <b-card-group columns>
        <p3 src="static/avatar.png"
            @change="fd => form.photos.unshift(fd)">
          <p class="text-center">Add Photo</p>
        </p3>
        <b-card no-body :img-src="photo.src"
                v-for="(photo, index) in form.photos" :key="index">
          <b-button block class="btn btn-sm" variant="danger"
                    @click="() => form.photos.splice(index, 1)">
            Remove
          </b-button>
        </b-card>
      </b-card-group>
      <div class="row justify-content-center">
        <b-button-group class="my-2">
          <b-button type="reset" variant="danger">Reset</b-button>
          <b-button type="submit" variant="primary">Submit</b-button>
        </b-button-group>
      </div>
    </b-form>
  </div>
</template>

<script>
  import VueGoogleAutocomplete from 'vue-google-autocomplete';
  import Http from './utils/Http';
  import Times from './utils/Times';
  import PicturePickerPreview from './common/PicturePickerPreview';

  export default {
    name: 'ad-form',
    components: { p3: PicturePickerPreview, VueGoogleAutocomplete },
    data() {
      return {
        Times,
        isedit: false,
        form: {
          name: null,
          tel: null,
          birthyear: Times.changeYears(-18),
          city: null,
          price: null,
          avatar: { src: 'static/avatar.png', file: null },
          photos: [],
          description: null,
        },
      };
    },
    mounted() {
      const adId = this.$route.query.id;
      this.isedit = !!adId;
      if (adId) {
        Http.doGet(`/api/ads/${adId}`).then((ad) => {
          this.form = ad;
          this.form.avatar = { src: ad.avatar || 'static/avatar.png', file: null };
          this.form.photos = (ad.photos || [])
            .map(photo => ({ src: photo, file: null }));
          this.$refs.location.update(ad.city);
        });
      }
    },
    methods: {
      onSubmit() {
        const location = this.$refs.location;
        const place = location.autocomplete.getPlace();
        let locality = this.form.city;
        if (place) {
          const addressData = location.formatResult(place);
          locality = addressData.locality;
        }
        const notRemovedPhotos = this.form.photos
          .filter(photo => photo.src && !photo.file)
          .map(photo => (photo.src));
        const formData = new FormData();
        formData.append('ad', new Blob([JSON.stringify({
          id: this.form.id,
          name: this.form.name,
          tel: this.form.tel,
          birthyear: this.form.birthyear,
          city: locality,
          price: this.form.price,
          description: this.form.description,
          photos: notRemovedPhotos,
        })], {
          type: 'application/json',
        }));
        if (this.form.avatar.file) {
          formData.append('avatar', this.form.avatar.file);
        }
        this.form.photos
          .filter(photo => photo.src && photo.file)
          .map(photo => (photo.file))
          .forEach((file) => {
            formData.append('newphoto', file);
          });
        Http.doPost('/api/ads', formData, false)
          .then(() => this.$router.push('/'));
      },
      onReset(evt) {
        evt.preventDefault();
        /* Reset our form values */
        this.form.name = null;
        this.form.tel = null;
        this.form.birthyear = Times.changeYears(-18);
        this.form.city = null;
        this.form.price = null;
        this.form.description = null;
        this.$refs.location.clear();
        this.$refs.avatar = { src: 'static/avatar.png', file: null };
        this.$refs.photos = [];
      },
    },
  };
</script>

<style scoped>
</style>
