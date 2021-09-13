<template>
  <div>
    <table class="table table-striped">
      <tr v-for="(lbl,idx) in labels" :key="idx">
        <td>{{ lbl.label }}</td>
        <td>
          <LoadableLabel :url="lbl.url"></LoadableLabel>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import LoadableLabel from "@/components/LoadableLabel";

export default {
  name: "Home",
  components: {LoadableLabel},
  data() {
    return {
      "labels": [{
        label: "На учете МПС",
        url: "registeredCount"
      }, {
        label: "Всего снято с учета",
        url: "unregisteredCount"
      }, {
        label: "Поставлено на учет в " + new Date().getFullYear(),
        url: "registeredCount/" + +new Date().getFullYear()
      }, {
        label: "Снято с учета в " + new Date().getFullYear(),
        url: "unregisteredCount/" + +new Date().getFullYear()
      }
      ]
    }
  }
}
</script>

<style scoped>

</style>