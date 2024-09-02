
export const getBaseUrl = () => {
  var locationArray = window.location.href.split('/');
  var url = locationArray[0] + '//' + locationArray[2] + '/';
  if (url.indexOf(':3000') > -1) {
    url = url.replace(':3000', ':8080')
  }
  return url;
}

//const Severity = {'info': 1, 'error':2, 'warn':3};
export const errorModal = (severity, message) => {
  alert(message);
}