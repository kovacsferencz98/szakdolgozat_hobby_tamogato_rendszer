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
                <material-card
                        color="#346C47"
                        :title="$t('manageLocations.manageLocationsLabel')"
                        :text="$t('manageLocations.manageLocationsText')"

                >
                    <v-alert
                            v-if="deleteMessage"
                            prominent
                            type="error"
                    >
                        {{deleteMessage}}
                    </v-alert>
                    <v-container fill-width>
                        <v-data-table
                                :headers="headers"
                                :items="allLocations"
                                sort-by="id"
                                :search="search"
                                class="elevation-1"
                                hide-default-footer
                                :page.sync="page"
                                :items-per-page="itemsPerPage"
                                @page-count="pageCount = $event"
                        >
                            <template v-slot:top>
                                <v-toolbar flat >
                                    <v-text-field
                                            v-model="search"
                                            append-icon="mdi-magnify"
                                            :label="$t('searchLabel')"
                                            single-line
                                            hide-details
                                    />
                                    <v-spacer/>
                                    <v-dialog v-model="dialog" max-width="50%">
                                        <template v-slot:activator="{ on }">
                                            <v-btn color="primary" dark class="mb-2" v-on="on">{{$t('newItemLabel')}}</v-btn>
                                        </template>
                                        <v-card>
                                            <v-card-title>
                                                <span class="headline">{{ formTitle }}</span>
                                            </v-card-title>

                                            <v-card-text>
                                                <v-form id="editForm"  @submit.prevent="save">
                                                    <v-container py-0>
                                                        <v-layout wrap>
                                                            <v-flex
                                                                    xs12
                                                                    md12
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageLocations.streetLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.street"
                                                                        type="text"
                                                                        name="street"
                                                                        required
                                                                        :error-messages="streetErrors"
                                                                        @input="$v.editedItem.street.$touch()"
                                                                        @blur="$v.editedItem.street.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageLocations.numberLabel')"
                                                                        class="purple-input"
                                                                        type="number"
                                                                        v-model="editedItem.number"
                                                                        name="number"
                                                                        required
                                                                        :error-messages="numberErrors"
                                                                        @input="$v.editedItem.number.$touch()"
                                                                        @blur="$v.editedItem.number.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageLocations.apartmentLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.apartment"
                                                                        name="apartment"
                                                                        required
                                                                        :error-messages="apartmentErrors"
                                                                        @input="$v.editedItem.apartment.$touch()"
                                                                        @blur="$v.editedItem.apartment.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageLocations.zipCodeLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.zip"
                                                                        name="zip"
                                                                        required
                                                                        :error-messages="zipErrors"
                                                                        @input="$v.editedItem.zip.$touch()"
                                                                        @blur="$v.editedItem.zip.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4>
                                                                <v-text-field
                                                                        :label="$t('manageLocations.cityLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.city"
                                                                        name="city"
                                                                        required
                                                                        :error-messages="cityErrors"
                                                                        @input="$v.editedItem.city.$touch()"
                                                                        @blur="$v.editedItem.city.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4>
                                                                <v-text-field
                                                                        :label="$t('manageLocations.regionLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.region"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4>
                                                                <v-text-field
                                                                        class="purple-input"
                                                                        :label="$t('manageLocations.countryLabel')"
                                                                        v-model="editedItem.country"
                                                                        name="country"
                                                                        required
                                                                        :error-messages="countryErrors"
                                                                        @input="$v.editedItem.country.$touch()"
                                                                        @blur="$v.editedItem.country.$touch()"
                                                                />
                                                            </v-flex>
                                                        </v-layout>
                                                    </v-container>
                                                </v-form>
                                                <v-alert
                                                        v-if="message"
                                                        prominent
                                                        type="error"
                                                >
                                                    {{message}}
                                                </v-alert>
                                            </v-card-text>

                                            <v-card-actions>
                                                <v-spacer></v-spacer>
                                                <v-btn color="blue darken-1" text @click="close">{{$t('closeLabel')}}</v-btn>
                                                <v-btn color="blue darken-1" text @click="save">{{$t('saveLabel')}}</v-btn>
                                            </v-card-actions>
                                        </v-card>
                                    </v-dialog>
                                </v-toolbar>
                            </template>
                            <template v-slot:item.action="{ item }">
                                <v-icon
                                        small
                                        class="mr-4"
                                        @click="editItem(item)"
                                >
                                    mdi-pencil
                                </v-icon>
                                <v-icon
                                        small
                                        @click="deleteItem(item)"
                                >
                                    mdi-delete
                                </v-icon>

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
                                            :label="$t('rowsPerPageLabel')"
                                    />
                                </v-flex>
                            </v-layout>
                        </v-container>
                    </v-container>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {validationMixin} from "vuelidate";
    import { maxLength,  minValue, required} from "vuelidate/lib/validators";
    import {mapActions, mapGetters} from "vuex";
    import Location from "../models/location";
    import gmapsInit from "../plugins/gmaps";

    export default {
        mixins: [validationMixin],
        validations: {
            editedItem : {
                street: {required, maxLength: maxLength(150)},
                number: {required, minValue: minValue(0.00000001)},
                apartment: {required, maxLength: maxLength(15)},
                zip: {required, maxLength: maxLength(15)},
                city: {required, maxLength: maxLength(150)},
                country: {required, maxLength: maxLength(150)},
            }
        },

        name: "ManageLocations",
        data: vm => ({
            search: '',
            dialog: false,
            show: false,
            deleteMessage: '',
            message:'',
            google:null,
            geocoder:null,
            page: 1,
            pageCount: 0,
            itemsPerPage: 10,
            possibleItemsPerPage: [5, 10, 15, 20],
            headers: [
                {
                    text: 'ID',
                    align: 'left',
                    value: 'id',
                },
                { text: vm.$t('manageLocations.streetLabel'), value: 'street' },
                { text: vm.$t('manageLocations.numberLabel'), value: 'number' },
                { text: vm.$t('manageLocations.apartmentLabel'), value: 'apartment' },
                { text: vm.$t('manageLocations.zipCodeLabel'), value: 'zip' },
                { text: vm.$t('manageLocations.cityLabel'), value: 'city'},
                { text: vm.$t('manageLocations.regionLabel'), value: 'region' },
                { text: vm.$t('manageLocations.countryLabel'), value: 'country' },
                { text: vm.$t('manageLocations.latitudeLabel'), value: 'latitude' },
                { text: vm.$t('manageLocations.longitudeLabel'), value: 'longitude' },
                { text: vm.$t('actionsLabel'), value: 'action', sortable: false },
            ],
            editedIndex: -1,
            editedItem: new Location(),
            defaultItem: new Location(),
        }),
        computed: {
            ...mapGetters('location', [
                'allLocations',
                'locationObtainErrorCode',
                'locationObtainError'
            ]),

            formTitle () {
                return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
            },

            streetErrors () {
                const errors = [];
                if (!this.$v.editedItem.street.$dirty) return errors
                !this.$v.editedItem.street.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageLocations.streetLabel'), '150']));
                !this.$v.editedItem.street.required && errors.push(this.$t("requiredError", [this.$t('manageLocations.streetLabel')]));
                return errors
            },
            numberErrors () {
                const errors = [];
                if (!this.$v.editedItem.number.$dirty) return errors
                !this.$v.editedItem.number.minValue && errors.push(this.$t("minValueError", [this.$t('manageLocations.numberLabel'), '0']));
                !this.$v.editedItem.number.required && errors.push(this.$t("requiredError", [this.$t('manageLocations.numberLabel')]));
                return errors
            },
            apartmentErrors () {
                const errors = [];
                if (!this.$v.editedItem.apartment.$dirty) return errors
                !this.$v.editedItem.apartment.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageLocations.apartmentLabel'), '15']));
                !this.$v.editedItem.apartment.required && errors.push(this.$t("requiredError", [this.$t('manageLocations.apartmentLabel')]));
                return errors
            },
            zipErrors () {
                const errors = [];
                if (!this.$v.editedItem.zip.$dirty) return errors
                !this.$v.editedItem.zip.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageLocations.zipCodeLabel'), '15']));
                !this.$v.editedItem.zip.required && errors.push(this.$t("requiredError", [this.$t('manageLocations.zipCodeLabel')]));
                return errors
            },
            cityErrors () {
                const errors = [];
                if (!this.$v.editedItem.city.$dirty) return errors
                !this.$v.editedItem.city.maxLength &&  errors.push(this.$t("maxLengthError", [this.$t('manageLocations.cityLabel'), '150']));
                !this.$v.editedItem.city.required && errors.push(this.$t("requiredError", [this.$t('manageLocations.cityLabel')]));
                return errors
            },
            countryErrors () {
                const errors = [];
                if (!this.$v.editedItem.country.$dirty) return errors
                !this.$v.editedItem.country.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageLocations.countryLabel'), '150']));
                !this.$v.editedItem.country.required && errors.push(this.$t("requiredError", [this.$t('manageLocations.countryLabel')]));
                return errors
            },
        },
        watch: {
            dialog (val) {
                val || this.close()
            },
        },
        created () {
            this.initialize()
        },
        async mounted() {
            try{
                this.google = await gmapsInit();
                this.geocoder = new this.google.maps.Geocoder();
            }  catch (error) {
                // eslint-disable-next-line no-console
                console.error(error);
            }
        },
        methods: {
            ...mapActions('location', [
                'getLocations',
                'deleteLocation',
                'createLocation',
                'updateLocation'
            ]),

            initialize () {
                this.getLocations();
            },
            editItem (item) {
                console.log('Edit ' + this.allLocations.indexOf(item));
                this.editedIndex = this.allLocations.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.dialog = true
            },
            async deleteItem (item) {
                this.deleteMessage='';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await this.deleteLocation(item.id);
                    if(!requestOk) {
                        this.deleteMessage = this.locationObtainError;
                    }
                }
            },
            close () {
                this.dialog = false
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem);
                    this.editedIndex = -1;
                    this.message = '';
                    this.$v.$reset();
                }, 300)
            },
            createAddress() {
                let addressString = "";
                addressString = addressString + this.editedItem.country + ", ";
                if (this.editedItem.region) {
                    addressString = addressString + this.editedItem.region + ", ";
                }
                addressString = addressString + this.editedItem.city + ", " + this.editedItem.street + " " + this.editedItem.number + ", ap " + this.editedItem.apartment;
                return addressString;
            },
            save () {
                this.$v.$touch();
                this.message = '';
                if (this.$v.$invalid) {
                    this.message =  this.$t('fillOutMessage');
                } else {
                    let addressString = this.createAddress();
                    console.log("Geocode: " + addressString);
                    this.geocoder.geocode({address: addressString}, (results, status) => {
                        if (status !== 'OK' || !results[0]) {
                            throw new Error(status);
                        }
                        this.editedItem.latitude = results[0].geometry.location.lat();
                        this.editedItem.longitude = results[0].geometry.location.lng();
                        console.log(results[0].geometry.location.lat() + " " + results[0].geometry.location.lng());

                        if (this.editedIndex > -1) {
                            this.update();
                        } else {
                            this.createNew();
                        }
                    });
                }
            },
            async update() {
                let requestOk = await this.updateLocation(this.editedItem);
                if(!requestOk) {
                    this.message = this.locationObtainError;
                } else {
                    this.close()
                }
            },
            async createNew() {
                let requestOk = await this.createLocation(this.editedItem);
                if(!requestOk) {
                    this.message = this.locationObtainError;
                } else {
                    this.close()
                }
            },
        },
    }
</script>

<style scoped>

</style>