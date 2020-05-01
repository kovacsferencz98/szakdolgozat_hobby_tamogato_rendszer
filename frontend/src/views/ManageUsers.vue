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
                        :title="$t('manageUsers.manageUsersLabel')"
                        :text="$t('manageUsers.manageUsersText')"
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
                                :items="allUsers"
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
                                            <v-btn color="primary" dark class="mb-2" v-on="on">New Item</v-btn>
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
                                                                    v-if="editedIndex === -1"
                                                                    xs12
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageUsers.emailLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.email"
                                                                        :error-messages="emailErrors"
                                                                        type="email"
                                                                        required
                                                                        @input="$v.editedItem.email.$touch()"
                                                                        @blur="$v.editedItem.email.$touch()"/>

                                                            </v-flex>
                                                            <v-flex
                                                                    v-else
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageUsers.emailLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.email"
                                                                        :error-messages="emailErrors"
                                                                        type="email"
                                                                        required
                                                                        @input="$v.editedItem.email.$touch()"
                                                                        @blur="$v.editedItem.email.$touch()"/>

                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        class="purple-input"
                                                                        :label="$t('manageUsers.usernameLabel')"
                                                                        v-model="editedItem.username"
                                                                        type="text"
                                                                        name="username"
                                                                        required
                                                                        :error-messages="usernameErrors"
                                                                        @input="$v.editedItem.username.$touch()"
                                                                        @blur="$v.editedItem.username.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    v-if="editedIndex === -1"
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        class="purple-input"
                                                                        :label="$t('manageUsers.passwordLabel')"
                                                                        v-model="editedItem.password"
                                                                        name="password"
                                                                        :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                                                                        :type="show ? 'text' : 'password'"
                                                                        required
                                                                        :error-messages="passwordErrors"
                                                                        @click:append="show = !show"
                                                                        @input="$v.editedItem.password.$touch()"
                                                                        @blur="$v.editedItem.password.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageUsers.firstNameLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.firstName"
                                                                        type="text"
                                                                        name="first-name"
                                                                        required
                                                                        :error-messages="firstNameErrors"
                                                                        @input="$v.editedItem.firstName.$touch()"
                                                                        @blur="$v.editedItem.firstName.$touch()"/>

                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageUsers.lastNameLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.lastName"
                                                                        type="text"
                                                                        name="last-name"
                                                                        required
                                                                        :error-messages="lastNameErrors"
                                                                        @input="$v.editedItem.lastName.$touch()"
                                                                        @blur="$v.editedItem.lastName.$touch()"/>

                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-select class="purple-input"
                                                                          :label="$t('manageUsers.rolesLabel')"
                                                                          v-model="editedItem.roles"
                                                                          :items="roles"
                                                                          multiple
                                                                          chips
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
                            <template v-slot:item.activated="{ item }">
                                <v-checkbox v-model="item.activated" @change="activateUser(item)"/>
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
    import {Role} from "../models/role";
    import User from "../models/user";
    import {validationMixin} from "vuelidate";
    import {email, maxLength, minLength, required} from "vuelidate/lib/validators";
    import {mapActions, mapGetters} from "vuex";

    export default {
        mixins: [validationMixin],
        validations: {
            editedItem : {
                username: {required, maxLength: maxLength(20), minLength: minLength(5)},
                password: {required, maxLength: maxLength(20), minLength: minLength(6)},
                firstName: { required, maxLength: maxLength(50)},
                lastName: { required, maxLength: maxLength(50)},
                email: { required, email}}
        },

        name: "Manageusers",
        data: vm => ({
            search: '',
            dialog: false,
            show: false,
            message:'',
            deleteMessage: '',
            page: 1,
            pageCount: 0,
            itemsPerPage: 10,
            possibleItemsPerPage: [5, 10, 15, 20],
            roles: [Role.admin, Role.user, Role.moderator],
            headers: [
                {
                    text: 'ID',
                    align: 'left',
                    value: 'id',
                },
                { text: vm.$t('manageUsers.usernameLabel'), value: 'username' },
                { text: vm.$t('manageUsers.firstNameLabel'), value: 'firstName' },
                { text: vm.$t('manageUsers.lastNameLabel'), value: 'lastName' },
                { text: vm.$t('manageUsers.emailLabel'), value: 'email' },
                { text: vm.$t('manageUsers.activatedLabel'), value: 'activated', sortable: false},
                { text: vm.$t('manageUsers.rolesLabel'), value: 'roles' },
                { text: vm.$t('actionsLabel'), value: 'action', sortable: false },
            ],
            editedIndex: -1,
            editedItem: new User(),
            defaultItem: new User(),
        }),
        computed: {
            ...mapGetters('user', [
                'allUsers',
                'obtainErrorCode',
                'obtainError'
            ]),

            formTitle () {
                return this.editedIndex === -1 ? this.$t('newItemLabel') : this.$t('editItemLabel')
            },

            firstNameErrors () {
                const errors = [];
                if (!this.$v.editedItem.firstName.$dirty) return errors
                !this.$v.editedItem.firstName.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageUsers.firstNameLabel'), '50']));
                !this.$v.editedItem.firstName.required && errors.push(this.$t("requiredError", [this.$t('manageUsers.firstNameLabel')]));
                return errors
            },
            lastNameErrors () {
                const errors = [];
                if (!this.$v.editedItem.lastName.$dirty) return errors
                !this.$v.editedItem.lastName.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageUsers.lastNameLabel'), '50']));
                !this.$v.editedItem.lastName.required && errors.push(this.$t("requiredError", [this.$t('manageUsers.lastNameLabel')]));
                return errors
            },
            emailErrors () {
                const errors = [];
                if (!this.$v.editedItem.email.$dirty) return errors;
                !this.$v.editedItem.email.email && errors.push(this.$t('emailInvalidError'));
                !this.$v.editedItem.email.required && errors.push(this.$t("requiredError", [this.$t('manageUsers.emailLabel')]));
                return errors;
            },
            usernameErrors() {
                console.log('uname errors');
                const errors = [];
                if (!this.$v.editedItem.username.$dirty) return errors;
                !this.$v.editedItem.username.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageUsers.usernameLabel'), '20']));
                !this.$v.editedItem.username.required && errors.push(this.$t("requiredError", [this.$t('manageUsers.usernameLabel')]));
                !this.$v.editedItem.username.minLength && errors.push(this.$t("minLengthError", [this.$t('manageUsers.usernameLabel'), '5']));
                return errors;
            },
            passwordErrors() {
                const errors = [];
                if (!this.$v.editedItem.password.$dirty) return errors;
                !this.$v.editedItem.password.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageUsers.passwordLabel'), '20']));
                !this.$v.editedItem.password.required && errors.push(this.$t("requiredError", [this.$t('manageUsers.passwordLabel')]));
                !this.$v.editedItem.password.minLength && errors.push(this.$t("minLengthError", [this.$t('manageUsers.passwordLabel'), '6']));
                return errors;
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
        methods: {
            ...mapActions('user', [
                'getUsers',
                'deleteUser',
                'createUser',
                'updateUser'
            ]),

            initialize () {
                this.getUsers();
            },
            editItem (item) {
                console.log('Edit ' + this.allUsers.indexOf(item));
                this.editedIndex = this.allUsers.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.dialog = true
            },
            async deleteItem (item) {
                this.deleteMessage='';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await this.deleteUser(item.username);
                    if(!requestOk) {
                        this.deleteMessage = this.obtainError;
                    }
                }
            },
            activateUser(item){
                const index = this.allUsers.indexOf(item)
                console.log("Activate user: " + this.allUsers[index].username);
                this.updateUser(item);
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
            save () {
                this.$v.$touch();
                this.deleteMessage = '';
                this.message = '';
                if (this.$v.$invalid) {
                    this.message = this.$t('fillOutMessage');
                } else {
                    if (this.editedIndex > -1) {
                        this.update();
                    } else {
                        this.createNew();
                    }
                }
            },
            async update() {
                let userOk = await this.updateUser(this.editedItem);
                if(userOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            async createNew() {
                let userOk = await this.createUser(this.editedItem);
                if(!userOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
        },
    }
</script>

<style scoped>

</style>