import React from 'react';
import { Provider } from "react-redux";
import './App.css';
import Main from './components/main.js';
import { composeWithDevTools } from 'redux-devtools-extension';
import { SnackbarProvider } from 'notistack';
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter } from "react-router-dom";
import { store, initStore } from './modules/store.js';
import { loginReducer } from './components/login';

function App({ classes }) {
  initStore(loginReducer);

  return (
    <Provider store={store}>
      <BrowserRouter>
        <SnackbarProvider maxSnack={3}>
          <Main />
        </SnackbarProvider>
      </BrowserRouter>
    </Provider>
  );
}

export default App;