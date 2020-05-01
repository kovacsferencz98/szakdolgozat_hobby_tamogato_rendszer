import LocationService from "../../services/location.service";


const state =  {
    allLocations: [],
    selectedLocation: null,
    locationObtainErrorCode: 0,
    locationObtainError: '',
};

const getters = {
    allLocations: (state) => {
        return state.allLocations
    },

    selectedLocation: (state) => {
        return state.selectedLocation;
    },

    locationObtainErrorCode(state){
        return state.locationObtainErrorCode;
    },

    locationObtainError(state){
        return state.locationObtainError;
    }
};

const actions = {
    async getLocations({commit}) {
        try {
            console.log("Get all locations");
            const locations = await LocationService.getLocations();
            commit('locationsObtained', locations);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getLocation({commit}, id) {
        try {
            const location = await LocationService.getLocation(id);
            commit('locationObtained', location);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async deleteLocation({commit, dispatch}, id) {
        try {
            const response = await LocationService.deleteLocation(id);
            console.log("Location deleted: " + response);
            await dispatch('getLocations');
            console.log("All locations updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async createLocation({commit, dispatch}, location) {
        try {
            const response = await LocationService.createLocation(location);
            console.log("Location created: " + response);
            await dispatch('getLocations');
            console.log("All locations updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async updateLocation({commit, dispatch}, location) {
        try {
            const response = await LocationService.updateLocation(location);
            console.log("Location updated: " + response);
            await dispatch('getLocations');
            console.log("All locations updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },
}

const mutations = {
    locationsObtained(state, locations) {
        state.allLocations = locations;
    },

    locationObtained(state, location) {
        state.selectedLocation = location;
    },

    obtainError(state,  {errorCode, errorMessage}) {
        state.locationObtainError = errorMessage;
        state.locationObtainErrorCode = errorCode;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}