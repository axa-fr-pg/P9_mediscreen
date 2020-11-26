import React, {useState, useEffect} from 'react';
import axios from "axios";
import patientsApiUrl from './api';

function getPatients(setPatients, setUpdateRequired, setError) {
    axios.get(patientsApiUrl)
        .then(response => {
            setPatients(response.data);
            setUpdateRequired(false);
            if (response.data.length === 0) setError('Database is empty !');
        })
        .catch( error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data);
            } else {
                setError(error.message + " : check that the server is up and that the database is available !");
            }
        });
}

function PatientsList({patients, setPatients, error, updateRequired, setUpdateRequired, setError}) {

    useEffect(() => {
        if (updateRequired) getPatients(setPatients, setUpdateRequired, setError);
    });

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
                {patients.map(patient => (
                    <tr key={patient.id} onClick={()=>window.location.href="/patients/"+patient.id}>
                        <td>{patient.id}</td>
                        <td>{patient.family}</td>
                        <td>{patient.dob}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </nav>
    );
}

function PatientsError({error}) {

    if (! error) return null;
    return (
        <footer>
            {error}
        </footer>
    );
}

function generateRandomPatients(event, inputField, randomVolume, setUpdateRequired, setError) {
    event.preventDefault();
    inputField.blur();
    setError("Processing request...");

    axios.post(patientsApiUrl+"/random/"+randomVolume)
        .then(response => {
            setUpdateRequired(true);
            setError(response.data.length + " random patients have been generated successfully !");
        })
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data);
            } else {
                setError(error.message + " : check that the server is up and that the database is available !");
            }
        });
}

function PatientsRandom({setUpdateRequired, setError}) {
    const [randomVolume, setRandomVolume] = useState(5);
    const [inputField, setInputField] = useState(null);

    function onChange (field) {
        console.log("Saisie : ", field.target.nodeName);
        setRandomVolume(field.target.value);
        setInputField(field.target);
    }

    return (
        <form>
            <label>
                <button onClick={(event) => generateRandomPatients(event, inputField, randomVolume, setUpdateRequired, setError)}>Add</button>
                <input className="smallest-width" value={randomVolume} onChange={onChange} />
                random patient(s) to database
            </label>
        </form>
    );
}

function Patients() {
    const [patients, setPatients] = useState([]);
    const [error, setError] = useState('');
    const [updateRequired, setUpdateRequired] = useState('false');

    return (
        <div>
            <h1>Patient list</h1>
            <PatientsList patients={patients} setPatients={setPatients} updateRequired={updateRequired}
                          setUpdateRequired={setUpdateRequired} setError={setError} />
            <button onClick={() => window.location.href='/patients/new'}>Register new patient</button>
            <PatientsRandom setUpdateRequired={setUpdateRequired} setError={setError} />
            <PatientsError error={error} />
        </div>
    );
}

export default Patients;