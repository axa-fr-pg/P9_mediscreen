import {notesApiUrl} from "../api/URLs";
import {handleStateWithRootReducer} from "./rootReducer";

import {
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

const doctorState = {
    ...pagingFilterSortingState,
    getNoteListUrl: getNoteListUrl(pagingFilterSortingState),
    noteList: emptyPatientList,
    noteFields: {},
    isUpdateRequired : true,
    isModifyAllowed : false
};

function getNoteListUrl(state) {
    let url = notesApiUrl
        + "?page=" + state.paging.pageNumber
        + "&size=" + state.paging.rowsPerPage
    ;
    if (!!state.filter.e) {
        url = url + "&e=" + state.filter.e;
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

        default :
            updatedState = state;
    }
    return {
        ...updatedState,
        getNoteListUrl: getNoteListUrl(updatedState)
    }
};

export default doctorReducer;