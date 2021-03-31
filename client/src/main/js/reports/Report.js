import React, {useState} from 'react';
import axios from "axios";
import {reportsApiUrl} from '../api/URLs';
import Patient from "../patients/Patient";
import Notes from "../notes/Notes";

function Report() {

    const [error, setError] = useState('');
    const [patient, setPatient] = useState({assessment : ''});
    const patientId = window.location.pathname.split("/").pop();

    function DisplayError() {
        if (! error) return null;
        return (
            <footer>
                {error}
            </footer>
        );
    }

    React.useEffect(() => {
        if (isNaN(patientId)) {
            setError('It looks like you entered an invalid URL. Patient id must have a numeric value. Please check your request or ask your IT support !');
        } else {
            axios.get(reportsApiUrl + "/patients?id=" + patientId)
                .then(response => {
                    setPatient(response.data);
                    setError('');
                })
                .catch( error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }, [patientId]);

    return (
        <div>
            <h1>{patient.assessment}</h1>
            <Patient report="true"/>
            <Notes report="true"/>
            <DisplayError />
        </div>
    );
}

export default Report;