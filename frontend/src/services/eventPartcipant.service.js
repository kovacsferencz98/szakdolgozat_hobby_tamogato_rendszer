import ApiService from './api.service'


class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const EventParticipantService = {
    /**
     * Obtains every even tParticipant entity from the back-end.
     *
     * @returns eventParticipants
     * @throws ApiRequestError
     **/
    getEventParticipants: async function() {
        try {
            console.log("Get every event participant");
            const response = await ApiService.get('/event-participants/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the EventParticipant entity from the back-end based on username.
     *
     * @returns eventParticipant
     * @throws ApiRequestError
     **/
    getEventParticipant: async function(id) {
        try {
            console.log("Get event participant with id: " + id);
            const response = await ApiService.get('/event-participants/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Deletes the EventParticipant entity from the back-end based on username.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    deleteEventParticipant: async function(id) {
        try {
            console.log("Delete event participant with id: " + id);
            const response = await ApiService.delete('/event-participants/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Creates the EventParticipant entity on the back-end based.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    createEventParticipant: async function(eventParticipant) {
        try {
            console.log("Create event participant : " + eventParticipant.id);
            const response = await ApiService.post('/event-participants/', eventParticipant);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Updates the EventParticipant entity on the back-end.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    updateEventParticipant: async function(eventParticipant) {
        try {
            console.log("Update event participant with id: " + eventParticipant.id);
            const response = await ApiService.put('/event-participants/', eventParticipant);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
};

export default EventParticipantService

export { EventParticipantService,  ApiRequestError}