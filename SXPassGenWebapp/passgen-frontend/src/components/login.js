import React, { useState, useEffect, useRef } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Modal from '@material-ui/core/Modal';
import Input from '@material-ui/core/Input';
import InputLabel from '@material-ui/core/InputLabel';
import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import Button from '@material-ui/core/Button';
import { useDispatch, useSelector, useStore } from "react-redux";
import { useHistory } from "react-router-dom";
import { getBaseUrl, errorModal } from '../modules/utils';
import { submitLogin } from '../modules/jwt';
import { initStore } from '../modules/store';
import Collapse from '@material-ui/core/Collapse';
import Alert from '@material-ui/lab/Alert';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';

/* Login dialog */

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
  }
}));

export const loginReducer = (state = 0, action) => {
  if (state === 0) {
    state = { security: { auth: false, token: null, token_expiry: null } };
    return state;
  }

  switch (action.type) {
    case 'AUTH':
      state.security.auth = true;
      state.security.token = action.payload.token;
      state.security.token_expiry = action.payload.token_expiry;
      state.security.refresh_token = action.payload.refresh_token;
      return state;
    case 'NOT_AUTH':
      state.security.auth = false;
      return state;
    case 'SETUP':
      state.security.auth = false;
      state.security.token = null;
      state.security.token_expiry = null;
      return state;
    default:
      // No change
      return state;
  }
};

export default function Login(props) {
  const classes = useStyles();
  const [modalStyle] = React.useState(getModalStyle);
  const [userValue, setUserValue] = React.useState("");
  const [auth, setAuth] = React.useState(false);
  const [passwordValue, setPasswordValue] = React.useState("");
  const [buttonDisabled, setButtonDisabled] = React.useState(true);
  const [errorMessage, setErrorMessage] = React.useState("");
  const passwordRef = useRef();
  const dispatch = useDispatch();
  const history = useHistory();
  const store = useStore();
  var clientId = props.clientId;
  var clientSecret = props.clientSecret;
  var refreshDate = useSelector(state => state.loginReducer.security.token_expiry);
  var refreshToken = useSelector(state => state.loginReducer.security.refresh_token);
  var token = useSelector(state => state.loginReducer.security.token);
  var tempToken;
  var tempRefreshToken;
  var baseUrl = getBaseUrl();

  store.subscribe(() => {
    let state = store.getState();
    let reduxAuth = state.loginReducer.security.auth;
    if (reduxAuth !== auth) {
      setAuth(state.loginReducer.security.auth);
    }
  });

  const userEntry = (e) => {
    setUserValue(e.target.value);
    var passvalue = passwordValue;
    if (e.target.value.length > 0 && passvalue.length > 0) {
      setButtonDisabled(false);
    }
  }

  const passwordEntry = (e) => {
    setPasswordValue(e.target.value);
    var uservalue = userValue;
    if (e.target.value.length > 0 && uservalue.length > 0) {
      setButtonDisabled(false);
    }
  }

  // Poll userid and password if autocomplete is in use 
  // In Firefox we do not see any events
  useInterval(() => {
    try {
      let user = document.getElementById('user').value;
      setUserValue(user);
      let password = document.getElementById('password').value;
      setPasswordValue(password);
      if (user.length > 0 && password.length > 0) {
        setButtonDisabled(false);
      }
      console.log('Checking login');
    }
    catch (err) {
    }
  }, 1000);

  function useInterval(callback, delay) {
    const savedCallback = useRef();

    // Remember the latest function.
    useEffect(() => {
      savedCallback.current = callback;
    }, [callback]);

    // Set up the interval.
    useEffect(() => {
      function tick() {
        savedCallback.current();
      }
      if (delay !== null) {
        let id = setInterval(tick, delay);
        return () => clearInterval(id);
      }
    }, [delay]);
  }

  const handleClose = () => {
    //setOpen(false);
  };

  const SubmitLogin = e => {
    e.preventDefault();
    submitLogin(baseUrl, clientId, clientSecret, userValue, passwordValue).then(function (response) {
      dispatch({ type: 'AUTH', payload: { token: response.jwt.token, token_expiry: response.jwt.token_expiry, refresh_token: response.jwt.refresh_token } });
      setAuth(true);
      console.log("Successful login ");
      history.push({ pathname: "/" });
    },
      function (error) {
        setErrorMessage(error);
      });
  }

  return (
    <Modal
      aria-labelledby="simple-modal-title"
      aria-describedby="simple-modal-description"
      open={!auth}
      BackdropProps={{ style: { backgroundColor: "grey" } }}
      onClose={handleClose}>
      <div style={modalStyle} className={classes.paper}>
        <h2 id="modal-title">Login</h2>
        <Collapse in={errorMessage !== ""}>
          <Alert severity="error">
            {errorMessage}
          </Alert>
        </Collapse>

        <form onSubmit={SubmitLogin}>
          <Grid
            container
            direction="column"
            justify="center">
            <FormControl>
              <InputLabel htmlFor="user">Username</InputLabel>
              <Input id="user" aria-describedby="user-helper-text" autoFocus onKeyUp={userEntry} />
            </FormControl>
            <FormControl>
              <InputLabel htmlFor="password">Password</InputLabel>
              <Input id="password" ref={passwordRef} aria-describedby="password-helper-text" onKeyUp={passwordEntry} type="password" />
            </FormControl>
            <FormControl margin="normal">
              <Button variant="contained" disabled={buttonDisabled} color="primary" className={classes.margin} size="medium" type="submit" >
                Login {auth}
              </Button>
            </FormControl>
          </Grid>
        </form>
      </div>
    </Modal>
  );
}
