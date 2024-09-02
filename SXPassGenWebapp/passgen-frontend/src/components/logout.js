import React from 'react';
import Button from '@material-ui/core/Button';
import { useDispatch } from "react-redux";
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import { useHistory } from "react-router-dom";

/* Logout dialog */

export default function Logout(props) {
  const dispatch = useDispatch();
  var history = useHistory();

  const handleClose = () => {
    history.goBack();
  };

  const handleLogout = () => {
    dispatch({ type: 'NOT_AUTH', token: null });
    history.push('/');
  };

  return (
    <Dialog
    open={true}
    maxWidth="xs"
    fullWidth={true}
    onClose={handleClose}
    BackdropProps={{ style: { backgroundColor: "grey" } }}
    aria-labelledby="alert-dialog-title"
    aria-describedby="alert-dialog-description"
  >
    <DialogTitle id="alert-dialog-title">Logout</DialogTitle>
    <DialogContent>
      <DialogContentText id="alert-dialog-description">
        Do you wish to Logout ?
      </DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button onClick={handleLogout} variant="contained" color="primary">
        Ok
      </Button>
      <Button onClick={handleClose} variant="contained" color="primary" autoFocus>
        Cancel
      </Button>
    </DialogActions>
  </Dialog>
);
}
