import ApiService from './api.service'


class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const UserDetailsService = {
    /**
     * Obtains every user detail entity from the back-end.
     *
     * @returns userDetails
     * @throws ApiRequestError
     **/
    getUserDetails: async function() {
        try {
            console.log("Get every user detail");
            const response = await ApiService.get('/user-details/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the user detail entity from the back-end based on username.
     *
     * @returns userDetail
     * @throws ApiRequestError
     **/
    getUserDetail: async function(id) {
        try {
            console.log("Get user detail: " + id);
            const response = await ApiService.get('/user-details/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Deletes the user detail entity from the back-end based on username.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    deleteUserDetail: async function(id) {
        try {
            console.log("Delete user detail: " + id);
            const response = await ApiService.delete('/user-details/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Creates the user detail entity on the back-end based.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    createUserDetail: async function(userDetails, location) {
        try {
            console.log("Create user detail : " + userDetails.userUsername);
            const response = await ApiService.post('/user-details/', {userDetails:userDetails, location:location});
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Updates the user detail entity on the back-end based.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    updateUserDetail: async function(userDetails) {
        try {
            console.log("Update user detail: " + userDetails.userUsername);
            const response = await ApiService.put('/user-details/', userDetails);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
};

export default UserDetailsService

export { UserDetailsService,  ApiRequestError}