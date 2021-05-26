import React, {useState} from 'react';
import axios from "axios";
import {reportsApiUrl} from '../api/URLs';
import Patient from "../patients/Patient";
import Notes from "../notes/Notes";
import {getRiskColorClassName} from "./Reports"
import Modal from "../modal/modal";
import {useHistory} from "react-router";
import {useDispatch} from "react-redux";
import {ACTION_DISPLAY_ERROR_MODAL} from "../reducers/reducerConstants";

function Report() {

    const [patient, setPatient] = useState({assessment : ''});
    const patientId = window.location.pathname.split("/").pop();
    const history = useHistory();
    const dispatch = useDispatch();

    React.useEffect(() => {
        if (isNaN(patientId)) {
            dispatch({
                type: ACTION_DISPLAY_ERROR_MODAL,
                payload: 'It looks like you entered an invalid URL. Patient id must have a numeric value. ' +
                    'Please check your request or ask your IT support !'});
        } else {
            axios.get(reportsApiUrl + "/patients?id=" + patientId)
                .then(response => {
                    setPatient(response.data);
                })
                .catch( exception => {
                    dispatch({
                        type: ACTION_DISPLAY_ERROR_MODAL,
                        payload: "Please ask your IT support : it seems that the server or the database is unavailable ! "
                            + exception.message});
                });
        }
    }, [patientId]);

    function closeErrorModal() {
        history.push('/reports');
    }

    return (
        <div>
            <h1 className={getRiskColorClassName(patient.assessment)}>{patient.assessment}</h1>
            <Patient report="true"/>
            <Notes report="true"/>
            <Modal errorClosureAction={closeErrorModal}/>
        </div>
    );
}

export default Report;