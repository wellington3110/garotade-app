import 'typeface-roboto';
import React from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import Login from "./pages/account/login/Login";
import Events from "./pages/events/Events";
import NewAccount from "./pages/account/newAccount/NewAccount";
import AccountManager from "./pages/account/accountManagement/AccountManager";
import httpClient from "./config/httpClient";
import AccountAPI from "./api/AccountAPI";
import EventsAPI from "./api/EventsAPI";

const accountAPI = new AccountAPI(httpClient);
const eventsAPI = new EventsAPI(httpClient);

function App() {
  return(
      <Router>
          <Switch>
              <Route path="/new-account">
                  <NewAccount accountAPI={accountAPI}/>
              </Route>
              <Route path="/account/:accountNumber">
                  <AccountManager accountAPI={accountAPI}/>
              </Route>
              <Route path="/events/:aggregateIdentifier">
                  <Events eventsAPI={eventsAPI}/>
              </Route>
              <Route path="/" component={Login}/>
          </Switch>
      </Router>
  );
}

export default App;
