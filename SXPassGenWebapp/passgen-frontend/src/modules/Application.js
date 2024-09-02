//import checkTokenExpiry from './Jwt';
import axios from 'axios';
var deepEqual = require('deep-equal');

var applications = [];

function applicationExists(val) {
    var result = false;
    applications.forEach(function (entry) {
        if (deepEqual(entry, val)) {
            result = true;
            return;
        }
    });
    return result;
}

export function clearApplicationData() {
    applications = [];
}

export function getApplicationData(url, token) {
    return new Promise(function (resolve, reject) {
        if (typeof (token) == 'undefined' || token == null) return [false, null];

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'get',
            url: url + 'application/applications',
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Get application failed" + response.status);
                    reject('Get application failed');
                    return;
                }

                var update = false;
                var arraySizeChanged = false;
                var newApplications = Array.from(applications);
                if (response.data.length !== applications.length) {
                    arraySizeChanged = true;
                    newApplications = [];
                    update = true;
                }
                // Look for updated apps or 
                response.data.forEach(function (entry) {
                    // If the array is the same length then look for changes - if the arrays are not the same length then just take 
                    // the complete contents
                    if (entry.type === 'P') {
                        entry.typeDescription = 'Passticket application'
                    }
                    else if (entry.type === 'S') {
                        entry.typeDescription = 'S/Key application'
                    }
                    else if (entry.type === 'W') {
                        entry.typeDescription = 'Password'
                    }

                    if (arraySizeChanged || !applicationExists(entry)) {
                        newApplications.push(entry);
                        update = true;
                    }
                });

                if (update) {
                    applications = newApplications;
                }


                resolve([update, applications]);
                return;
            });
    });
}

export function saveApplication(url, token, application) {
    return new Promise(function (resolve, reject) {

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'post',
            url: url + 'application/application',
            data: application
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Save app failed" + response.status);
                    reject('Save app failed');
                    return;
                }

                if (response.data.success) {
                    console.log("Save app success" + response);
                    resolve([true, response.data.data]);
                    return;
                }

                reject(response.message);
                return;
            });
    });
}

export function deleteApplication(url, token, application) {
    return new Promise(function (resolve, reject) {

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'delete',
            url: url + 'application/application/' + application.id,
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Delete app failed" + response.status);
                    reject('Delete app failed');
                    return;
                }

                if (response.data.success) {
                    console.log("Delete app success" + response);
                    resolve(true);
                    return;
                }

                reject(response.message);
                return;
            });
    });
}

export function getApplicationCredential(url, token, application, sequence) {
    return new Promise(function (resolve, reject) {

        let sequenceString = "";

        if ( sequence && sequence !== "") {
            sequenceString = "/"+sequence;
        }

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'get',
            url: url + 'application/credential/' + application.application+sequenceString,
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Credential request failed - " + response.status);
                    reject('Credential request failed');
                    return;
                }

                if (response.data.success) {
                    console.log("Credential request failed -" + response);
                    resolve(response.data);
                    return;
                }

                reject(response.data.message);
                return;
            });
    });
}
