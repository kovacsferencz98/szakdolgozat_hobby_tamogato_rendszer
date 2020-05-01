export default class Account {
    constructor(userId=null, username='', email='', password='', firstName='', lastName='',
                roles=[], roleId=null, detailsId=null, description='', phoneNumber='', profilePicId=null,
                userUsername='', locationId=null, country='', region='', city='', street='',
                number=null, apartment='', zip ='', latitude=null, longitude=null, langKey='en') {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.langKey = langKey;
        this.roleId = roleId;
        this.detailsId = detailsId;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.profilePicId = profilePicId;
        this.userUsername = userUsername;
        this.locationId = locationId;
        this.country = country;
        this.region = region;
        this.city= city;
        this.street = street;
        this.number = number;
        this.apartment = apartment;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}