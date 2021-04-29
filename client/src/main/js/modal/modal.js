import React, {useEffect, useState} from 'react';
import ModalBoolean from "@axa-fr/react-toolkit-modal-boolean";
import '@axa-fr/react-toolkit-modal-default/dist/modal.scss';

function Modal({message, closureAction, className, title}) {

    const [isOpen, setIsOpen] = useState(false);
    useEffect(()=>{
        if (!isOpen) {
            setIsOpen(message.length>0);
        }
    }, [isOpen, message.length]);

    function onClosure() {
        setIsOpen(false);
        if (closureAction !== undefined) {
            closureAction();
        }
    }

    return (
        <ModalBoolean
            id="id-modal-boolean"
            className={className}
            title={title}
            submitTitle="OK"
            isOpen={isOpen}
            onSubmit={onClosure}
            onCancel={undefined}
        >
            {message}
        </ModalBoolean>
    );
}

export default Modal;