<template>
  <vue-swing ref="slider" @throwout="onRemove" :config="config" v-if="compact">
    <slot></slot>
  </vue-swing>
  <b-card-group columns v-else>
    <slot></slot>
  </b-card-group>
</template>

<script>
  import VueSwing from 'vue-swing';

  export default {
    name: 'ads-container',
    props: {
      onRemove: {
        type: Function,
        required: false,
      },
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
        config: {
          allowedDirections: [
            VueSwing.Direction.LEFT,
            VueSwing.Direction.RIGHT,
          ],
          minThrowOutDistance: 100,
          maxThrowOutDistance: 200,
        },
      };
    },
  };
</script>

<style scoped>
</style>
