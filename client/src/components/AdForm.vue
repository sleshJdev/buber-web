<template>
  <div>
    <h2 class="text-center text-info">
      {{isedit ? 'Editing ad' : 'New Ad'}}
    </h2>
    <p class="text-center">
      <small class="text-warning">Fill out fields before post your ad</small>
    </p>
    <div class="row justify-content-center">
      <b-form class="pl-5 pr-5 col-9" @submit.prevent="onSubmit" @reset="onReset">
        <b-form-group id="form-group-name"
                      label="Your Name:"
                      label-for="form-name"
                      description="How you would like another users addressed to you"
                      horizontal>
          <b-form-input id="form-name"
                        size="sm"
                        type="text"
                        v-model="form.name"
                        required
                        placeholder="Enter your phone number">
          </b-form-input>
        </b-form-group>
        <b-form-group id="form-group-tel"
                      label="Phone number:"
                      label-for="form-tel"
                      description="Your phone number will be visible only in your pfofile."
                      horizontal>
          <b-form-input id="form-tel"
                        type="tel"
                        size="sm"
                        v-model="form.tel"
                        required
                        placeholder="Enter your phone number">
          </b-form-input>
        </b-form-group>
        <b-form-group id="form-group-price"
                      label="Price $/h:"
                      label-for="form-price"
                      description="Your dollar price per hour."
                      horizontal>
          <b-form-input id="form-price"
                        type="number"
                        size="sm"
                        v-model="form.price"
                        required
                        placeholder="Enter your price per hour, $">
          </b-form-input>
        </b-form-group>
        <b-form-group id="form-group-birthyear"
                      label="Birthyear:"
                      label-for="form-group-birthyear"
                      description="When you born?"
                      horizontal>
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
        <b-form-group id="form-group-location"
                      label="Your Location:"
                      label-for="form-location"
                      horizontal>
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
        <b-form-group id="form-group-banner"
                      label="Avatar:"
                      label-for="form-banner-0"
                      horizontal>
          <b-form-file id="form-banner"
                       ref="avatar"
                       size="sm"
                       class="form-control form-control-sm"
                       :state="Boolean(form.avatar)"
                       v-model="form.avatar"
                       accept="image/*"
                       required
                       placeholder="Avatar">
          </b-form-file>
        </b-form-group>
        <b-form-group horizontal>
          <div class="custom-control-inline mb-1"
               v-for="(photo, index) in form.photos" :key="index">
            <b-button class="mr-1" variant="danger"
                      @click="removePictureInput(photo)">X
            </b-button>
            <b-form-file class="form-control form-control-sm"
                         accept="image/*" required placeholder="Photo"
                         size="sm" v-model="photo.value"></b-form-file>
          </div>
          <b-button typy="button" size="sm" variant="success"
                    @click="addMoreAvatarInput">
            Add more picture
          </b-button>
        </b-form-group>
        <b-form-group id="form-goup-description"
                      label="Ad description"
                      label-for="form-description"
                      horizontal>
          <b-form-textarea id="form-description"
                           v-model="form.description"
                           size="sm"
                           placeholder="Enter ad description"
                           :rows="3"
                           :max-rows="6">
          </b-form-textarea>
        </b-form-group>
        <div class="row justify-content-end">
          <b-button-group>
            <b-button type="reset" variant="danger">Reset</b-button>
            <b-button type="submit" variant="primary">Submit</b-button>
          </b-button-group>
        </div>
      </b-form>
    </div>
  </div>
</template>

<script>
  import VueGoogleAutocomplete from 'vue-google-autocomplete';
  import Http from './utils/Http';
  import Times from './utils/Times';

  export default {
    name: 'ad-form',
    components: { VueGoogleAutocomplete },
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
          avatar: null,
          photos: null,
          description: null,
        },
      };
    },
    mounted() {
      const adId = this.$route.query.id;
      this.isedit = !!adId;
      if (adId) {
        Http.doGet(`/api/ads/${adId}`)
          .then((ad) => {
            this.form = ad;
            this.$refs.location.update(ad.city);
          });
      }
    },
    methods: {
      removePictureInput(pic) {
        const idx = this.form.photos.indexOf(pic);
        if (idx >= 0) {
          this.form.photos.splice(idx, 1);
        }
      },
      addMoreAvatarInput() {
        this.form.photos.push({ value: null });
      },
      onSubmit() {
        const location = this.$refs.location;
        const place = location.autocomplete.getPlace();
        const addressData = location.formatResult(place);
        const formData = new FormData();
        formData.append('ad', new Blob([JSON.stringify({
          name: this.form.name,
          tel: this.form.tel,
          birthyear: this.form.birthyear,
          city: addressData.locality,
          price: this.form.price,
          description: this.form.description,
        })], {
          type: 'application/json',
        }));
        formData.append('avatar', this.form.avatar);
        this.form.photos.forEach((it) => {
          if (it.value) {
            formData.append('photo', it.value);
          }
        });
        Http.doPost('/api/ads', formData, false)
          .then(() => this.$router.push('/'));
      },
      onReset(evt) {
        evt.preventDefault();
        /* Reset our form values */
        this.form.tel = null;
        this.form.name = null;
        this.form.description = null;
        this.$refs.location.clear();
        this.$refs.avatar.reset();
      },
    },
  };
</script>

<style scoped>
  .custom-control-inline {
    width: 100%;
  }
</style>
