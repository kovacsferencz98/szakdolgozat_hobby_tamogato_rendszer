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
                        :title="$t('resetPassword.label')"
                        :text="$t('resetPassword.text')"
                >
                    <v-form id="regForm"  @submit.prevent="resetPassword">
                        <v-container py-0>
                            <v-layout wrap>
                                <v-flex
                                        xs12
                                >
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('register.passwordLabel')"
                                            v-model="password"
                                            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                                            :type="show ? 'text' : 'password'"
                                            name="password"
                                            counter
                                            required
                                            @click:append="show = !show"
                                            :error-messages="passwordErrors"
                                            @input="$v.password.$touch()"
                                            @blur="$v.password.$touch()"/>
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
                                        {{$t('resetPassword.submitLabel')}}
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
                    <v-alert
                            v-if="successMessage"
                            prominent
                            type="success"
                    >
                        {{successMessage}}
                    </v-alert>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import {maxLength, minLength, required} from "vuelidate/lib/validators";
    import {validationMixin} from "vuelidate";
    import AccountService from "../services/account.service";

    export default {
        mixins: [validationMixin],
        validations: {
            password: {required, maxLength: maxLength(20), minLength: minLength(6)},
        },
        name: "ResetPassword",
        data() {
            return {
                message: '',
                show: false,
                successMessage: '',
                password: '',
                resetKey:''
            };
        },
        computed: {
            passwordErrors() {
                const errors = [];
                if (!this.$v.password.$dirty) return errors;
                !this.$v.password.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.passwordLabel'), '20']));
                !this.$v.password.required && errors.push(this.$t("requiredError", [this.$t('register.passwordLabel')]));
                !this.$v.password.minLength && errors.push(this.$t("minLengthError", [this.$t('register.passwordLabel'), '6']));
                return errors;
            },
        },
        created() {
            this.resetKey = this.$route.params.key;
        },
        methods: {

            async resetPassword() {
                this.$v.$touch();
                this.message = '';
                this.successMessage = '';
                if (this.$v.$invalid) {
                    this.message = this.$t("fillOutMessage");
                } else {
                    try {
                        await AccountService.resetPassword(this.password, this.resetKey);
                        this.successMessage = this.$t('resetPassword.requestOk');
                        this.password = '';
                    }
                    catch (error) {
                        console.log("Error: " + error);
                        this.message =
                            (error && error.message) ||
                            this.$t('resetPassword.requestFail');

                    }
                }
            }

        }
    }
</script>

<style scoped>

</style>