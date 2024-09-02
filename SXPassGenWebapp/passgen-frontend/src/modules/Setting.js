//import checkTokenExpiry from './Jwt';
import axios from 'axios';
var deepEqual = require('deep-equal');

export function getSettingsData(url, token) {
    return new Promise(function (resolve, reject) {
        if (typeof (token) == 'undefined' || token == null) return [false, null];

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'get',
            url: url + 'setting/settings',
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Get settings failed" + response.status);
                    reject('Get settings failed');
                    return;
                }

                resolve([true, response.data.data]);
                return;
            });
    });
}

export function saveSettings(url, token, form) {
    return new Promise(function (resolve, reject) {
        let data = new FormData();
        data.append('license',form[0].value);
        data.append('caFile',form[1].files[0]);
        data.append('caFilePassword',form[2].value);
        data.append('serverCertFile',form[3].files[0]);
        data.append('serverCertFilePassword',form[4].value);
        data.append('port',form[5].value);
        data.append('tls',form[6].checked);

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'post',
            url: url + 'setting/settings',
            data: data,
            headers: { 'Content-Type': 'multipart/form-data' }
        }).then(function (response) {
            if (response.status !== 200) {
                console.log("Save settings failed" + response.status);
                reject('Save settings failed');
                return;
            }

            if (response.data.success) {
                console.log("Save settings success" + response);
                resolve([true, response.data.data]);
                return;
            }

            reject(response.message);
            return;
        },err => {
            console.log("Save settings failed" + err);
            reject(err);
            return;
        });
    });
}

