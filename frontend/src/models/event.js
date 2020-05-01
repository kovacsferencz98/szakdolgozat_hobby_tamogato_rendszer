export default class Event {
 constructor(id=null, name='', description='', maxAttendance=null, minAttendance=null, currentAttendance=0, createdAt=null, startsAt='', price=null,
             typeId=null, locationId=null, createdById=null, typeName='', createdByUsername = '') {
     this.id = id;
     this.name = name;
     this.description = description;
     this.maxAttendance = maxAttendance;
     this.minAttendance = minAttendance;
     this.currentAttendance = currentAttendance;
     this.createdAt = createdAt;
     this.startsAt = startsAt;
     this.price = price;
     this.typeId = typeId;
     this.typeName = typeName;
     this.locationId = locationId;
     this.createdById = createdById;
     this.createdByUsername = createdByUsername;
 }
}

