import React, { useRef, useEffect } from 'react';
import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import Button from '@material-ui/core/Button';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Checkbox from '@material-ui/core/Checkbox';
import ListItemText from '@material-ui/core/ListItemText';
import Collapse from '@material-ui/core/Collapse';
import Alert from '@material-ui/lab/Alert';
import { makeStyles } from '@material-ui/core/styles';
import { getToken } from '../modules/jwt.js';
import { saveUser, deleteUser } from '../modules/User.js';
import { store } from '../modules/store.js';
import { useSnackbar } from 'notistack';
import { useHistory } from "react-router-dom";
import { getRoleData } from '../modules/Role.js'
import { getBaseUrl } from '../modules/utils';

function getModalStyle() {
  const top = 50;
  const left = 50;
  return {
    top: `${top}%`,
    left: `${left}%`,
    border: '3px solid #000',
    margin: '10px',
    padding: '20px 20px 20px 20px',
    backgroundColor: 'rgb(255, 255, 255)',
    transform: `translate(-${top}%, -${left}%)`,
  };
}

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
  }
}));

export default function UserDetails(props) {
  const classes = useStyles();
  const [id, setId] = React.useState(-1);
  const [username, setUsername] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [type, setType] = React.useState("P");
  const [userDeleteDialog, setUserDeleteDialog] = React.useState(false);
  const [roles, setRoles] = React.useState([]);
  const [selectedRoles, setSelectedRoles] = React.useState([]);
  const [allRoles, setAllRoles] = React.useState([]);
  const [modalStyle] = React.useState(getModalStyle);
  const [token, setToken] = React.useState(props.token);
  const [focusComponent, setFocusComponent] = React.useState();
  const [errorMessage, setErrorMessage] = React.useState("");
  const { enqueueSnackbar, closeSnackbar } = useSnackbar();
  var history = useHistory();
  var locationArray = window.location.href.split('/');
  var baseUrl = getBaseUrl();
  let userRef = useRef();
  let passwordRef = useRef();

  const getSelected = (roles) => {
    let selected = [];
    if (!roles || !selected) return "";
    roles.forEach(function (userRole) {
      selected.push(userRole.id);
    });
    return selected;
  }

  const updateSelected = (eventRoles) => {
    let newSelected = [];
    if (!allRoles || !eventRoles) return "";
    allRoles.forEach(function (userRole) {
      if (eventRoles.indexOf(userRole.id) > -1) {
        newSelected.push(userRole);
      }
    });
    setRoles(newSelected);
    setSelectedRoles(eventRoles);
    return;
  }

  if (props.user.id != id) {
    setUsername(props.user.username);
    setId(props.user.id);
    setType(props.user.type);
    setPassword(props.user.password);
    setRoles(props.user.roles);
    setSelectedRoles(getSelected(props.user.roles));
  }

  const handleChange = (event) => {
    updateSelected(event.target.value);
  };

  const action = key => (
    <React.Fragment>
      <Button onClick={() => { closeSnackbar(key) }}>
        OK
        </Button>
    </React.Fragment>
  );

  const handleUserDeleteDialogClose = () => {
    setUserDeleteDialog(false);
  };

  const getRolesListFromIds = (selected) => {
    let result = "";
    selected.forEach(function (selectedItem) {
      allRoles.forEach(function (role) {
        if (role.id === selectedItem) {
          if (result.length > 0) {
            result = result + ", " + role.roleName;
          }
          else {
            result = role.roleName;
          }
        }
      });
    });
    return result;
  }

  useEffect(() => {
    if (errorMessage !== "") {
      focusComponent.current.focus();
    }
  })

  const handleUserDelete = () => {
    let token = getToken();
    let userValue = {
      id: id,
      username: username,
      password: password,
      roles: []
    }

    setUserDeleteDialog(false);
    deleteUser(baseUrl, token, userValue).then((value) => {
      store.dispatch({ type: 'USER_UPDATE' });
      console.log('Delete user successful');
      history.push({ pathname: "/users" });
      enqueueSnackbar('Delete complete', { 'variant': 'success', 'persist': true, action });
    },
      (value) => {
        console.log('Delete user failed - ' + value);
        enqueueSnackbar('Delete failed - ' + value, { 'variant': 'error', 'persist': true, action });
      },
    );
  };

  function handleUserSave() {
    let token = getToken();
    let userValue = {
      id: id,
      username: username,
      password: password,
      roles: roles
    }

    if (!username || username === "") {
      setErrorMessage('Invalid username');
      setFocusComponent(userRef);
      return;
    }

    if (!password || password === "") {
      setErrorMessage('Invalid password');
      setFocusComponent(passwordRef);
      return;
    }

    saveUser(baseUrl, token, userValue).then((value) => {
      store.dispatch({ type: 'USER_UPDATE' });
      enqueueSnackbar('Save complete', { 'variant': 'success', 'persist': true, action });
      history.push({ pathname: "/users" });
    },
      (value) => {
        enqueueSnackbar('Save failed - ' + value.data, { 'variant': 'error', 'persist': true, action });
      },
    );
  }

  function displayUserDeleteDialog() {
    setUserDeleteDialog(true);
  }

  function handleUserCancel() {
    history.push({ pathname: "/users" });
  }

  if (allRoles.length == 0) {
    getRoleData(baseUrl, token).then((value) => {
      // Result is arr - 1st value indicates if we have an update, 2nd is the data.
      let tempProcessedRoles = [];
      if (value.length === 2) {
        setAllRoles(value[1]);
        setSelectedRoles(getSelected(roles));
      }
    },
      (value) => {
        alert('Error fetching application details' + value);
      },
    );
  }

  return (
    <div>
      <h2>User details</h2>
      <div>
        <form>
          <Collapse in={errorMessage !== ""}>
            <Alert severity="error">
              {errorMessage}
            </Alert>
          </Collapse>
          <Grid
            container
            direction="column"
            justify="center">
            <FormControl>
            </FormControl>

            <FormControl>
              <InputLabel htmlFor="username">Name</InputLabel>
              <Input id="username" value={username} inputRef={userRef} onChange={(e) => setUsername(e.target.value)} />
            </FormControl>

            <FormControl>
              <InputLabel htmlFor="password">Password</InputLabel>
              <Input id="passwordkey" value={password} inputRef={passwordRef} type="password" onChange={(e) => setPassword(e.target.value)} />
            </FormControl>

            <FormControl>
              <InputLabel htmlFor="roles">Roles</InputLabel>
              <Select
                id="roles"
                value={selectedRoles}
                defaultValue=""
                multiple
                renderValue={(selected) => getRolesListFromIds(selected)}
                onChange={handleChange}
              >
                {allRoles.map((item) => (
                  <MenuItem key={item.id} value={item.id}>
                    <Checkbox checked={selectedRoles.indexOf(item.id) > -1} />
                    <ListItemText primary={item.roleName} />
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl>
              <Grid container spacing={3}>
                <Grid item xs={4}>
                  <Button variant="contained" color="primary" onClick={handleUserSave} className={classes.button}>
                    Save
                  </Button>
                </Grid>
                <Grid item xs={4}>
                  <Button variant="contained" color="primary" onClick={displayUserDeleteDialog} className={classes.button}>
                    Delete
                  </Button>
                </Grid>
                <Grid item xs={4}>
                  <Button variant="contained" color="primary" onClick={handleUserCancel} className={classes.button}>
                    Cancel
                  </Button>
                </Grid>
              </Grid>
            </FormControl>
          </Grid>
        </form>
      </div>
      <Dialog
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
        open={userDeleteDialog}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle id="alert-dialog-title">{"Confirm User delete"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Delete User ?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleUserDelete} color="primary" variant="contained">
            Ok
          </Button>
          <Button onClick={handleUserDeleteDialogClose} color="primary" autoFocus variant="contained">
            Cancel
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
