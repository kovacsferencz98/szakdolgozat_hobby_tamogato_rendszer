export default class EventDetail {
    constructor(eventDetails=null, eventLocation=null, participants=[], isOwner=false, isParticipant=false, isOver=false,
                ratingOfEventByUser=0) {
        this.eventDetails = eventDetails;
        this.eventLocation = eventLocation;
        this.participants = participants;
        this.isOwner = isOwner;
        this.isParticipant = isParticipant;
        this.isOver = isOver;
        this.ratingOfEventByUser = ratingOfEventByUser;
    }
}