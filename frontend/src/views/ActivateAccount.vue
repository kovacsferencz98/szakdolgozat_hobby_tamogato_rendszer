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
                        :title="$t('activateAccount.label')"
                        :text="$t('activateAccount.text')"
                >
                    <v-progress-circular
                            v-if="loading"
                            :size="70"
                            :width="7"
                            color="#346C47"
                            indeterminate
                    />
                    <v-alert
                            v-if="successMessage"
                            prominent
                            type="success"
                    >
                        {{successMessage}}
                    </v-alert>
                    <v-alert
                            v-if="message"
                            prominent
                            type="error"
                    >
                        {{message}}
                    </v-alert>
                    <div
                            class="my-2"
                            v-if="message"
                    >
                        <v-btn depressed color="error" @click="activateAccount">{{this.$t('activateAccount.tryAgain')}}</v-btn>
                    </div>
                    <div
                            class="my-2"
                            v-if="successMessage"
                    >
                        <v-btn depressed color="success" to="/login" style="text-decoration: none">{{this.$t('activateAccount.login')}}</v-btn>
                    </div>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import AccountService from "../services/account.service";

    export default {
        name: "ActivateAccount",
        data() {
            return {
                loading: true,
                successMessage: '',
                message:'',
                key: ''
            };
        },
        methods: {
            async activateAccount() {
                this.loading=true;
                this.successMessage = '';
                this.message = '';
                try {
                    await AccountService.activateAccount(this.key);
                    this.loading = false;
                    this.successMessage = this.$t('activateAccount.requestOk');
                } catch (error) {
                    this.loading = false;
                    this.message =
                        (error && error.message) ||
                        this.$t('activateAccount.requestFail');
                }
            }
        },
        created() {
            this.key = this.$route.params.key;
        },
        mounted() {
            this.activateAccount()
        }
    }
</script>

<style scoped>

</style>