import Vue from 'vue';
import Router from 'vue-router';
import Home from '../components/Home';
import AdForm from '../components/AdForm';
import AdReview from '../components/AdReview';
import SignInForm from '../components/SignInForm';
import Profile from '../components/Profile';

Vue.use(Router);

export default new Router({
  routes: [
    {
      name: 'ad-review',
      path: '/ads/review',
      component: AdReview,
    }, {
      path: '/',
      component: Home,
    }, {
      name: 'ads-editor',
      path: '/ads/editor',
      component: AdForm,
    }, {
      name: 'sign-in',
      path: '/sign-in',
      component: SignInForm,
    }, {
      name: 'profile',
      path: '/profile',
      component: Profile,
    },
  ],
});
