import React from 'react';
import '@axa-fr/react-toolkit-modal-default/dist/af-modal.css';
import '@axa-fr/react-toolkit-button/dist/af-button.css';
import Modal from "./modal";

function ModalError({message, closureAction}) {
    return (
        <Modal
            message={message}
            closureAction={closureAction}
            className="modal-error"
            title="Request could not be processed"
        />
    );
}

export default ModalError;