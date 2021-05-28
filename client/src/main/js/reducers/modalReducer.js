import {ACTION_DISPLAY_MODAL_ERROR, ACTION_DISPLAY_MODAL_SUCCESS, ACTION_HIDE_MODAL} from './reducerConstants';

const initialState = {
    message: 'modal text',
    isVisible : false,
    isError : false,
    className : "modal-success",
    title : "modal title"
};

const modalReducer = (state = initialState, action) => {

    if (!!action.payload && action.payload.toString().length === 0 ) {
        return {
            ...state,
            isVisible: false
        }
    }
    switch (action.type) {
        case ACTION_DISPLAY_MODAL_ERROR :
            return {
                ...state,
                isError: true,
                isVisible: true,
                className : "modal-error",
                title : "Request could not be processed",
                message: action.payload
            };
        case ACTION_DISPLAY_MODAL_SUCCESS :
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