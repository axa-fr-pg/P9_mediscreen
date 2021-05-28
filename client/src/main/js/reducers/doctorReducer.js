import {notesApiUrl} from "../api/URLs";
import {handleStateWithRootReducer} from "./rootReducer";

import {
    ACTION_SET_DOCTOR_PATIENT_ID,
    ACTION_SET_FILTER_E
} from "./reducerConstants";

const pagingState = {
    pageNumber: 0,
    rowsPerPage: 10
};

const filterState = {
    id: '',
    e: ''
};

const pagingFilterState = {
    paging: pagingState,
    filter: filterState
};

const emptyNoteList = {
    totalElements: 0,
    totalPages: 0,
    content: []
};

const doctorState = {
    ...pagingFilterState,
    getNoteListUrl: getNoteListUrl(pagingFilterState),
    noteList: emptyNoteList,
    noteFields: {},
    patientId: -1,
    isUpdateRequired: true,
    isModifyAllowed: false
};

function getNoteListUrl(state) {

    let url = notesApiUrl;
    if (state.patientId >= 0) {
        url = url + "/patients/" + state.patientId;
    } else {
        url = url + "?page=" + state.paging.pageNumber
            + "&size=" + state.paging.rowsPerPage;
        if (!!state.filter.e) {
            url = url + "&e=" + state.filter.e;
        }
    }
    return url;
}

const doctorReducer = (inputState = doctorState, action) => {

    const state = handleStateWithRootReducer(inputState, action);
    let updatedState;

    switch (action.type) {

        case ACTION_SET_FILTER_E :
            updatedState = {
                ...state,
                filter: {
                    ...state.filter,
                    e: action.payload
                }
            };
            break;

        case ACTION_SET_DOCTOR_PATIENT_ID :
            updatedState = {
                ...state,
                patientId: action.payload
            };
            break;

        default :
            updatedState = state;
    }
    return {
        ...updatedState,
        getNoteListUrl: getNoteListUrl(updatedState)
    }
};

export default doctorReducer;