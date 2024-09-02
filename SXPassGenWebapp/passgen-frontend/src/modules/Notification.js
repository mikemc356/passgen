
import axios from 'axios';

var notifications = [];

function notificationExists(val) {
  var result = false;
  notifications.forEach(function (entry) {
      if (entry.id ===  val.id) {
          result = true;
          return;
      }
  });
  return result;
}

export function getServerNotifications(url, token) {
  return new Promise(function (resolve, reject) {
    if (typeof (token) == 'undefined' || token == null) return;

    axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
    axios({
      method: 'get',
      url: url + 'notification/getForUser',
    },
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(function (response) {
        if (response.status !== 200) {
          console.log("Get notifications failed" + response.status);
          reject('Get notifications failed');
          return;
        }
        var newNotifications = Array.from(notifications);
        var addedNotifications = [];
        if (JSON.stringify(response) === JSON.stringify(notifications)) return;;

        response.data.forEach(function (entry) {
          if (!notificationExists(entry)) {
            newNotifications.push(entry);
            addedNotifications.push(entry);
          }
        });

        notifications = newNotifications;
        resolve(addedNotifications);
        return;
      });
  });
}

export function getAllServerNotifications(url, token) {
  return new Promise(function (resolve, reject) {
    if (typeof (token) == 'undefined' || token == null) return;

    axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
    axios({
      method: 'get',
      url: url + 'notification/getForUser',
    },
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(function (response) {
        if (response.status !== 200) {
          console.log("Get notifications failed" + response.status);
          reject('Get notifications failed');
          return;
        }
        var newNotifications = Array.from(notifications);
        var addedNotifications = [];
        if (JSON.stringify(response.data) === JSON.stringify(notifications)) return;;

        resolve(response.data);
        return;
      });
  });
}



