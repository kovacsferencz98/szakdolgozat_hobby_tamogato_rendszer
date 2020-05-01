import EventTypeService from "../../services/eventType.service";


const state =  {
    allEventTypes: [],
    selectedEventType: null,
    obtainErrorCode: 0,
    obtainError: '',
};

const getters = {
    allEventTypes: (state) => {
        return state.allEventTypes
    },

    selectedEventType: (state) => {
        return state.selectedEventType;
    },

    obtainErrorCode(state){
        return state.obtainErrorCode;
    },

    obtainError(state){
        return state.obtainError;
    }
};

const actions = {
    async getEventTypes({commit}) {
        try {
            console.log("Get all event types");
            const eventTypes = await EventTypeService.getEventTypes();
            commit('eventTypesObtained', eventTypes);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getEventType({commit}, id) {
        try {
            const eventType = await EventTypeService.getEventType(id);
            commit('eventTypesObtained', eventType);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async deleteEventType({commit, dispatch}, id) {
        try {
            const response = await EventTypeService.deleteEventType(id);
            console.log("Event type deleted: " + response);
            await dispatch('getEventTypes');
            console.log("All event types updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async createEventType({commit, dispatch}, eventType) {
        try {
            const response = await EventTypeService.createEventType(eventType);
            console.log("Event type created: " + response);
            await dispatch('getEventTypes');
            console.log("All event types updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async updateEventType({commit, dispatch}, eventType) {
        try {
            const response = await EventTypeService.updateEventType(eventType);
            console.log("Event type updated: " + response);
            await dispatch('getEventTypes');
            console.log("All event types updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },
}

const mutations = {
    eventTypesObtained(state, eventTypes) {
        state.allEventTypes = eventTypes;
    },

    eventTypeObtained(state, eventType) {
        state.selectedEventType = eventType;
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