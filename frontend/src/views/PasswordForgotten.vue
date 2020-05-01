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
                        :title="$t('passwordForgotten.label')"
                        :text="$t('passwordForgotten.text')"
                >
                    <v-form id="regForm"  @submit.prevent="requestPassword">
                        <v-container py-0>
                            <v-layout wrap>
                                <v-flex
                                        xs12
                                >
                                    <v-text-field
                                            :label="$t('register.emailLabel')"
                                            class="purple-input"
                                            v-model="email"
                                            :error-messages="emailErrors"
                                            type="email"
                                            required
                                            @input="$v.email.$touch()"
                                            @blur="$v.email.$touch()"/>

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
                                        {{$t('passwordForgotten.requestLabel')}}
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
    import {validationMixin} from "vuelidate";
    import {email, required} from "vuelidate/lib/validators";
    import AccountService from "../services/account.service";

    export default {
        mixins: [validationMixin],
        validations: {
            email: {required, email}
        },
        name: "PasswordForgotten",
        data() {
            return {
                message: '',
                successMessage: '',
                email: ''
            };
        },
        computed: {
            emailErrors() {
                const errors = [];
                if (!this.$v.email.$dirty) return errors;
                !this.$v.email.email && errors.push(this.$t('emailInvalidError'));
                return errors;
            },
        },
        methods: {
            async requestPassword() {
                this.$v.$touch();
                this.message = '';
                this.successMessage = '';
                if (this.$v.$invalid) {
                    this.message = this.$t("fillOutMessage");
                } else {
                    try {
                        await AccountService.requestPasswordReset(this.email);
                        this.successMessage = this.$t('passwordForgotten.requestOk');
                        this.email = '';
                    }
                    catch (error) {
                        this.message =
                            (error && error.message) ||
                            this.$t('passwordForgotten.requestFail');
                    }
                }
            }

        }
    }
</script>

<style scoped>

</style>