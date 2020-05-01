<template>
  <div id="app">
    <v-app light>
      <home-navbar/>

      <home-drawer />
      <!-- <div class="container"> -->
      <v-content>
        <router-view />
      </v-content>
      <!--  </div> -->
    </v-app>
  </div>
</template>

<script>
  import {mapActions, mapGetters} from "vuex";

  export default {
    components: { HomeDrawer: () => import('@/layouts/HomeDrawer'),
      HomeNavbar: () => import('@/layouts/HomeNavbar'), },
    computed: {
      ...mapGetters('auth', [
        'loggedIn'
      ])
    },
    methods: {
      ...mapActions('auth', [
        'logout'
      ]),

      handleLogout() {
        this.logout();
      }
    }
  };
</script>

<style lang="scss">
  @import '@/styles/index.scss';

  /* Remove in 1.2 */
  .v-datatable thead th.column.sortable i {
    vertical-align: unset;
  }
  .v-content {
    background-color: #eeeeee;
  }

 /* #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }

  #nav {
    padding: 30px;
  }

  #nav a {
    font-weight: bold;
    color: #2c3e50;
  }

  #nav a.router-link-exact-active {
    color: #42b983;
  }*/
</style>
