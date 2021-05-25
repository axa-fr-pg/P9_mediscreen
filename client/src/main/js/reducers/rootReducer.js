import {combineReducers} from 'redux';
import {PATIENT} from './reducerConstants';
import patientReducer from './patientReducer';

const rootReducer = combineReducers({
    [PATIENT] : patientReducer
});

export default rootReducer;