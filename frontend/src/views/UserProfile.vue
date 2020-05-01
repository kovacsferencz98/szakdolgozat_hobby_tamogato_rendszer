<template>
    <v-container
            fill-height
            fluid
            grid-list-xl>
        <v-layout
                justify-center
                wrap
        >
            <v-flex
                    xs12
            >
                <v-alert color="error" v-if="message" dark>{{message}}</v-alert>
            </v-flex>
            <v-flex
                    xs12
                    md8
            >
                <material-card
                        color="#346C47"
                        :title="$t('userProfile.userProfileLabel')"
                        :text="$t('userProfile.userProfileText')"

                >
                    <v-flex xs12 >
                        <v-container fill-width>
                            <v-data-table
                                    :headers="ownEventHeaders"
                                    :items="selectedUserProfile.ownEvents"
                                    sort-by="id"
                                    :search="searchOwnEvents"
                                    class="elevation-1"
                                    hide-default-footer
                                    :page.sync="ownEventsPage"
                                    :items-per-page="ownEventsItemsPerPage"
                                    @page-count="ownEventsPageCount = $event"
                            >
                                <template v-slot:top>
                                    <v-toolbar flat >
                                        <v-toolbar-title>{{$t('userEvents.ownEvents')}}</v-toolbar-title>
                                        <v-spacer/>
                                        <v-text-field
                                                v-model="searchOwnEvents"
                                                append-icon="mdi-magnify"
                                                :label="$t('searchLabel')"
                                                single-line
                                                hide-details
                                        />
                                    </v-toolbar>
                                </template>
                                <template v-slot:item.name="{ item }">
                                    <v-btn
                                            text
                                            color="primary"
                                            :to="{name:'EventView', params:{id:item.id}}"
                                    >
                                        {{item.name}}
                                    </v-btn>
                                </template>
                                <template v-slot:item.currentAttendance="{ item }">
                                    {{item.currentAttendance}} ({{item.minAttendance}} - {{item.maxAttendance}})
                                </template>
                                <template v-slot:no-data>
                                    <v-btn color="primary" @click="initialize">{{$t('tryAgainLabel')}}</v-btn>
                                </template>
                            </v-data-table>
                            <v-container py-0>
                                <v-layout wrap>
                                    <v-flex
                                            xs12
                                            md9
                                    >
                                        <v-pagination
                                                v-model="ownEventsPage"
                                                :length="ownEventsPageCount"
                                                class="my-4"
                                                :total-visible="7"
                                                circle
                                        />
                                    </v-flex>

                                    <v-flex
                                            xs12
                                            md3
                                    >
                                        <v-select
                                                v-model="ownEventsItemsPerPage"
                                                :items="possibleItemsPerPage"
                                                :label="$t('rowsPerPageLabel')"
                                        />
                                    </v-flex>
                                </v-layout>
                            </v-container>
                        </v-container>
                    </v-flex>

                    <v-flex xs12 >
                        <v-container fill-width>
                            <v-data-table
                                    :headers="participateEventHeaders"
                                    :items="selectedUserProfile.participateEvents"
                                    sort-by="id"
                                    :search="searchParticipateEvents"
                                    class="elevation-1"
                                    hide-default-footer
                                    :page.sync="participateEventsPage"
                                    :items-per-page="participateEventsItemsPerPage"
                                    @page-count="participateEventsPageCount = $event"
                            >
                                <template v-slot:top>
                                    <v-toolbar flat >
                                        <v-toolbar-title>{{$t('userEvents.joinedEvents')}}</v-toolbar-title>
                                        <v-spacer/>
                                        <v-text-field
                                                v-model="searchParticipateEvents"
                                                append-icon="mdi-magnify"
                                                :label="$t('searchLabel')"
                                                single-line
                                                hide-details
                                        />
                                    </v-toolbar>
                                </template>
                                <template v-slot:item.name="{ item }">
                                    <v-btn
                                            text
                                            :to="{name:'EventView', params:{id:item.id}}"
                                            color="primary"
                                    >
                                        {{item.name}}
                                    </v-btn>
                                </template>
                                <template v-slot:item.currentAttendance="{ item }">
                                    {{item.currentAttendance}} ({{item.minAttendance}} - {{item.maxAttendance}})
                                </template>
                                <template v-slot:item.createdByUsername="{ item }">
                                    <v-btn
                                            text
                                            :to="{name:'UserProfile', params:{id:item.createdById}}"
                                            color="primary"
                                    >
                                        {{item.createdByUsername}}
                                    </v-btn>
                                </template>
                                <template v-slot:no-data>
                                    <v-btn color="primary" @click="initialize">{{$t('tryAgainLabel')}}</v-btn>
                                </template>
                            </v-data-table>
                            <v-container py-0>
                                <v-layout wrap>
                                    <v-flex
                                            xs12
                                            md9
                                    >
                                        <v-pagination
                                                v-model="participateEventsPage"
                                                :length="participateEventsPageCount"
                                                class="my-4"
                                                :total-visible="7"
                                                circle
                                        />
                                    </v-flex>

                                    <v-flex
                                            xs12
                                            md3
                                    >
                                        <v-select
                                                v-model="participateEventsItemsPerPage"
                                                :items="possibleItemsPerPage"
                                                :label="$t('rowsPerPageLabel')"
                                        />
                                    </v-flex>
                                </v-layout>
                            </v-container>
                        </v-container>
                    </v-flex>
                </material-card>
            </v-flex>
            <v-flex
                    xs12
                    md4
            >
                <material-card class="v-card-profile" width="100%">
                    <v-avatar
                            slot="offset"
                            class="mx-auto d-block"
                            size="130"
                    >
                        <img
                                :src="selectedUserProfile.userDetails.profilePicId ? 'http://localhost:8080/api/downloadPicture/'+selectedUserProfile.userDetails.profilePicId : require('../assets/avatar.png')"
                        >
                    </v-avatar>
                    <v-card-text class="text-s-center">
                        <h4 class="card-title font-weight-light">{{selectedUserProfile.user.firstName + ' ' + selectedUserProfile.user.lastName}}</h4>
                        <p class="card-description font-weight-light">{{selectedUserProfile.userDetails.description}}</p>
                        <div class="card-description font-weight-light">
                            <strong>{{$t('userProfile.ownerRating')}}: </strong>
                            <v-rating
                                    v-model="selectedUserProfile.ratingAsOwner"
                                    :length="5"
                                    color="orange"
                                    :readonly="true"
                                    :dense="true"
                                    background-color="orange lighten-3"
                            />
                            <span class="grey--text text--lighten-2 caption mr-2">
                                 ({{ selectedUserProfile.ratingAsOwner }})
                            </span>
                        </div>
                        <div class="card-description font-weight-light">
                            <strong>{{$t('userProfile.participantRating')}} </strong>
                            <v-rating
                                    v-model="selectedUserProfile.ratingAsParticipant"
                                    :length="5"
                                    color="orange"
                                    :readonly="true"
                                    :dense="true"
                                    half-increments
                                    background-color="orange lighten-3"
                            />
                            <span class="grey--text text--lighten-2 caption mr-2">
                                 ({{ selectedUserProfile.ratingAsParticipant }})
                            </span>
                        </div>
                    </v-card-text>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from "vuex";
    import router from "../router";

    export default {
        name: "UserProfile",
        data:  vm => ({
            userId: null,
            searchOwnEvents: '',
            searchParticipateEvents: '',
            message: '',
            ownEventsPage: 1,
            ownEventsPageCount: 0,
            ownEventsItemsPerPage: 10,
            possibleItemsPerPage: [5, 10, 15],
            ownEventHeaders: [
                { text: vm.$t('manageEvents.eventNameLabel'), value: 'name' },
                { text: vm.$t('manageEvents.typeLabel'), value: 'typeName' },
                { text: vm.$t('userEvents.attendance'), value: 'currentAttendance' },
                { text: vm.$t('manageEvents.startLabel'), value: 'startsAt' },
            ],
            participateEventsPage: 1,
            participateEventsPageCount: 0,
            participateEventsItemsPerPage: 10,
            participateEventHeaders: [
                { text:  vm.$t('manageEvents.eventNameLabel'), value: 'name' },
                { text:  vm.$t('manageEvents.typeLabel'), value: 'typeName' },
                { text:  vm.$t('userEvents.attendance'), value: 'currentAttendance' },
                { text: vm.$t('manageEvents.startLabel'), value: 'startsAt' },
                { text: vm.$t('eventView.createdByLabel'), value: 'createdByUsername' }
            ],
        }),
        computed: {
            ...mapGetters('user', [
                'selectedUserProfile',
                'obtainErrorCode',
                'obtainError'
            ]),
        },
        created() {
            this.userId = this.$route.params.id;
            this.initialize();
        },
        methods: {
            ...mapActions('user', [
                'getUserProfile'
            ]),

            async initialize() {
                let userOk = await this.getUserProfile(this.userId);
                if(!userOk) {
                    if(parseInt(this.obtainErrorCode) === 404) {
                        await router.push({path: "/404"});
                    } else {
                        this.message = this.obtainError;
                    }
                }
            }
        }
    }
</script>

<style scoped>

</style>