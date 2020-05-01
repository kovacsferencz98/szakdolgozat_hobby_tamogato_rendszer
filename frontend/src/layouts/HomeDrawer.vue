<template>
    <v-navigation-drawer
            id="nav-drawer"
            v-model="drawerLocal"
            app
            dark
            floating
            persistent
            mobile-break-point="970"
            width="260"
            height="100%"
            :src="require('../assets/green-leaf-plant.jpg')"
    >
        <v-list
                dense
                nav
                height="98%"
                max-height="100%"
                color="transparent"
        >
            <v-list-item to="/" >
                <v-list-item-avatar
                        class="align-self-center"
                        color="white"
                        contain
                >
                    <v-img
                            :src="require('../assets/hobby-png-1.png')"
                            max-height="34"
                            contain
                    />
                </v-list-item-avatar>

                <v-list-item-content
                        max-height="34"
                        align="center"
                        justify="center" style="height: 34px; "
                >
                    <v-list-item-title
                            style="height: 34px; "
                            justify="center"
                            max-height="34"
                            class="title font-weight-black text-center">
                        {{appTitle}}
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>
            <v-divider class="mb-2" />
            <v-list-item
                    to="/register"
                    avatar
                    v-if="!loggedIn"

            >
                <v-list-item-action>
                    <v-icon>mdi-account-plus</v-icon>
                </v-list-item-action>
                <v-list-item-content
                        style="height: 2em"
                >
                    <v-list-item-title
                            style="height: 1.5em"
                            class="subtitle-1 font-weight-bold ">{{$t('navbar.register')}}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
            <v-list-item
                    to="/login"
                    avatar
                    class="v-list-item"
                    v-if="!loggedIn"
            >
                <v-list-item-action>
                    <v-icon>mdi-login</v-icon>
                </v-list-item-action>
                <v-list-item-content
                        style="height: 2em"
                >
                    <v-list-item-title
                            style="height: 1.5em"
                            class="subtitle-1 font-weight-bold "
                    >{{$t('navbar.login')}}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>


            <v-list-item
                    to="/user-events"
                    avatar
                    class="v-list-item"
                    v-if="loggedIn"
            >
                <v-list-item-action>
                    <v-icon >mdi-calendar-star</v-icon>
                </v-list-item-action>
                <v-list-item-content
                        style="height: 2em"
                >
                    <v-list-item-title
                            style="height: 1.5em"
                            class="subtitle-1 font-weight-bold "
                    >{{$t("navbar.ownEvents")}}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
            <v-list-item
                    to="/browse-events"
                    avatar
                    class="v-list-item"
                    v-if="loggedIn"
            >
                <v-list-item-action>
                    <v-icon >mdi-shield-search</v-icon>
                </v-list-item-action>
                <v-list-item-content
                        style="height: 2em"
                >
                    <v-list-item-title
                            style="height: 1.5em"
                            class="subtitle-1 font-weight-bold "
                    >
                        {{$t("navbar.browse")}}
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>


            <v-list-group
                    prepend-icon="mdi-account-cog"
                    value="true"
                    v-if="isAdmin && loggedIn"
                    dark
                    active
                    color="white"
            >
                <template v-slot:activator>
                        <v-list-item-title
                                style="height: 1.5em"
                                class="subtitle-1 font-weight-bold ">{{$t("navbar.manage")}}</v-list-item-title>
                </template>

                <v-list-item
                        v-for="(item, i) in manageItems"
                        :key="i"
                        :to="item.to"
                >
                    <v-list-item-content
                            style="height: 2em"
                    >
                        <v-list-item-title
                                style="height: 1.5em"
                                class="subtitle-2 font-weight-medium "
                                v-text="item.title"/>
                    </v-list-item-content>
                    <v-list-item-icon>
                        <v-icon v-text="item.icon"/>
                    </v-list-item-icon>
                </v-list-item>

            </v-list-group>
            <v-list-item
                    avatar
                    class="v-list-item"
                    v-if="loggedIn"
                    to="/profile">
                <v-list-item-action>
                    <v-icon>mdi-account</v-icon>
                </v-list-item-action>
                <v-list-item-content
                        style="height: 2em"
                >
                    <v-list-item-title
                            style="height: 1.5em"
                            class="subtitle-1 font-weight-bold "
                    >{{$t('navbar.profile')}}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
            <v-list-item
                    avatar
                    class="v-list-item"
                    v-if="loggedIn"
                    @click.prevent="handleLogout">
                <v-list-item-action>
                    <v-icon>mdi-logout</v-icon>
                </v-list-item-action>
                <v-list-item-content
                        style="height: 2em"
                >
                    <v-list-item-title
                            style="height: 1.5em"
                            class="subtitle-1 font-weight-bold "
                    >{{$t('navbar.logout')}}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </v-list>
    </v-navigation-drawer>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from 'vuex'
    import {Role} from "../models/role";
    import {AccountStorageService} from "../services/storage.service";


    export default {
        name: "HomeDrawer",
        data(){
            return {
                appTitle: 'HobbyHelper',
                sidebar: false,
                manageItems: [
                    {title: this.$t("navbar.manageUsers"), to:'/manage-users', icon:'mdi-account-edit'},
                    {title:this.$t("navbar.manageUserInfo"), to:'/manage-user-details', icon:'mdi-account-details'},
                    {title: this.$t("navbar.manageLocation"), to:'/manage-locations', icon:'mdi-crosshairs-gps'},
                    {title: this.$t("navbar.manageEventTypes"), to:'/manage-event-types', icon:'mdi-format-list-bulleted-type'},
                    {title: this.$t("navbar.manageEvents"), to:'/manage-events', icon:'mdi-calendar-edit'},
                    {title: this.$t("navbar.manageEventParticipants"), to:'/manage-event-participants', icon:'mdi-account-group'}
                ]
            }

        },


        computed: {
            ...mapGetters('auth', [
                'loggedIn',
                'currentAccount'
            ]),

            ...mapGetters('drawer', [
                'drawer'
            ]),

            drawerLocal: {
                get () {
                    return this.drawer
                },
                set (val) {
                    this.setDrawer(val)
                }
            },
            isAdmin() {
                if(this.currentAccount)
                    console.log('is admin: ' +  this.currentAccount.roles.includes(Role.admin) + ' '  + this.currentAccount.roles);
                return AccountStorageService.getAccount() && AccountStorageService.getAccount().roles.includes(Role.admin)
            }
        },

        methods: {
            ...mapMutations('drawer', ['setDrawer']),

            ...mapActions('auth', [
                'logout'
            ]),

            handleLogout() {
                this.logout();
            }
        }

    }
</script>

<style  >
</style>