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
                    md8
            >
                <material-card
                        color="#346C47"
                        :title="$t('profile.profileLabel')"
                        :text="$t('profile.profileText')"

                >
                    <v-form id="regForm"  @submit.prevent="uploadPicture">
                        <v-container py-0>
                            <v-layout wrap>
                                <v-flex
                                        xs12
                                >
                                    <v-text-field
                                            :label="$t('register.emailLabel')"
                                            class="purple-input"
                                            v-model="account.email"
                                            :error-messages="emailErrors"
                                            type="email"
                                            required
                                            @input="$v.account.email.$touch()"
                                            @blur="$v.account.email.$touch()"/>

                                </v-flex>

                                <v-flex
                                        xs12
                                        md6
                                >
                                    <v-text-field
                                            :label="$t('register.firstNameLabel')"
                                            class="purple-input"
                                            v-model="account.firstName"
                                            type="text"
                                            name="first-name"
                                            required
                                            :error-messages="firstNameErrors"
                                            @input="$v.account.firstName.$touch()"
                                            @blur="$v.account.firstName.$touch()"/>

                                </v-flex>
                                <v-flex
                                        xs12
                                        md6
                                >
                                    <v-text-field
                                            :label="$t('register.lastNameLabel')"
                                            class="purple-input"
                                            v-model="account.lastName"
                                            type="text"
                                            name="last-name"
                                            required
                                            :error-messages="lastNameErrors"
                                            @input="$v.account.lastName.$touch()"
                                            @blur="$v.account.lastName.$touch()"/>

                                </v-flex>
                                <v-flex
                                        xs12
                                        md12
                                >
                                    <v-text-field
                                            :label="$t('register.streetLabel')"
                                            class="purple-input"
                                            v-model="account.street"
                                            type="text"
                                            name="street"
                                            required
                                            :error-messages="streetErrors"
                                            @input="$v.account.street.$touch()"
                                            @blur="$v.account.street.$touch()"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4
                                >
                                    <v-text-field
                                            :label="$t('register.numberLabel')"
                                            class="purple-input"
                                            type="number"
                                            v-model="account.number"
                                            name="number"
                                            required
                                            :error-messages="numberErrors"
                                            @input="$v.account.number.$touch()"
                                            @blur="$v.account.number.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4
                                >
                                    <v-text-field
                                            :label="$t('register.apartmentLabel')"
                                            class="purple-input"
                                            v-model="account.apartment"
                                            name="apartment"
                                            required
                                            :error-messages="apartmentErrors"
                                            @input="$v.account.apartment.$touch()"
                                            @blur="$v.account.apartment.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4
                                >
                                    <v-text-field
                                            :label="$t('register.zipCodeLabel')"
                                            class="purple-input"
                                            v-model="account.zip"
                                            name="zip"
                                            required
                                            :error-messages="zipErrors"
                                            @input="$v.account.zip.$touch()"
                                            @blur="$v.account.zip.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4>
                                    <v-text-field
                                            :label="$t('register.cityLabel')"
                                            class="purple-input"
                                            v-model="account.city"
                                            name="city"
                                            required
                                            :error-messages="cityErrors"
                                            @input="$v.account.city.$touch()"
                                            @blur="$v.account.city.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4>
                                    <v-text-field
                                            :label="$t('register.regionLabel')"
                                            class="purple-input"
                                            v-model="account.region"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4>
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('register.countryLabel')"
                                            v-model="account.country"
                                            name="country"
                                            required
                                            :error-messages="countryErrors"
                                            @input="$v.account.country.$touch()"
                                            @blur="$v.account.country.$touch()"
                                    />
                                </v-flex>
                                <v-flex xs12>
                                    <v-textarea
                                            class="purple-input"
                                            :label="$t('register.descriptionLabel')"
                                            v-model="account.description"
                                            name="description"
                                            required
                                            :error-messages="descriptionErrors"
                                            @input="$v.account.description.$touch()"
                                            @blur="$v.account.description.$touch()"
                                    />
                                </v-flex>
                                <v-flex xs12>
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
                                <v-flex
                                        xs12
                                        text-xs-right
                                >
                                    <v-btn
                                            class="mx-0 font-weight-light"
                                            type="submit"
                                            color="#346C47"
                                    >
                                        {{$t('profile.saveLabel')}}
                                    </v-btn>
                                </v-flex>
                                <v-flex xs-12>
                                    <v-btn color="black" to="/change-password" text small>{{$t('profile.changePassword')}}</v-btn>
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
                                :src="currentAccount.profilePicId ? 'http://localhost:8080/api/downloadPicture/'+currentAccount.profilePicId : require('../assets/avatar.png')"
                        >
                    </v-avatar>
                    <v-card-text class="text-xs-center">
                        <h4 class="card-title font-weight-light">{{currentAccount.firstName + ' ' + currentAccount.lastName}}</h4>
                        <p class="card-description font-weight-light">{{currentAccount.description}}</p>
                    </v-card-text>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>

</template>

<script>
    import {mapActions, mapGetters} from "vuex";
    import {validationMixin} from "vuelidate";
    import {email, maxLength, minValue, required} from "vuelidate/lib/validators";
    import Account from "../models/account";
    import gmapsInit from "../plugins/gmaps";
    import PictureService from "../services/picture.service"

    export default {
        mixins: [validationMixin],
        validations: {
            account : {
                firstName: { required, maxLength: maxLength(50)},
                lastName: { required, maxLength: maxLength(50)},
                email: { required, email},
                street: {required, maxLength: maxLength(150)},
                number: {required, minValue: minValue(0.00000001)},
                apartment: {required, maxLength: maxLength(15)},
                zip: {required, maxLength: maxLength(15)},
                city: {required, maxLength: maxLength(150)},
                country: {required, maxLength: maxLength(150)},
                description: {required}
            }
        },

        name: "Profile",
        data() {
            return {
                account: new Account(),
                file:null,
                message: '',
                show: false,
                rules: [
                    value => !value || value.size < 3000000 || 'Avatar size should be less than 3 MB!',
                ],
            }
        },
        computed: {
            ...mapGetters('auth', [
                'loggedIn',
                'currentAccount',
                'accountUpdateErrorCode',
                'accountUpdateError'
            ]),

            firstNameErrors () {
                const errors = [];
                if (!this.$v.account.firstName.$dirty) return errors
                !this.$v.account.firstName.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.firstNameLabel'), '50']));
                !this.$v.account.firstName.required && errors.push(this.$t("requiredError", [this.$t('register.firstNameLabel')]));
                return errors
            },
            lastNameErrors () {
                const errors = [];
                if (!this.$v.account.lastName.$dirty) return errors
                !this.$v.account.lastName.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.lastNameLabel'), '50']));
                !this.$v.account.lastName.required && errors.push(this.$t("requiredError", [this.$t('register.lastNameLabel')]));
                return errors
            },
            emailErrors () {
                const errors = [];
                if (!this.$v.account.email.$dirty) return errors;
                !this.$v.account.email.email && errors.push(this.$t('emailInvalidError'));
                !this.$v.account.email.required && errors.push(this.$t("requiredError", [this.$t('register.emailLabel')]));
                return errors;
            },
            streetErrors () {
                const errors = [];
                if (!this.$v.account.street.$dirty) return errors
                !this.$v.account.street.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.streetLabel'), '150']));
                !this.$v.account.street.required && errors.push(this.$t("requiredError", [this.$t('register.streetLabel')]));
                return errors
            },
            numberErrors () {
                const errors = [];
                if (!this.$v.account.number.$dirty) return errors
                !this.$v.account.number.minValue && errors.push(this.$t("minValueError", [this.$t('register.numberLabel'), '0']));
                !this.$v.account.number.required && errors.push(this.$t("requiredError", [this.$t('register.numberLabel')]));
                return errors
            },
            apartmentErrors () {
                const errors = [];
                if (!this.$v.account.apartment.$dirty) return errors
                !this.$v.account.apartment.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.apartmentLabel'), '15']));
                !this.$v.account.apartment.required && errors.push(this.$t("requiredError", [this.$t('register.apartmentLabel')]));
                return errors
            },
            zipErrors () {
                const errors = [];
                if (!this.$v.account.zip.$dirty) return errors
                !this.$v.account.zip.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.zipCodeLabel'), '15']));
                !this.$v.account.zip.required && errors.push(this.$t("requiredError", [this.$t('register.zipCodeLabel')]));
                return errors
            },
            cityErrors () {
                const errors = [];
                if (!this.$v.account.city.$dirty) return errors
                !this.$v.account.city.maxLength &&  errors.push(this.$t("maxLengthError", [this.$t('register.cityLabel'), '150']));
                !this.$v.account.city.required && errors.push(this.$t("requiredError", [this.$t('register.cityLabel')]));
                return errors
            },
            countryErrors () {
                const errors = [];
                if (!this.$v.account.country.$dirty) return errors
                !this.$v.account.country.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.countryLabel'), '150']));
                !this.$v.account.country.required && errors.push(this.$t("requiredError", [this.$t('register.countryLabel')]));
                return errors
            },
            descriptionErrors () {
                const errors = [];
                if (!this.$v.account.description.$dirty) return errors;
                !this.$v.account.description.required && errors.push(this.$t("requiredError", [this.$t('register.descriptionLabel')]));
                return errors
            },
        },
        async mounted() {
            if (!this.loggedIn) {
                this.$router.push('/login');
            }
            this.account = Object.assign(this.account, this.currentAccount);

            try{
                this.google = await gmapsInit();
                this.geocoder = new this.google.maps.Geocoder();
            }  catch (error) {
                // eslint-disable-next-line no-console
                console.error(error);
            }
        },
        methods: {
            ...mapActions('auth', [
                'updateAccount'
            ]),

            createAddress() {
                let addressString = "";
                addressString = addressString + this.account.country + ", ";
                if (this.account.region) {
                    addressString = addressString + this.account.region + ", ";
                }
                addressString = addressString + this.account.city + ", " + this.account.street + " " + this.account.number + ", ap " + this.account.apartment;
                return addressString;
            },
            async handleSave() {
                console.log('start to save!');
                this.$v.$touch();
                this.message = '';
                if (this.$v.$invalid) {
                    this.message = this.$t("fillOutMessage");
                } else {
                    let addressString = this.createAddress();
                    console.log("Geocode: " + addressString);
                    this.geocoder.geocode({address: addressString}, (results, status) => {
                        if (status !== 'OK' || !results[0]) {
                            throw new Error(status);
                        }
                        this.account.latitude = results[0].geometry.location.lat();
                        this.account.longitude = results[0].geometry.location.lng();
                        console.log(results[0].geometry.location.lat() + " " + results[0].geometry.location.lng());

                        this.updateAccount(this.account).then(
                            response => {
                                console.log("Update response " + response);
                                if(response) {
                                    this.account = Object.assign(this.account, this.currentAccount);
                                } else {
                                    this.message =  this.accountUpdateError || this.$t('profile.error');
                                }
                            }
                        );

                    });
                }
            },
            uploadPicture() {
                if(this.file) {
                    this.$v.$touch();
                    PictureService.savePicture(this.file).then(
                        response => {
                            console.log("Picture save success");
                            this.account.profilePicId = response.id;
                            this.handleSave();
                        },
                        error => {
                            this.message = this.$t("pictureUploadError");
                            console.log((error.response && error.response.data) ||
                                error.message ||
                                error.toString());
                        }
                    );
                } else {
                    this.handleSave();
                }
            },
            handleFileUpload(file){
                console.log("File changed: " + file.name);
                if (!file.name)
                    return;
                this.file = file;
            }
        }
    }
</script>