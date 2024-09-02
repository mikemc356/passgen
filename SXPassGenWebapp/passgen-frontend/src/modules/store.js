
import { createStore, combineReducers } from "redux";
import { loginReducer } from '../components/login';
import { composeWithDevTools } from 'redux-devtools-extension';

export var store;

const rootReducer = (state = 0, action) => {
  switch (action.type) {
    default:
      // No change
      state = { security: { auth: false, token: null, token_expiry: null } };
      return state;
  }
};

function lastAction(state = null, action) {
  return action;
}

export function initStore(loginReducer) {
  const reducers = combineReducers({ rootReducer, loginReducer, lastAction });
  store = createStore(reducers, composeWithDevTools());
}
