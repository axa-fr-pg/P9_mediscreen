import React, {useEffect, useState} from 'react';
import ModalBoolean from "@axa-fr/react-toolkit-modal-boolean";
import '@axa-fr/react-toolkit-modal-default/dist/af-modal.css';
import '@axa-fr/react-toolkit-button/dist/af-button.css';

function ModalError({message, closureAction}) {
    const [isOpen, setIsOpen] = useState(message.length > 0);

    useEffect(() => {
        setIsOpen(message.length > 0);
    }, [message]);

    if (!isOpen) {
        return null;
    }

    function onClosure() {
        setIsOpen(false);
        if (closureAction !== undefined) {
            closureAction();
        }
    }

    return (
        <ModalBoolean
            id="id-modal-boolean"
            className="modal-error"
            title="An error has occurred"
            submitTitle="OK"
            isOpen={isOpen}
            onSubmit={onClosure}
            onCancel
        >
            {message}
        </ModalBoolean>
    );
}

export default ModalError;