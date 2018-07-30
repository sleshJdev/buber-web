<template>
  <vue-swing ref="slider"
             :throwin="onThrowIn"
             :config="config"
             v-if="compact">
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
      onThrowOut: {
        type: Function,
        required: false,
      },
      compact: {
        type: Boolean,
        required: false,
      },
    },
    data() {
      return {
        config: {
          throwOutConfidence: (xOffset, yOffset, element) => {
            const confidence = VueSwing.Card.throwOutConfidence(xOffset, yOffset, element);
            return Math.min(confidence * 3, 1);
          },
          isThrowOut: (xOffset, yOffset, element, throwOutConfidence) => {
            if (throwOutConfidence === 1) {
              this.onThrowOut();
            }
            return false;
          },
          // transform: (element, coordinateX, coordinateY, rotation) => {
          //   const signOffset = Math.sign(Math.random() - 0.5);
          //   const x = coordinateX + (signOffset * 2);
          //   const y = coordinateY + (signOffset * 4);
          //   VueSwing.Card.transform(element, x, y, rotation);
          // },
          // rotation(coordinateX, coordinateY, element, maxRotation) {
          //   const signOffset = Math.sign(Math.random() - 0.5);
          //   const angle = VueSwing.Card.rotation(coordinateX, coordinateY, element, maxRotation);
          //   return angle === 0 ? (signOffset * (Math.random() * 5)) : angle;
          // },
        },
      };
    },
    methods: {
      onThrowIn(event) {
          const signOffset = Math.sign(Math.random() - 0.5);
          const x = signOffset * 2;
          const y = signOffset * 4;
          const a = signOffset * 4;
          VueSwing.Card.transform(event.target, x, y, a);
      },
    },
  };
</script>

<style scoped>
</style>
