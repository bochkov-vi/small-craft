import Vue from 'vue'
import App from './App.vue'
import router from "@/router";
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'font-awesome/css/font-awesome.min.css'
import i18n from "@/i18n";
//

Vue.config.productionTip = false
Vue.use(BootstrapVue)

new Vue({
    i18n,
    router,
    render: h => h(App, {props: {routes: router.getRoutes(), i18n: i18n}}),
}).$mount('#app')
