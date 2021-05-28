import {combineReducers} from 'redux';
import {STATE_PATIENT, STATE_DOCTOR, STATE_MODAL} from './reducerConstants';
import modalReducer from './modalReducer';
import patientReducer from './patientReducer';
import doctorReducer from './doctorReducer';
import {
    ACTION_SET_PAGE_NUMBER,
    ACTION_SET_ROWS_PER_PAGE,
    ACTION_SET_SORTING_FIELD,
    ACTION_SET_SORTING_DIRECTION,
    ACTION_SET_UPDATE_REQUIRED,
    ACTION_SET_MODIFY_ALLOWED
} from "./reducerConstants";

export function handleStateWithRootReducer(state, action) {

    let updatedState;

    switch (action.type) {
        case ACTION_SET_PAGE_NUMBER :
            updatedState = {
                ...state,
                paging: {
                    ...state.paging,
                    pageNumber: action.payload
                }
            };
            break;

        case ACTION_SET_ROWS_PER_PAGE :
            updatedState = {
                ...state,
                paging: {
                    ...state.paging,
                    rowsPerPage: action.payload
                }
            };
            break;

        case ACTION_SET_SORTING_FIELD :
            updatedState = {
                ...state,
                sorting: {
                    ...state.sorting,
                    field: action.payload
                }
            };
            break;

        case ACTION_SET_SORTING_DIRECTION :
            updatedState = {
                ...state,
                sorting: {
                    ...state.sorting,
                    direction: action.payload
                }
            };
            break;

        case ACTION_SET_UPDATE_REQUIRED :
            updatedState = {
                ...state,
                isUpdateRequired:  action.payload
            };
            break;

        case ACTION_SET_MODIFY_ALLOWED :
            updatedState = {
                ...state,
                isModifyAllowed:  action.payload
            };
            break;

        default :
            updatedState = state;
    }
    return updatedState;
}

export const rootReducer = combineReducers({
    [STATE_MODAL] : modalReducer,
    [STATE_PATIENT] : patientReducer,
    [STATE_DOCTOR] : doctorReducer
});
