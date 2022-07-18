import { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';
import DiningSearches from './DiningSearches'
import SearchDefForm from './SearchDefForm'

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

  return (
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
              <p/>
              Use the form below to create and submit search, and search results will be displayed below the search form.  Click the "+" symbol to 
              expand the results of a search, and the "-" symbol to collapse the search results.  Once you are finished with a search, 
              you may delete the search and its results by clicking on the trashcan icon.
		    </div>        
       <SearchDefForm submittedSearches={submittedSearches} setSubmittedSearches={setSubmittedSearches}/>
       <p/>
       <DiningSearches submittedSearches={submittedSearches} setSubmittedSearches={setSubmittedSearches}/>
    </div>
  );
}

export default App;
