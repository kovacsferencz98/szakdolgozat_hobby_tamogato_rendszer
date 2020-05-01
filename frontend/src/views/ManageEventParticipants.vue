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
                        :title=" $t('manageEventParticipants.manageEventParticipantsLabel')"
                        :text=" $t('manageEventParticipants.manageEventParticipantsText')"

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
                                :items="allEventParticipants"
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
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-select class="purple-input"
                                                                          :label=" $t('manageEventParticipants.userLabel')"
                                                                          v-model="editedItem.userUsername"
                                                                          :items="usernameList"
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
                                                                <v-select class="purple-input"
                                                                          :label="$t('manageEventParticipants.eventLabel')"
                                                                          v-model="editedItem.eventName"
                                                                          :items="eventList"
                                                                          chips
                                                                          required
                                                                          :error-messages="eventNameErrors"
                                                                          @change="$v.editedItem.eventName.$touch()"
                                                                          @blur="$v.editedItem.eventName.$touch()"
                                                                />
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md-6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEventParticipants.partRatingLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.ratingOfParticipant"
                                                                        type="number"
                                                                        name="ratingOfParticipant"
                                                                        required
                                                                        :error-messages="ratingOfParticipantErrors"
                                                                        @input="$v.editedItem.ratingOfParticipant.$touch()"
                                                                        @blur="$v.editedItem.ratingOfParticipant.$touch()"/>
                                                            </v-flex>
                                                            <v-flex
                                                                    xs12
                                                                    md-6
                                                                    pa-md-4 mx-lg-auto
                                                            >
                                                                <v-text-field
                                                                        :label="$t('manageEventParticipants.eventRatingLabel')"
                                                                        class="purple-input"
                                                                        v-model="editedItem.ratingOfEvent"
                                                                        type="number"
                                                                        name="ratingOfEvent"
                                                                        required
                                                                        :error-messages="ratingOfEventErrors"
                                                                        @input="$v.editedItem.ratingOfEvent.$touch()"
                                                                        @blur="$v.editedItem.ratingOfEvent.$touch()"/>
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
                            <template v-slot:item.approved="{ item }">
                                <v-checkbox v-model="item.approved" @change="approveUser(item)"/>
                            </template>
                            <template v-slot:item.participated="{ item }">
                                <v-checkbox v-model="item.participated" @change="changeParticipated(item)"/>
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
    import {validationMixin} from "vuelidate";
    import {minValue, required, maxValue} from "vuelidate/lib/validators";
    import {mapActions, mapGetters} from "vuex";
    import EventParticipant from "../models/event-participant";

    export default {
        mixins: [validationMixin],
        validations: {
            editedItem : {
                userUsername: {required},
                eventName: {required},
                ratingOfParticipant:{required, minValue: minValue(0.00000000000001), maxValue:maxValue(5)},
                ratingOfEvent:{required, minValue: minValue(0.00000000000001), maxValue:maxValue(5)}
            }
        },

        name: "ManageEventParticipants",
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
                { text: vm.$t('manageEventParticipants.userLabel'), value: 'userUsername' },
                { text:  vm.$t('manageEventParticipants.eventLabel'), value: 'eventName' },
                { text:  vm.$t('manageEventParticipants.partRatingLabel'), value: 'ratingOfParticipant' },
                { text:  vm.$t('manageEventParticipants.eventRatingLabel'), value: 'ratingOfEvent' },
                { text:  vm.$t('manageEventParticipants.approvedLabel'), value: 'approved' },
                { text:  vm.$t('manageEventParticipants.joinedAtLabel'), value: 'joinedAt' },
                { text: vm.$t('manageEventParticipants.actionsLabel'), value: 'action', sortable: false },
            ],
            editedIndex: -1,
            editedItem: new EventParticipant(),
            defaultItem: new EventParticipant(),
        }),
        computed: {
            ...mapGetters('eventParticipant', [
                'allEventParticipants',
                'obtainErrorCode',
                'obtainError'
            ]),

            ...mapGetters('user', [
                'allUsers'
            ]),

            ...mapGetters('event', [
                'allEvents'
            ]),

            formTitle () {
                return this.editedIndex === -1 ? this.$t('newItemLabel') : this.$t('editItemLabel')
            },

            usernameList() {
                return this.allUsers.map(user => user.username);
            },

            eventList() {
                return this.allEvents.map(event => event.name);
            },

            ratingOfParticipantErrors () {
                const errors = [];
                if (!this.$v.editedItem.ratingOfParticipant.$dirty) return errors;
                !this.$v.editedItem.ratingOfParticipant.minValue && errors.push(this.$t("minValueError", [this.$t('manageEventParticipants.partRatingLabel'), '0']));
                !this.$v.editedItem.ratingOfParticipant.maxValue && errors.push(this.$t("maxValueError", [this.$t('manageEventParticipants.partRatingLabel'), '5']));
                !this.$v.editedItem.ratingOfParticipant.required && errors.push(this.$t("requiredError", [this.$t('manageEventParticipants.partRatingLabel')]));
                return errors
            },
            ratingOfEventErrors () {
                const errors = [];
                if (!this.$v.editedItem.ratingOfEvent.$dirty) return errors;
                !this.$v.editedItem.ratingOfEvent.minValue && errors.push(this.$t("minValueError", [this.$t('manageEventParticipants.eventRatingLabel'), '0']));
                !this.$v.editedItem.ratingOfEvent.maxValue &&  errors.push(this.$t("maxValueError", [this.$t('manageEventParticipants.eventRatingLabel'), '5']));
                !this.$v.editedItem.ratingOfEvent.required && errors.push(this.$t("requiredError", [this.$t('manageEventParticipants.eventRatingLabel')]));
                return errors
            },
            eventNameErrors () {
                const errors = [];
                if (!this.$v.editedItem.eventName.$dirty) return errors;
                !this.$v.editedItem.eventName.required &&  errors.push(this.$t("requiredError", [this.$t('manageEventParticipants.eventLabel')]));
                return errors;
            },
            userUsernameErrors () {
                const errors = [];
                if (!this.$v.editedItem.userUsername.$dirty) return errors;
                !this.$v.editedItem.userUsername.required && errors.push(this.$t("requiredError", [this.$t('manageEventParticipants.userLabel')]));
                return errors;
            }
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
            ...mapActions('eventParticipant', [
                'getEventParticipants',
                'deleteEventParticipant',
                'createEventParticipant',
                'updateEventParticipant'
            ]),

            ...mapActions('user', [
                'getUsers'
            ]),

            ...mapActions('event', [
                'getEvents'
            ]),

            initialize () {
                this.getEventParticipants();

                if(!this.allUsers || !this.allUsers.length) {
                    this.getUsers();
                }


                if(!this.allEvents || !this.allEvents.length) {
                    this.getEvents();
                }
            },
            editItem (item) {
                console.log('Edit ' + this.allEventParticipants.indexOf(item));
                this.editedIndex = this.allEventParticipants.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.dialog = true
            },
            async deleteItem (item) {
                this.deleteMessage = '';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await  this.deleteEventParticipant(item.id);
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
                if (this.$v.$invalid) {
                    this.message = this.$t("fillOutMessage");
                } else {
                    this.editedItem.userId=this.userIdOfUsername(this.editedItem.userUsername);
                    this.editedItem.eventId=this.eventIdOfName(this.editedItem.eventName);
                    if (this.editedIndex > -1) {
                        this.update();
                    } else {
                        this.createNew();
                    }
                    this.close()
                }
            },
            async update() {
                let requestOk = await this.updateEventParticipant(this.editedItem);
                if(requestOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            async createNew() {
                let requestOk = await this.createEventParticipant(this.editedItem);
                if(!requestOk) {
                    this.message = this.obtainError;
                } else {
                    this.close()
                }
            },
            approveUser(item){
                const index = this.allEventParticipants.indexOf(item);
                console.log("Approve participant: " + this.allEventParticipants[index].userUsername);
                this.updateEventParticipant(item);
            },
            changeParticipated(item){
                const index = this.allEventParticipants.indexOf(item);
                console.log("Change participated: " + this.allEventParticipants[index].userUsername);
                this.updateEventParticipant(item);
            },


            userIdOfUsername(username) {
                for(let idx in this.allUsers) {
                    if(username === this.allUsers[idx].username)
                        return this.allUsers[idx].id;
                }
                return null;
            },
            eventIdOfName(name) {
                for(let idx in this.allEvents) {
                    if(name === this.allEvents[idx].name)
                        return this.allEvents[idx].id;
                }
                return null;
            }
        },
    }
</script>

<style scoped>

</style>