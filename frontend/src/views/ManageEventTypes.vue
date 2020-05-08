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
                        :title="$t('manageEventTypes.manageEventTypesLabel')"
                        :text="$t('manageEventTypes.manageEventTypesText')"

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
                                :items="allEventTypes"
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
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEventTypes.typeName')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.name"
                                                                        type="text"
                                                                        name="eventTypeName"
                                                                        required
                                                                        :error-messages="nameErrors"
                                                                        @input="$v.editedItem.name.$touch()"
                                                                        @blur="$v.editedItem.name.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-file-input
                                                                        v-model="iconFile"
                                                                        :rules="rules"
                                                                        ref="iconUpload"
                                                                        accept="image/png, image/svg+xml"
                                                                        placeholder="Pick an icon"
                                                                        prepend-icon="mdi-image"
                                                                        :label="$t('manageEventTypes.iconLabel')"
                                                                        id="iconUpload"
                                                                        @change="handleIconUpload"
                                                                        @blur="$v.editedItem.iconUrl.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-file-input
                                                                        v-model="bannerFile"
                                                                        :rules="rules"
                                                                        ref="bannerUpload"
                                                                        accept="image/png, image/jpeg, image/bmp"
                                                                        placeholder="Pick a banner"
                                                                        prepend-icon="mdi-image"
                                                                        :label="$t('manageEventTypes.bannerLabel')"
                                                                        id="bannerUpload"
                                                                        @change="handleBannerUpload"
                                                                        @blur="$v.editedItem.bannerUrl.$touch()"/>
                                                            </v-flex>
                                                            <v-flex xs12>
                                                                <v-textarea
                                                                        class="purple-input"
                                                                        :label="$t('manageEventTypes.descriptionLabel')"
                                                                        v-model="editedItem.description"
                                                                        name="description"
                                                                        required
                                                                        :error-messages="descriptionErrors"
                                                                        @input="$v.editedItem.description.$touch()"
                                                                        @blur="$v.editedItem.description.$touch()"
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
                                <v-btn color="primary" @click="initialize">{{$t('resetLabel')}}</v-btn>
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
                                            :label="$t('rowsPerPage')"
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
    import {required} from "vuelidate/lib/validators";
    import {mapActions, mapGetters} from "vuex";
    import EventType from "../models/event-type";

    export default {
        mixins: [validationMixin],
        validations: {
            editedItem : {
                name: {required},
                description: {required},
            }
        },

        name: "ManageEventTypes",
        data: vm => ({
            iconFile:null,
            bannerFile:null,
            search: '',
            dialog: false,
            show: false,
            message:'',
            deleteMessage: '',
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
                { text: vm.$t('manageEventTypes.nameLabel'), value: 'name' },
                { text: vm.$t('manageEventTypes.descriptionLabel'), value: 'description', sortable: false  },
                { text: vm.$t('manageEventTypes.iconLabel'), value: 'iconUrl', sortable: false  },
                { text: vm.$t('manageEventTypes.bannerLabel'), value: 'bannerUrl', sortable: false  },
                { text: vm.$t('actionsLabel'), value: 'action', sortable: false },
            ],
            editedIndex: -1,
            editedItem: new EventType(),
            defaultItem: new EventType(),
            rules: [
                value => !value || value.size < 10000000 ||  this.$t("register.avatarSize"),
            ]
        }),
        computed: {
            ...mapGetters('eventType', [
                'allEventTypes',
                'obtainErrorCode',
                'obtainError'
            ]),

            formTitle () {
                return this.editedIndex === -1 ?  this.$t('newItemLabel') : this.$t('editItemLabel')
            },

            nameErrors () {
                const errors = [];
                if (!this.$v.editedItem.name.$dirty) return errors;
                !this.$v.editedItem.name.required &&  errors.push(this.$t("requiredError", [this.$t('manageEventTypes.typeName')]));
                return errors;
            },
            descriptionErrors () {
                const errors = [];
                if (!this.$v.editedItem.description.$dirty) return errors;
                !this.$v.editedItem.description.required && errors.push(this.$t("requiredError", [this.$t('manageEventTypes.descriptionLabel')]));
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
            ...mapActions('eventType', [
                'getEventTypes',
                'deleteEventType',
                'createEventType',
                'updateEventType'
            ]),

            initialize () {
                this.getEventTypes();
            },
            editItem (item) {
                console.log('Edit ' + this.allEventTypes.indexOf(item));
                this.editedIndex = this.allEventTypes.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.dialog = true
            },
            async deleteItem (item) {
                this.deleteMessage = '';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await  this.deleteEventType(item.id);
                    if(!requestOk) {
                        this.deleteMessage = this.obtainError;
                    }
                }
            },
            close () {
                this.dialog = false
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem);
                    this.editedIndex = -1;
                    this.message = '';
                    this.iconFile=null;
                    this.bannerFile=null;
                    this.$v.$reset();
                }, 300)
            },
            save () {
                this.$v.$touch();
                this.message = '';
                this.deleteMessage = '';
                if (this.$v.$invalid) {
                    this.message = this.$t('fillOutMessage')
                } else {
                    if (this.editedIndex > -1) {
                        this.update()
                    } else {
                        this.createNew()
                    }
                    this.close()
                }
            },
            async update() {
                let requestOk = await this.updateEventType(this.editedItem);
                if(requestOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            async createNew() {
                let requestOk = await this.createEventType(this.editedItem);
                if(!requestOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            handleIconUpload(){
                console.log("Icon changed");
                if (!this.iconFile.name)
                    return;
                this.editedItem.iconUrl = this.iconFile.name;
                this.$v.editedItem.iconUrl.$touch();
            },
            handleBannerUpload(){
                console.log("Banner changed");
                if (!this.bannerFile.name)
                    return;
                this.editedItem.bannerUrl = this.bannerFile.name;
                this.$v.editedItem.bannerUrl.$touch();
            },
        },
    }
</script>

<style scoped>

</style>