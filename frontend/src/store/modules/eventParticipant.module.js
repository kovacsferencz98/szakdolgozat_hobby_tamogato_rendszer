import EventParticipantService from "../../services/eventPartcipant.service";


const state =  {
    allEventParticipants: [],
    selectedEventParticipant: null,
    obtainErrorCode: 0,
    obtainError: '',
};

const getters = {
    allEventParticipants: (state) => {
        return state.allEventParticipants
    },

    selectedEventParticipant: (state) => {
        return state.selectedEventParticipant;
    },

    obtainErrorCode(state){
        return state.obtainErrorCode;
    },

    obtainError(state){
        return state.obtainError;
    }
};

const actions = {
    async getEventParticipants({commit}) {
        try {
            console.log("Get all eventParticipants");
            const eventParticipants = await EventParticipantService.getEventParticipants();
            commit('eventParticipantsObtained', eventParticipants);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getEventParticipant({commit}, id) {
        try {
            const eventParticipant = await EventParticipantService.getEventParticipant(id);
            commit('eventParticipantsObtained', eventParticipant);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async deleteEventParticipant({commit, dispatch}, id) {
        try {
            const response = await EventParticipantService.deleteEventParticipant(id);
            console.log("EventParticipant deleted: " + response);
            await dispatch('getEventParticipants');
            console.log("All eventParticipants updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async createEventParticipant({commit, dispatch}, eventParticipant) {
        try {
            const response = await EventParticipantService.createEventParticipant(eventParticipant);
            console.log("EventParticipant created: " + response);
            await dispatch('getEventParticipants');
            console.log("All eventParticipants updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async updateEventParticipant({commit, dispatch}, eventParticipant) {
        try {
            const response = await EventParticipantService.updateEventParticipant(eventParticipant);
            console.log("EventParticipant updated: " + response);
            await dispatch('getEventParticipants');
            console.log("All eventParticipants updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },
}

const mutations = {
    eventParticipantsObtained(state, eventParticipants) {
        state.allEventParticipants = eventParticipants;
    },

    eventParticipantObtained(state, eventParticipant) {
        state.selectedEventParticipant = eventParticipant;
    },

    obtainError(state,  {errorCode, errorMessage}) {
        state.obtainError = errorMessage;
        state.obtainErrorCode = errorCode;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}