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
import { makeStyles } from '@material-ui/core/styles';
import { getToken } from '../modules/jwt.js';
import { saveApplication, deleteApplication } from '../modules/Application.js';
import { store } from '../modules/store.js';
import { useSnackbar } from 'notistack';
import { useHistory } from "react-router-dom";
import { getBaseUrl } from '../modules/utils';
import Collapse from '@material-ui/core/Collapse';
import Alert from '@material-ui/lab/Alert';

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

export default function ApplicationDetails(props) {
  const classes = useStyles();
  const [id, setId] = React.useState(-1);
  const [application, setApplication] = React.useState("");
  const [key, setKey] = React.useState("");
  const [seed, setSeed] = React.useState("");
  const [gmtOffset, setGmtOffset] = React.useState(0);
  const [type, setType] = React.useState("P");
  const [userName, setUserName] = React.useState("");
  const [errorMessage, setErrorMessage] = React.useState("");
  const [deleteDialog, setDeleteDialog] = React.useState(false);
  const [modalStyle] = React.useState(getModalStyle);
  const [focusComponent, setFocusComponent] = React.useState();
  const { enqueueSnackbar, closeSnackbar } = useSnackbar();
  var history = useHistory();
  var baseUrl = getBaseUrl();
  const passwordRef = useRef();
  const applicationRef = useRef();
  const userRef = useRef();
  const seedRef = useRef();

  const action = key => (
    <React.Fragment>
      <Button onClick={() => { closeSnackbar(key) }}>
        OK
        </Button>
    </React.Fragment>
  );

  const handleDeleteDialogClose = () => {
    setDeleteDialog(false);
  };

  useEffect(() => {
    if ( errorMessage !== "" ) {
      focusComponent.current.focus();
    }
  })

  const handleDelete = () => {
    let token = getToken();
    let applicationValue = {
      id: id,
      application: application,
      key: key,
      seed: seed,
      gmtOffset: gmtOffset,
      type: type,
      userName: userName
    }

    setDeleteDialog(false);
    deleteApplication(baseUrl, token, applicationValue).then((value) => {
      store.dispatch({ type: 'APP_UPDATE' });
      console.log('Delete app successful');
      history.push({ pathname: "/" });
      enqueueSnackbar('Delete complete', { 'variant': 'success', 'persist': true, action });
    },
      (value) => {
        console.log('Delete app failed - ' + value);
        enqueueSnackbar('Delete failed - ' + value, { 'variant': 'error', 'persist': true, action });
      },
    );
  };

  if (props.app.id != id) {
    setApplication(props.app.application);
    setUserName(props.app.userName);
    setId(props.app.id);
    setType(props.app.type);
    setSeed(props.app.seed);
    setKey(props.app.key);
    setGmtOffset(props.app.gmtOffset);
  }

  function handleSave() {
    let token = getToken();
    let applicationValue = {
      id: id,
      application: application,
      key: key,
      seed: seed,
      gmtOffset: gmtOffset,
      type: type,
      userName: userName
    }

    // Validation  of input data
    if ( application.length === 0 ) {
      setErrorMessage('Application name required');
      setFocusComponent(applicationRef);
      return;
    }

    if ( type === 'P' && !key.match(/^[A-Fa-f0-9]{16}$/i)) {
      setErrorMessage('Invalid Passticket key');
      setFocusComponent(passwordRef);
      return;
    }

    if ( !key || key === "" ) {
      setErrorMessage('Invalid key/password');
      setFocusComponent(passwordRef);
      return;
    }

    if ( !userName || userName === "" ) {
      setErrorMessage('Invalid username');
      setFocusComponent(userRef);
      return;
    }

    saveApplication(baseUrl, token, applicationValue).then((value) => {
      store.dispatch({ type: 'APP_UPDATE' });
      history.push({ pathname: "/" });
      enqueueSnackbar('Save complete', { 'variant': 'success', 'persist': true, action });
    },
      (value) => {
        enqueueSnackbar('Save failed - ' + value, { 'variant': 'error', 'persist': true, action });
      },
    );
  }

  function changeGmtOffset(e, child) {
    setGmtOffset(child.props.value);
  }

  function displayDeleteDialog() {
    setDeleteDialog(true);
  }

  function handleCancel() {
    history.push({ pathname: "/" });
  }

  return (
    <div>
      <h2>Application details</h2>
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
              <InputLabel htmlFor="application">Name</InputLabel>
              <Input id="application" value={application} inputRef={applicationRef} onChange={(e) => setApplication(e.target.value)} />
            </FormControl>

            {type === 'P' ?
              <FormControl>
                <InputLabel htmlFor="gmt_offset">GMT Offset</InputLabel>
                <Select
                  id="gmt_offset"
                  value={gmtOffset}
                  defaultValue="0"
                  onChange={(e, child) => { setGmtOffset(child.props.value) }}
                >
                  <MenuItem value={-12}>-12</MenuItem>
                  <MenuItem value={-11}>-11</MenuItem>
                  <MenuItem value={-10}>-10</MenuItem>
                  <MenuItem value={-9}>-9</MenuItem>
                  <MenuItem value={-8}>-8</MenuItem>
                  <MenuItem value={-7}>-7</MenuItem>
                  <MenuItem value={-6}>-6</MenuItem>
                  <MenuItem value={-5}>-5</MenuItem>
                  <MenuItem value={-4}>-4</MenuItem>
                  <MenuItem value={-3}>-3</MenuItem>
                  <MenuItem value={-2}>-2</MenuItem>
                  <MenuItem value={-1}>-1</MenuItem>
                  <MenuItem value={0}>0</MenuItem>
                  <MenuItem value={1}>+1</MenuItem>
                  <MenuItem value={2}>+2</MenuItem>
                  <MenuItem value={3}>+3</MenuItem>
                  <MenuItem value={4}>+4</MenuItem>
                  <MenuItem value={5}>+5</MenuItem>
                  <MenuItem value={6}>+6</MenuItem>
                  <MenuItem value={7}>+7</MenuItem>
                  <MenuItem value={8}>+8</MenuItem>
                  <MenuItem value={9}>+9</MenuItem>
                  <MenuItem value={10}>+10</MenuItem>
                  <MenuItem value={11}>+11</MenuItem>
                  <MenuItem value={12}>+12</MenuItem>
                </Select>
              </FormControl>
              : <div></div>
            }

            <FormControl>
              {type === 'P'  ?
                <InputLabel htmlFor="key">Signon key</InputLabel>
                :
                null
              }

              {type === 'S'  ?
                <InputLabel htmlFor="key">Key</InputLabel>
                :
                null
              }

              {type === 'W'  ?
                <InputLabel htmlFor="key">Password</InputLabel>
                :
                null
              }

              <Input id="key" value={key} type="password" inputRef={passwordRef} onChange={(e) => setKey(e.target.value)} />
            </FormControl>

            {type === 'S'  ?
            <FormControl>
              <InputLabel htmlFor="seed">Seed</InputLabel>
              <Input id="seed" value={seed} inputRef={seedRef} onChange={(e) => setSeed(e.target.value)} />
            </FormControl>
             : <div></div> }

            <FormControl>
              <InputLabel htmlFor="user_name">User</InputLabel>
              <Input id="user_name" value={userName} inputRef={userRef} onChange={(e) => setUserName(e.target.value)} />
            </FormControl>

            <FormControl>
              <InputLabel htmlFor="type">Type</InputLabel>
              <Select
                id="type"
                value={type}
                defaultValue=""
                onChange={(e, child) => { setType(child.props.value) }}
              >
                <MenuItem value="P">Passticket application</MenuItem>
                <MenuItem value="S">S/Key application</MenuItem>
                <MenuItem value="W">Password</MenuItem>
              </Select>
            </FormControl>
            <FormControl>
              <Grid container spacing={3}>
                <Grid item xs={4}>
                  <Button variant="contained" color="primary" onClick={handleSave} className={classes.button}>
                    Save
                  </Button>
                </Grid>
                <Grid item xs={4}>
                  <Button variant="contained" color="primary" onClick={displayDeleteDialog} className={classes.button}>
                    Delete
                  </Button>
                </Grid>
                <Grid item xs={4}>
                  <Button variant="contained" color="primary" onClick={handleCancel} className={classes.button}>
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
        open={deleteDialog}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle id="alert-dialog-title">{"Confirm Application delete"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Delete application ?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDelete} color="primary" variant="contained">
            Ok
          </Button>
          <Button onClick={handleDeleteDialogClose} color="primary" autoFocus variant="contained">
            Cancel
          </Button>
        </DialogActions>
      </Dialog>

    </div>
  );
}
