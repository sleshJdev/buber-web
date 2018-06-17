// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import BootstrapVue from 'bootstrap-vue';
import { Modal } from 'bootstrap-vue/es/components';
import { Modal as ModalDirective } from 'bootstrap-vue/es/directives';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import Vue from 'vue';
import App from './App';
import router from './router';
import Http from './components/utils/Http';

Vue.use(BootstrapVue);
Vue.use(ModalDirective);
Vue.use(Modal);
Vue.config.productionTip = false;

Http.setRouter(router);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>',
});
