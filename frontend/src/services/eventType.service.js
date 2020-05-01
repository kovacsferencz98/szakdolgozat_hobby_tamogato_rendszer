import ApiService from './api.service'


class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const EventTypeService = {
    /**
     * Obtains every event type entity from the back-end.
     *
     * @returns eventTypes
     * @throws ApiRequestError
     **/
    getEventTypes: async function() {
        try {
            console.log("Get every event type");
            const response = await ApiService.get('/event-types/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the EventType entity from the back-end based on username.
     *
     * @returns eventType
     * @throws ApiRequestError
     **/
    getEventType: async function(id) {
        try {
            console.log("Get eventType with id: " + id);
            const response = await ApiService.get('/event-types/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Deletes the EventType entity from the back-end based on username.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    deleteEventType: async function(id) {
        try {
            console.log("Delete eventType with id: " + id);
            const response = await ApiService.delete('/event-types/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Creates the EventType entity on the back-end based.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    createEventType: async function(eventType) {
        try {
            console.log("Create eventType : " + eventType.id);
            const response = await ApiService.post('/event-types/', eventType);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Updates the EventType entity on the back-end.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    updateEventType: async function(eventType) {
        try {
            console.log("Update eventType with id: " + eventType.id);
            const response = await ApiService.put('/event-types/', eventType);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
};

export default EventTypeService

export { EventTypeService,  ApiRequestError}