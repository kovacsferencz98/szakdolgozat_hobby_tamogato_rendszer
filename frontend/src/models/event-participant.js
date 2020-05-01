export default class EventParticipant {
    constructor(id=null, joinedAt=null, approved=false, participated=false, ratingOfParticipant=null, ratingOfEvent=null, userUsername='', userId=null, eventName='', eventId=null) {
        this.id = id;
        this.joinedAt = joinedAt;
        this.approved = approved;
        this.participated = participated;
        this.ratingOfParticipant = ratingOfParticipant;
        this.ratingOfEvent = ratingOfEvent;
        this.userUsername = userUsername;
        this.userId = userId;
        this.eventName = eventName;
        this.eventId = eventId;
    }
}