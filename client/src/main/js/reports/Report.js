import React, {useRef, useState} from 'react';
import axios from "axios";
import {reportsApiUrl} from '../api/URLs';
import Patient from "../patients/Patient";
import Notes from "../notes/Notes";
import {getRiskColorClassName} from "./Reports"
import ModalError from "../modal/error";
import {useHistory} from "react-router";

function Report() {

    const error = useRef('');
    const [, setModal] = useState(false);
    const [patient, setPatient] = useState({assessment : ''});
    const patientId = window.location.pathname.split("/").pop();
    const history = useHistory();

    function setError (message) {
        error.current = message;
        setModal(message.length > 0);
    }

    React.useEffect(() => {
        if (isNaN(patientId)) {
            setError('It looks like you entered an invalid URL. Patient id must have a numeric value. Please check your request or ask your IT support !');
        } else {
            axios.get(reportsApiUrl + "/patients?id=" + patientId)
                .then(response => {
                    setPatient(response.data);
                })
                .catch( error => {
                    if (error.response) {
                        setError("Error " + error.response.status + " : Please ask your IT support ! it seems that the patient can't be read !");
                    } else {
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }, [patientId]);

    function closeErrorModal() {
        setError('');
        if (!window.location.href.includes('new')) {
            history.push('/reports');
        }
    }

    return (
        <div>
            <h1 className={getRiskColorClassName(patient.assessment)}>{patient.assessment}</h1>
            <Patient report="true"/>
            <Notes report="true"/>
            <ModalError message={error.current} closureAction={closeErrorModal}/>
        </div>
    );
}

export default Report;