<template>
  <div>
    <span v-if="label==null" class="fa fa-spin fa-spinner"></span>
    <span>{{ label }}</span>
  </div>
</template>

<script>
import {AXIOS} from "@/components/http-client";

export default {
  name: "LoadableLabel",
  props: {
    url: {
      type: String,
      required: true
    }
  },
  data: function () {
    return {label: null}
  },
  methods: {
    loadLabel: function () {
      return AXIOS.get(this.url).then(response => (this.label = response.data));
    }
  },
  mounted() {
    this.loadLabel();
  }
}
</script>

<style scoped>

</style>