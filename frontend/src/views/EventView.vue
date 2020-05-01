<template>
    <v-container
            fill-height
            fluid
            grid-list-xl>
        <v-layout
                wrap
        >
            <v-flex
                    xs12
            >
                <material-card
                        color="#346C47"
                        :title="selectedEventDetail.eventDetails.name"
                        :text="selectedEventDetail.eventDetails.typeName"
                >
                    <v-container
                            fill-height
                            fluid
                            grid-list-xl>
                        <v-layout
                                wrap
                        >
                            <v-flex
                                    xs12
                            >
                                <v-alert
                                        v-if="message"
                                        prominent
                                        type="error"
                                >
                                    {{message}}
                                </v-alert>
                            </v-flex>
                            <v-flex xs12 lg4>
                                <v-container fill-width>
                                    <v-card>
                                        <v-card-title  mx-auto>{{ $t('eventView.detailLabel') }}</v-card-title>
                                        <v-card-text>
                                            <v-row class="mx-auto my-1">
                                                <v-chip
                                                        class="ma-2"
                                                        color="primary"
                                                        outlined
                                                        pill
                                                        :to="{name:'UserProfile', params:{id:selectedEventDetail.eventDetails.createdById}}"
                                                >
                                                    <v-icon left>mdi-account-outline</v-icon>
                                                    {{ $t('eventView.createdByLabel') }}: {{selectedEventDetail.eventDetails.createdByUsername}}
                                                </v-chip>
                                            </v-row>
                                            <v-row  class="mx-auto my-2"><v-icon class="mx-1">mdi-currency-eur</v-icon> {{selectedEventDetail.eventDetails.price}}</v-row>
                                            <v-row  class="mx-auto my-2"><v-icon class="mx-1">mdi-calendar-clock</v-icon> {{ $t('eventView.startLabel') }}: {{selectedEventDetail.eventDetails.startsAt}}</v-row>
                                            <v-row  class="mx-auto my-2"><v-icon class="mx-1">mdi-account-group</v-icon>  {{ $t('eventView.attendanceLabel') }}: {{selectedEventDetail.eventDetails.currentAttendance}} ({{selectedEventDetail.eventDetails.minAttendance}} - {{selectedEventDetail.eventDetails.maxAttendance}})</v-row>
                                            <v-row  class="mx-auto my-2"><v-icon class="mx-1">mdi-information</v-icon>  {{ $t('eventView.descriptionLabel') }}: {{selectedEventDetail.eventDetails.description}}</v-row>
                                        </v-card-text>
                                        <v-card-actions class="pa-4">
                                            <v-row
                                                    v-if="selectedEventDetail.over && selectedEventDetail.participant"
                                            >
                                                {{ $t('eventView.rateLabel') }}
                                                <v-spacer></v-spacer>
                                                <span class="grey--text text--lighten-2 caption mr-2">
                                                    ({{ rating }})
                                                </span>
                                                <v-rating
                                                        v-model="selectedEventDetail.ratingOfEventByUser"
                                                        dense
                                                        length="5"
                                                        color="orange"
                                                        background-color="orange lighten-3"
                                                        @input="rateEvent()"
                                                />
                                            </v-row>
                                            <v-row
                                                    v-if="!selectedEventDetail.over && !selectedEventDetail.owner"
                                            >
                                                <v-btn
                                                        v-if="!selectedEventDetail.participant"
                                                        rounded color="primary"
                                                        dark
                                                        @click="handleJoinEvent"
                                                >
                                                    <v-icon left>mdi-plus</v-icon>  {{ $t('eventView.joinLabel') }}
                                                </v-btn>
                                                <v-btn
                                                        v-if="selectedEventDetail.participant"
                                                        rounded color="primary"
                                                        dark
                                                        @click="handleLeaveEvent"
                                                >
                                                    <v-icon left>mdi-minus</v-icon> {{ $t('eventView.leaveLabel') }}
                                                </v-btn>
                                            </v-row>
                                        </v-card-actions>
                                    </v-card>
                                </v-container>
                            </v-flex>
                            <v-flex xs12 lg8>
                                <v-container fill-width>
                                    <v-data-table
                                            :headers="headers"
                                            :items="selectedEventDetail.participants"
                                            sort-by="userId"
                                            :search="search"
                                            class="elevation-1"
                                            hide-default-footer
                                            :page.sync="page"
                                            :items-per-page="itemsPerPage"
                                            @page-count="pageCount = $event"
                                    >
                                        <template v-slot:top>
                                            <v-toolbar flat >
                                                <v-toolbar-title>{{ $t('eventView.participantsLabel') }}</v-toolbar-title>
                                                <v-spacer/>
                                                <v-text-field
                                                        v-model="search"
                                                        append-icon="mdi-magnify"
                                                        label="Search"
                                                        single-line
                                                        hide-details
                                                />
                                                <v-spacer/>
                                                <v-btn class="ma-2" outlined large fab  color="#346C47"
                                                       :to="{name:'Chat', params:{id:eventId}}"
                                                >
                                                    <v-icon  color="#346C47" >mdi-chat</v-icon>
                                                </v-btn>
                                            </v-toolbar>
                                        </template>
                                        <template v-slot:item.action="{ item }">
                                            <v-icon
                                                    v-if="!item.approved && selectedEventDetail.owner"
                                                    small
                                                    class="mr-4"
                                                    @click="approveItem(item)"
                                            >
                                                mdi-check-bold
                                            </v-icon>
                                            <v-icon
                                                    small
                                                    v-if="selectedEventDetail.owner"
                                                    @click="deleteItem(item)"
                                            >
                                                mdi-delete
                                            </v-icon>
                                            <div v-if="!selectedEventDetail.owner"
                                                 class = "font-weight-bold text-center body-1"
                                            >
                                                -
                                            </div>

                                        </template>
                                        <template v-slot:item.ratingOfParticipant="{ item }">
                                            <v-rating
                                                    v-model="item.ratingOfParticipant"
                                                    :length="5"
                                                    color="orange"
                                                    :readonly="!selectedEventDetail.owner || !selectedEventDetail.over"
                                                    :dense="true"
                                                    background-color="orange lighten-3"
                                                    @input="rateItem(item)"
                                            />
                                        </template>
                                        <template v-slot:item.ratingOfEvent="{ item }">
                                            <v-rating
                                                    v-model="item.ratingOfEvent"
                                                    :length="5"
                                                    color="orange"
                                                    :readonly="true"
                                                    :dense="true"
                                                    background-color="orange lighten-3"
                                            />
                                        </template>
                                        <template v-slot:item.userUsername="{ item }">
                                            <v-btn
                                                    color="primary"
                                                    text
                                                    :to="{name:'UserProfile', params:{id:item.userId}}"
                                            >
                                                {{item.userUsername}}
                                            </v-btn>
                                        </template>
                                        <template v-slot:no-data>
                                            <v-btn color="primary" @click="initialize">{{ $t('tryAgainLabel') }}</v-btn>
                                        </template>
                                    </v-data-table>
                                    <v-container py-0>
                                        <v-layout wrap>
                                            <v-flex
                                                    xs12
                                                    md9
                                            >
                                                <v-pagination
                                                        v-model="page"
                                                        :length="pageCount"
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
                                                        v-model="itemsPerPage"
                                                        :items="possibleItemsPerPage"
                                                        :label="$t('rowsPerPageLabel') "
                                                />
                                            </v-flex>
                                        </v-layout>
                                    </v-container>
                                </v-container>
                            </v-flex>
                            <v-flex xs12>
                                <div
                                        class="google-map"
                                        ref="googleMap"
                                        id="googleMap"
                                        style="width:100%; height:400px"
                                >
                                </div>
                            </v-flex>
                        </v-layout>
                    </v-container>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {mapActions, mapGetters} from "vuex";
    import gmapsInit from "../plugins/gmaps";

    export default {
        name: "EventView",

        data: vm => ({
            eventId: null,
            location: null,
            google:null,
            geocoder:null,
            map:null,
            message:'',
            search:'',
            page: 1,
            pageCount: 0,
            itemsPerPage: 5,
            possibleItemsPerPage: [5, 10],
            headers: [
                { text: vm.$t('eventView.userLabel'), value: 'userUsername' },
                { text:  vm.$t('eventView.joinedLabel'), value: 'joinedAt' },
                { text:  vm.$t('eventView.partRatingLabel'), value: 'ratingOfParticipant',  sortable: false },
                { text: vm.$t('eventView.eventRatingLabel'), value: 'ratingOfEvent',  sortable: false },
                { text:  vm.$t('eventView.approvedLabel'), value: 'approved',  sortable: false },
                { text:  vm.$t('actionsLabel'), value: 'action', sortable: false },
            ],
        }),
        computed: {
            ...mapGetters('event', [
                'selectedEventDetail',
                'obtainEventError',
                'obtainEventErrorCode'
            ]),

            ...mapGetters('eventParticipant', [
                'allEventParticipants'
            ]),

            ...mapGetters('location', [
                'selectedLocation'
            ]),

            ...mapGetters('eventType', [
                'selectedEventType'
            ]),
        },
        async mounted() {
            await this.initialize();

            this.$nextTick(() =>{
                this.initMap();
            });
        },
        async created() {

            this.eventId = this.$route.params.id;
        },
        methods: {
            ...mapActions('event', [
                'getEventDetail',
                'rateEvent',
                'rateEventParticipant',
                'joinEvent',
                'approveEventParticipant',
                'deleteEventParticipant'
            ]),

            ...mapActions('event', [
                'getUsers'
            ]),

            ...mapActions('location', [
                'getLocations'
            ]),

            async initialize () {
                await this.getEventDetail({id:this.eventId});
            },

            async approveItem (item) {
                this.message='';
                console.log("Aprove participant: " + item.userUsername);
                let requestOk = this.approveEventParticipant(item.id);
                if(!requestOk) {
                    this.message = this.obtainEventError;
                }
            },
            async deleteItem (item) {
                this.message = '';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await this.deleteEventParticipant(item.id);
                    if(!requestOk) {
                        this.message = this.obtainEventError;
                    }
                }
            },
            async rateItem(item) {
                this.message = '';
                console.log("Rate participant: " + item.userUsername);
                let requestOk = await this.rateEventParticipant(item.id, item.ratingOfParticipant);
                if(!requestOk) {
                    this.message = this.obtainEventError;
                }
            },
            async rateEvent() {
                this.message = '';
                console.log("Rate event: " + this.selectedEventDetail.ratingOfEventByUser);
                let requestOk = await this.rateEvent(this.selectedEventDetail.eventDetails.id, this.selectedEventDetail.ratingOfEventByUser);
                if(!requestOk) {
                    this.message = this.obtainEventError;
                }
            },
            async handleJoinEvent() {
                this.message = '';
                console.log("Join event: " + this.selectedEventDetail.eventDetails.id);
                let requestOk = await this.joinEvent(this.selectedEventDetail.eventDetails.id);
                if(!requestOk) {
                    this.message = this.obtainEventError;
                }
            },
            async handleLeaveEvent() {
                this.message = '';
                console.log("Leave event: " + this.selectedEventDetail.eventDetails.id);
                let requestOk = await this.leaveEvent(this.selectedEventDetail.eventDetails.id);
                if(!requestOk) {
                    this.message = this.obtainEventError;
                }
            },
            navigateToUserProfile(userId) {
                console.log("Navigate to profile of user: " + userId);
            },
            async initMap() {
                try {
                    this.google = await gmapsInit();
                    this.geocoder = new this.google.maps.Geocoder();

                    console.log("Google maps: ");
                    const mapContainer = this.$refs.googleMap;
                    console.log(this.$refs.mapContainer);
                    this.map = new this.google.maps.Map(mapContainer, {
                        "zoom": 18
                    });

                    const location = {
                        lat: this.selectedEventDetail.eventLocation.latitude,
                        lng: this.selectedEventDetail.eventLocation.longitude
                    };
                    this.map.setCenter(location);

                    // eslint-disable-next-line no-unused-vars
                    const marker = new this.google.maps.Marker(
                        {
                            position: location,
                            map: this.map,
                            title: this.selectedEventDetail.eventDetails.name
                        });
                } catch (error) {
                    // eslint-disable-next-line no-console
                    console.error(error);
                }
            }
        }
    }

</script>

<style scoped>

</style>