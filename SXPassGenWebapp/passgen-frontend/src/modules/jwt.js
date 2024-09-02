import { store } from './store';
import axios from 'axios';
import { getBaseUrl, errorModal } from '../modules/utils';

var clientId;
var clientSecret;

export const getToken = () => {
    let state = store.getState();
    return state.loginReducer.security.token;
};

export const saveToken = (token) => {
    store.dispatch({ type: 'AUTH', payload: token.jwt });
}

export const getRefreshToken = () => {
    let state = store.getState();
    return state.loginReducer.security.refresh_token;
};

export const submitLogin = (baseUrl, inputClientId, inputClientSecret, userValue, passwordValue) => {
    clientId = inputClientId;
    clientSecret = inputClientSecret;
    return new Promise(function (resolve, reject) {
        var authorizationBasic = window.btoa(clientId + ':' + clientSecret);
        var request = new Request(baseUrl + 'oauth/token', {
            method: 'POST',
            body: 'grant_type=password&username=' + userValue + '&password=' + passwordValue,
            headers: new Headers({
                'Authorization': 'Basic ' + authorizationBasic,
                'Content-Type': 'application/x-www-form-urlencoded'
            })
        });

        fetch(request).then(function (response) {
            if (response.status === 400) {
                console.log('Invalid password :' +
                    response.status);
                reject('Invalid password')
                return;
            }

            if (response.status !== 200) {
                console.log('Looks like there was a problem. Status Code: ' +
                    response.status);
                reject('Looks like there was a problem. Status Code: ' + response.status)
                return;
            }

            console.log("Successful login " + response);
            return response.json();
        }, function (error) {
            reject('Communication error');
            console.log('Error ' + error);
            return;
        }).then(function (response) {
            if (response) {
                var expiry = new Date(new Date().getTime() + (response.expires_in * 1000));
                resolve({ jwt: { token: response.access_token, token_expiry: expiry, refresh_token: response.refresh_token } });
                return;
            }
        });
    });
}

axios.interceptors.response.use(response => {
    return response;
}, err => {
    const originalReq = err.config;
    if (err.response.status != 401) {
        return err;
    }

    return new Promise((resolve, reject) => {
        const originalReq = err.config;
        originalReq._retry = true;
        let baseUrl = getBaseUrl();
        let authorizationBasic = window.btoa(clientId + ':' + clientSecret);
        let refreshToken = getRefreshToken();

        let res = fetch(baseUrl + 'oauth/token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': 'Basic ' + authorizationBasic,
            },
            redirect: 'follow',
            referrer: 'no-referrer',
            body: 'grant_type=refresh_token&client_id=' + clientId + '&client_secret=' + clientSecret + '&refresh_token=' + refreshToken,
        }).then(res => res.json()).then(res => {
            var expiry = new Date(new Date().getTime() + (res.expires_in * 1000));
            saveToken({ jwt: { token: res.access_token, token_expiry: expiry, refresh_token: res.refresh_token } })
            axios.defaults.headers.common['Authorization'] = 'Bearer ' + res.access_token;
            originalReq.headers['Authorization'] = 'Bearer ' + res.access_token;
            return axios(originalReq);
        });

        resolve(res);

        return Promise.reject(err);
    });
});