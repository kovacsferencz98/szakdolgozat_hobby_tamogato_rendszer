import ApiService from './api.service'

class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const UserService = {
    /**
     * Obtains every user entity from the back-end.
     *
     * @returns users
     * @throws ApiRequestError
     **/
    getUsers: async function() {
        try {
            console.log("Get every user");
            const response = await ApiService.get('/users/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status,(error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the user entity from the back-end based on username.
     *
     * @returns user
     * @throws ApiRequestError
     **/
    getUser: async function(username) {
        try {
            console.log("Get user: " + username);
            const response = await ApiService.get('/users/'+username+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status,(error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the user profile entity from the back-end based on user's id.
     *
     * @returns userProfile
     * @throws ApiRequestError
     **/
    getUserProfile: async function(id) {
        try {
            console.log("Get user profile: " + id);
            const response = await ApiService.get('/profile/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Deletes the user entity from the back-end based on username.
     *
     * @returns users
     * @throws ApiRequestError
     **/
    deleteUser: async function(username) {
        try {
            console.log("Delete user: " + username);
            const response = await ApiService.delete('/users/'+username+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Creates the user entity on the back-end based.
     *
     * @returns users
     * @throws ApiRequestError
     **/
    createUser: async function(user) {
        try {
            console.log("Create user: " + user.username);
            const response = await ApiService.post('/users/', user);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Updates the user entity on the back-end based.
     *
     * @returns users
     * @throws ApiRequestError
     **/
    updateUser: async function(user) {
        try {
            console.log("Update user: " + user.username);
            const response = await ApiService.put('/users/', user);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
};

export default UserService

export { UserService,  ApiRequestError}