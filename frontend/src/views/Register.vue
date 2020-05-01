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
                    md10
            >
                <material-card
                        color="#346C47"
                        :title="$t('register.registerLabel')"
                        :text="$t('register.registerText')"
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
                                            v-model="user.email"
                                            :error-messages="emailErrors"
                                            type="email"
                                            required
                                            @input="$v.user.email.$touch()"
                                            @blur="$v.user.email.$touch()"/>

                                </v-flex>
                                <v-flex
                                        xs12
                                        md6
                                >
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('register.usernameLabel')"
                                            v-model="user.username"
                                            type="text"
                                            name="username"
                                            required
                                            :error-messages="usernameErrors"
                                            @input="$v.user.username.$touch()"
                                            @blur="$v.user.username.$touch()"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                        md6
                                >
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('register.passwordLabel')"
                                            v-model="user.password"
                                            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                                            :type="show ? 'text' : 'password'"
                                            name="password"
                                            counter
                                            required
                                            @click:append="show = !show"
                                            :error-messages="passwordErrors"
                                            @input="$v.user.password.$touch()"
                                            @blur="$v.user.password.$touch()"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                        md6
                                >
                                    <v-text-field
                                            :label="$t('register.firstNameLabel')"
                                            class="purple-input"
                                            v-model="user.firstName"
                                            type="text"
                                            name="first-name"
                                            required
                                            :error-messages="firstNameErrors"
                                            @input="$v.user.firstName.$touch()"
                                            @blur="$v.user.firstName.$touch()"/>

                                </v-flex>
                                <v-flex
                                        xs12
                                        md6
                                >
                                    <v-text-field
                                            :label="$t('register.lastNameLabel')"
                                            class="purple-input"
                                            v-model="user.lastName"
                                            type="text"
                                            name="last-name"
                                            required
                                            :error-messages="lastNameErrors"
                                            @input="$v.user.lastName.$touch()"
                                            @blur="$v.user.lastName.$touch()"/>

                                </v-flex>
                                <v-flex
                                        xs12
                                        md12
                                >
                                    <v-text-field
                                            :label="$t('register.streetLabel')"
                                            class="purple-input"
                                            v-model="location.street"
                                            type="text"
                                            name="street"
                                            required
                                            :error-messages="streetErrors"
                                            @input="$v.location.street.$touch()"
                                            @blur="$v.location.street.$touch()"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4
                                >
                                    <v-text-field
                                            :label="$t('register.numberLabel')"
                                            class="purple-input"
                                            type="number"
                                            v-model="location.number"
                                            name="number"
                                            required
                                            :error-messages="numberErrors"
                                            @input="$v.location.number.$touch()"
                                            @blur="$v.location.number.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4
                                >
                                    <v-text-field
                                            :label="$t('register.apartmentLabel')"
                                            class="purple-input"
                                            v-model="location.apartment"
                                            name="apartment"
                                            required
                                            :error-messages="apartmentErrors"
                                            @input="$v.location.apartment.$touch()"
                                            @blur="$v.location.apartment.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4
                                >
                                    <v-text-field
                                            :label="$t('register.zipCodeLabel')"
                                            class="purple-input"
                                            v-model="location.zip"
                                            name="zip"
                                            required
                                            :error-messages="zipErrors"
                                            @input="$v.location.zip.$touch()"
                                            @blur="$v.location.zip.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4>
                                    <v-text-field
                                            :label="$t('register.cityLabel')"
                                            class="purple-input"
                                            v-model="location.city"
                                            name="city"
                                            required
                                            :error-messages="cityErrors"
                                            @input="$v.location.city.$touch()"
                                            @blur="$v.location.city.$touch()"
                                    />
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4>
                                    <v-text-field
                                            :label="$t('register.regionLabel')"
                                            class="purple-input"
                                            v-model="location.region"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                        md4>
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('register.countryLabel')"
                                            v-model="location.country"
                                            name="country"
                                            required
                                            :error-messages="countryErrors"
                                            @input="$v.location.country.$touch()"
                                            @blur="$v.location.country.$touch()"
                                    />
                                </v-flex>
                                <v-flex xs12>
                                    <v-textarea
                                            class="purple-input"
                                            :label="$t('register.descriptionLabel')"
                                            v-model="details.description"
                                            name="description"
                                            required
                                            :error-messages="descriptionErrors"
                                            @input="$v.details.description.$touch()"
                                            @blur="$v.details.description.$touch()"
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
                                        {{$t('register.registerLabel')}}
                                    </v-btn>
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
        </v-layout>
    </v-container>

</template>

<script>
    import User from '../models/user';
    import UserDetails from '../models/user-details'
    import Location from '../models/location'
    import {mapActions, mapGetters} from "vuex";
    import { validationMixin } from 'vuelidate'
    import { required, maxLength, email, minLength , minValue} from 'vuelidate/lib/validators'
    import gmapsInit from '../plugins/gmaps';
    import PictureService from "../services/picture.service";

    export default {
        mixins: [validationMixin],
        validations: {
            user : {
                username: {required, maxLength: maxLength(20), minLength: minLength(5)},
                password: {required, maxLength: maxLength(20), minLength: minLength(6)},
                firstName: { required, maxLength: maxLength(50)},
                lastName: { required, maxLength: maxLength(50)},
                email: { required, email}}
            ,
            location: {
                street: {required, maxLength: maxLength(150)},
                number: {required, minValue: minValue(0.00000001)},
                apartment: {required, maxLength: maxLength(15)},
                zip: {required, maxLength: maxLength(15)},
                city: {required, maxLength: maxLength(150)},
                country: {required, maxLength: maxLength(150)}
            },
            details: {
                description: {required}
            }
        },

        name: 'Register',
        data() {
            return {
                user: new User('', '', '', '', '', [] , '', -1),
                location: new Location(-1, '', '', '', '', null, '', '', -1, -1),
                details: new UserDetails(-1, '', null, '', '', -1, -1, ''),
                submitted: false,
                successful: false,
                message: '',
                show: false,
                file: null,
                google: null,
                geocoder: null,
                rules: [
                    value => !value || value.size < 3000000 || this.$t("register.avatarSize"),
                ],
            };
        },
        computed: {
            ...mapGetters('auth', [
                'loggedIn'
            ]),


            firstNameErrors () {
                const errors = [];
                if (!this.$v.user.firstName.$dirty) return errors
                !this.$v.user.firstName.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.firstNameLabel'), '50']));
                !this.$v.user.firstName.required && errors.push(this.$t("requiredError", [this.$t('register.firstNameLabel')]));
                return errors
            },
            lastNameErrors () {
                const errors = [];
                if (!this.$v.user.lastName.$dirty) return errors
                !this.$v.user.lastName.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.lastNameLabel'), '50']));
                !this.$v.user.lastName.required && errors.push(this.$t("requiredError", [this.$t('register.lastNameLabel')]));
                return errors
            },
            emailErrors () {
                const errors = [];
                if (!this.$v.user.email.$dirty) return errors;
                !this.$v.user.email.email && errors.push(this.$t('emailInvalidError'));
                !this.$v.user.email.required && errors.push(this.$t("requiredError", [this.$t('register.emailLabel')]));
                return errors;
            },
            usernameErrors() {
                console.log('uname errors');
                const errors = [];
                if (!this.$v.user.username.$dirty) return errors;
                !this.$v.user.username.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.usernameLabel'), '20']));
                !this.$v.user.username.required && errors.push(this.$t("requiredError", [this.$t('register.usernameLabel')]));
                !this.$v.user.username.minLength && errors.push(this.$t("minLengthError", [this.$t('register.usernameLabel'), '5']));
                return errors;
            },
            passwordErrors() {
                const errors = [];
                if (!this.$v.user.password.$dirty) return errors;
                !this.$v.user.password.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.passwordLabel'), '20']));
                !this.$v.user.password.required && errors.push(this.$t("requiredError", [this.$t('register.passwordLabel')]));
                !this.$v.user.password.minLength && errors.push(this.$t("minLengthError", [this.$t('register.passwordLabel'), '6']));
                return errors;
            },
            streetErrors () {
                const errors = [];
                if (!this.$v.location.street.$dirty) return errors
                !this.$v.location.street.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.streetLabel'), '150']));
                !this.$v.location.street.required && errors.push(this.$t("requiredError", [this.$t('register.streetLabel')]));
                return errors
            },
            numberErrors () {
                const errors = [];
                if (!this.$v.location.number.$dirty) return errors
                !this.$v.location.number.minValue && errors.push(this.$t("minValueError", [this.$t('register.numberLabel'), '0']));
                !this.$v.location.number.required && errors.push(this.$t("requiredError", [this.$t('register.numberLabel')]));
                return errors
            },
            apartmentErrors () {
                const errors = [];
                if (!this.$v.location.apartment.$dirty) return errors
                !this.$v.location.apartment.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.apartmentLabel'), '15']));
                !this.$v.location.apartment.required && errors.push(this.$t("requiredError", [this.$t('register.apartmentLabel')]));
                return errors
            },
            zipErrors () {
                const errors = [];
                if (!this.$v.location.zip.$dirty) return errors
                !this.$v.location.zip.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.zipCodeLabel'), '15']));
                !this.$v.location.zip.required && errors.push(this.$t("requiredError", [this.$t('register.zipCodeLabel')]));
                return errors
            },
            cityErrors () {
                const errors = [];
                if (!this.$v.location.city.$dirty) return errors
                !this.$v.location.city.maxLength &&  errors.push(this.$t("maxLengthError", [this.$t('register.cityLabel'), '150']));
                !this.$v.location.city.required && errors.push(this.$t("requiredError", [this.$t('register.cityLabel')]));
                return errors
            },
            countryErrors () {
                const errors = [];
                if (!this.$v.location.country.$dirty) return errors
                !this.$v.location.country.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.countryLabel'), '150']));
                !this.$v.location.country.required && errors.push(this.$t("requiredError", [this.$t('register.countryLabel')]));
                return errors
            },
            descriptionErrors () {
                const errors = [];
                if (!this.$v.details.description.$dirty) return errors;
                !this.$v.details.description.required && errors.push(this.$t("requiredError", [this.$t('register.descriptionLabel')]));
                return errors
            },
        },
        async mounted() {
            if (this.loggedIn) {
                this.$router.push('/profile');
            }

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
                'register'
            ]),

            createAddress() {
                let addressString = "";
                addressString = addressString + this.location.country + ", ";
                if (this.location.region) {
                    addressString = addressString + this.location.region + ", ";
                }
                addressString = addressString + this.location.city + ", " + this.location.street + " " + this.location.number + ", ap " + this.location.apartment;
                return addressString;
            },
            handleRegister() {
                console.log('submit!');
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
                        this.location.latitude = results[0].geometry.location.lat();
                        this.location.longitude = results[0].geometry.location.lng();
                        console.log(results[0].geometry.location.lat() + " " + results[0].geometry.location.lng());

                        this.submitted = true;
                        this.register({user: this.user, location: this.location, details: this.details}).then(
                            data => {
                                this.message = data.message;
                                this.successful = true;
                            },
                            error => {
                                this.message =
                                    (error.response && (error.response.data.title || error.response.data.message)) ||
                                    error.message ||
                                    error.toString();
                                this.successful = false;
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
                            this.details.profilePicId = response.id;
                            this.handleRegister();
                        },
                        error => {
                            this.message = this.$t("pictureUploadError");
                            console.log((error.response && error.response.data.message) ||
                                error.message ||
                                error.toString());
                        }
                    );
                } else {
                    this.handleRegister();
                }
            },
            handleFileUpload(file){
                console.log("File changed");
                if (!file.name)
                    return;
                this.file = file;
            }
        }
    };
</script>

<style scoped>
    label {
        display: block;
        margin-top: 10px;
    }

    .card-container.card {
        max-width: 350px !important;
        padding: 40px 40px;
    }

    .card {
        background-color: #f7f7f7;
        padding: 20px 25px 30px;
        margin: 0 auto 25px;
        margin-top: 50px;
        -moz-border-radius: 2px;
        -webkit-border-radius: 2px;
        border-radius: 2px;
        -moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
        -webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
        box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
    }

    .profile-img-card {
        width: 96px;
        height: 96px;
        margin: 0 auto 10px;
        display: block;
        -moz-border-radius: 50%;
        -webkit-border-radius: 50%;
        border-radius: 50%;
    }
</style>