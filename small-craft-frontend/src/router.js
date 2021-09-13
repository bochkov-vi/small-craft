import Vue from 'vue'
import Router from 'vue-router'
/*import HelloWorld from '@/components/HelloWorld'
import Greeting from '@/components/Greeting'*/
import Home from '@/components/Home'
import BoatPage from "@/components/BoatPage";

Vue.use(Router)

export default window.router = new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            component: Home,
        },
        {
            path: '/home',
            name: 'homePage',
            component: Home,
            meta: {icon: "fa-home"},
        },
        {
            path: '/boat',
            name: 'boatPage',
            component: BoatPage,
            meta: {icon: "fa-ship"}
        },
    ]
})