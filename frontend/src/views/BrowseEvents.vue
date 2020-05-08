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
                        :title="$t('browseEvents.browseEventsLabel')"
                        :text="$t('browseEvents.browseEventsText')"
                >
                    <v-container
                            fill-height
                            fluid
                            grid-list-xl>
                        <v-layout
                                wrap
                        >
                            <v-flex xs12 >
                                <v-container fill-width>
                                    <v-card>
                                        <v-card-title  mx-auto>{{$t('browseEvents.filterLabel')}}</v-card-title>
                                        <v-card-text>
                                            <v-container
                                                    fill-height
                                                    fluid
                                                    grid-list-xl>
                                                <v-layout
                                                        wrap
                                                >
                                                    <v-flex
                                                            xs12
                                                            md6
                                                            pa-md-4 mx-lg-auto
                                                    >
                                                        <v-select class="purple-input"
                                                                  :label="$t('browseEvents.typeLabel')"
                                                                  v-model="selectedTypes"
                                                                  :items="eventTypeList"
                                                                  :menu-props="{ maxHeight: '400' }"
                                                                  chips
                                                                  multiple
                                                        />
                                                    </v-flex>
                                                    <v-flex
                                                            xs12
                                                            md6
                                                            pa-md-4 mx-lg-auto
                                                    >
                                                        <v-select class="purple-input"
                                                                  :label="$t('browseEvents.distanceLabel')"
                                                                  v-model="distance"
                                                                  :items="possibleDistance"
                                                                  item-text="`${data.item.name}`"
                                                                  item-value="`${data.item.value}`"
                                                                  :menu-props="{ maxHeight: '400' }"
                                                                  chips
                                                        >
                                                            <template slot="selection" slot-scope="data">
                                                                {{ data.item.name}}
                                                            </template>
                                                            <template slot="item" slot-scope="data">
                                                                <v-list-item-content>
                                                                    <v-list-item-title v-html="`${data.item.name}`">
                                                                    </v-list-item-title>
                                                                </v-list-item-content>
                                                            </template>
                                                        </v-select>
                                                    </v-flex>
                                                    <v-flex
                                                            xs6
                                                            md3
                                                            pa-md-4 mx-lg-auto
                                                    >
                                                        <v-text-field
                                                                :label="$t('browseEvents.minPriceLabel')"
                                                                class="purple-input"
                                                                v-model="minPrice"
                                                                name="minPrice"
                                                        />
                                                    </v-flex>
                                                    <v-flex
                                                            xs6
                                                            md3
                                                            pa-md-4 mx-lg-auto
                                                    >
                                                        <v-text-field
                                                                :label="$t('browseEvents.maxPriceLabel')"
                                                                class="purple-input"
                                                                v-model="maxPrice"
                                                                name="maxPrice"
                                                        />
                                                    </v-flex>
                                                    <v-flex
                                                            xs6
                                                            md3
                                                            pa-md-4 mx-lg-auto
                                                    >
                                                        <v-text-field
                                                                :label="$t('browseEvents.minParticipantLabel')"
                                                                class="purple-input"
                                                                v-model="minAttendance"
                                                                name="minAttendance"
                                                        />
                                                    </v-flex>
                                                    <v-flex
                                                            xs6
                                                            md3
                                                            pa-md-4 mx-lg-auto
                                                    >
                                                        <v-text-field
                                                                :label="$t('browseEvents.maxParticipantLabel')"
                                                                class="purple-input"
                                                                v-model="maxAttendance"
                                                                name="maxAttendance"
                                                        />
                                                    </v-flex>
                                                </v-layout>
                                            </v-container>
                                        </v-card-text>
                                    </v-card>
                                </v-container>
                            </v-flex>
                            <v-flex xs12>
                                <v-alert v-if="!eventsNotEmpty" color="#346C47" class="text--white text-center" dark>{{$t('browseEvents.noEvents')}}</v-alert>
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
                            <v-flex xs12>
                                <div class="text-center">
                                    <v-btn
                                            color="#346C47"
                                            class="ma-2 white--text"
                                            @click="refreshEvents"
                                    >
                                        {{$t('browseEvents.reload')}}
                                    </v-btn>
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
    import MarkerClusterer from '@google/markerclusterer';

    export default {
        name: "BrowseEvents",
        data: () => ({
            search: '',
            google:null,
            geocoder:null,
            map:null,
            selectedTypes: [],
            minPrice: null,
            maxPrice: null,
            minAttendance: null,
            maxAttendance: null,
            distance: null,
            userLocation: null,
            eventLocationMap: new Map(),
            markers: [],
            possibleDistance: [
                {name:'5 km', value: 5},
                {name:'10 km', value: 10},
                {name:'15 km', value: 15},
                {name:'20 km', value: 20},
                {name:'Any', value: null},
            ]
        }),
        watch: {
            // eslint-disable-next-line no-unused-vars
            selectedTypes: function (val) {
                this.updateMarkers();
            },
            // eslint-disable-next-line no-unused-vars
            minPrice:  function (val) {
                this.updateMarkers();
            },
            // eslint-disable-next-line no-unused-vars
            maxPrice:  function (val) {
                this.updateMarkers();
            },
            // eslint-disable-next-line no-unused-vars
            minAttendance:  function (val) {
                this.updateMarkers();
            },
            // eslint-disable-next-line no-unused-vars
            maxAttendance:  function (val) {
                this.updateMarkers();
            },
            // eslint-disable-next-line no-unused-vars
            distance: function (val) {
                this.updateMarkers();
            }
        },
        computed: {
            ...mapGetters('event', [
                'activeEvents',
                'obtainEventErrorCode',
                'obtainEventError'
            ]),

            ...mapGetters('location', [
                'allLocations',
            ]),

            ...mapGetters('eventType', [
                'allEventTypes'
            ]),

            ...mapGetters('auth', [
                'currentAccount'
            ]),

            eventsNotEmpty: function () {
                return this.activeEvents && Array.isArray(this.activeEvents) && this.activeEvents.length;
            },

            eventTypeList() {
                return this.allEventTypes.map(type => type.name);
            },

            filteredEvents() {
                var events = this.activeEvents;
                events = this.filterType(events);
                events = this.filterPrice(events);
                events = this.filterAttendance(events);
                events = this.filterDistance(events);
                return events;
            },
        },
        async mounted() {
            try{
                await this.initialize();

                this.google = await gmapsInit();
                this.geocoder = new this.google.maps.Geocoder();
                const mapContainer = this.$refs.googleMap;
                this.map = new this.google.maps.Map(mapContainer, {
                    "zoom": 18
                });

                await navigator.geolocation.getCurrentPosition(
                    position => {
                        this.userLocation = {lat: position.coords.latitude, lng: position.coords.longitude}
                        console.log("Current location: " + this.userLocation);
                        this.map.setCenter(this.userLocation);
                    },
                    error => {
                        console.log(error.message);
                        this.userLocation = {lat: this.currentAccount.latitude, lng: this.currentAccount.longitude};
                        this.map.setCenter(this.userLocation);
                    }
                );

                new MarkerClusterer(this.map, this.markers, {
                    imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m',
                });

                if(this.eventsNotEmpty) {
                    this.mapEventsToLocations();
                    this.filteredEvents.forEach(this.addMarker);
                }
            }  catch (error) {
                // eslint-disable-next-line no-console
                console.error(error);
            }
        },
        created () {
            if (this.$route.params.type){
                this.selectedTypes.push(this.$route.params.type);
            }

            const vm = this;
            window.navigateToEvent = function(id) {
                vm.$router.push({name:'EventView', params:{id:id}})
            };
        },
        methods: {
            ...mapActions('event', [
                'getActiveEvents',
            ]),

            ...mapActions('location', [
                'getLocations',
                'getLocation'
            ]),

            ...mapActions('eventType', [
                'getEventTypes'
            ]),

            async initialize() {
                await this.getActiveEvents();

                if(!this.allLocations || !this.allLocations.length) {
                    await this.getLocations();
                }

                if(!this.allEventTypes || !this.allEventTypes.length) {
                    await this.getEventTypes();
                }
            },

            filterType(events) {
                if(!!this.selectedTypes && this.selectedTypes.length) {
                    return events.filter(event => this.selectedTypes.includes(event.typeName));
                } else {
                    return events;
                }
            },
            filterPrice(events) {
                let result = events;
                if(this.minPrice) {
                    result = result.filter(event => event.price > this.minPrice)
                }
                if(this.maxPrice) {
                    result = result.filter(event => event.price < this.maxPrice)
                }
                return result;
            },
            filterAttendance(events) {
                let result = events;
                if(this.minAttendance) {
                    result = result.filter(event => event.currentAttendance >= this.minAttendance)
                }
                if(this.maxAttendance) {
                    result = result.filter(event => event.currentAttendance <= this.maxAttendance)
                }
                return result;
            },
            filterDistance(events) {
                let result = events;
                if(this.distance && this.distance.value) {
                    result = result.filter(this.checkEventDistance)
                }
                return result;
            },
            checkEventDistance(event) {
                var eventLocation =this.findLocationForEvent(event);
                var calculatedDistance = this.getDistanceFromLatLonInKm(eventLocation.latitude, eventLocation.longitude, this.userLocation.lat, this.userLocation.lng);
                return calculatedDistance <= this.distance.value;
            },

            getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
                var R = 6371; // Radius of the earth in km
                var dLat = this.deg2rad(lat2-lat1);  // deg2rad below
                var dLon = this.deg2rad(lon2-lon1);
                var a =
                    Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
                    Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
                var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                var d = R * c; // Distance in km
                return d;
            },

            deg2rad(deg) {
                return deg * (Math.PI/180)
            },

            findLocationForEvent(event) {
                for(let idx in this.allLocations) {
                    if(event.locationId === this.allLocations[idx].id) {
                        return this.allLocations[idx];
                    }
                }
            },

            mapEventsToLocations() {
                this.eventLocationMap.clear();
                for(let idx in this.activeEvents) {
                    var eventLocation = this.findLocationForEvent(this.activeEvents[idx]);
                    var latLng = {lat: eventLocation.latitude, lng: eventLocation.longitude};
                    this.eventLocationMap.set(this.activeEvents[idx].id, latLng);
                }
            },

            addMarker(event) {
                var latLng =  this.eventLocationMap.get(event.id);
                var map = this.map;

                var div = document.createElement('DIV');
                div.innerHTML='<table class="InfoWindow_TableOuter">' +
                    '<tr> <td><span class="InfoWindowTitle" >'+event.name+'</span> </td></tr>' +
                    '<tr> <td><span class="InfoWindowHead" >'+this.$t('browseEvents.typeLabel')+': </span> </td></tr>' +
                    '<tr><td><span class="InfoWindowText">'+event.typeName+'</span> </td> </tr>' +
                    ' <tr><td><span class="InfoWindowHead" >'+this.$t('browseEvents.priceLabel')+': </span> </td></tr>' +
                    '<tr> <td><span class="InfoWindowText">'+event.price+'</span> </td></tr>' +
                    '<tr><td><span class="InfoWindowHead" >'+this.$t('browseEvents.startLabel')+': </span> </td></tr>' +
                    '<tr><td><span class="InfoWindowText">'+event.startsAt+'</span> </td></tr> ' +
                    '<tr><td><span class="InfoWindowHead" >'+this.$t('browseEvents.attendanceLabel')+': </span> </td></tr>' +
                    '<tr><td><span class="InfoWindowText">'+event.currentAttendance+'</span> </td></tr> ' +
                    '<tr> <td><span class="InfoWindowHead" ><a  onclick="navigateToEvent('+event.id+');">'+this.$t('browseEvents.buttonLabel')+'</a></span> </td></tr>' +
                    '</table>';

                var infowindow = new this.google.maps.InfoWindow({
                    content: div
                });


                const marker = new this.google.maps.Marker({
                    position: latLng,
                    map: map,
                    title: event.name
                });
                marker.addListener('click', function() {
                    infowindow.open(map, marker);
                });
                this.markers.push(marker);
            },
            updateMarkers() {
                this.markers.forEach(marker => marker.setMap(null));
                this.markers = [];
                this.filteredEvents.forEach(this.addMarker);
            },
            navigateToEvent(id) {
                this.$router.push({name:'EventView', params:{id:id}})
            },
            async refreshEvents() {
                await this.initialize();

                if(this.eventsNotEmpty) {
                    this.mapEventsToLocations();
                    this.updateMarkers();
                }
            }
        },
    }

</script>

<style scoped>


</style>