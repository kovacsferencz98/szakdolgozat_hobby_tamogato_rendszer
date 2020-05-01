import EventService from "../../services/event.service";
import router from "../../router";


const state =  {
    allEvents: [],
    selectedEvent: null,
    selectedEventDetail:null,
    activeEvents: [],
    ownEvents:[],
    participateEvents:[],
    obtainEventErrorCode: 0,
    obtainEventError: '',
};

const getters = {
    allEvents: (state) => {
        return state.allEvents
    },

    selectedEvent: (state) => {
        return state.selectedEvent;
    },

    selectedEventDetail: (state) => {
        return state.selectedEventDetail;
    },

    obtainEventErrorCode(state){
        return state.obtainErrorCode;
    },

    ownEvents: (state) => {
        return state.ownEvents;
    },

    participateEvents: (state) => {
        return state.participateEvents;
    },

    activeEvents: (state) => {
        return state.activeEvents;
    },

    obtainEventError(state){
        return state.obtainError;
    }
};

const actions = {
    async getEvents({commit}) {
        try {
            console.log("Get all events");
            const events = await EventService.getEvents();
            commit('eventsObtained', events);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getActiveEvents({commit}) {
        try {
            console.log("Get all active events");
            const events = await EventService.getActiveEvents();
            commit('activeEventsObtained', events);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getOwnEvents({commit}) {
        try {
            console.log("Get own events");
            const events = await EventService.getOwnEvents();
            commit('ownEventsObtained', events);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getParticipateEvents({commit}) {
        try {
            console.log("Get participate events");
            const events = await EventService.getParticipateEvents();
            commit('participateEventsObtained', events);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },


    async getEvent({commit}, id) {
        try {
            const event = await EventService.getEvent(id);
            commit('eventObtained', event);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getEventDetail({commit}, {id}) {
        try {
            console.log("Get event detail: " + id);
            const eventDetail = await EventService.getEventDetail(id);
            commit('eventDetailObtained', eventDetail);
            return true;
        } catch (e) {
            if(e.errorCode === 404) {
                await router.push({path:"/404"});
            } else {
                commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
                return false;
            }
        }
    },

    async deleteEvent({commit, dispatch}, id) {
        try {
            const response = await EventService.deleteEvent(id);
            console.log("Event deleted: " + response);
            await dispatch('getEvents');
            console.log("All events updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async createEvent({commit, dispatch}, {event, location}) {
        try {
            const response = await EventService.createEvent(event, location);
            console.log("Event created: " + response);
            await dispatch('getEvents');
            console.log("All events updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async updateEvent({commit, dispatch}, event) {
        try {
            const response = await EventService.updateEvent(event);
            console.log("Event updated: " + response);
            await dispatch('getEvents');
            console.log("All events updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async rateEvent({commit, dispatch, state }, {eventId, rating}) {
        try {
            const response = await EventService.rateEvent(eventId, rating);
            console.log("Event rated: " + response);
            await dispatch('getEventDetail', {id : state.selectedEventDetail.eventDetails.id});
            console.log("Event Detail obtained");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async rateEventParticipant({commit, dispatch, state }, {eventParticipantId, rating}) {
        try {
            const response = await EventService.rateParticipant(eventParticipantId, rating);
            console.log("EventParticipant rated: " + response);
            await dispatch('getEventDetail', {id : state.selectedEventDetail.eventDetails.id});
            console.log("Event Detail obtained");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async joinEvent({commit, dispatch, state }, eventId) {
        try {
            const response = await EventService.joinEvent(eventId);
            console.log("Event joined: " + response);
            await dispatch('getEventDetail', {id : state.selectedEventDetail.eventDetails.id});
            console.log("Event Detail obtained");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async leaveEvent({commit, dispatch, state }, eventId) {
        try {
            const response = await EventService.leaveEvent(eventId);
            console.log("Event left: " + response);
            await dispatch('getEventDetail', {id : state.selectedEventDetail.eventDetails.id});
            console.log("Event Detail obtained");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async approveEventParticipant({commit, dispatch, state }, eventParticipantId) {
        try {
            const response = await EventService.approveParticipant(eventParticipantId);
            console.log("EventParticipant approved: " + response);
            console.log("Get event detail: " + state.selectedEventDetail.eventDetails.id);
            await dispatch('getEventDetail', {id : state.selectedEventDetail.eventDetails.id});
            console.log("Event Detail obtained");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async  deleteEventParticipant({commit, dispatch, state }, eventParticipantId) {
        try {
            const response = await EventService.deleteParticipant(eventParticipantId);
            console.log("EventParticipant deleted: " + response);
            await dispatch('getEventDetail', {id : state.selectedEventDetail.eventDetails.id});
            console.log("Event Detail obtained");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },
}

const mutations = {
    eventsObtained(state, events) {
        state.allEvents = events;
    },

    activeEventsObtained(state, events) {
        state.activeEvents = events;
    },

    ownEventsObtained(state, events) {
        state.ownEvents = events;
    },

    participateEventsObtained(state, events) {
        state.participateEvents = events;
    },

    eventObtained(state, event) {
        state.selectedEvent = event;
    },

    eventDetailObtained(state, eventDetail) {
        state.selectedEventDetail = eventDetail;
    },

    obtainError(state,  {errorCode, errorMessage}) {
        state.obtainError = errorMessage;
        state.obtainErrorCode = errorCode;
    }
};

export default {
    namespaced: true,
    getters,
    state,
    actions,
    mutations
}