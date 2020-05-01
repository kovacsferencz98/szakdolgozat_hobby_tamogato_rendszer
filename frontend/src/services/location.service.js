import ApiService from './api.service'


class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const LocationService = {
    /**
     * Obtains every location entity from the back-end.
     *
     * @returns locations
     * @throws ApiRequestError
     **/
    getLocations: async function() {
        try {
            console.log("Get every location");
            const response = await ApiService.get('/locations/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the location entity from the back-end based on id.
     *
     * @returns location
     * @throws ApiRequestError
     **/
    getLocation: async function(id) {
        try {
            console.log("Get location with id: " + id);
            const response = await ApiService.get('/locations/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Deletes the location entity from the back-end based on id.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    deleteLocation: async function(id) {
        try {
            console.log("Delete location with id: " + id);
            const response = await ApiService.delete('/locations/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Creates the location entity on the back-end.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    createLocation: async function(location) {
        try {
            console.log("Create location : " + location.id);
            const response = await ApiService.post('/locations/', location);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Updates the location entity on the back-end.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    updateLocation: async function(location) {
        try {
            console.log("Update location with id: " + location.id);
            const response = await ApiService.put('/locations/', location);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
};

export default LocationService

export { LocationService,  ApiRequestError}