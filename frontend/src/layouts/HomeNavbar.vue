<template>
    <v-app-bar
            id="nav-bar"
            color="#1E5832"
            dense
            app
            fixed
    >

        <v-app-bar-nav-icon
                @click.stop="toggleDrawer"
        />

        <v-toolbar-title
                v-if="!drawer"
                class="hidden-sm-and-down tertiary--text font-weight-light title"
        >
            <router-link to="/" tag="span" style="cursor: pointer; color: white" class="title font-weight-bold">
                {{ appTitle }}
            </router-link>
        </v-toolbar-title>
        <v-spacer/>
        <v-toolbar-items>
            <v-flex
                    align-center
                    layout
                    py-2
            >
                <v-btn to="/register" class="ml-0 hidden-sm-and-down" text v-if="!loggedIn && !drawer" >
                    <v-icon left dark>mdi-account-plus</v-icon>
                    {{$t("navbar.register")}}
                </v-btn>
                <v-btn to="/login" class="ml-0 hidden-sm-and-down" text  v-if="!loggedIn && !drawer" >
                    <v-icon >mdi-login</v-icon>
                    {{$t("navbar.login")}}
                </v-btn>
                <v-btn text to="/user-events"  class="hidden-sm-and-down"  v-if="loggedIn && !drawer">
                    <v-icon >mdi-calendar-star</v-icon>
                    {{$t("navbar.ownEvents")}}
                </v-btn>
                <v-btn text to="/browse-events" class="hidden-sm-and-down"  v-if="loggedIn && !drawer">
                    <v-icon >mdi-shield-search</v-icon>
                    {{$t("navbar.browse")}}
                </v-btn>
                <v-menu
                        close-on-click
                        close-on-content-click
                        offset-y
                        v-if="isAdmin && loggedIn && !drawer"
                        class="hidden-sm-and-down"
                >
                    <template v-slot:activator="{ on }">
                        <v-btn
                                class="ml-0 hidden-sm-and-down"
                                text
                                v-on="on"
                        >
                            <v-icon>mdi-account-cog</v-icon>
                            {{$t("navbar.manage")}}
                        </v-btn>
                    </template>
                    <v-list flat>
                        <v-subheader>{{$t("navbar.userManagement")}}</v-subheader>
                        <v-list-item-group v-model="item" color="primary">
                            <v-list-item
                                    v-for="(item, i) in manageItems"
                                    :key="i"
                                    :to="item.to"
                            >
                                <v-list-item-icon>
                                    <v-icon v-text="item.icon"></v-icon>
                                </v-list-item-icon>
                                <v-list-item-content>
                                    <v-list-item-title v-text="item.title"></v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </v-list-item-group>
                    </v-list>
                </v-menu>
                <v-btn text to="/profile"  class="hidden-xs-only" v-if="loggedIn && !drawer">
                    <v-icon >mdi-account</v-icon>
                </v-btn>
                <v-btn text @click="handleLogout" class="hidden-xs-only"  v-if="loggedIn && !drawer">
                    <v-icon>mdi-logout</v-icon>
                </v-btn>

                <v-menu
                        close-on-click
                        close-on-content-click
                        offset-y
                        class="hidden-xs-only"
                >
                    <template v-slot:activator="{ on }">
                        <v-btn
                                class="ml-0 "
                                text
                                v-on="on"

                        >
                            <v-icon>mdi-translate</v-icon>
                        </v-btn>
                    </template>
                    <v-list flat>
                        <v-list-item-group v-model="item" color="primary">
                            <v-list-item
                                    v-for="(item, i) in languageItems"
                                    :key="i"
                                    @click="handleLanguageChange(item)"
                            >
                                <v-list-item-content>
                                    <v-list-item-title v-text="item.title"></v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </v-list-item-group>
                    </v-list>
                </v-menu>
            </v-flex>
        </v-toolbar-items>
    </v-app-bar>
</template>

<script>
    import {mapActions, mapGetters, mapMutations} from "vuex";
    import {Role} from "../models/role";
    import {AccountStorageService} from "../services/storage.service";

    export default {
        name: "HomeNavbar",
        data(){
            return {
                appTitle: 'HobbyHelper',
                sidebar: false,
                item:1,
                manageItems: [
                    {title: this.$t("navbar.manageUsers"), to:'/manage-users', icon:'mdi-account-edit'},
                    {title:this.$t("navbar.manageUserInfo"), to:'/manage-user-details', icon:'mdi-account-details'},
                    {title: this.$t("navbar.manageLocation"), to:'/manage-locations', icon:'mdi-crosshairs-gps'},
                    {title: this.$t("navbar.manageEventTypes"), to:'/manage-event-types', icon:'mdi-format-list-bulleted-type'},
                    {title: this.$t("navbar.manageEvents"), to:'/manage-events', icon:'mdi-calendar-edit'},
                    {title: this.$t("navbar.manageEventParticipants"), to:'/manage-event-participants', icon:'mdi-account-group'}
                ],
                languageItems : [
                    {title: 'English', key:'en'},
                    {title: 'Magyar', key:'hu'}
                ]
            }

        },
        watch: {
            accountProxy : function() {
                if(this.loggedIn) {
                    console.log("Account changed, lang key: " + this.currentAccount.langKey);
                    this.$i18n.locale = this.currentAccount.langKey;
                    /*for(var i = 0; i < this.languageItems.length; i++) {
                        if(this.languageItems[i].key === this.currentAccount.langKey)
                            this.item = i+1;
                    }*/
                }
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

            accountProxy() {
                return this.currentAccount;
            },
            isAdmin() {
                if(this.currentAccount)
                    console.log('is admin: ' +  this.currentAccount.roles.includes(Role.admin) + ' '  + this.currentAccount.roles);
                return AccountStorageService.getAccount() && AccountStorageService.getAccount().roles.includes(Role.admin)
            }
        },
        methods: {
            ...mapActions('auth', [
                'logout',
                'changeLanguage'
            ]),

            ...mapMutations('drawer', ['toggleDrawer']),

            handleLogout() {
                this.logout();
            },

            async handleLanguageChange(item) {
                if(this.changeLanguage(item.key)) {
                    console.log('Set i18n locale ' + item.key);
                    this.$i18n.locale = item.key;
                }
            }
        },
        mounted() {
        }
    };
</script>

<style>

</style>