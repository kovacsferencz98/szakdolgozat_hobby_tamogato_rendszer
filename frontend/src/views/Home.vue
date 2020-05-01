<template>
  <div>
    <v-row no-gutters>
      <v-img
              :max-height="'calc(100vh - ' + $vuetify.application.top + 'px)'"
              :src="require('../assets/aurora.jpg')"
      >
        <v-theme-provider dark>
          <v-container fill-height>
            <v-row
                    align="center"
                    class="white--text mx-auto"
                    justify="center"
            >
              <v-col
                      class="white--text text-center"
                      cols="12"
                      tag="h1"
              >
                <v-img :src="require('../assets/hobby-logo-white.png')" alt="Hobby" height="200" contain/>
                <br/>
                <br/>
                <span
                        class="font-weight-light"
                        :class="[$vuetify.breakpoint.smAndDown ? 'display-1' : 'display-2']"
                >
                      {{$t('home.welcome')}}
                    </span>

                <br>

                <span
                        :class="[$vuetify.breakpoint.smAndDown ? 'display-3': 'display-4']"
                        class="font-weight-black"
                >
                      HobbyHelper
                    </span>

              </v-col>

              <v-btn
                      class="align-self-end"
                      fab
                      outlined
                      @click="$vuetify.goTo('#features')"
              >
                <v-icon>mdi-chevron-triple-down</v-icon>
              </v-btn>
            </v-row>
          </v-container>
        </v-theme-provider>
      </v-img>
    </v-row>

    <section id="features">
      <div class="py-4"></div>
      <v-layout
              column
              wrap
              class="my-10"
              align-center
      >
        <v-flex xs12 sm4 class="my-4">
          <div class="text-center">
            <h2 class="display-2 font-weight-black">{{$t('home.firstTitle')}}</h2>
            <span class="headline font-weight-bold">
               {{$t('home.firstText')}}
              </span>
          </div>
        </v-flex>
        <v-responsive
                class="mx-auto mb-8"
                width="56"
        >
          <v-divider class="mb-1"/>

          <v-divider/>
        </v-responsive>
        <v-flex xs12>
          <v-container grid-list-xl>
            <v-layout row wrap align-center>
              <v-flex xs12 md4>
                <v-card
                        class="px-4"
                        flat
                        color="white" >
                  <v-card-text class="text-center">
                    <v-theme-provider dark>
                      <div>
                        <v-avatar
                                color="primary"
                                size="88"
                        >
                          <v-icon
                                  x-large
                          >mdi-magnify</v-icon>
                        </v-avatar>
                      </div>
                    </v-theme-provider>
                  </v-card-text>
                  <v-card-title primary-title class="layout justify-center">
                    <div class="headline text-center">{{$t('home.searchTitle')}}</div>
                  </v-card-title>
                  <v-card-text class="text-center">
                    {{$t('home.searchText')}}
                  </v-card-text>
                </v-card>
              </v-flex>
              <v-flex xs12 md4>
                <v-card
                        class="px-4"
                        flat
                        color="white">
                  <v-card-text class="text-center">
                    <v-theme-provider dark>
                      <div>
                        <v-avatar
                                color="primary"
                                size="88"
                        >
                          <v-icon
                                  x-large
                          >mdi-plus-circle</v-icon>
                        </v-avatar>
                      </div>
                    </v-theme-provider>
                  </v-card-text>
                  <v-card-title primary-title class="layout justify-center">
                    <div class="headline">{{$t('home.createTitle')}}</div>
                  </v-card-title>
                  <v-card-text class="text-center">
                    {{$t('home.createText')}}
                  </v-card-text>
                </v-card>
              </v-flex>
              <v-flex xs12 md4>
                <v-card
                        class="px-4"
                        flat
                        color="white">
                  <v-card-text class="text-center">
                    <v-theme-provider dark>
                      <div>
                        <v-avatar
                                color="primary"
                                size="88"
                        >
                          <v-icon
                                  x-large
                          >mdi-chat-processing</v-icon>
                        </v-avatar>
                      </div>
                    </v-theme-provider>
                  </v-card-text>
                  <v-card-title primary-title class="layout justify-center">
                    <div class="headline text-center">{{$t('home.chatTitle')}}</div>
                  </v-card-title>
                  <v-card-text class="text-center">
                    {{$t('home.chatText')}}
                  </v-card-text>
                </v-card>
              </v-flex>
            </v-layout>
          </v-container>
        </v-flex>
      </v-layout>
    </section>

    <section>
      <v-parallax v-bind:src="require('../assets/people-silhouette-during-sunset-853168.jpg')"  :height="$vuetify.breakpoint.smAndDown ? 700 :500">
        <v-container fill-height>
          <v-layout column align-center justify-center>
            <div class="display-2 white--text mb-4 text-center font-weight-black">{{$t('home.secondTitle')}}</div>
            <div class="headline white--text mb-4 text-center font-weight-bold">{{$t('home.secondText')}}</div>
            <v-btn
                    class="mt-12"
                    color="#346C47"
                    dark
                    large
                   to="/login"
            >
              {{$t('home.getStarted')}}
            </v-btn>
          </v-layout>
        </v-container>
      </v-parallax>
    </section>

    <section>
      <div class="py-12"></div>
      <v-container grid-list-xl>
        <v-flex xs12 class="my-4">
          <div class="text-center">
            <h2 class="display-2 font-weight-black">{{$t('home.about')}}</h2>
          </div>
        </v-flex>

        <v-responsive
                class="mx-auto mb-12"
                width="56"
        >
          <v-divider class="mb-1"></v-divider>

          <v-divider></v-divider>
        </v-responsive>

        <v-layout row wrap justify-center class="my-12">

          <v-flex xs12 md4>

            <v-card
                    class="px-4"
                    flat
                    color="white">
              <v-card-text class="text-center">
                <v-theme-provider dark>
                  <div>
                    <v-avatar
                            color="primary"
                            size="88"
                    >
                      <v-icon
                              x-large
                      >mdi-information</v-icon>
                    </v-avatar>
                  </div>
                </v-theme-provider>
              </v-card-text>
              <v-card-title primary-title class="layout justify-center">
                <div class="headline">{{$t('home.companyInfo')}}</div>
              </v-card-title>
              <v-card-text class="text-center">
                {{$t('home.infoText')}}
              </v-card-text>
            </v-card>
          </v-flex>
          <v-flex xs12 md4 >
            <v-card
                    class="px-4"
                    flat
                    color="white">
              <v-card-text class="text-center">
                <v-theme-provider dark>
                  <div>
                    <v-avatar
                            color="primary"
                            size="88"
                    >
                      <v-icon
                              x-large
                      >mdi-smart-card</v-icon>
                    </v-avatar>
                  </div>
                </v-theme-provider>
              </v-card-text>
              <v-card-title primary-title class="layout justify-center">
                <div class="headline">{{$t('home.contact')}}</div>
              </v-card-title>
              <v-list class="transparent">
                <v-list-item>
                  <v-list-item-action>
                    <v-icon color="#346C47" class="text--lighten-2">mdi-phone</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>666-666-6666</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-action>
                    <v-icon color="#346C47" class="text--lighten-2">mdi-map-marker</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>Debrecen, HU</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
                <v-list-item>
                  <v-list-item-action>
                    <v-icon color="#346C47" class=" text--lighten-2">mdi-email</v-icon>
                  </v-list-item-action>
                  <v-list-item-content>
                    <v-list-item-title>fake@mail.com</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card>
          </v-flex>
          <v-flex xs12 md4 >
            <v-card
                    class="px-4"
                    flat
                    color="white">
              <v-card-text class="text-center">
                <v-theme-provider dark>
                  <div>
                    <v-avatar
                            color="primary"
                            size="88"
                    >
                      <v-icon
                              x-large
                      >mdi-share-variant</v-icon>
                    </v-avatar>
                  </div>
                </v-theme-provider>
              </v-card-text>
              <v-card-title primary-title class="layout justify-center">
                <div class="headline">{{$t('home.socialMedia')}}</div>
              </v-card-title>
              <v-layout row wrap>
                <v-flex xs6 >
                  <v-theme-provider dark>
                    <div class="text-center">
                      <v-btn icon href="https://www.facebook.com/ferencz.kovacs.50" target="_blank" style="text-decoration: none">
                        <v-avatar
                                color="#3b5998"
                                size="70"
                        >
                          <v-icon
                                  x-large
                          >mdi-facebook</v-icon>
                        </v-avatar>
                      </v-btn>
                    </div>
                  </v-theme-provider>
                </v-flex>
                <v-flex xs6 >
                  <div >
                    <v-theme-provider dark>
                      <div class="text-center">
                        <v-btn icon href="https://www.instagram.com/kferi98/" target="_blank" style="text-decoration: none">
                        <v-avatar
                                color="#F58529"
                                size="70"
                        >
                          <v-icon
                                  x-large
                          >mdi-instagram</v-icon>
                        </v-avatar>
                        </v-btn>
                      </div>
                    </v-theme-provider>
                  </div>
                </v-flex>
                <v-flex xs6 >
                  <div >
                    <v-theme-provider dark>
                      <div class="text-center">
                        <v-btn icon href="https://twitter.com/KovcsFerencz" target="_blank" style="text-decoration: none">
                          <v-avatar
                                  color="#00acee"
                                  size="70"
                          >
                            <v-icon
                                    x-large
                            >mdi-twitter</v-icon>
                          </v-avatar>
                        </v-btn>
                      </div>
                    </v-theme-provider>
                  </div>
                </v-flex>
                <v-flex xs6 >
                  <div >
                    <v-theme-provider dark>
                      <div class="text-center">
                        <v-btn icon href="https://www.linkedin.com/in/ferencz-kovacs-8a593a1a2/" target="_blank" style="text-decoration: none">
                          <v-avatar
                                  color="#2867B2"
                                  size="70"
                          >
                            <v-icon
                                    x-large
                            >mdi-linkedin</v-icon>
                          </v-avatar>
                        </v-btn>
                      </div>
                    </v-theme-provider>
                  </div>
                </v-flex>
              </v-layout>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </section>
    <v-footer
            class="justify-center"
            color="#292929"
            height="75"
    >
      <div class="title font-weight-light grey--text text--lighten-1 text-center">
        &copy; {{ (new Date()).getFullYear() }} — {{$t("home.made")}}
        <v-icon color="primary" size="18">
        mdi-heart
        </v-icon> {{$t('home.by')}} <a href="https://github.com/kovacsferencz98/" target="_blank">Ferencz Kovács</a>
      </div>
    </v-footer>
  </div>
</template>

<script>

  export default {
    name: 'home'
  }
</script>

<style  lang="scss">
</style>
