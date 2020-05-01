import ApiService from './api.service'
import TokenService, {AccountStorageService} from './storage.service'
import {ApiRequestError} from "./eventPartcipant.service";


class AuthenticationError extends Error {
    constructor(errorCode, message) {
        super(message)
        this.name = this.constructor.name
        this.message = message
        this.errorCode = errorCode
    }
}

const AccountService = {
    /**
     * Login the user and store the access token to TokenService.
     *
     * @returns access_token
     * @throws AuthenticationError
     **/
    login: async function(username, password) {
        const requestData = {
            method: 'post',
            url: "/authenticate/",
            data: {
                username: username,
                password: password
            }/*,
            auth: {
                username: process.env.VUE_APP_CLIENT_ID,
                password: process.env.VUE_APP_CLIENT_SECRET
            }*/
        }

        try {
            console.log("Login user service");
            const response = await ApiService.customRequest(requestData)
            console.log(response);
            // response.data.access_token = response.data.refresh_token = response.data.token //remove after testing
            TokenService.saveToken(response.data.id_token)
            TokenService.saveRefreshToken(response.data.refresh_token)
            ApiService.setHeader()

            // NOTE: We haven't covered this yet in our ApiService
            //       but don't worry about this just yet - I'll come back to it later
            //ApiService.mount401Interceptor();

            return response.data.id_token
        } catch (error) {
            console.log("Login error");
            throw new AuthenticationError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    register: async function(user, location, details) {
        console.log('user service reg');
        const requestData = {
            method: 'post',
            url: "/register/",
            data: {
                username:  user.username,
                email: user.email,
                password:  user.password,
                firstName: user.firstName,
                lastName: user.lastName,
                description: details.description,
                profilePicId: details.profilePicId,
                country: location.country,
                region: location.region,
                city: location.city,
                street: location.street,
                number: location.number,
                apartment: location.apartment,
                zip: location.zip,
                latitude: location.latitude,
                longitude: location.longitude
            }/*,
            auth: {
                username: process.env.VUE_APP_CLIENT_ID,
                password: process.env.VUE_APP_CLIENT_SECRET
            }*/
        };
        try {
            const response = await ApiService.customRequest(requestData);
            console.log("Reg response: " + response);
            return response;
        } catch (error) {
            console.log("Register error");
            throw new AuthenticationError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Refresh the access token.
     **/
    refreshToken: async function() {
        const refreshToken = TokenService.getRefreshToken()

        const requestData = {
            method: 'post',
            url: "/o/token/",
            data: {
                grant_type: 'refresh_token',
                refresh_token: refreshToken
            },
            auth: {
                username: process.env.VUE_APP_CLIENT_ID,
                password: process.env.VUE_APP_CLIENT_SECRET
            }
        }

        try {
            const response = await ApiService.customRequest(requestData)

            TokenService.saveToken(response.data.access_token)
            TokenService.saveRefreshToken(response.data.refresh_token)
            // Update the header in ApiService
            ApiService.setHeader()

            return response.data.access_token
        } catch (error) {
            throw new AuthenticationError(error.response.status, (error.response.data.title || error.response.data.message))
        }

    },

    /**
     * Logout the current user by removing the token from storage.
     *
     * Will also remove `Authorization Bearer <token>` header from future requests.
     **/
    logout() {
        // Remove the token and remove Authorization header from Api Service as well
        TokenService.removeToken()
        TokenService.removeRefreshToken()
        AccountStorageService.removeAccount();
        ApiService.removeHeader()

        // NOTE: Again, we'll cover the 401 Interceptor a bit later.
        //ApiService.unmount401Interceptor()
    },

    getCurrentAccount: async function() {
        try {
            console.log("obtain current account");
            const response = await ApiService.get('/account/');
            console.log(response);
            AccountStorageService.saveAccount(response.data);
            return response.data
        } catch (error) {
            throw new AuthenticationError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },
    updateCurrentAccount: async function(account) {
        console.log('user service update account');
        const requestData = {
            method: 'post',
            url: "/account/",
            data: {
                userId : account.userId,
                email: account.email,
                firstName: account.firstName,
                lastName: account.lastName,
                detailsId : account.detailsId,
                profilePicId: account.profilePicId,
                description: account.description,
                locationId: account.locationId,
                country: account.country,
                region: account.region,
                city: account.city,
                street: account.street,
                number: account.number,
                apartment: account.apartment,
                zip: account.zip,
                latitude: account.latitude,
                longitude: account.longitude
            }
        };
        try {
            const response = await ApiService.customRequest(requestData);
            console.log("update account response: " + response);
            AccountStorageService.saveAccount(response.data);
            return response.data;
        }  catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
    requestPasswordReset: async function(mail) {
        try {
            console.log("Request password reset: " + mail);
            await ApiService.post('/account/reset-password/init/', {mail:mail});
            return true;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
    resetPassword: async function(password, key) {
        try {
            console.log("Reset password: " + key);
            await ApiService.post('/account/reset-password/finish/', {key: key, newPassword: password});
            return true;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
    changePassword: async function(currentPassword, newPassword) {
        try {
            console.log("Change password");
            await ApiService.post('/account/change-password/', {currentPassword: currentPassword, newPassword: newPassword});
            return true;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
    activateAccount : async function(key) {
        try {
            console.log("Activate account");
            await ApiService.get('/activate?key='+key);
            return true;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
    changeLanguageKey: async function(key) {
        try {
            console.log("Activate account");
            await ApiService.post('/language/', {langKey:key} );
            return true;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    }
}

export default AccountService

export { AccountService, AuthenticationError }