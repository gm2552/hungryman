import { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';
import DiningSearches from './DiningSearches'
import SearchDefForm from './SearchDefForm'
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom';

function App() {

  const [submittedSearches, setSubmittedSearches] = useState();

  const loadSearches = () => {
    axios.get('/api/search/search')
    .then(resp => setSubmittedSearches(resp.data))
    .catch(error => console.error(error))
  };

  useEffect(() => {
    loadSearches();
    const interval = setInterval(() => {
      loadSearches();
    }, 60000);
    return () => clearInterval(interval);

  }, []); 

  const login = () => {
    let port = (window.location.port ? ':' + window.location.port : '');
    window.location.href = '//' + window.location.hostname + port + '/diningsearch';
  }

  const logout = () => {
    let port = (window.location.port ? ':' + window.location.port : '');
    window.location.href = '//' + window.location.hostname + port + '/scg-logout?redirect=/';
  }

  return (
    <Router>
      <div align="left"  className="App">
            <div className="bannerHeader"> 
              <div className="bannerCenter">
              <label className="bannerLabel">Hungryman Dining Availability Search</label>
              </div>      	
            </div>
            <div className="container welcomeContent">
                      The Hungryman Dining Availability application continously searches for dining availability based on desired search
                      parameters.  Once a search is submitted, the Hugryman system will continously scour different sources for dining
                      availability that match your preferences and display dining availability in near real time.  Searches will continue even 
                      after you leave the application, so you may return a later time to check to see if any new availability has been found.
            </div>          
            <Switch>
              <Route path="/home">
                <div align="center" id="login">
                  <button onClick={login}>Login</button>
                </div>
              </Route>
              <Route path="/diningsearch">
                <div align="center" id="logout">
                  <button onClick={logout}>Logout</button>
                </div>
                <p/>
                <SearchDefForm submittedSearches={submittedSearches} setSubmittedSearches={setSubmittedSearches}/>
                <p/>
                <DiningSearches submittedSearches={submittedSearches} setSubmittedSearches={setSubmittedSearches}/>
              </Route>
              <Route>
                <Redirect to="/home"/>
              </Route>
            </Switch>       
      </div>
    </Router>
  );
}

export default App;
