<template>
  <div id="app">
    <b-navbar type="light" variant="light">
      <b-nav>
        <b-nav-item v-show="route.name" class="nav-item" v-for="(route,idx,) in routes" :key="idx" :to="route.path" exact exact-active-class="active" active-class="active">
          <i :class="route.meta.icon" class="fa"/>
          {{ $t(route.name) }}
        </b-nav-item>
      </b-nav>
    </b-navbar>
    <div class="container">
      <keep-alive>
        <router-view/>
      </keep-alive>
    </div>
  </div>
</template>

<script>


export default {
  name: 'App',
  props: {
    routes: {
      type: Array,
      required: true
    }
  }
}
</script>

<style>
#app {

}
</style>
