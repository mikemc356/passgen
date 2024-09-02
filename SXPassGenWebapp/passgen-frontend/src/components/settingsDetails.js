import React, { useRef, Fragment } from 'react';
import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import FormLabel from '@material-ui/core/FormLabel';
import Input from '@material-ui/core/Input';
import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import Collapse from '@material-ui/core/Collapse';
import Alert from '@material-ui/lab/Alert';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import { getToken } from '../modules/jwt.js';
import { getBaseUrl } from '../modules/utils';
import { useSnackbar } from 'notistack';
import { saveSettings, getSettingsData } from '../modules/Setting.js';

const useStyles = makeStyles(theme => ({
  paper: {
    position: 'absolute',
    width: 400,
    backgroundColor: theme.palette.background.paper,
    border: '2px solid #000',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
    margin: theme.spacing(2, 4, 3),
  },
  button: {
    margin: '25px'
  },
  borderBox: {
    backgroundColor: 'white',
    border: '1px solid #A9A9A9',
    margin: theme.spacing(2, 0, 0),
    padding: theme.spacing(2, 4, 3)
  },
  updateSpaced: {
    margin: theme.spacing(2, 0, 0),
    padding: theme.spacing(2, 4, 3)
  }
}));

export default function Settings() {
  const [focusComponent, setFocusComponent] = React.useState();
  const [errorMessage, setErrorMessage] = React.useState("");
  const [license, setLicense] = React.useState("");
  const licenseRef = useRef();
  const caCertRef = useRef();
  const portRef = useRef();
  const caFilePasswordRef = useRef();
  const serverCertFilePasswordRef = useRef();
  const [caFilePassword, setCaFilePassword] = React.useState("");
  const [serverCertFilePassword, setServerCertFilePassword] = React.useState("");
  const [caCert, setCACert] = React.useState("");
  const [port, setPort] = React.useState("");
  const [tls, setTls] = React.useState(false);
  const [caCertMessage, setCaCertMessage] = React.useState("CA Certificate not selected");
  const [serverCertMessage, setServerCertMessage] = React.useState("Server Certificate not selected");
  const [licenseMessage, setLicenseMessage] = React.useState("License not valid");
  const [refresh, setRefresh] = React.useState(false);
  const { enqueueSnackbar, closeSnackbar } = useSnackbar();
  var classes = useStyles();
  var baseUrl = getBaseUrl();

  const action = key => (
    <React.Fragment>
      <Button onClick={() => { closeSnackbar(key) }}>
        OK
        </Button>
    </React.Fragment>
  );

  const useFirstRender = () => {
    const ref = useRef(true);
    const firstRender = ref.current;
    ref.current = false;
    return firstRender;
  };

  function handleChange(e, child) {
    console.log(e.target.files[0]);
  }

  function setSettings(settings) {
    settings.forEach(function (entry) {
      if (entry.name === 'license') {
        setLicenseMessage(entry.value);
      }
      if (entry.name === 'port') {
        setPort(entry.value);
      }
      if (entry.name === 'tls') {
        if (entry.value === "true" ) {
          setTls(true);
        }
        else {
          setTls(false);
        }
      }
      if (entry.name === 'caCertFileMessage') {
        setCaCertMessage(entry.value);
      }
      if (entry.name === 'serverCertFileMessage') {
        setServerCertMessage(entry.value);
      }
      if (entry.name === 'licenseMessage') {
        setLicenseMessage(entry.value);
      }
      if (entry.name === 'license') {
        setLicense(entry.value);
      }
    });
  }

  const handleUpdate = (e) => {
    e.preventDefault();
    let token = getToken();
    let baseUrl = getBaseUrl();
    saveSettings(baseUrl, token, e.target).then((value) => {
      enqueueSnackbar('Settings updated', { 'variant': 'success', 'persist': true, action });
      // This updates state which forces a rerender
      setRefresh(true);
    },
      (value) => {
        console.log('Settings update failed - ' + value);
        enqueueSnackbar('Settings updated failed - ' + value, { 'variant': 'error', 'persist': true, action });
      },
    );
  };

  if (useFirstRender() || refresh ) {
    setRefresh(false);
    let token = getToken();
    getSettingsData(baseUrl, token).then((value) => {
      // Result is arr - 1st value indicates if we have an update, 2nd is the data.
      if (value.length === 2) {
        if (value[0]) {
          setSettings(value[1]);
        }
      }
    },
      (error) => {
        alert('Error fetching settings' + error);
      },
    );
  }

  return (
    <div className={classes.canvas}>
      <div>
        <form onSubmit={handleUpdate} id="settingsForm" autocomplete="off">
          <Collapse in={errorMessage !== ""}>
            <Alert severity="error">
              {errorMessage}
            </Alert>
          </Collapse>
          <Grid
            container
            direction="column"
            justify="center">

            <FormControl className={classes.borderBox}>
              <h4>License key</h4>
              <FormLabel>{licenseMessage}</FormLabel>
              <Input id="license" value={license} inputRef={licenseRef} onChange={(e) => setLicense(e.target.value)} />
            </FormControl>

            <FormControl className={classes.borderBox}>
              <FormControl>
                <h4>CA Truststore</h4>
                <FormLabel>{caCertMessage}</FormLabel>
                <input
                  accept="*"
                  style={{ display: 'none' }}
                  id="caFile"
                  multiple
                  type="file"
                  onChange={handleChange}
                />
                <label htmlFor="caFile">
                  <Button color="primary" variant="contained" component="span" size="small">
                    Select CA Keystore file
                </Button>
                </label>
              </FormControl>
              <FormControl>
                <InputLabel htmlFor="caFilePassword">Truststore password</InputLabel>
                <Input id="caFilePassword" type="password" value={caFilePassword} inputRef={caFilePasswordRef} onChange={(e) => setCaFilePassword(e.target.value)} />
              </FormControl>
            </FormControl>

            <FormControl className={classes.borderBox}>
              <FormControl>
                <h4>Server Truststore</h4>
                <FormLabel>{serverCertMessage}</FormLabel>
                <input
                  accept="*"
                  style={{ display: 'none' }}
                  id="serverCertFile"
                  multiple
                  type="file"
                  onChange={handleChange}
                />
                <label htmlFor="serverCertFile">
                  <Button color="primary" variant="contained" component="span" size="small">
                    Select Server Keystore file
                </Button>
                </label>
              </FormControl>
              <FormControl>
                <InputLabel htmlFor="serverCertFilePassword">Keystore password</InputLabel>
                <Input id="serverCertFilePassword" type="password" value={serverCertFilePassword} inputRef={serverCertFilePasswordRef} onChange={(e) => setServerCertFilePassword(e.target.value)} />
              </FormControl>
            </FormControl>

            <FormControl className={classes.borderBox}>
              <h4>Server port/TLS</h4>
              <div className="container">
                <div className="row">
                  <div className="col-md-6">
                    <Input id="port" value={port} inputRef={portRef} onChange={(e) => setPort(e.target.value)} />
                  </div>
                  <div className="col-md-6">
                    <Checkbox
                      id="tls"
                      color="primary"
                      checked={tls}
                      onChange={(e) => setTls(e.target.checked) } />
                    TLS
                  </div>
                </div>
              </div>
            </FormControl>

            <FormControl className={classes.updateSpaced}>
              <Button color="primary" variant="contained" type="submit">
                Update
              </Button>
            </FormControl>

          </Grid>
        </form>
      </div>
    </div>
  );
}
