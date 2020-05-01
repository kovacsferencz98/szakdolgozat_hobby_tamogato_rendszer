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
                        :title="$t('userEvents.userEventsLabel')"
                        :text="$t('userEvents.userEventsText')"
                >
                    <v-container
                            fill-height
                            fluid
                            grid-list-xl>
                        <v-layout
                                wrap
                        >
                            <v-flex xs12>
                                <v-container fill-width>
                                    <v-data-table
                                            :headers="ownEventHeaders"
                                            :items="ownEvents"
                                            sort-by="id"
                                            :search="searchOwnEvents"
                                            class="elevation-1"
                                            hide-default-footer
                                            :page.sync="ownEventsPage"
                                            :items-per-page="ownEventsItemsPerPage"
                                            @page-count="ownEventsPageCount = $event"
                                    >
                                        <template v-slot:top>
                                            <v-toolbar flat >
                                                <v-toolbar-title>{{$t('userEvents.ownEvents')}}</v-toolbar-title>
                                                <v-spacer/>
                                                <v-text-field
                                                        v-model="searchOwnEvents"
                                                        append-icon="mdi-magnify"
                                                       :label="$t('searchLabel')"
                                                        single-line
                                                        hide-details
                                                />
                                                <v-spacer/>
                                                <v-dialog v-model="dialog" max-width="50%">
                                                    <template v-slot:activator="{ on }">
                                                        <v-btn color="primary" dark class="mb-2" v-on="on">{{$t('userEvents.addEvent')}}</v-btn>
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
                                                                                md6
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.eventNameLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedItem.name"
                                                                                    type="text"
                                                                                    name="eventName"
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
                                                                            <v-select class="purple-input"
                                                                                      :label="$t('manageEvents.typeLabel')"
                                                                                      v-model="editedItem.typeName"
                                                                                      :items="eventTypeList"
                                                                                      chips
                                                                                      required
                                                                                      :error-messages="typeNameErrors"
                                                                                      @change="$v.editedItem.typeName.$touch()"
                                                                                      @blur="$v.editedItem.typeName.$touch()"
                                                                            />
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md-6
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.maxAttendanceLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedItem.maxAttendance"
                                                                                    type="number"
                                                                                    name="maxAttendance"
                                                                                    required
                                                                                    :error-messages="maxAttendanceErrors"
                                                                                    @input="$v.editedItem.maxAttendance.$touch()"
                                                                                    @blur="$v.editedItem.maxAttendance.$touch()"/>
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md-6
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.minAttendanceLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedItem.minAttendance"
                                                                                    type="number"
                                                                                    name="minAttendance"
                                                                                    required
                                                                                    :error-messages="minAttendanceErrors"
                                                                                    @input="$v.editedItem.minAttendance.$touch()"
                                                                                    @blur="$v.editedItem.minAttendance.$touch()"/>
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md-6
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    v-model="computedStartDateFormatted"
                                                                                    :label="$t('manageEvents.startLabel')"
                                                                                    hint="MM/DD/YYYY HH:mm format"
                                                                                    persistent-hint
                                                                                    readonly
                                                                                    :error-messages="startsAtErrors"
                                                                                    @input="checkStartDateTime()"
                                                                                    @blur="checkStartDateTime()"
                                                                                    @click="showDateDialog = true"
                                                                            />
                                                                            <v-dialog
                                                                                    ref="dateDialog"
                                                                                    v-model="showDateDialog"
                                                                                    :return-value.sync="startDate"
                                                                                    persistent
                                                                                    width="290px"
                                                                            >
                                                                                <v-date-picker dark v-model="startDate" :show-current="new Date().toISOString().substr(0, 10)" >
                                                                                    <v-spacer></v-spacer>
                                                                                    <v-btn text color="primary" @click="showDateDialog = false">{{$t('cancelLabel')}}</v-btn>
                                                                                    <v-btn text color="primary" @click="showTimePicker()">{{$t('okLabel')}}</v-btn>
                                                                                </v-date-picker>
                                                                            </v-dialog>
                                                                            <v-dialog
                                                                                    ref="timeDialog"
                                                                                    v-model="showTimeDialog"
                                                                                    :return-value.sync="startTime"
                                                                                    persistent
                                                                                    width="290px"
                                                                            >
                                                                                <v-time-picker
                                                                                        dark
                                                                                        v-if="showTimeDialog"
                                                                                        v-model="startTime"
                                                                                        full-width
                                                                                >
                                                                                    <v-spacer></v-spacer>
                                                                                    <v-btn text color="primary" @click="showTimeDialog = false">{{$t('cancelLabel')}}</v-btn>
                                                                                    <v-btn text color="primary" @click="saveTime()">{{$t('okLabel')}}</v-btn>
                                                                                </v-time-picker>
                                                                            </v-dialog>
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md-6
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.priceLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedItem.price"
                                                                                    type="number"
                                                                                    name="price"
                                                                                    required
                                                                                    :error-messages="priceErrors"
                                                                                    @input="$v.editedItem.price.$touch()"
                                                                                    @blur="$v.editedItem.price.$touch()"/>
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-textarea
                                                                                    class="purple-input"
                                                                                    :label="$t('manageEvents.descriptionLabel')"
                                                                                    v-model="editedItem.description"
                                                                                    name="description"
                                                                                    required
                                                                                    :error-messages="descriptionErrors"
                                                                                    @input="$v.editedItem.description.$touch()"
                                                                                    @blur="$v.editedItem.description.$touch()"
                                                                            />
                                                                        </v-flex>

                                                                        <v-flex
                                                                                xs12
                                                                                md12
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.streetLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedLocation.street"
                                                                                    type="text"
                                                                                    name="street"
                                                                                    required
                                                                                    :error-messages="streetErrors"
                                                                                    @input="$v.editedLocation.street.$touch()"
                                                                                    @blur="$v.editedLocation.street.$touch()"/>
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md4
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.numberLabel')"
                                                                                    class="purple-input"
                                                                                    type="number"
                                                                                    v-model="editedLocation.number"
                                                                                    name="number"
                                                                                    required
                                                                                    :error-messages="numberErrors"
                                                                                    @input="$v.editedLocation.number.$touch()"
                                                                                    @blur="$v.editedLocation.number.$touch()"
                                                                            />
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md4
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.apartmentLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedLocation.apartment"
                                                                                    name="apartment"
                                                                                    required
                                                                                    :error-messages="apartmentErrors"
                                                                                    @input="$v.editedLocation.apartment.$touch()"
                                                                                    @blur="$v.editedLocation.apartment.$touch()"
                                                                            />
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md4
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.zipCodeLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedLocation.zip"
                                                                                    name="zip"
                                                                                    required
                                                                                    :error-messages="zipErrors"
                                                                                    @input="$v.editedLocation.zip.$touch()"
                                                                                    @blur="$v.editedLocation.zip.$touch()"
                                                                            />
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md4
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.cityLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedLocation.city"
                                                                                    name="city"
                                                                                    required
                                                                                    :error-messages="cityErrors"
                                                                                    @input="$v.editedLocation.city.$touch()"
                                                                                    @blur="$v.editedLocation.city.$touch()"
                                                                            />
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md4
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    :label="$t('manageEvents.regionLabel')"
                                                                                    class="purple-input"
                                                                                    v-model="editedLocation.region"/>
                                                                        </v-flex>
                                                                        <v-flex
                                                                                xs12
                                                                                md4
                                                                                pa-md-4 mx-lg-auto
                                                                        >
                                                                            <v-text-field
                                                                                    class="purple-input"
                                                                                    :label="$t('manageEvents.countryLabel')"
                                                                                    v-model="editedLocation.country"
                                                                                    name="country"
                                                                                    required
                                                                                    :error-messages="countryErrors"
                                                                                    @input="$v.editedLocation.country.$touch()"
                                                                                    @blur="$v.editedLocation.country.$touch()"
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
                                        <template v-slot:item.name="{ item }">
                                            <v-btn
                                                    text
                                                    color="primary"
                                                    :to="{name:'EventView', params:{id:item.id}}"
                                            >
                                                {{item.name}}
                                            </v-btn>
                                        </template>
                                        <template v-slot:item.currentAttendance="{ item }">
                                            {{item.currentAttendance}} ({{item.minAttendance}} - {{item.maxAttendance}})
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
                                                        v-model="ownEventsPage"
                                                        :length="ownEventsPageCount"
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
                                                        v-model="ownEventsItemsPerPage"
                                                        :items="possibleItemsPerPage"
                                                        :label="$t('rowsPerPageLabel')"
                                                />
                                            </v-flex>
                                        </v-layout>
                                    </v-container>
                                </v-container>
                            </v-flex>

                            <v-flex xs12>
                                <v-container fill-width>
                                    <v-data-table
                                            :headers="participateEventHeaders"
                                            :items="participateEvents"
                                            sort-by="id"
                                            :search="searchParticipateEvents"
                                            class="elevation-1"
                                            hide-default-footer
                                            :page.sync="participateEventsPage"
                                            :items-per-page="participateEventsItemsPerPage"
                                            @page-count="participateEventsPageCount = $event"
                                    >
                                        <template v-slot:top>
                                            <v-toolbar flat >
                                                <v-toolbar-title>{{$t('userEvents.joinedEvents')}}</v-toolbar-title>
                                                <v-spacer/>
                                                <v-text-field
                                                        v-model="searchParticipateEvents"
                                                        append-icon="mdi-magnify"
                                                        label="Search"
                                                        single-line
                                                        hide-details
                                                />
                                            </v-toolbar>
                                        </template>
                                        <template v-slot:item.name="{ item }">
                                            <v-btn
                                                    text
                                                    :to="{name:'EventView', params:{id:item.id}}"
                                                    color="primary"
                                            >
                                                {{item.name}}
                                            </v-btn>
                                        </template>
                                        <template v-slot:item.currentAttendance="{ item }">
                                            {{item.currentAttendance}} ({{item.minAttendance}} - {{item.maxAttendance}})
                                        </template>
                                        <template v-slot:item.createdByUsername="{ item }">
                                            <v-btn
                                                    text
                                                    :to="{name:'UserProfile', params:{id:item.createdById}}"
                                                    color="primary"
                                            >
                                                {{item.createdByUsername}}
                                            </v-btn>
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
                                                        v-model="participateEventsPage"
                                                        :length="participateEventsPageCount"
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
                                                        v-model="participateEventsItemsPerPage"
                                                        :items="possibleItemsPerPage"
                                                        :label="$t('rowsPerPageLabel')"
                                                />
                                            </v-flex>
                                        </v-layout>
                                    </v-container>
                                </v-container>
                            </v-flex>

                        </v-layout>
                    </v-container>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import Event from "../models/event";
    import Location from "../models/location";
    import {validationMixin} from "vuelidate";
    import {maxLength, maxValue, minValue, required} from "vuelidate/lib/validators";
    import {mapActions, mapGetters} from "vuex";
    import gmapsInit from "../plugins/gmaps";

    export default {
        mixins: [validationMixin],
        validations: {
            editedItem : {
                name: {required},
                description: {required},
                maxAttendance:{required, maxValue: maxValue(100)},
                minAttendance:{required, minValue: minValue(0.00000001)},
                price:{required, minValue: minValue(0.00000001)},
                typeName:{required},
            },
            editedLocation : {
                street: {required, maxLength: maxLength(150)},
                number: {required, minValue: minValue(0.00000001)},
                apartment: {required, maxLength: maxLength(15)},
                zip: {required, maxLength: maxLength(15)},
                city: {required, maxLength: maxLength(150)},
                country: {required, maxLength: maxLength(150)},
            },
            startTime : {required},
            startDate: {required}
        },
        name: "UserEvents",
        data: vm => ({
            searchOwnEvents: '',
            searchParticipateEvents: '',
            dialog: false,
            show: false,
            deleteMessage: '',
            message:'',
            google:null,
            geocoder:null,
            ownEventsPage: 1,
            ownEventsPageCount: 0,
            ownEventsItemsPerPage: 10,
            possibleItemsPerPage: [5, 10, 15, 20],
            startDate:  new Date().toISOString().substr(0, 10),
            startDateFormatted: vm.formatDate(new Date().toISOString().substr(0, 10)),
            startTime: '',
            showDateDialog : false,
            showTimeDialog : false,
            ownEventHeaders: [
                {
                    text: 'ID',
                    align: 'left',
                    value: 'id',
                },
                { text: vm.$t('manageEvents.eventNameLabel'), value: 'name' },
                { text: vm.$t('manageEvents.typeLabel'), value: 'typeName' },
                { text: vm.$t('userEvents.attendance'), value: 'currentAttendance' },
                { text: vm.$t('manageEvents.createdAtLabel'), value: 'createdAt' },
                { text: vm.$t('manageEvents.startLabel'), value: 'startsAt' },
                { text: vm.$t('actionsLabel'), value: 'action', sortable: false },
            ],
            participateEventsPage: 1,
            participateEventsPageCount: 0,
            participateEventsItemsPerPage: 10,
            participateEventHeaders: [
                { text: vm.$t('manageEvents.eventNameLabel'), value: 'name' },
                { text: vm.$t('manageEvents.typeLabel'), value: 'typeName' },
                { text: vm.$t('userEvents.attendance'), value: 'currentAttendance' },
                { text: vm.$t('manageEvents.startLabel'), value: 'startsAt' },
                { text: vm.$t('manageEvents.createdByLabel'), value: 'createdByUsername' },
            ],
            editedIndex: -1,
            editedItem: new Event(),
            defaultItem: new Event(),
            editedLocation: new Location(),
            defaultLocation: new Location(),
        }),

        computed: {
            ...mapGetters('event', [
                'allEvents',
                'activeEvents',
                'ownEvents',
                'participateEvents',
                'obtainEventErrorCode',
                'obtainEventError'
            ]),

            ...mapGetters('auth', [
                'currentAccount'
            ]),

            ...mapGetters('location', [
                'allLocations',
                'selectedLocation'
            ]),

            ...mapGetters('eventType', [
                'allEventTypes'
            ]),

            formTitle () {
                return this.editedIndex === -1 ?  this.$t('newItemLabel') : this.$t('editItemLabel')
            },

            computedStartDateFormatted () {
                return this.formatDate(this.startDate) + ' ' + this.startTime;
            },


            usernameList() {
                return this.allUsers.map(user => user.username);
            },

            locationList() {
                return this.allLocations.map(location => location.id + ': ' + location.longitude + '; ' +location.latitude)
            },

            eventTypeList() {
                return this.allEventTypes.map(type => type.name);
            },

            nameErrors () {
                const errors = [];
                if (!this.$v.editedItem.name.$dirty) return errors;
                !this.$v.editedItem.name.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.eventNameLabel')]));
                return errors;
            },
            descriptionErrors () {
                const errors = [];
                if (!this.$v.editedItem.description.$dirty) return errors;
                !this.$v.editedItem.description.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.descriptionLabel')]));
                return errors;
            },
            maxAttendanceErrors () {
                const errors = [];
                if (!this.$v.editedItem.maxAttendance.$dirty) return errors;
                !this.$v.editedItem.maxAttendance.maxValue && errors.push(this.$t("maxValueError", [this.$t('manageEvents.maxAttendanceLabel'), '100']));
                !this.$v.editedItem.maxAttendance.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.maxAttendanceLabel')]));
                return errors
            },
            minAttendanceErrors () {
                const errors = [];
                if (!this.$v.editedItem.minAttendance.$dirty) return errors;
                !this.$v.editedItem.minAttendance.minValue && errors.push(this.$t("minValueError", [this.$t('manageEvents.minAttendanceLabel'), '0']));
                !this.$v.editedItem.minAttendance.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.minAttendanceLabel')]));
                return errors
            },
            startsAtErrors () {
                const errors = [];
                if (!this.$v.startTime.$dirty && !this.$v.startDate.$dirty) return errors;
                !this.$v.startTime.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.startTimeLabel')]));
                !this.$v.startDate.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.startDateLabel')]));
                return errors;
            },
            priceErrors () {
                const errors = [];
                if (!this.$v.editedItem.price.$dirty) return errors;
                !this.$v.editedItem.price.minValue && errors.push(this.$t("minValueError", [this.$t('manageEvents.priceLabel'), '0']));
                !this.$v.editedItem.price.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.priceLabel')]));
                return errors
            },
            typeNameErrors () {
                const errors = [];
                if (!this.$v.editedItem.typeName.$dirty) return errors;
                !this.$v.editedItem.typeName.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.typeLabel')]));
                return errors;
            },

            streetErrors () {
                const errors = [];
                if (!this.$v.editedLocation.street.$dirty) return errors
                !this.$v.editedLocation.street.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.streetLabel'), '150']));
                !this.$v.editedLocation.street.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.streetLabel')]));
                return errors
            },
            numberErrors () {
                const errors = [];
                if (!this.$v.editedLocation.number.$dirty) return errors
                !this.$v.editedLocation.number.minValue && errors.push(this.$t("minValueError", [this.$t('manageEvents.numberLabel'), '0']));
                !this.$v.editedLocation.number.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.numberLabel')]));
                return errors
            },
            apartmentErrors () {
                const errors = [];
                if (!this.$v.editedLocation.apartment.$dirty) return errors
                !this.$v.editedLocation.apartment.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.apartmentLabel'), '15']));
                !this.$v.editedLocation.apartment.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.apartmentLabel')]));
                return errors
            },
            zipErrors () {
                const errors = [];
                if (!this.$v.editedLocation.zip.$dirty) return errors
                !this.$v.editedLocation.zip.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.zipCodeLabel'), '15']));
                !this.$v.editedLocation.zip.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.zipCodeLabel')]));
                return errors
            },
            cityErrors () {
                const errors = [];
                if (!this.$v.editedLocation.city.$dirty) return errors
                !this.$v.editedLocation.city.maxLength &&  errors.push(this.$t("maxLengthError", [this.$t('manageEvents.cityLabel'), '150']));
                !this.$v.editedLocation.city.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.cityLabel')]));
                return errors
            },
            countryErrors () {
                const errors = [];
                if (!this.$v.editedLocation.country.$dirty) return errors
                !this.$v.editedLocation.country.maxLength && errors.push(this.$t("maxLengthError", [this.$t('manageEvents.countryLabel'), '150']));
                !this.$v.editedLocation.country.required && errors.push(this.$t("requiredError", [this.$t('manageEvents.countryLabel')]));
                return errors
            },
        },
        watch: {
            dialog (val) {
                val || this.close()
            },

            // eslint-disable-next-line no-unused-vars
            date (val) {
                this.startDateFormatted = this.formatDate(this.startDate)
            },
        },
        async mounted() {
            try{
                this.google = await gmapsInit();
                this.geocoder = new this.google.maps.Geocoder();
            }  catch (error) {
                // eslint-disable-next-line no-console
                console.error(error);
            }
        },
        created () {
            this.initialize()
        },
        methods: {
            ...mapActions('event', [
                'getOwnEvents',
                'getParticipateEvents',
                'deleteEvent',
                'createEvent',
                'updateEvent',
                'obtainEventError'
            ]),

            ...mapActions('eventType', [
                'getEventTypes'
            ]),

            ...mapActions('location', [
                'getLocations',
                'deleteLocation',
                'createLocation',
                'updateLocation',
                'getLocation',
                'locationObtainErrorCode',
                'locationObtainError'
            ]),

            initialize () {
                this.getOwnEvents();
                this.getParticipateEvents();

                if(!this.allEventTypes || !this.allEventTypes.length) {
                    this.getEventTypes();
                }
            },
            editItem (item) {
                console.log('Edit ' + this.allEvents.indexOf(item));
                this.editedIndex = this.allEvents.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.getLocation(this.editedItem.locationId).then(() => {
                    this.editedLocation = Object.assign({}, this.selectedLocation);

                });
                this.startDate = this.editedItem.startsAt.substr(0, 10);
                this.startTime = this.editedItem.startsAt.substr(11);
                this.dialog = true
            },
            async deleteItem (item) {
                this.deleteMessage = '';
                if(confirm(this.$t('confirmDeleteLabel') )) {
                    let requestOk = await this.deleteEvent(item.id);
                    if(!requestOk) {
                        this.deleteMessage = this.obtainEventError;
                    }
                }
            },
            close () {
                this.dialog = false;
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem);
                    this.editedLocation = Object.assign({}, this.defaultLocation);
                    this.editedIndex = -1;
                    this.message = '';
                    this.$v.$reset();
                    this.startTime = '';
                    this.startDate =  new Date().toISOString().substr(0, 10);
                }, 300)
            },
            createAddress() {
                let addressString = "";
                addressString = addressString + this.editedLocation.country + ", ";
                if (this.editedLocation.region) {
                    addressString = addressString + this.editedLocation.region + ", ";
                }
                addressString = addressString + this.editedLocation.city + ", " + this.editedLocation.street + " " + this.editedLocation.number + ", ap " + this.editedLocation.apartment;
                return addressString;
            },
            async save () {
                this.$v.$touch();
                this.deleteMessage = '';
                this.message = '';
                if (this.$v.$invalid) {
                    this.message = this.$t('fillOutMessage');
                } else {
                    let addressString = this.createAddress();
                    console.log("Geocode: " + addressString);
                    this.geocoder.geocode({address: addressString}, (results, status) => {
                        if (status !== 'OK' || !results[0]) {
                            throw new Error(status);
                        }
                        this.editedItem.latitude = results[0].geometry.location.lat();
                        this.editedItem.longitude = results[0].geometry.location.lng();
                        console.log(results[0].geometry.location.lat() + " " + results[0].geometry.location.lng());

                        this.editedItem.typeId=this.eventTypeIdOfName(this.editedItem.typeName);
                        this.editedItem.createdById = this.currentAccount.userId;
                        this.editedItem.createdByUsername = this.currentAccount.username;
                        this.editedItem.startsAt = this.startDate + ' ' + this.startTime;
                        if (this.editedIndex > -1) {
                            this.update();
                        } else {
                           this.createNew();
                        }
                    });
                }
            },
            async update() {
                let locationOk = await this.updateLocation(this.editedLocation);
                let eventOk = false;
                if(locationOk) {
                   eventOk = await this.updateEvent(this.editedItem);
                }
                if(!locationOk) {
                    this.message = this.locationObtainError;
                } else if (!eventOk) {
                    this.message = this.obtainEventError
                } else {
                    this.close()
                }
            },
            async createNew() {
               let eventOk = await this.createEvent(this.editedItem, this.editedLocation);
                if(!eventOk) {
                    this.message = this.obtainEventError;
                } else {
                    this.close()
                }
            },

            eventTypeIdOfName(name) {
                for(let idx in this.allEventTypes) {
                    if(name === this.allEventTypes[idx].name)
                        return this.allEventTypes[idx].id;
                }
                return null;
            },

            formatDate (date) {
                if (!date) return null

                const [year, month, day] = date.split('-')
                return `${month}/${day}/${year}`
            },
            parseDate (date) {
                if (!date) return null

                const [month, day, year] = date.split('/')
                return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`
            },
            showTimePicker() {
                this.$refs.dateDialog.save(this.startDate);
                this.showDateDialog = false;
                this.showTimeDialog = true;
            },
            checkStartDateTime() {
                this.$v.startTime.$touch();
                this.$v.startDate.$touch();
            },
            saveTime() {
                this.$refs.timeDialog.save(this.startTime);
                this.showTimeDialog = false;
            }
        }
    }
</script>

<style scoped>

</style>