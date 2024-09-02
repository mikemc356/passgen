import axios from 'axios';
var deepEqual = require('deep-equal');

var users = [];

function userExists(val) {
    var result = false;
    users.forEach(function (entry) {
        if (deepEqual(entry,val)) {
            result = true;
            return;
        }
    });
    return result;
}

export function clearUserData() {
    users = [];
}

export function getUserData(url, token) {
    return new Promise(function (resolve, reject) {
        if (typeof (token) == 'undefined' || token == null) return [false, null];

        axios({
            method: 'get',
            url: url + 'user/users',
          },
          {
            headers: {
              'Authorization': `Bearer ${token}` 
            }
          }).then(function (response) {
            if (response.status !== 200) {
                console.log("Get User failed" + response.status);
                reject('Get User failed');
                return;
            }
            var update = false;
            var arraySizeChanged = false;
            var newUsers = Array.from(users);
            if (response.data.length !== users.length ) {
                arraySizeChanged = true;
                newUsers = [];
                update = true;
            }

            response.data.forEach(function (entry) {
                if (arraySizeChanged  || !userExists(entry)) {
                    newUsers.push(entry);
                    update = true;
                }
                console.log('Users ' + entry);
            });

            if (update) {
                users = newUsers;
            }

            resolve([update, users]);
            return;
        })
    });
}

export function saveUser(url, token, user) {
    return new Promise(function (resolve, reject) {

        axios({
            method: 'post',
            url: url + 'user/user',
            data: user
          },
          {
            headers: {
              'Authorization': `Bearer ${token}` 
            }
          }).then(function (response) {
            if (response.status !== 200) {
                console.log("Save user failed" + response.status);
                reject('Save user failed');
                return;
            }

            if (response.data.success) {
                console.log("Save user success" + response);
                resolve(true);
                return;
            }

            let message = response.message;
            if ( response.data ) {
                message = message + ' ' + response.data;
            }
            reject(message);
            return;
        });
    });
}

export function deleteUser(url, token, user) {
    return new Promise(function (resolve, reject) {

        axios({
            method: 'delete',
            url: url + 'user/user/'+user.id,
            data: user
          },
          {
            headers: {
              'Authorization': `Bearer ${token}` 
            }
          }).then(function (response) {
            if (response.status !== 200) {
                console.log("Delete user failed" + response.status);
                reject('Delete user failed');
                return;
            }

            if (response.data.success) {
                console.log("Delete user success" + response);
                resolve([true, response.data.data]);
                return;
            }
            reject(response.message);
            return;
        });
    });
}

