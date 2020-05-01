export default class User {
    constructor(id = null, username='', email='', password ='', activated=false,  firstName ='', lastName='', roles=[]) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.activated = activated;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }
}