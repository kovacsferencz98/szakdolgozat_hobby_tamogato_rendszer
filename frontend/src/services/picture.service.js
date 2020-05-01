import ApiService from './api.service'


class ApiRequestError extends Error {
    constructor(errorCode, message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        this.errorCode = errorCode
    }
}

const PictureService = {

    /**
     * Obtains the location entity from the back-end based on id.
     *
     * @returns picture
     * @throws ApiRequestError
     **/
    getPicture: async function(id) {
        try {
            console.log("Get picture with id: " + id);
            const response = await ApiService.get('/downloadPicture/'+id+'/');
            console.log(response);

            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    },

    /**
     * Saves the picture entity on the back-end.
     *
     * @returns response body
     * @throws ApiRequestError
     **/
    savePicture: async function(file) {
        try {
            console.log("Save picutre");
            let formData = new FormData();
            formData.append('file', file);

            const requestData = {
                method: 'post',
                url: "/uploadPicture/",
                data: formData
            };

            const response = await ApiService.customRequest(requestData);
            console.log(response);
            return response.data;
        } catch (error) {
            throw new ApiRequestError(error.response.status, (error.response.data.title || error.response.data.message));
        }
    }
};

export default PictureService

export { PictureService,  ApiRequestError}