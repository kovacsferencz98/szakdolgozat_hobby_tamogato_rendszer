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
                    md6
                    mx-auto
                    class="py-12"
            >
                <material-card class="v-card-profile" width="100%" >
                    <v-avatar
                            slot="offset"
                            class="mx-auto d-block"
                            size="130"
                    >
                        <img
                                contain
                                :src="require('../assets/login-avatar.png')"
                        >
                    </v-avatar>
                    <v-card-text >
                        <v-form id="regForm"  @submit.prevent="handleLogin">
                            <v-container py-0>
                                <v-layout wrap>
                                    <v-flex
                                            xs12
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
                                    <v-flex xs-12>
                                        <v-btn color="black" to="/forgotten-password" text small>{{$t('login.forgotPassword')}}</v-btn>
                                    </v-flex>
                                    <v-flex
                                            xs12
                                            class="text-center"
                                    >
                                        <v-btn
                                                class="mx-auto font-weight-light"
                                                type="submit"
                                                color="#346C47"
                                        >
                                            {{$t('login.loginText')}}
                                        </v-btn>
                                    </v-flex>
                                    <v-flex
                                            xs12
                                            class="text-center">
                                        <v-alert
                                                v-if="message"
                                                prominent
                                                type="error"
                                        >
                                            {{message}}
                                        </v-alert>
                                    </v-flex>
                                </v-layout>
                            </v-container>
                        </v-form>
                    </v-card-text>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import User from '../models/user';
    import { mapGetters, mapActions } from "vuex";
    import {validationMixin} from "vuelidate";
    import {maxLength, minLength, required} from "vuelidate/lib/validators";

    export default {
        mixins: [validationMixin],
        validations: {
            user: {
                username: {required, maxLength: maxLength(20), minLength: minLength(5)},
                password: {required, maxLength: maxLength(20), minLength: minLength(6)},
            }
        },
        name: 'Login',
        data() {
            return {
                user: new User('', '', ''),
                loading: false,
                message: '',
                show: false,
            };
        },
        computed: {
            ...mapGetters('auth', [
                'authenticating',
                'authenticationError',
                'authenticationErrorCode',
                'authenticationSuccess',
                'loggedIn'
            ]),

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

            //???
            /*loggedIn() {
                return this.$store.state.auth.status.loggedIn;
            }*/
        },
        created() {
            if (this.loggedIn) {
                this.$router.push('/profile');
            }
        },
        methods: {
            ...mapActions('auth', [
                'login'
            ]),

            async handleLogin() {
                this.loading = true;
                this.$v.$touch();
                this.message = '';
                if (this.$v.$invalid) {
                    this.message = this.$t("fillOutMessage");
                } else {
                    this.login({username: this.user.username, password: this.user.password}).then(
                        response => {
                            if (!response) {
                                console.log("Error with login");
                                this.message = this.$t('login.badCredentials');
                                this.user.password = "";
                                this.user.username = "";
                                this.loading = false;
                            } else {
                                console.log("Login success");
                                this.user.password = "";
                                this.user.username = "";
                                this.loading = false;
                            }
                        }
                    )
                }
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