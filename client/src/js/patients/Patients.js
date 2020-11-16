import React, {useState, useEffect} from 'react';
import axios from "axios";
import patientsApiUrl from './api';

function Patients () {

    const [error, setError] = useState('');
    const [patients, setPatients] = useState([]);

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
                        setError(error.message + " : check that the server is up and running !");
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

    return (
        <div>
            <h1>Patient list</h1>
            {displayPatients()}
            <button onClick={() => window.location.href='/patients/new'}>Add patient</button>
            {displayError()}
        </div>
    );
}

export default Patients;