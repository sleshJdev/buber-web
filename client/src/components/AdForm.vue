<template>
  <div>
    <h2 class="text-center text-info">New Ad</h2>
    <p class="text-center">
      <small class="text-warning">Fill out fields before post your ad</small>
    </p>
    <div class="row justify-content-center">
      <b-form class="pl-5 pr-5 col-9" @submit="onSubmit" @reset="onReset">
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
        <b-form-group id="form-group-location"
                      label="Your Location:"
                      label-for="form-location"
                      horizontal>
          <vue-google-autocomplete
            id="map"
            size="sm"
            ref="location"
            :enable-geolocation="true"
            classname="form-control form-control-sm"
            placeholder="Start typing"
            v-on:placechanged="getAddressData"
            required>
          </vue-google-autocomplete>
        </b-form-group>
        <b-form-group id="form-group-banner"
                      label="Banner:"
                      label-for="form-banner"
                      horizontal>
          <b-form-file id="form-banner"
                       ref="banner"
                       size="sm"
                       class="form-control form-control-sm"
                       :state="Boolean(form.banner)"
                       v-model="form.banner"
                       accept="image/*"
                       required
                       placeholder="Choose or drag&drop a banner for your ad">
          </b-form-file>
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

  export default {
    name: 'ad-form',
    components: { VueGoogleAutocomplete },
    data() {
      return {
        form: {
          tel: null,
          name: null,
          location: null,
          banner: null,
          description: null,
        },
      };
    },
    methods: {
      getAddressData(addressData, placeResultData) {
        this.form.location = {
          placeId: placeResultData.place_id,
          address: placeResultData.formatted_address,
        };
      },
      onSubmit(evt) {
        evt.preventDefault();

        const formData = new FormData();
        formData.append('ad', new Blob([JSON.stringify({
          tel: this.form.tel,
          name: this.form.name,
          location: this.form.location,
          description: this.form.description,
        })], {
          type: 'application/json',
        }));
        formData.append('file', this.form.banner);

        Http.doPost('/api/ads', formData).then(() => {
          this.$router.push('/');
        });
      },
      onReset(evt) {
        evt.preventDefault();
        /* Reset our form values */
        this.form.tel = null;
        this.form.name = null;
        this.form.description = null;
        this.$refs.location.clear();
        this.$refs.banner.reset();
      },
    },
  };
</script>

<style scoped>

</style>
