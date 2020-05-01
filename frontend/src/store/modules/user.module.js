import UserService from "../../services/users.service";

const state =  {
    allUsers: [],
    selectedUser: null,
    selectedUserProfile: null,
    obtainErrorCode: 0,
    obtainError: '',
};

const getters = {
    allUsers: (state) => {
        return state.allUsers
    },

    selectedUser: (state) => {
        return state.selectedUser;
    },

    obtainErrorCode(state){
        return state.obtainErrorCode;
    },

    obtainError(state){
        return state.obtainError;
    },
    selectedUserProfile(state) {
        return state.selectedUserProfile;
    }
};

const actions = {
    async getUsers({commit}) {
        try {
            console.log("Get all users");
            const users = await UserService.getUsers();
            commit('usersObtained', users);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getUser({commit}, username) {
        try {
            const user = await UserService.getUser(username);
            commit('userObtained', user);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async getUserProfile({commit}, id) {
        try {
            const userProfile = await UserService.getUserProfile(id);
            commit('userProfileObtained', userProfile);
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async deleteUser({commit, dispatch}, username) {
        try {
            const response = await UserService.deleteUser(username);
            console.log("User deleted: " + response);
            await dispatch('getUsers');
            console.log("All users updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async createUser({commit, dispatch}, user) {
        try {
            const response = await UserService.createUser(user);
            console.log("User created: " + response);
            await dispatch('getUsers');
            console.log("All users updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async updateUser({commit, dispatch}, user) {
        try {
            const response = await UserService.updateUser(user);
            console.log("User updated: " + response);
            await dispatch('getUsers');
            console.log("All users updated");
            return true;
        } catch (e) {
            commit('obtainError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },
}

const mutations = {
    usersObtained(state, users) {
        state.allUsers = users;
    },

    userProfileObtained(state, userProfile) {
        state.selectedUserProfile = userProfile;
    },

    userObtained(state, user) {
        state.selectedUser = user;
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