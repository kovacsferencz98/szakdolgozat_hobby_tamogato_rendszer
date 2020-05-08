import ApiService from './api.service'


class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const EventService = {
    /**
     * Obtains every event entity from the back-end.
     *
     * @returns events
     * @throws ApiRequestError
     **/
    getEvents: async function() {
        try {
            console.log("Get every event");
            const response = await ApiService.get('/events/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains every event entity created by the user.
     *
     * @returns events
     * @throws ApiRequestError
     **/
    getOwnEvents: async function() {
        try {
            console.log("Get every own event");
            const response = await ApiService.get('/own-events/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains every event entity the user participates in.
     *
     * @returns events
     * @throws ApiRequestError
     **/
    getParticipateEvents: async function() {
        try {
            console.log("Get every event the user participates in");
            const response = await ApiService.get('/participate-events/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains every active event entity the user participates in.
     *
     * @returns events
     * @throws ApiRequestError
     **/
    getActiveEvents: async function() {
        try {
            console.log("Get every event the user participates in");
            const response = await ApiService.get('/active-events/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the Event entity from the back-end based on eventId.
     *
     * @returns event
     * @throws ApiRequestError
     **/
    getEvent: async function(id) {
        try {
            console.log("Get event with id: " + id);
            const response = await ApiService.get('/events/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Obtains the EventDetail entity from the back-end based on eventId.
     *
     * @returns event
     * @throws ApiRequestError
     **/
    getEventDetail: async function(id) {
        try {
            console.log("Get eventDetail with id: " + id);
            const response = await ApiService.get('/event-detail/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Rates the given event
     * @param id the id of the event
     * @param rating the rating
     * @returns response body
     */
    rateEvent: async function(id, rating) {
        const requestData = {
            method: 'post',
            url: "/rate-event/"+id+"/",
            data: {
                rating: rating
            }
        };
        try {
            console.log("Rate event:"+id);
            const response = await ApiService.customRequest(requestData);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Rates the given participant
     * @param id the id of the participant
     * @param rating the rating
     * @returns response body
     */
    rateParticipant: async function(id, rating) {
        const requestData = {
            method: 'post',
            url: "/rate-participant/"+id+"/",
            data: {
                rating: rating
            }
        };
        try {
            console.log("Rate event participant:"+id);
            const response = await ApiService.customRequest(requestData);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Active user joins the given event
     * @param id the id of the event
     * @returns response body
     */
    joinEvent: async function(id) {
        const requestData = {
            method: 'post',
            url: "/join-event/"+id+"/",
            data: {}
        };
        try {
            console.log("Join event:"+id);
            const response = await ApiService.customRequest(requestData);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Active user leaves the given event
     * @param id the id of the event
     * @returns response body
     */
    leaveEvent: async function(id) {
        const requestData = {
            method: 'delete',
            url: "/leave-event/"+id+"/",
            data: {}
        };
        try {
            console.log("Leave event:"+id);
            const response = await ApiService.customRequest(requestData);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Approves the given participant
     * @param id the id of the participant
     * @returns response body
     */
    approveParticipant: async function(id) {
        const requestData = {
            method: 'post',
            url: "/approve-participant/"+id+"/",
            data: {}
        };
        try {
            console.log("Approve participant:"+id);
            const response = await ApiService.customRequest(requestData);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Deletes the given participant
     * @param id the id of the participant
     * @returns response body
     */
    deleteParticipant: async function(id) {
        const requestData = {
            method: 'delete',
            url: "/delete-participant/"+id+"/",
            data: {}
        };
        try {
            console.log("Delete participant:"+id);
            const response = await ApiService.customRequest(requestData);
            console.log(response);

            return response;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message))
        }
    },

    /**
     * Deletes the Event entity from the back-end based on eventId.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    deleteEvent: async function(id) {
        try {
            console.log("Delete event with id: " + id);
            const response = await ApiService.delete('/events/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Creates the Event entity on the back-end based.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    createEvent: async function(event, location) {
        console.log("Start of create");
        try {
            console.log("Post event");
            console.log(event);
            console.log(location);
            const response = await ApiService.post('/events/', {event:event, location:location});
            console.log(response);
            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Updates the Event entity on the back-end.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    updateEvent: async function(event) {
        try {
            console.log("Update event with id: " + event.id);
            const response = await ApiService.put('/events/', event);
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },
};

export default EventService

export { EventService,  ApiRequestError}