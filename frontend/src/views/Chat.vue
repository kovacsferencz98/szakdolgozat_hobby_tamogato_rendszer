<template>
    <v-container
            fill-height
            fluid
            grid-list-xl>
        <v-layout
                wrap
        >
            <v-flex
                    xs12
            >
                <material-card
                        color="#346C47"
                        :title=" $t('chat.title') + selectedEventDetail.eventDetails.name "
                        :text="$t('chat.text')"
                >
                    <v-container
                            fill-height
                            fluid
                            grid-list-xl
                    >

                        <v-layout
                                wrap
                        >
                            <v-flex
                                    xs12
                            >
                                <v-alert
                                        v-if="errorMessage"
                                        prominent
                                        type="error"
                                >
                                    {{errorMessage}}
                                </v-alert>
                            </v-flex>
                            <v-flex
                                    xs12
                            >
                                <v-snackbar
                                        v-for="(alert) in alerts"
                                        :key="alert.message"
                                        :color="alert.color"
                                        v-model="alert.show"
                                >
                                    {{alert.message}}
                                    <v-btn
                                            dark
                                            text
                                            @click="removeAlert(alert)"
                                    >
                                        Close
                                    </v-btn>
                                </v-snackbar>
                            </v-flex>
                            <v-flex
                                    xs12
                            >
                                <v-list
                                        max-height="50vh"
                                        class="overflow-y-auto"
                                        three-line
                                        v-chat-scroll="{always: false, smooth: true, scrollonremoved:true}"
                                >
                                    <template v-for="(message, index) in messages">
                                        <v-list-item
                                                :key="index"
                                                v-if="isUserTheSender(message)"
                                        >

                                            <v-list-item-content>
                                                <v-list-item-title class="text-end" >{{message.sender}}</v-list-item-title>
                                                <v-list-item-subtitle class="text-end">{{message.content}}</v-list-item-subtitle>
                                            </v-list-item-content>

                                            <v-list-item-avatar
                                                    right
                                            >
                                                <v-img  :src="pictureMap.get(message.sender) ? 'http://localhost:8080/api/downloadPicture/'+ pictureMap.get(message.sender) : require('../assets/avatar.png')"></v-img>
                                            </v-list-item-avatar>
                                        </v-list-item>
                                        <v-list-item
                                                :key="index"
                                                v-else
                                        >

                                            <v-list-item-avatar
                                                    left
                                            >
                                                <v-img  :src="pictureMap.get(message.sender) ? 'http://localhost:8080/api/downloadPicture/'+ pictureMap.get(message.sender) : require('../assets/avatar.png')"></v-img>
                                            </v-list-item-avatar>

                                            <v-list-item-content>
                                                <v-list-item-title>{{message.sender}}</v-list-item-title>
                                                <v-list-item-subtitle>{{message.content}}</v-list-item-subtitle>
                                            </v-list-item-content>

                                        </v-list-item>
                                    </template>
                                </v-list>
                            </v-flex>
                            <v-flex
                                    xs12
                            >
                                <v-text-field
                                        :label="$t('chat.messageLabel')"
                                        class="purple-input"
                                        v-model="messageText"
                                        type="text"
                                        required
                                        append-icon="mdi-send"
                                        @click:append="handleSendMessage()"
                                        @keyup.enter="handleSendMessage()"
                                />
                            </v-flex>
                        </v-layout>
                    </v-container>
                </material-card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    import SockJS from "sockjs-client";
    import Stomp from "webstomp-client";
    import {mapActions, mapGetters} from "vuex";
    import TokenService from "../services/storage.service";

    export default {
        name: "Chat",
        data: () => ({
            errorMessage: '',
            messages: [],
            messageText: "",
            connected: false,
            roomId: null,
            alerts: [],
            socket: null,
            stompClient: null,
            pictureMap: new Map()

        }),
        computed: {
            ...mapGetters('auth', [
                'loggedIn',
                'currentAccount'
            ]),

            ...mapGetters('event', [
                'selectedEventDetail'
            ]),

            ...mapGetters('user', [
                'selectedUserProfile',
            ]),


        },
        methods: {

            ...mapActions('event', [
                'getEventDetail',
                'obtainEventError'
            ]),

            ...mapActions('user', [
                'getUserProfile',
                'obtainError'
            ]),

            send() {
                console.log("Send message:" + this.messageText);
                if (this.stompClient && this.stompClient.connected) {
                    const msg = {
                        content: this.messageText,
                        sender: this.currentAccount.username,
                        type: "CHAT"
                    };
                    console.log(JSON.stringify(msg));
                    this.stompClient.send("/app/sendMessage/"+this.roomId, JSON.stringify(msg), {});
                }
            },
            connect() {
                this.socket = new SockJS("http://localhost:9090/ws");
                this.stompClient = Stomp.over(this.socket);
                console.log("Start to connect");
                this.stompClient.connect(
                    {"X-Authorization": `Bearer ${TokenService.getToken()}`},
                    frame => {
                        this.connected = true;
                        console.log(frame);
                        this.stompClient.subscribe("/user/queue/errors",function(message) {
                            alert("Error " + message.body);
                        });
                        this.stompClient.subscribe("/app/oldermessages/"+this.roomId, tick => {
                            console.log("Older messages: " + tick);
                            var oldMessages =  JSON.parse(tick.body);
                            if(!!oldMessages && Array.isArray(oldMessages) && oldMessages.length) {
                                oldMessages.forEach(message => this.messages.push(message));
                            }
                        });

                        this.stompClient.subscribe("/chat/"+this.roomId, message => this.handleMessage(message));
                    },
                    error => {
                        console.log("Connect error: " + error);
                        this.connected = false;
                    }
                );
            },
            disconnect() {
                if (this.stompClient) {
                    this.stompClient.disconnect();
                }
                this.connected = false;
            },
            tickleConnection() {
                this.connected ? this.disconnect() : this.connect();
            },
            handleMessage(payload) {
                console.log("Handle message: " + payload);
                var message = JSON.parse(payload.body);

                if(message.type === 'JOIN') {
                    message.content = message.sender + ' joined!';
                    this.alerts.push({
                        message : message.content,
                        color : "success",
                        show: true
                    });
                } else if (message.type === 'LEAVE') {
                    message.content = message.sender + ' left!';
                    this.alerts.push({
                        message : message.content,
                        color : "error",
                        show: true
                    });
                } else {
                    console.log("New chat message: " + message);
                    this.messages.push(message);
                }
            },
            removeAlert(alert) {
                for (var i = 0, l = this.alerts.length; i < l; i++) {
                    if(this.alerts[i].content === alert.content) {
                        this.alerts.splice(i, 1);
                    }
                }
            },

            handleSendMessage() {
                if(this.messageText) {
                    this.send();
                    this.messageText = "";
                }
            },
            async initialize () {
                let eventOk = await this.getEventDetail({id:this.roomId});
                if(!eventOk) {
                    this.errorMessage = this.obtainError
                }
            },
            lastMessage(index) {
                return index === this.messages.length - 1 ;
            },
            isUserTheSender(message) {
                return message.sender === this.currentAccount.username;
            },
            findPictureIdForUser(username) {
                for(var i = 0; i < this.selectedEventDetail.participants.length; i++) {
                    if(this.selectedEventDetail.participants[i].userUsername === username)
                        return this.selectedEventDetail.profilePicId
                }
            },
            async initialiazeParticipantPictures() {
                for(var i = 0; i < this.selectedEventDetail.participants.length; i++) {
                    let profileOk = await this.getUserProfile(this.selectedEventDetail.participants[i].userId);
                    if(profileOk)
                        this.pictureMap.set(this.selectedEventDetail.participants[i].userUsername, this.selectedUserProfile.userDetails.profilePicId);
                }
            },
            async initializeOwnerPicture() {
                let profileOk = await this.getUserProfile(this.selectedEventDetail.eventDetails.createdById);
                if(profileOk)
                    this.pictureMap.set(this.selectedEventDetail.eventDetails.createdByUsername, this.selectedUserProfile.userDetails.profilePicId);
            }
        },
        created() {
            this.roomId = this.$route.params.id;
        },
        async mounted() {
            await this.initialize();
            this.connect();
            this.initialiazeParticipantPictures();
            this.initializeOwnerPicture();
        }
    }
</script>

<style scoped>

</style>