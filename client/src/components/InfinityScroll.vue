<template>
  <div>
    <slot></slot>
  </div>
</template>

<script>
  export default {
    name: 'InfinityScroll',
    props: {
      onEnding: {
        type: Function,
        required: true,
      },
      compact: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        prevWorkHeight: 0,
        workHeight: 0,
      };
    },
    methods: {
      notifyIfNeeded() {
        if (!this.compact
          && window.scrollY > this.workHeight
          && this.workHeight !== this.prevWorkHeight) {
          this.prevWorkHeight = this.workHeight;
          this.onEnding();
        }
      },
      refreshWorkHeight() {
        const span = window.document.createElement('span');
        window.document.body.appendChild(span);
        this.workHeight = span.offsetTop - (2 * window.innerHeight);
        window.document.body.removeChild(span);
        window.removeEventListener('scroll', this.notifyIfNeeded);
        window.addEventListener('scroll', this.notifyIfNeeded);
      },
    },
    updated() {
      this.refreshWorkHeight();
    },
    beforeDestroy() {
      window.removeEventListener('scroll', this.notifyIfNeeded);
    },
  };
</script>

<style scoped>

</style>
