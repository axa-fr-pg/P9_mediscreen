import React from 'react';
import ReactDOM from 'react-dom';
import {createStore} from 'redux';
import {rootReducer} from './main/js/reducers/rootReducer'
import {Provider} from 'react-redux';
import MediscreenClient from './main/js/MediscreenClient';

const store = createStore(rootReducer);

ReactDOM.render(
  <React.StrictMode>
      <Provider store={store}>
          <MediscreenClient />
      </Provider>
  </React.StrictMode>,
  document.getElementById('root')
);