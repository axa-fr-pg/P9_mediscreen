import React, {useState, useEffect} from 'react';
import axios from "axios";
import patientsApiUrl from './api';

function Patients () {

    const [error, setError] = useState('');
    const [patients, setPatients] = useState([]);
    const [randomVolume, setRandomVolume] = useState(5);

    useEffect(() => {
        axios.get(patientsApiUrl)
            .then(response => {
                setPatients(response.data);
                if (response.data.length === 0) setError('Database is empty !');
                else setError('');
            })
            .catch( error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " : check that the server is up and that the database is available !");
                    }
            });
    }, []);

    function displayError() {
        if (! error) return null;
        return (
            <footer>
                {error}
            </footer>
        );
    }

    function generateRandomPatients() {
        axios.post(patientsApiUrl+"/random/"+randomVolume)
            .then(response => {
                setPatients(response.data);
                if (response.data.length === 0) setError('Could not populate database !');
                else setError('');
            })
            .catch(error => {
                if (error.response) {
                    setError(error.response.status + " " + error.response.data);
                } else {
                    setError(error.message + " : check that the server is up and that the database is available !");
                }
            });
    }

    function displayPatients() {
        if (patients.length === 0) return null;
        return (
            <nav>
                <table>
                    <tr>
                        <th>Patient id</th>
                        <th>Family name</th>
                        <th>Date of birth</th>
                    </tr>
                    {patients.map(patient => (
                        <tr onClick={()=>window.location.href="/patients/"+patient.id}>
                            <td>{patient.id}</td>
                            <td>{patient.family}</td>
                            <td>{patient.dob}</td>
                        </tr>
                    ))}
                </table>
            </nav>
        );
    }

    function displayRandomRequest() {
        return (
            <form>
                <label>
                    <button onClick={generateRandomPatients}>Add</button>
                    <input className="smallest-width" defaultValue={randomVolume} onChange={onChange}/>
                    random patients to database
                </label>
            </form>
        );
    }

    function onChange (field) {
        field.persist();
        setRandomVolume(field.target.value);
    }

    return (
        <div>
            <h1>Patient list</h1>
            {displayPatients()}
            <button onClick={() => window.location.href='/patients/new'}>Register new patient</button>
            {displayRandomRequest()}
            {displayError()}
        </div>
    );
}

export default Patients;