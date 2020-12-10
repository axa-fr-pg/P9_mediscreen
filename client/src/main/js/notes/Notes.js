import React, {useState} from "react";
import Note from "./Note";
import {useHistory} from "react-router";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";

function generateRandomNotes(event, patientIdGiven, inputFieldRandomVolume, randomVolume, inputFieldPatientIdForRandom,
                             patientIdForRandom, setUpdateRequired, setError) {
    let url = notesApiUrl;
    event.preventDefault();
    if (!!inputFieldRandomVolume) {
        inputFieldRandomVolume.blur();
    }
    if (!!inputFieldPatientIdForRandom) {
        inputFieldPatientIdForRandom.blur();
    }
    setError("Processing request...");

    if (patientIdGiven) {
        url = url + "/patient/" + patientIdForRandom;
    }

    axios.post(url + "/random/" + randomVolume)
        .then(response => {
            setUpdateRequired(true);
            setError(response.data.length + " random notes have been generated successfully !");
        })
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data+ " ! Please ask your IT support : it looks like the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
            }
        });
}

function onChangePatientIdGiven(patientIdGiven, setPatientIdGiven, setError) {
    setPatientIdGiven(!patientIdGiven);
    setError('');
    console.log("patientIdGiven ", patientIdGiven);
}

function PatientIdSwitch({patientIdGiven, setPatientIdGiven, setError}) {

    return(
        <div key={"switch-patient-id"} className="switch-patient-id">
            <Switch checked={patientIdGiven} onChange={() => onChangePatientIdGiven(patientIdGiven, setPatientIdGiven, setError)}
                    checkedIcon={false} uncheckedIcon={false} height={15} width={30} handleDiameter={15} />
        </div>
    );
}

function NotesRandom({setUpdateRequired, setError}) {
    const [randomVolume, setRandomVolume] = useState(5);
    const [inputFieldRandomVolume, setInputFieldRandomVolume] = useState(null);
    const [patientIdGiven, setPatientIdGiven] = useState(false);
    const [patientIdForRandom, setPatientIdForRandom] = useState(0);
    const [inputFieldPatientIdForRandom, setInputFieldPatientIdForRandom] = useState(null);

    function onChangePatientIdForRandom (field) {
        setPatientIdForRandom(field.target.value);
        setInputFieldPatientIdForRandom(field.target);
    }

    function onChangeRandomVolume (field) {
        setRandomVolume(field.target.value);
        setInputFieldRandomVolume(field.target);
    }

    return (
        <form>
            <div className="div-random">
                <button onClick={(event) => generateRandomNotes(event, patientIdGiven, inputFieldRandomVolume, randomVolume, inputFieldPatientIdForRandom, patientIdForRandom, setUpdateRequired, setError)}>
                    Add
                </button>
                <input className="input-small" value={randomVolume} onChange={onChangeRandomVolume} />
                <label >
                    random note(s) to database
                </label>
                <PatientIdSwitch patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} setError={setError} />
                <div hidden={patientIdGiven}>
                    <label>regardless of the patient Id</label>
                </div>
                <div hidden={!patientIdGiven}>
                    <label>for patient Id </label>
                    <input className="input-small"  value={patientIdForRandom} onChange={onChangePatientIdForRandom} />
                </div>
            </div>
        </form>
    );
}

function Notes() {
    const [updateRequired, setUpdateRequired] = useState('false');
    const [error, setError] = useState('');
    const history = useHistory();
    history.push('/notes/patient/1/new');

    return (
        <div>
            <h1>Note list</h1>
            <NotesRandom setUpdateRequired={setUpdateRequired} setError={setError} />
            <Note />
        </div>
    );
}

export default Notes;