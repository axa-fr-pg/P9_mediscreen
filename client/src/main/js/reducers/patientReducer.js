import {patientsApiUrl} from "../api/URLs";
import {handleStateWithRootReducer} from "./rootReducer";
import {
    ACTION_SET_FILTER_DOB,
    ACTION_SET_FILTER_FAMILY,
    ACTION_SET_FILTER_ID,
    ACTION_SET_PATIENT_LIST,
    ACTION_SET_PATIENT_FIELD,
    ACTION_SET_ALL_PATIENT_FIELDS
} from "./reducerConstants";

const pagingState = {
    pageNumber: 0,
    rowsPerPage: 10
};

const filterState = {
    id: '',
    family: '',
    dob: ''
};

const sortingState = {
    field : 'id',
    direction: 'asc'
};

const pagingFilterSortingState = {
    paging: pagingState,
    filter: filterState,
    sorting: sortingState
};

const emptyPatientList = {
    totalElements : 0,
    totalPages : 0,
    content : []
};

const patientState = {
    ...pagingFilterSortingState,
    getPatientListUrl: getPatientListUrl(pagingFilterSortingState),
    patientList: emptyPatientList,
    patientFields: {},
    isUpdateRequired : true,
    isModifyAllowed : false
};

function getPatientListUrl(state) {
    let url = patientsApiUrl
        + "?page=" + state.paging.pageNumber
        + "&size=" + state.paging.rowsPerPage
        + "&sort=" + state.sorting.field + "," + state.sorting.direction
    ;
    if (!!state.filter.id) {
        url = url + "&id=" + state.filter.id;
    }
    if (!!state.filter.family) {
        url = url + "&family=" + state.filter.family;
    }
    if (!!state.filter.dob) {
        url = url + "&dob=" + state.filter.dob;
    }
    return url;
}

const patientReducer = (inputState = patientState, action) => {

    const state = handleStateWithRootReducer(inputState, action);
    let updatedState;

    switch (action.type) {

        case ACTION_SET_FILTER_ID :
            updatedState = {
                ...state,
                filter: {
                    ...state.filter,
                    id: action.payload
                }
            };
            break;

        case ACTION_SET_FILTER_FAMILY :
            updatedState = {
                ...state,
                filter: {
                    ...state.filter,
                    family: action.payload
                }
            };
            break;

        case ACTION_SET_FILTER_DOB :
            updatedState = {
                ...state,
                filter: {
                    ...state.filter,
                    dob: action.payload
                }
            };
            break;

        case ACTION_SET_PATIENT_LIST :
            updatedState = {
                ...state,
                patientList:  action.payload
            };
            break;

        case ACTION_SET_PATIENT_FIELD :
            updatedState = {
                ...state,
                patientFields:  {
                    ...state.patientFields,
                    [action.payload.field] : action.payload.value
                }
            };
            break;

        case ACTION_SET_ALL_PATIENT_FIELDS :
            updatedState = {
                ...state,
                patientFields:  action.payload
            };
            break;

        default :
            updatedState = state;
    }
    return {
        ...updatedState,
        getPatientListUrl: getPatientListUrl(updatedState)
    }
};

export default patientReducer;