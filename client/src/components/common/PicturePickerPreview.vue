<template>
  <b-card @dragover="onDragOver" @drop="onDrop" no-body>
    <input type="file" class="d-none" accept="image/*"
           ref="fileInput" @change="onInputFileChange"/>
    <slot></slot>
    <b-card-img @click="openFileDialog" :src="src">
    </b-card-img>
  </b-card>
</template>

<script>
  export default {
    name: 'PicturePickerPreview',
    props: ['src'],
    methods: {
      openFileDialog() {
        this.$refs.fileInput.click();
      },
      onInputFileChange(e) {
        this.sendPicture(e.target.files[0]);
      },
      onDragOver(e) {
        e.dataTransfer.dropEffect = 'copy';
      },
      onDrop(e) {
        this.sendPicture(e.dataTransfer.files[0]);
      },
      sendPicture(file) {
        const fr = new FileReader();
        fr.onload = (e) => {
          const src = e.target.result;
          this.$emit('change', { src, file });
        };
        fr.readAsDataURL(file);
      },
    },
  };
</script>

<style scoped>
  .file-input {
    display: none;
    position: absolute;
    top: -999999px;
    left: -999999px;
  }
</style>
