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
                        :title="$t('manageUserDetails.manageUserDetailsLabel')"
                        :text="$t('manageUserDetails.manageUserDetailsText')"

                >
                    <v-container fill-width>
                        <v-alert
                                v-if="deleteMessage"
                                prominent
                                type="error"
                        >
                            {{deleteMessage}}
                        </v-alert>
                        <v-data-table
                                :headers="headers"
                                :items="allUserDetails"
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
                                                <v-form id="editForm"  @submit.prevent="uploadPicture">
                                                    <v-container py-0>
                                                        <v-layout wrap>
                                                            <v-flex
                                                                    xs12
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-select class="purple-input"
                                                                          :label="$t('manageUserDetails.usernameLabel')"
                                                                          v-model="editedItem.userUsername"
                                                                          :items="freeUserUsernames"
                                                                          chips
                                                                          required
                                                                          :error-messages="userUsernameErrors"
                                                                          @change="$v.editedItem.userUsername.$touch()"
                                                                          @blur="$v.editedItem.userUsername.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-textarea
                                                                        class="purple-input"
                                                                        :label="$t('manageUserDetails.descriptionLabel')"
                                                                        v-model="editedItem.description"
                                                                        name="description"
                                                                        required
                                                                        :error-messages="descriptionErrors"
                                                                        @input="$v.editedItem.description.$touch()"
                                                                        @blur="$v.editedItem.description.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md12
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEvents.streetLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedLocation.street"
                                                                        type="text"
                                                                        name="street"
                                                                        required
                                                                        :error-messages="streetErrors"
                                                                        @input="$v.editedLocation.street.$touch()"
                                                                        @blur="$v.editedLocation.street.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEvents.numberLabel')"
                                                                        class="purple-input"
                                                                        type="number"
                                                                        v-model="editedLocation.number"
                                                                        name="number"
                                                                        required
                                                                        :error-messages="numberErrors"
                                                                        @input="$v.editedLocation.number.$touch()"
                                                                        @blur="$v.editedLocation.number.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEvents.apartmentLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedLocation.apartment"
                                                                        name="apartment"
                                                                        required
                                                                        :error-messages="apartmentErrors"
                                                                        @input="$v.editedLocation.apartment.$touch()"
                                                                        @blur="$v.editedLocation.apartment.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEvents.zipCodeLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedLocation.zip"
                                                                        name="zip"
                                                                        required
                                                                        :error-messages="zipErrors"
                                                                        @input="$v.editedLocation.zip.$touch()"
                                                                        @blur="$v.editedLocation.zip.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEvents.cityLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedLocation.city"
                                                                        name="city"
                                                                        required
                                                                        :error-messages="cityErrors"
                                                                        @input="$v.editedLocation.city.$touch()"
                                                                        @blur="$v.editedLocation.city.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEvents.regionLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedLocation.region"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md4
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        class="purple-input"
                                                                        :label="$t('manageEvents.countryLabel')"
                                                                        v-model="editedLocation.country"
                                                                        name="country"
                                                                        required
                                                                        :error-messages="countryErrors"
                                                                        @input="$v.editedLocation.country.$touch()"
                                                                        @blur="$v.editedLocation.country.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-file-input
                                                                        :rules="rules"
                                                                        accept="image/png, image/jpeg, image/bmp"
                                                                        :placeholder="$t('register.avatarText')"
                                                                        prepend-icon="mdi-camera"
                                                                        :label="$t('register.avatarLabel')"
                                                                        id="file"
                                                                        ref="file"
                                                                        @change="handleFileUpload"
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
                                                <v-btn color="blue darken-1" text @click="close">{{$t('cancelLabel')}}</v-btn>
                                                <v-btn color="blue darken-1" text @click="uploadPicture">{{$t('saveLabel')}}</v-btn>
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
    import User from "../models/user";
    import {validationMixin} from "vuelidate";
    import {required, maxLength, minValue} from "vuelidate/lib/validators";
    import {mapActions, mapGetters} from "vuex";
    import UserDetails from "../models/user-details";
    import Location from "../models/location";
    import gmapsInit from "../plugins/gmaps";
    import PictureService from "../services/picture.service";

    export default {
        mixins: [validationMixin],
        validations: {
            editedItem : {
                description: {required},
                userUsername: {required}
            },
            editedLocation : {
                street: {required, maxLength: maxLength(150)},
                number: {required, minValue: minValue(0.00000001)},
                apartment: {required, maxLength: maxLength(15)},
                zip: {required, maxLength: maxLength(15)},
                city: {required, maxLength: maxLength(150)},
                country: {required, maxLength: maxLength(150)},
            }
        },

        name: "ManageUserDetails",
        data: vm => ({
            search: '',
            dialog: false,
            deleteMessage:'',
            message:'',
            file:null,
            page: 1,
            pageCount: 0,
            itemsPerPage: 10,
            google:null,
            geocoder:null,

            possibleItemsPerPage: [5, 10, 15, 20],
            headers: [
                {
                    text: 'ID',
                    align: 'left',
                    value: 'id',
                },
                { text: vm.$t('manageUserDetails.descriptionLabel'), value: 'description', sortable: false},
                { text: vm.$t('manageUserDetails.avatarLabel'), value: 'profilePicUrl', sortable: false},
                { text: vm.$t('manageUserDetails.usernameLabel'), value: 'userUsername'},
                { text: vm.$t('manageUserDetails.residenceLabel'), value: 'residenceId' },
                { text: vm.$t('actionsLabel'), value: 'action', sortable: false },
            ],
            editedIndex: -1,
            freeUserUsernames: [],
            editedItem: new UserDetails(),
            defaultItem: new UserDetails(),
            editedLocation: new Location(),
            defaultLocation: new Location(),
            rules: [
                value => !value || value.size < 3000000 || this.$t("register.avatarSize"),
            ]
        }),
        computed: {
            ...mapGetters('userDetails', [
                'allUserDetails',
                'obtainErrorCode',
                'obtainError'
            ]),

            ...mapGetters('user', [
                'allUsers'
            ]),

            ...mapGetters('location', [
                'allLocations',
                'selectedLocation',
                'locationObtainErrorCode',
                'locationObtainError'
            ]),

            freeUsers() {
                var freeUsers = [];
                for(let idx in this.allUsers) {
                    if(!this.isUserInUserDetails(this.allUsers[idx].username))
                        freeUsers.push(Object.assign(new User(), this.allUsers[idx]));
                }
                console.log(freeUsers);
                return freeUsers;

            },

            locationList() {
                return this.allLocations.map(location => location.id + ': ' + location.longitude + '; ' +location.latitude)
            },

            formTitle () {
                return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
            },

            descriptionErrors () {
                const errors = [];
                if (!this.$v.editedItem.description.$dirty) return errors
                !this.$v.editedItem.description.required &&  errors.push(this.$t("requiredError", [this.$t('manageUserDetails.descriptionLabel')]));
                return errors
            },
            profilePicUrlErrors () {
                const errors = [];
                if (!this.$v.editedItem.profilePicUrl.$dirty) return errors;
                !this.$v.editedItem.profilePicUrl.required && errors.push(this.$t("requiredError", [this.$t('manageUserDetails.avatarLabel')]));
                return errors;
            },
            userUsernameErrors() {
                const errors = [];
                if (!this.$v.editedItem.userUsername.$dirty) return errors;
                !this.$v.editedItem.userUsername.required && errors.push(this.$t("requiredError", [this.$t('manageUserDetails.usernameLabel')]));
                return errors;
            },
            streetErrors () {
                const errors = [];
                if (!this.$v.editedLocation.street.$dirty) return errors
                !this.$v.editedLocation.street.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.streetLabel'), '150']));
                !this.$v.editedLocation.street.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.streetLabel')]));
                return errors
            },
            numberErrors () {
                const errors = [];
                if (!this.$v.editedLocation.number.$dirty) return errors
                !this.$v.editedLocation.number.minValue && errors.push(this.$t("minValueError", [this.$t('manageEvents.numberLabel'), '0']));
                !this.$v.editedLocation.number.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.numberLabel')]));
                return errors
            },
            apartmentErrors () {
                const errors = [];
                if (!this.$v.editedLocation.apartment.$dirty) return errors
                !this.$v.editedLocation.apartment.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.apartmentLabel'), '15']));
                !this.$v.editedLocation.apartment.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.apartmentLabel')]));
                return errors
            },
            zipErrors () {
                const errors = [];
                if (!this.$v.editedLocation.zip.$dirty) return errors
                !this.$v.editedLocation.zip.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.zipCodeLabel'), '15']));
                !this.$v.editedLocation.zip.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.zipCodeLabel')]));
                return errors
            },
            cityErrors () {
                const errors = [];
                if (!this.$v.editedLocation.city.$dirty) return errors
                !this.$v.editedLocation.city.maxLength &&  errors.push(this.$t("maxLengthError", [this.$t('manageEvents.cityLabel'), '150']));
                !this.$v.editedLocation.city.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.cityLabel')]));
                return errors
            },
            countryErrors () {
                const errors = [];
                if (!this.$v.editedLocation.country.$dirty) return errors
                !this.$v.editedLocation.country.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.countryLabel'), '150']));
                !this.$v.editedLocation.country.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.countryLabel')]));
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
            ...mapActions('userDetails', [
                'getUserDetails',
                'deleteUserDetail',
                'createUserDetail',
                'updateUserDetail'
            ]),

            ...mapActions('user', [
                'getUsers'
            ]),

            ...mapActions('location', [
                'getLocations',
                'deleteLocation',
                'createLocation',
                'updateLocation',
                'getLocation'
            ]),

            initialize () {
                this.getUserDetails();

                if(!this.allUsers || !this.allUsers.length) {
                    this.getUsers();
                }

                if(!this.allLocations || !this.allLocations.length) {
                    this.getLocations();
                }
            },
            findFreeUserUsernames() {
                return this.freeUsers.map(user => user.username);
            },
            async editItem (item) {
                console.log('Edit ' + this.allUserDetails.indexOf(item));
                this.editedIndex = this.allUserDetails.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.freeUserUsernames = this.findFreeUserUsernames();
                if(this.editedIndex > -1) {
                    this.freeUserUsernames.push(this.editedItem.userUsername);
                }
                await this.getLocation(this.editedItem.residenceId);
                this.editedLocation = Object.assign({}, this.selectedLocation);
                this.dialog = true
            },
            async deleteItem (item) {
                this.deleteMessage='';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await  this.deleteUserDetail(item.id);
                    if(!requestOk) {
                        this.deleteMessage = this.obtainError;
                    }
                }
            },
            close () {
                this.dialog = false;
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem);
                    this.editedLocation = Object.assign({}, this.defaultLocation);
                    this.editedIndex = -1;
                    this.message = '';
                    this.file=null;
                    this.$v.$reset();
                }, 300)
            },
            createAddress() {
                let addressString = "";
                addressString = addressString + this.editedLocation.country + ", ";
                if (this.editedLocation.region) {
                    addressString = addressString + this.editedLocation.region + ", ";
                }
                addressString = addressString + this.editedLocation.city + ", " + this.editedLocation.street + " " + this.editedLocation.number + ", ap " + this.editedLocation.apartment;
                return addressString;
            },
            save () {
                this.$v.$touch();
                this.message = '';
                this.deleteMessage = '';
                if (this.$v.$invalid) {
                    this.message = this.$t('fillOutMessage');
                } else {
                    let addressString = this.createAddress();
                    console.log("Geocode: " + addressString);
                    this.geocoder.geocode({address: addressString}, (results, status) => {
                        if (status !== 'OK' || !results[0]) {
                            throw new Error(status);
                        }
                        this.editedLocation.latitude = results[0].geometry.location.lat();
                        this.editedLocation.longitude = results[0].geometry.location.lng();
                        console.log(results[0].geometry.location.lat() + " " + results[0].geometry.location.lng());
                        this.editedItem.userId = this.userIdOfUsername(this.editedItem.userUsername);
                        if (this.editedIndex > -1) {
                            this.update();
                        } else {
                            this.createNew();
                        }
                    });
                }
            },
            async update() {
                let locationOk = await this.updateLocation(this.editedLocation);
                let userDetailOk = false;
                if(locationOk) {
                    userDetailOk = await this.updateUserDetail(this.editedItem);
                }
                if(!locationOk) {
                    this.message = this.locationObtainError;
                } else if (!userDetailOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            async createNew() {
                let userDetailOk = await this.createUserDetail({userDetail : this.editedItem, location: this.editedLocation});
                if(!userDetailOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            isUserInUserDetails(username) {
                for(let idx in this.allUserDetails) {
                    if(username === this.allUserDetails[idx].userUsername)
                        return true;
                }
                return false;
            },
            userIdOfUsername(username) {
                for(let idx in this.allUsers) {
                    if(username === this.allUsers[idx].username)
                        return this.allUsers[idx].id;
                }
                return null;
            },
            uploadPicture() {
                console.log(this.file);
                if(this.file) {
                    this.$v.$touch();
                    console.log("Start upload file");
                    PictureService.savePicture(this.file).then(
                        response => {
                            console.log("Picture save success");
                            this.editedItem.profilePicId = response.id;
                            this.save();
                        },
                        error => {
                            this.message = this.$t("pictureUploadError");
                            console.log((error.response && error.response.data) ||
                                error.message ||
                                error.toString());
                        }
                    );
                } else {
                    console.log("No upload file");
                    this.save();
                }
            },
            handleFileUpload(file){
                console.log("File changed: " + file.name);
                if (!file.name)
                    return;
                this.file = file;
            },
            extractResidenceId(listItem) {
                var endIdx = listItem.indexOf(':');
                if(endIdx > -1) {
                    return listItem.substring(0, endIdx);
                }
                return ''
            }
        },
    }
</script>

<style scoped>

</style>