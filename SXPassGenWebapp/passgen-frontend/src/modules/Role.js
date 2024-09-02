import axios from 'axios';

var roles = [];

function roleExists(val) {
    var result = false;
    roles.forEach(function (entry) {
        if (entry.id === val.id) {
            result = true;
            return;
        }
    });
    return result;
}

export function clearRoleData() {
    roles = [];
}

export function getRoleData(url, token) {
    return new Promise(function (resolve, reject) {
        if (typeof (token) == 'undefined' || token == null) return [false, null];

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'get',
            url: url + 'role/roles',
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Get role failed" + response.status);
                    reject('Get role failed');
                    return;
                }

                var update = false;
                var newroles = Array.from(roles);
                response.data.forEach(function (entry) {
                    if (!roleExists(entry)) {
                        newroles.push(entry);
                        update = true;
                    }
                    console.log('roles ' + entry);
                });

                if (update) {
                    roles = newroles;
                }

                resolve([update, roles]);
                return;
            });
    });
}

export function saveRole(url, token, role) {
    return new Promise(function (resolve, reject) {
        let bodyValue = Object.assign({}, role);

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'post',
            url: url + 'role/role',
            data: role
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
                if (response.status !== 200) {
                    console.log("Save role failed" + response.status);
                    reject('Save role failed');
                    return;
                }

                if (response.data.success) {
                    console.log("Save role success" + response);
                    resolve(true);
                    return;
                }
                reject(response.data.message);

                return;
            });
    });
}

export function deleterole(url, token, role) {
    return new Promise(function (resolve, reject) {

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios({
            method: 'delete',
            url: url + 'role/role/'+role.id,
        },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(function (response) {
            if (response.status !== 200) {
                console.log("Delete role failed" + response.status);
                reject('Delete role failed');
                return;
            }
            if (response.success) {
                console.log("Delete role success" + response);
                resolve(true);
            }
            return;
        });
    });
}

