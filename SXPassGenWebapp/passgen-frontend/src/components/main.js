import React, { useRef } from 'react';
import '../App.css';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import HomeIcon from '@material-ui/icons/Home';
import GroupIcon from '@material-ui/icons/Group';
import SettingsIcon from '@material-ui/icons/Settings';
import PowerSettingsNewIcon from '@material-ui/icons/PowerSettingsNew';
import FileCopyIcon from '@material-ui/icons/FileCopy';
import TextField from '@material-ui/core/TextField';
import Paper from '@material-ui/core/Paper';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Collapse from '@material-ui/core/Collapse';
import Login from './login.js';
import Logout from './logout.js';
import { useDispatch, useSelector, useStore } from "react-redux";
import { useHistory } from "react-router-dom";
import { useSnackbar } from 'notistack';
import ApplicationDetails from './ApplicationDetails';
import UserDetails from './UserDetails';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableRow from '@material-ui/core/TableRow';
import AddCircleRoundedIcon from '@material-ui/icons/AddCircleRounded';
import TablePagination from '@material-ui/core/TablePagination';
import { getApplicationData, getApplicationCredential } from '../modules/Application.js'
import { getUserData } from '../modules/User.js'
import { getRoleData } from '../modules/Role.js'
import { getServerNotifications } from '../modules/Notification.js'
import SideNav, { NavItem, NavIcon, NavText } from '@trendmicro/react-sidenav';
import '@trendmicro/react-sidenav/dist/react-sidenav.css';
import Settings from './settingsDetails.js'
import { getBaseUrl } from '../modules/utils.js';
import { getToken } from '../modules/jwt.js';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { Tooltip } from '@material-ui/core';
import Grid from '@material-ui/core/Grid';

const useStyles = makeStyles(theme => ({
  // This group of buttons will be aligned to the right
  listStyle: {
    marginLeft: '5%',
    marginRight: '20%',
    marginTop: '0%',
    backgroundColor: 'white',
  },
  rightPanel: {
    marginLeft: '5%',
    marginRight: '20%',
    marginTop: '5%',
    height: '100%',
    borderRadius: '10px',
    border: '2px solid #B6B6B4',
    backgroundColor: 'white',
  },
  cardStyle: {
    marginLeft: '5%',
    marginRight: '5%',
    width: '100%',
    border: '2px solid #B6B6B4',
    backgroundColor: 'white'
  },
  tableStyle: {
  },
  iconStyle: {
    width: '100%',
    display: 'inline'
  },
  rightToolbar: {
    marginLeft: 'auto',
    marginRight: -12,
  },
  menuButton: {
    marginRight: 16,
    marginLeft: -12,
  },
  canvas: {
    backgroundColor: '#f9f5f4',
    position: 'fixed',
    width: '100%',
    height: '100%',
    left: 0,
    top: 0
  },
  title: {
    backgroundColor: '#f9f5f4',
    color: 'black',
    padding: '1%',
    width: '100%',
    fontWeight: 'bold',
    border: 'solid 1px',
    left: 0,
    top: 0
  },
  credentialDialog: {
    textAlign: 'center',
    paddingLeft: '90px',
    paddingRight: '90px'
  },
  credentialBox: {
    width: '100px',
  },
  credentialBoxSKey: {
    width: '320px',
  }
}));

function Main() {

  let emptyApplication = {
    id: -1,
    application: "",
    key: "",
    gmtOffset: 0,
    type: "",
    userName: ""
  }

  let emptyUser = {
    id: -1,
    username: "",
    password: "",
    roles: []
  }

  const [applications, setApplications] = React.useState([]);
  const [selectedApplication, setSelectedApplication] = React.useState(emptyApplication);
  const [users, setUsers] = React.useState([]);
  const [roles, setRoles] = React.useState([]);
  const [selectedUser, setSelectedUser] = React.useState(emptyUser);
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(6);
  const [newAction, setNewAction] = React.useState("app");
  const [displayAddIcon, setDisplayAddIcon] = React.useState("inline");
  const [dialogTitle, setDialogTitle] = React.useState("Credential");
  const [credential, setCredential] = React.useState("???????");
  const [credentialDialog, setCredentialDialog] = React.useState(false);
  const [sequenceDialog, setSequenceDialog] = React.useState(false);
  const [sequence, setSequence] = React.useState(null);
  const [copySuccess, setCopySuccess] = React.useState("");
  const [credentialBoxClass, setCredentialBoxClass] = React.useState("classes.credentialBox");

  var classes = useStyles();
  const dispatch = useDispatch();
  var token = useSelector(state => state.loginReducer.security.token);
  var refresh_date = useSelector(state => state.loginReducer.security.token_expiry);
  var refresh_token = useSelector(state => state.loginReducer.security.refresh_token);
  React.useEffect(() => { });
  const store = useStore();
  const { enqueueSnackbar, closeSnackbar } = useSnackbar();
  var history = useHistory();
  var locationArray = window.location.href.split('/');
  var baseUrl = getBaseUrl();
  const textAreaRef = useRef();

  store.subscribe(() => {
    let state = store.getState();
    if (state.lastAction && state.lastAction.type) {
      if (state.lastAction.type === "APP_UPDATE") {
        console.log('App update requested');

        getApplicationData(baseUrl, token).then((value) => {
          // Result is arr - 1st value indicates if we have an update, 2nd is the data.
          console.log('App update received');
          if (value.length === 2) {
            if (value[0]) {
              console.log('App update applied');
              setApplications(value[1]);
            }
          }
        },
          (error) => {
            alert('Error fetching application details' + error);
          },
        );
      }
      else if (state.lastAction.type === "USER_UPDATE") {
        getUserData(baseUrl, token).then((value) => {
          // Result is arr - 1st value indicates if we have an update, 2nd is the data.
          if (value.length === 2) {
            if (value[0]) {
              setUsers(value[1]);
            }
          }
        },
          (error) => {
            alert('Error fetching user details' + error);
          },
        );
      }
    }

    let reduxAuth = state.loginReducer.security.auth;
  });

  const action = key => (
    <React.Fragment>
      <Button onClick={() => { closeSnackbar(key) }}>
        OK
        </Button>
    </React.Fragment>
  );

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleLogout = (event) => {
    setApplications([]);
    Login.logout();
  };

  const handleRefresh = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  function copyToClipboard(e) {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(textAreaRef.current.innerHTML);
    }
  };

  const requestCredential = (row) => {
    if (row) {
      setSelectedApplication(row);
    }

    if (row && row.type === 'S' && !sequence) {
      setSequenceDialog(true);
      setSelectedApplication(row);
      return;
    }

    let token = getToken();

    getApplicationCredential(baseUrl, token, selectedApplication, sequence).then((value) => {
      // Result is credential
      setSequence(null);
      if (value.success) {
        let result = JSON.parse(value.data);
        if (result.type === "P") {
          setDialogTitle("Passticket");
          setCredential(result.credential);
          setCredentialBoxClass("credentialBox");
          setCredentialDialog(true);
        } else if (result.type === "W") {
          setDialogTitle("Password");
          setCredential(result.credential);
          setCredentialBoxClass("credentialBox");
          setCredentialDialog(true);
        } else if (result.type === "S") {
          setDialogTitle("S/Key");
          setCredential(result.credential);
          setCredentialBoxClass("credentialBoxSKey");
          setCredentialDialog(true);
        }
      }
    },
      (error) => {
        enqueueSnackbar('Request failed - ' + error, { 'variant': 'error', 'persist': true, action });      
        setSequence(null);
      },
    );
  };

  getServerNotifications(baseUrl, token).then((value) => {
    // Result is new notifications.
    value.forEach(function (entry) {
      enqueueSnackbar(entry.message, { 'variant': entry.severity, 'persist': true, action });
    });
  },
    (error) => {
      alert('Error fetching notification details' + error);
    },
  );

  getApplicationData(baseUrl, token).then((value) => {
    // Result is arr - 1st value indicates if we have an update, 2nd is the data.
    if (value.length === 2) {
      if (value[0]) {
        setApplications(value[1]);
      }
    }
  },
    (error) => {
      alert('Error fetching application details' + error);
    },
  );

  return (
    <div className={classes.canvas}>
      <div className={classes.title}>
        <div className="container">
          <div className="col-md-1">
          </div>
          <div className="col-md-8">
            <Switch>
              <Route exact path={["/", "/app/:id"]} >
                <h1>PassGen application management</h1>
              </Route>
              <Route exact path={["/users", "/user/:id"]} >
                <h1>PassGen user management</h1>
              </Route>
              <Route exact path='/settings'>
                <h1>PassGen settings</h1>
              </Route>
              <Route exact path='/'>
                <h1>PassGen application management</h1>
              </Route>
              <Route exact path='/logout'>
                <h1>PassGen application management</h1>
              </Route>
            </Switch>
          </div>
        </div>
      </div>
      <Login clientId="sxclientid" clientSecret="sxclientsecret" />
      <SideNav style={{ 'backgroundColor': 'blue' }}
        onSelect={(selected) => {
          switch (selected) {
            case 'home':
              setPage(0);
              history.push("/");
              setNewAction("app");
              setDisplayAddIcon("inline");
              break;

            case 'users':
              setPage(0);
              getRoleData(baseUrl, token).then((value) => {
                // Result is arr - 1st value indicates if we have an update, 2nd is the data.
                if (value.length === 2) {
                  if (value[0]) {
                    setRoles(value[1]);

                    getUserData(baseUrl, token).then((value) => {
                      // Result is arr - 1st value indicates if we have an update, 2nd is the data.
                      if (value.length === 2) {
                        if (value[0]) {
                          setUsers(value[1]);
                        }
                      }
                    },
                      (error) => {
                        alert('Error fetching application details' + error);
                      },
                    );
                  }
                }
              },
                (error) => {
                  alert('Error fetching application details' + error);
                },
              );

              history.push("/users");
              setNewAction("user");
              setDisplayAddIcon("inline");
              break;

            case 'settings':
              setPage(0);
              history.push("/settings");
              setNewAction("");
              setDisplayAddIcon("hidden");
              break;

            case 'logout':
              history.push("/logout");
              break;
          }
        }}
      >
        <SideNav.Toggle />
        <SideNav.Nav defaultSelected="home">
          <NavItem eventKey="home">
            <NavIcon>
              <HomeIcon />
            </NavIcon>
            <NavText>Home</NavText>
          </NavItem>
          <NavItem eventKey="users">
            <NavIcon>
              <GroupIcon />
            </NavIcon>
            <NavText>Users</NavText>
          </NavItem>
          <NavItem eventKey="settings">
            <NavIcon>
              <SettingsIcon />
            </NavIcon>
            <NavText>Settings</NavText>
          </NavItem>
          <NavItem eventKey="logout">
            <NavIcon>
              <PowerSettingsNewIcon />
            </NavIcon>
            <NavText>Logout</NavText>
          </NavItem>
        </SideNav.Nav>
      </SideNav>

      <div className="container">
        <div className="row">
        </div>
        <div className="row">
          <div className="col-md-6">
          </div>
          <div className="col-md-1">
            <Collapse in={displayAddIcon !== "hidden"}>
              <Tooltip title="Add">
                <IconButton aria-label="add an application">
                  <AddCircleRoundedIcon color="primary" className={classes.iconStyle} fontSize="large" onClick={() => { setSelectedApplication(emptyApplication); setSelectedUser(emptyUser); history.push({ pathname: "/" + newAction + "/:", data: -1 }) }} />
                </IconButton>
              </Tooltip>
            </Collapse>
          </div>
          <div className="col-md-5">
          </div>
        </div>
        <div className="row">
          <div className="col-md-6">
            <Switch>
              <Route exact path={["/", "/app/:id"]}>
                <TableContainer component={Paper}>
                  <Table className={classes.tableStyle} aria-label="customized table">
                    <TableBody>
                      {applications.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => (
                        <Tooltip title="Single click to display application, double click for application credential generation">
                          <TableRow key={row.id} hover className={classes.cardStyle} onClick={() => { setSelectedApplication(row); history.push({ pathname: "/app/:" + row.id, data: row.id }); }} onDoubleClick={() => { requestCredential(row); }}>
                            <TableCell><h3>{row.application}</h3>
                              {row.typeDescription}
                            </TableCell>
                          </TableRow>
                        </Tooltip>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
                <TablePagination
                  rowsPerPageOptions={[5, 10, 25]}
                  component="div"
                  count={applications.length}
                  rowsPerPage={rowsPerPage}
                  page={page}
                  onChangePage={handleChangePage}
                  onChangeRowsPerPage={handleChangeRowsPerPage}
                />
              </Route>
              <Route exact path={["/users", "/user/:id"]}>
                <TableContainer component={Paper}>
                  <Table className={classes.tableStyle} aria-label="customized table">
                    <TableBody>
                      {users.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => (
                        <TableRow key={row.id} hover className={classes.cardStyle} onClick={() => { setSelectedUser(row); history.push({ pathname: "/user/:" + row.id, data: row.id }); }}>
                          <TableCell><h3>{row.username}</h3>
                            {row.typeDescription}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
                <TablePagination
                  rowsPerPageOptions={[5, 10, 25]}
                  component="div"
                  count={users.length}
                  rowsPerPage={rowsPerPage}
                  page={page}
                  onChangePage={handleChangePage}
                  onChangeRowsPerPage={handleChangeRowsPerPage}
                />
              </Route>
              <Route exact path='/settings'>
                <Settings />
              </Route>
            </Switch>
          </div>
          <div className="col-md-6">
            <Switch>
              <Route exact path="/app/:id" >
                <ApplicationDetails app={selectedApplication} />
              </Route>
              <Route exact path="/user/:id" >
                <UserDetails user={selectedUser} token={token} />
              </Route>
              <Route exact path='/'>
              </Route>
              <Route exact path='/logout'>
                <Logout />
              </Route>
            </Switch>
          </div>
        </div>
      </div>
      <Dialog
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
        open={credentialDialog}
        maxWidth="sm"
        className={classes.credentialDialog}
      >
        <div className={classes.credentialDialog}>
          <DialogTitle id="alert-dialog-title">{dialogTitle}</DialogTitle>
          <DialogContent>
            <DialogContentText id="alert-dialog-description">
              <Grid
                container
                direction="row">
                <textarea id="outlined-basic" value={credential} disabled="true" variant="outlined" ref={textAreaRef} className={classes[credentialBoxClass]} />
                <IconButton color="primary-light" aria-label="Copy to clipboard">
                  <FileCopyIcon onClick={copyToClipboard} />
                </IconButton>
              </Grid>
            </DialogContentText>
          </DialogContent>
          <DialogActions style={{ justifyContent: 'center' }}>
            <Button onClick={() => { setCredentialDialog(false); setCopySuccess(""); }} color="primary" variant="contained" className={classes.margin}>
              Ok
          </Button>
          </DialogActions>
          <div>
            {copySuccess}
          </div>
        </div>
      </Dialog>
      <Dialog
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
        open={sequenceDialog}
        maxWidth="sm"
        className={classes.credentialDialog}
      >
        <DialogTitle id="alert-dialog-title">Enter S/Key sequence</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            <TextField id="outlined-basic" label="Sequence" className={classes.credentialBox} variant="outlined" onChange={(e) => setSequence(e.target.value)} />
          </DialogContentText>
        </DialogContent>
        <DialogActions style={{ justifyContent: 'center' }}>
          <Button onClick={() => { setSequenceDialog(false); requestCredential(); }} color="primary" variant="contained" className={classes.margin}>
            Ok
            </Button>
        </DialogActions>
        <div>
          {copySuccess}
        </div>
      </Dialog>
    </div>
  );
}

export default Main
