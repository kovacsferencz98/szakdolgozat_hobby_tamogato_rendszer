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
                        :title="$t('changePassword.label')"
                        :text="$t('changePassword.text')"
                >
                    <v-form id="regForm"  @submit.prevent="resetPassword">
                        <v-container py-0>
                            <v-layout wrap>
                                <v-flex
                                        xs12
                                >
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('changePassword.currentPassword')"
                                            v-model="currentPassword"
                                            :append-icon="showCurrentPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                            :type="showCurrentPassword ? 'text' : 'password'"
                                            name="password"
                                            counter
                                            required
                                            @click:append="showCurrentPassword = !showCurrentPassword"
                                            :error-messages="currentPasswordErrors"
                                            @input="$v.currentPassword.$touch()"
                                            @blur="$v.currentPassword.$touch()"/>
                                </v-flex>
                                <v-flex
                                        xs12
                                >
                                    <v-text-field
                                            class="purple-input"
                                            :label="$t('changePassword.newPassword')"
                                            v-model="newPassword"
                                            :append-icon="showNewPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                            :type="showNewPassword ? 'text' : 'password'"
                                            name="password"
                                            counter
                                            required
                                            @click:append="showNewPassword = !showNewPassword"
                                            :error-messages="newPasswordErrors"
                                            @input="$v.newPassword.$touch()"
                                            @blur="$v.newPassword.$touch()"/>
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
                                        {{$t('changePassword.submitLabel')}}
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
            currentPassword: {required, maxLength: maxLength(20), minLength: minLength(6)},
            newPassword: {required, maxLength: maxLength(20), minLength: minLength(6)},
        },
        name: "ChangePassword",
        data() {
            return {
                message: '',
                showCurrentPassword: false,
                showNewPassword: false,
                successMessage: '',
                currentPassword: '',
                newPassword: '',
            };
        },
        computed: {
            currentPasswordErrors() {
                const errors = [];
                if (!this.$v.currentPassword.$dirty) return errors;
                !this.$v.currentPassword.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.passwordLabel'), '20']));
                !this.$v.currentPassword.required && errors.push(this.$t("requiredError", [this.$t('register.passwordLabel')]));
                !this.$v.currentPassword.minLength && errors.push(this.$t("minLengthError", [this.$t('register.passwordLabel'), '6']));
                return errors;
            },
            newPasswordErrors() {
                const errors = [];
                if (!this.$v.newPassword.$dirty) return errors;
                !this.$v.newPassword.maxLength && errors.push(this.$t("maxLengthError", [this.$t('register.passwordLabel'), '20']));
                !this.$v.newPassword.required && errors.push(this.$t("requiredError", [this.$t('register.passwordLabel')]));
                !this.$v.newPassword.minLength && errors.push(this.$t("minLengthError", [this.$t('register.passwordLabel'), '6']));
                return errors;
            },
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
                        await AccountService.changePassword(this.currentPassword, this.newPassword);
                        this.successMessage = this.$t('changePassword.requestOk');
                        this.currentPassword='';
                        this.newPassword='';
                        this.$v.$reset();
                    }
                    catch (error) {
                        console.log(error.message);
                        this.message = (error && error.message) ||
                            this.$t('changePassword.requestFail');
                    }
                }
            }

        }
    }
</script>

<style scoped>

</style>