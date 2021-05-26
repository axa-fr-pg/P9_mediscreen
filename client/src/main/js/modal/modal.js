import React from 'react';
import ModalBoolean from "@axa-fr/react-toolkit-modal-boolean";
import '@axa-fr/react-toolkit-modal-default/dist/modal.scss';
import {useDispatch, useSelector} from "react-redux";
import {ACTION_HIDE_MODAL, STATE_MODAL} from "../reducers/reducerConstants";

function Modal({successClosureAction, errorClosureAction}) {

    const modalState = useSelector(state => state[STATE_MODAL]);
    const dispatch = useDispatch();

    function onClosure() {
        dispatch({type: ACTION_HIDE_MODAL});
        if (modalState.isError) {
            if (errorClosureAction !== undefined) {
                errorClosureAction();
            }
        } else {
            if (successClosureAction !== undefined) {
                successClosureAction();
            }
        }
    }

    return (
        <ModalBoolean
            id="id-modal-boolean" className={modalState.className} title={modalState.title} submitTitle="OK"
            isOpen={modalState.isVisible} onSubmit={onClosure} onCancel={undefined}
        >
            {modalState.message}
        </ModalBoolean>
    );
}

export default Modal;