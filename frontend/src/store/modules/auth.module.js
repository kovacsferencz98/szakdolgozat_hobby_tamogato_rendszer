import { AccountService, AuthenticationError } from '../../services/account.service'
import TokenService, {AccountStorageService} from '../../services/storage.service'
import router from '../../router'
import Account  from "../../models/account";


const state =  {
    authenticating: false,
    accessToken: TokenService.getToken(),
    authenticationSuccess: false,
    authenticationErrorCode: 0,
    authenticationError: '',
    currentAccount: AccountStorageService.getAccount(),
    accountUpdateErrorCode: 0,
    accountUpdateError: '',
};

const getters = {
    loggedIn: (state) => {
        return state.accessToken ? true : false
    },

    authenticationErrorCode: (state) => {
        return state.authenticationErrorCode;
    },

    authenticationError(state){
        return state.authenticationError;
    },

    authenticationSuccess(state){
        return state.authenticationSuccess;
    },

    authenticating: (state) => {
        return state.authenticating;
    },

    currentAccount: (state) => {
        return state.currentAccount;
    },

    accountUpdateErrorCode: (state) => {
        return state.accountUpdateErrorCode;
    },

    accountUpdateError(state){
        return state.accountUpdateError;
    },
};

const actions = {
    async login({ commit, dispatch }, {username, password}) {
        commit('loginRequest');
        try {
            const token = await AccountService.login(username, password);
            commit('loginSuccess', token);
            await dispatch('getAccount');

            // Redirect the user to the page he first tried to visit or to the home view
            //router.push(router.history.current.query.redirect || '/');
            try {
                await router.push(router.history.current.query.redirect || '/');
            } catch (err) {
                throw new Error(`Problem occurred while redirecting: ${err}.`);
            }
            return true
        } catch (e) {
            console.log("Login store error");
            if (e instanceof AuthenticationError) {
                commit('loginError', {errorCode: e.errorCode, errorMessage: e.message})
            }
            return false
        }
    },

    async getAccount({commit, dispatch}) {
        try {
            const account = await AccountService.getCurrentAccount();
            commit('currentAccountObtained', account);
        } catch (err) {
            console.log("Error while obtaining current account: " + err);
            dispatch('logout');
        }
    },

    logout({ commit }) {
        console.log("Log out");
        AccountService.logout()
        commit('logoutSuccess')
        try {
            router.push('/login')
        } catch (err) {
            throw new Error(`Problem occurred while redirecting: ${err}.`);
        }
    },

    register({ commit }, {user, location, details}) {
        console.log('store reg');
        console.log('store reg details: '  + details);
        return AccountService.register(user, location, details).then(
            response => {
                commit('registerSuccess');
                return Promise.resolve(response.data);
            },
            error => {
                commit('registerFailure');
                return Promise.reject(error);
            }
        );
    },

    async updateAccount({ commit, dispatch }, account) {
        console.log('Store update account');
        try {
            const newAccount = await AccountService.updateCurrentAccount(account);
            commit('currentAccountUpdated', newAccount);
            await dispatch('getAccount');
            return true;
        } catch (e) {
            commit('accountUpdateError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    },

    async changeLanguage({commit, dispatch, state}, key) {
        console.log('Store change language');
        try {
            await AccountService.changeLanguageKey(key);
            if(state.accessToken) {
                await dispatch('getAccount');
            }
            return true;
        } catch (e) {
            commit('accountUpdateError', {errorCode: e.errorCode, errorMessage: e.message});
            return false;
        }
    }
}

const mutations = {
    loginRequest(state) {
        state.authenticating = true;
        state.authenticationError = '';
        state.authenticationErrorCode = 0;
    },

    loginSuccess(state, accessToken) {
        state.accessToken = accessToken;
        state.authenticationSuccess = true;
        state.authenticating = false;
    },

    loginError(state, {errorCode, errorMessage}) {
        state.authenticating = false;
        state.authenticationErrorCode = errorCode;
        state.authenticationError = errorMessage;
        state.currentAccount = null;
    },

    logoutSuccess(state) {
        state.accessToken = '';
        state.currentAccount = null;
    },

    registerSuccess(state) {
        state.authenticationSuccess = false;
    },

    registerFailure(state) {
        state.authenticationSuccess = false;
    },

    currentAccountObtained(state, account) {
        state.currentAccount = Object.assign(new Account(), account);
    },

    currentAccountUpdated(state, account) {
        state.currentAccount = Object.assign(new Account(), account);
    },

    accountUpdateError(state,  {errorCode, errorMessage}) {
        state.accountUpdateError = errorMessage;
        state.accountUpdateErrorCode = errorCode;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}