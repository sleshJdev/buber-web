<template>
  <b-form class="pl-5 pr-5" @submit="onSubmit" @reset="onReset">
    <h1 class="text-center">Fill out fields before post your ad</h1>
    <b-form-group id="form-group-tel"
                  label="Phone number:"
                  label-for="form-tel"
                  description="Your phone number will be visible only in your pfofile.">
      <b-form-input id="form-tel"
                    type="tel"
                    v-model="form.tel"
                    required
                    placeholder="Enter your phone number">
      </b-form-input>
    </b-form-group>
    <b-form-group id="form-group-location"
                  label="Your Location:"
                  label-for="form-location">
      <vue-google-autocomplete
        id="map"
        :enable-geolocation="true"
        classname="form-control"
        placeholder="Start typing"
        v-on:placechanged="getAddressData"
        required>
      </vue-google-autocomplete>
    </b-form-group>
    <b-form-group id="form-group-banner"
                  label="Banner:"
                  label-for="form-banner">
      <b-form-file id="form-banner"
                   :state="Boolean(form.banner)"
                   v-model="form.banner"
                   accept="image/*"
                   required
                   placeholder="Choose a banner">
      </b-form-file>
    </b-form-group>
    <b-form-group id="form-goup-description"
                  label="Ad description"
                  label-for="form-description">
      <b-form-textarea id="form-description"
                       v-model="form.description"
                       placeholder="Enter ad description"
                       :rows="3"
                       :max-rows="6">
      </b-form-textarea>
    </b-form-group>
    <b-button type="submit" variant="primary">Submit</b-button>
    <b-button type="reset" variant="danger">Reset</b-button>
  </b-form>
</template>

<script>
  import VueGoogleAutocomplete from 'vue-google-autocomplete';

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
          formattedAddress: placeResultData.formatted_address,
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

        fetch('/api/ads', {
          method: 'POST',
          body: formData,
        }).then(() => {
          this.$router.push('/');
        });
      },
      onReset(evt) {
        evt.preventDefault();
        /* Reset our form values */
        this.form.tel = null;
        this.form.name = null;
        this.form.location = null;
        this.form.banner = null;
        this.form.description = null;
      },
    },
  };
</script>

<style scoped>

</style>
