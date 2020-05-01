
const TOKEN_KEY = 'access_token'
const CURRENT_ACCOUNT = 'current_account'
const REFRESH_TOKEN_KEY = 'refresh_token'

/**
 * Manage the how Access Tokens are being stored and retreived from storage.
 *
 * Current implementation stores to localStorage. Local Storage should always be
 * accessed through this instace.
 **/
const TokenService = {
    getToken() {
        console.log("Token service: "  + localStorage.getItem(TOKEN_KEY));
        return localStorage.getItem(TOKEN_KEY);
    },

    saveToken(accessToken) {
        console.log("saveToken: "  + accessToken);
        localStorage.setItem(TOKEN_KEY, accessToken);
    },

    removeToken() {
        localStorage.removeItem(TOKEN_KEY)
    },

    getRefreshToken() {
        return localStorage.getItem(REFRESH_TOKEN_KEY)
    },

    saveRefreshToken(refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
    },

    removeRefreshToken() {
        localStorage.removeItem(REFRESH_TOKEN_KEY)
    }

}

export const AccountStorageService = {
    getAccount() {
        var account = null;
        if(localStorage.getItem(CURRENT_ACCOUNT)) {
            account = JSON.parse(localStorage.getItem(CURRENT_ACCOUNT));
        }
        //console.log(acc.username);
        //console.log(acc.roles);
        return account;
    },
    saveAccount(account) {
        console.log("Account service save Account ");
        console.log(account.username);
        console.log(account.roles);
        console.log(account.langKey)
        localStorage.setItem(CURRENT_ACCOUNT,  JSON.stringify(account));
    },

    removeAccount() {
        console.log("Account service remove Account ");
        localStorage.removeItem(CURRENT_ACCOUNT)
    },
}

export default TokenService