import UserDetailsService from "../../services/userDetails.service";


const state =  {
    allUserDetails: [],
    selectedUserDetail: null,
    obtainErrorCode: 0,
    obtainError: '',
};

const getters = {
    allUserDetails: (state) => {
        return state.allUserDetails
    },

    selectedUserDetail: (state) => {
        return state.selectedUserDetail;
    },

    obtainErrorCode(state){
        return state.obtainErrorCode;
    },

    obtainError(state){
        return state.obtainError;
    }
};

const actions = {
    async getUserDetails({commit}) {
        try {
            console.log("Get all user details");
            const userDetails = await UserDetailsService.getUserDetails();
            commit('userDetailsObtained', userDetails);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getUserDetail({commit}, id) {
        try {
            const userDetail = await UserDetailsService.getUserDetail(id);
            commit('userDetailsObtained', userDetail);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async deleteUserDetail({commit, dispatch}, id) {
        try {
            const response = await UserDetailsService.deleteUserDetail(id);
            console.log("User detail deleted: " + response);
            await dispatch('getUserDetails');
            console.log("All user details updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async createUserDetail({commit, dispatch}, {userDetail, location}) {
        try {
            const response = await UserDetailsService.createUserDetail(userDetail, location);
            console.log("User detail created: " + response);
            await dispatch('getUserDetails');
            console.log("All user details updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async updateUserDetail({commit, dispatch}, userDetail) {
        try {
            const response = await UserDetailsService.updateUserDetail(userDetail);
            console.log("User detail updated: " + response);
            await dispatch('getUserDetails');
            console.log("All user details updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },
}

const mutations = {
    userDetailsObtained(state, userDetails) {
        state.allUserDetails = userDetails;
    },

    userDetailObtained(state, userDetail) {
        state.selectedUserDetail = userDetail;
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