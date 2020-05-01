export default class UserDetails {
    constructor(id=null, description="", profilePicId=null, userUsername="", userId=null, residenceId=null) {
        this.id = id;
        this.description = description;
        this.profilePicId = profilePicId;
        this.userUsername = userUsername;
        this.userId = userId;
        this.residenceId = residenceId;
    }
}
