import React, {useState} from "react";
import Note from "./Note";
import {useHistory} from "react-router";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";

function generateRandomNotes(event, inputField, randomVolume, setUpdateRequired, setError) {
    event.preventDefault();
    if (!!inputField) {
        inputField.blur();
    }
    setError("Processing request...");

    axios.post(notesApiUrl+"/random/"+randomVolume)
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

function displayPatientIdSwitch(patientIdGiven, setPatientIdGiven, setError) {

    return(
        <div key={"switch-patient-id"} className="switch-div">
            <label>View</label>
            <div className="switch-patient-id">
                <Switch checked={patientIdGiven} onChange={() => onChangePatientIdGiven(patientIdGiven, setPatientIdGiven, setError)}
                        checkedIcon={false} uncheckedIcon={false} height={15} width={30} handleDiameter={15} />
            </div>
            <label>Edit</label>
        </div>
    );
}

function NotesRandom({setUpdateRequired, setError}) {
    const [randomVolume, setRandomVolume] = useState(5);
    const [inputField, setInputField] = useState(null);
    const [patientIdGiven, setPatientIdGiven] = useState(false);

    function onChange (field) {
        setRandomVolume(field.target.value);
        setInputField(field.target);
    }

    return (
        <form>
            <label className="random-label">
                <button onClick={(event) => generateRandomNotes(event, inputField, randomVolume, setUpdateRequired, setError)}>Add</button>
                <input className="smallest-width" value={randomVolume} onChange={onChange} />
                random note(s) to database
                {displayPatientIdSwitch(patientIdGiven, setPatientIdGiven, setError)}
            </label>
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