import React, {useState, useEffect} from 'react';
import axios from "axios";
import {patientsApiUrl} from '../api/URLs';
import {useHistory} from "react-router";

function getPatients(pageNumber, setPatients, setUpdateRequired, setError) {
    axios.get(patientsApiUrl + "/pages/" + pageNumber)
        .then(response => {
            setPatients(response.data);
            setUpdateRequired(false);
            if (response.data.numberOfElements === 0) {
                setError('It seems that the database is empty : please add some patients or ask your IT support.');
            }
        })
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
}

function PatientList({patients, setPatients, error, updateRequired, setUpdateRequired, setError, history}) {

    const [pageNumber, setPage] = useState(0);

    useEffect(() => {
        if (updateRequired) getPatients(pageNumber, setPatients, setUpdateRequired, setError);
    });

    function onclickBack() {
        setPage(pageNumber-1);
        setUpdateRequired(true);
    }

    function onclickNext() {
        setPage(pageNumber+1);
        setUpdateRequired(true);
    }

    if (patients.length === 0) return null;
    return (
        <nav>
            <table>
                <thead>
                <tr>
                    <th>Patient id</th>
                    <th>Family name</th>
                    <th>Date of birth</th>
                </tr>
                </thead>
                <tbody>
                {patients.content.map(patient => (
                    <tr key={patient.id} onClick={() => history.push("/patients/" + patient.id)}>
                        <td>{patient.id}</td>
                        <td>{patient.family}</td>
                        <td>{patient.dob}</td>
                    </tr>
                ))}
                </tbody>
            </table>
            <nav className="nav-paging-patients">
                <button disabled={pageNumber===0} onClick={onclickBack}>Back</button>
                Page {pageNumber+1} of {patients.totalPages}
                <button disabled={pageNumber>=patients.totalPages-1} onClick={onclickNext}>Next</button>
            </nav>
        </nav>
    );
}

function PatientsError({error}) {

    if (!error) return null;
    return (
        <footer>
            {error}
        </footer>
    );
}

function generateRandomPatients(event, inputField, randomVolume, setUpdateRequired, setError) {
    event.preventDefault();
    if (!!inputField) {
        inputField.blur();
    }
    setError("Processing request...");

    axios.post(patientsApiUrl + "/random/" + randomVolume)
        .then(response => {
            setUpdateRequired(true);
            setError(response.data.length + " random patients have been generated successfully !");
        })
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
}

function PatientsRandom({setUpdateRequired, setError}) {
    const [randomVolume, setRandomVolume] = useState(5);
    const [inputField, setInputField] = useState(null);

    function onChange(field) {
        setRandomVolume(field.target.value);
        setInputField(field.target);
    }

    return (
        <form>
            <div className="div-random">
                <button
                    onClick={(event) => generateRandomPatients(event, inputField, randomVolume, setUpdateRequired, setError)}>Add
                </button>
                <input className="input-narrow" value={randomVolume} onChange={onChange}/>
                <label>
                    random patient(s) to database
                </label>
            </div>
        </form>
    );
}

function Patients() {
    const [patients, setPatients] = useState([]);
    const [error, setError] = useState('');
    const [updateRequired, setUpdateRequired] = useState('false');
    const history = useHistory();

    return (
        <div>
            <h1>Patient list</h1>
            <PatientList patients={patients} setPatients={setPatients} updateRequired={updateRequired}
                         setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <button className="button-new" onClick={() => history.push('/patients/new')}>Register new patient</button>
            <PatientsRandom setUpdateRequired={setUpdateRequired} setError={setError}/>
            <PatientsError error={error}/>
        </div>
    );
}

export default Patients;