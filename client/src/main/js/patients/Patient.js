import React, {useEffect, useRef, useState} from 'react';
import axios from "axios";
import {patientsApiUrl} from '../api/URLs';
import Switch from "react-switch";
import moment from 'moment'
import ModalError from "../modal/error";
import ModalSuccess from "../modal/success";
import {useHistory} from "react-router";

const patientFields = [
    {field: "id", label: "Patient id", readOnly: true},
    {field: "family", label: "Family name"},
    {field: "given", label: "Given name"},
    {field: "dob", label: "Date of birth"},
    {field: "sex", label: "Sex"},
    {field: "address", label: "Address"},
    {field: "phone", label: "Phone"}
];

export const NUMBER_OF_PATIENT_FIELDS = 7;

function ModifySwitch({modify, onChangeModify, report}) {
    if (window.location.href.includes('new') || !report === false) {
        return null;
    }
    return (
        <div key={"div-read-only"} className="div-read-only">
            <label>View</label>
            <div className="switch-read-only">
                <Switch checked={modify} onChange={onChangeModify} checkedIcon={false} uncheckedIcon={false}/>
            </div>
            <label>Edit</label>
        </div>
    );
}

function PatientField({patient, modify, field, label, readOnly}) {

    const [value, setValue] = useState(undefined);
    const patientData = patient.current;

    useEffect(() => {
        if (value === undefined && patientData !== undefined && patientData[field] !== undefined) {
            setValue(patientData[field]);
        }
    }, [field, patientData, value]);

    if (field === 'id' && value === 'new') {
        return null;
    }

    function onChangeField(field) {
        field.persist(); // make input available asynchronously
        patient.current[field.target.name] = field.target.value;
        setValue(field.target.value);
    }

    const disabled = !!readOnly;
    return (<div key={field}>
        <label>{label}
            <input value={value} name={field} onChange={onChangeField}
                   disabled={disabled || modify === false}/>
        </label>
    </div>);
}

function PatientFields({patient, modify}) {
    return patientFields.map(fieldSpec => {
        return (
            <PatientField key={fieldSpec.field} patient={patient} modify={modify}
                   field={fieldSpec.field} label={fieldSpec.label} readOnly={fieldSpec.readOnly}
            />);
    });
}

function SaveButton({modify, onClickSave}) {
    if (!modify) return null;
    return (
        <button className="button-save" onClick={onClickSave}>Save</button>
    );
}


function Patient({report}) {
    const error = useRef('');
    const success = useRef('');
    const patient = useRef({id: window.location.pathname.split("/").pop()});
    const [, setModal] = useState(false);
    const [, setPatientReady] = useState(false);
    const [modify, setModify] = useState(window.location.href.includes('new'));
    const history = useHistory();

    function setError (message) {
        error.current = message;
        setModal(message.length > 0);
    }

    function setSuccess (message) {
        success.current = message;
        setModal(message.length > 0);
    }

    useEffect(() => {
        if (patient.current.id === 'new') return;
        if (isNaN(parseInt(patient.current.id))) {
            setError('It looks like you entered an invalid URL. Patient id must have a numeric value. Please check your request or ask your IT support !');
        } else {
            axios.get(patientsApiUrl + "/" + patient.current.id)
                .then(response => {
                    setPatient(response.data);
                })
                .catch(exception => {
                    patient.current.id = 'not-found';
                    if (exception.response) {
                        setError(exception.response.status + " " + exception.response.data);
                    } else {
                        setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }, []);

    function setPatient(data) {
        patient.current = data;
        setPatientReady(true);
    }

    function onClickSave(event) {
        event.preventDefault();
        const body = {...patient.current};
        const givenDate = moment(body.dob, "YYYY-MM-DD", true).toDate();
        const givenTime = givenDate.getTime();

        if (isNaN(givenTime) || givenTime < -5000000000000) {
            setError("Please enter a valid date of birth with format YYYY-MM-DD (" + body.dob + " is invalid).");
        } else if (body.id === 'new') {
            body.id = 0;
            axios.post(patientsApiUrl, body)
                .then(response => {
                    body.id = response.data.id;
                    setSuccess("Patient created successfully with id=" + body.id);
                })
                .catch(exception => {
                    if (exception.response) {
                        setError(exception.response.status + " " + exception.response.data);
                    } else {
                        setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        } else {
            axios.put(patientsApiUrl + "/" + patient.current.id, body)
                .then(response => {
                    setPatient(response.data);
                    setSuccess('Patient has been saved successfully !');
                })
                .catch(exception => {
                    if (exception.response) {
                        setError(exception.response.status + " " + exception.response.data);
                    } else {
                        setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }

    function onChangeModify() {
        setModify(!modify);
    }

    function displayTitle() {
        const view = modify ? 'edit' : 'view';
        const title = patient.current.id === 'new' ? 'creation' : view;
        return (
            <h1>Patient {title}</h1>
        );
    }

    function closeErrorModal() {
        setError('');
        if (!window.location.href.includes('new')) {
            history.push('/patients');
        }
    }

    function closeSuccessModal() {
        setModify(false);
        setSuccess('');
        if (window.location.href.includes('new')) {
            history.push('/patients')
        }
    }

    return (
        <div>
            {displayTitle()}
            <form>
                <PatientFields patient={patient} modify={modify}/>
                <ModifySwitch modify={modify} onChangeModify={onChangeModify} report={report}/>
                <SaveButton modify={modify} onClickSave={onClickSave}/>
                <ModalError message={error.current} closureAction={closeErrorModal}/>
                <ModalSuccess message={success.current} closureAction={closeSuccessModal}/>
            </form>
        </div>
    );
}

export default Patient;