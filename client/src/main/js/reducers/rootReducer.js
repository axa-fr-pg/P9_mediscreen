import {combineReducers} from 'redux';
import {STATE_PATIENT, STATE_MODAL} from './reducerConstants';
import modalReducer from './modalReducer';
import patientReducer from './patientReducer';

const rootReducer = combineReducers({
    [STATE_MODAL] : modalReducer,
    [STATE_PATIENT] : patientReducer
});

export default rootReducer;