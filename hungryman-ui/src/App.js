import { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';
import DiningSearches from './DiningSearches'
import SearchDefForm from './SearchDefForm'

function App() {

  const [submittedSearches, setSubmittedSearches] = useState();

  useEffect(() => {

    axios.get('/api/search')
    //axios.get('http://hungryman.perfect300rock.com/api/search')
    .then(resp => setSubmittedSearches(resp.data))
    .catch(error => console.error(error))
  }, []); 

  return (
    <div align="left"  className="App">
       <SearchDefForm submittedSearches={submittedSearches} setSubmittedSearches={setSubmittedSearches}/>
       <p/>
       <DiningSearches submittedSearches={submittedSearches} setSubmittedSearches={setSubmittedSearches}/>
    </div>
  );
}

export default App;
