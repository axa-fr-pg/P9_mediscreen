import React from 'react';
import Modal from "./modal";

function ModalSuccess({message, closureAction}) {
    return (
        <Modal
            message={message}
            closureAction={closureAction}
            className="modal-success"
            title="Request was successful"
        />
    );
}

export default ModalSuccess;