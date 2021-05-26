import {ACTION_DISPLAY_ERROR_MODAL, ACTION_DISPLAY_SUCCESS_MODAL, ACTION_HIDE_MODAL} from './reducerConstants';

const initialState = {
    message: 'modal text',
    isVisible : false,
    isError : false,
    className : "modal-success",
    title : "modal title"
};

const modalReducer = (state = initialState, action) => {
    console.log("modalReducer with action " + action.type + " " + action.payload);
    if (!!action.payload && action.payload.toString().length === 0 ) {
        return {
            ...state,
            isVisible: false
        }
    }
    switch (action.type) {
        case ACTION_DISPLAY_ERROR_MODAL :
            return {
                ...state,
                isError: true,
                isVisible: true,
                className : "modal-error",
                title : "Request could not be processed",
                message: action.payload
            };
        case ACTION_DISPLAY_SUCCESS_MODAL :
            return {
                ...state,
                isError: false,
                isVisible: true,
                className : "modal-success",
                title : "Request was successful",
                message: action.payload
            };
        case ACTION_HIDE_MODAL :
            return {
                ...state,
                isVisible: false
            }
        default :
            return state;
    }
};

export default modalReducer