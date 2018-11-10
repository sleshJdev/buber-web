<template>
    <div :class="{crop: url && url.includes('leolist')}">
      <slot></slot>
    </div>
</template>

<script>
  export default {
    name: 'CropCardImg',
    props: ['url'],
  };
</script>

<style scoped>
  .crop {
    overflow: hidden;
  }

  .crop img {
    margin-bottom: -20%;
  }
</style>
