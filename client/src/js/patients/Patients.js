import React, {useState, useEffect} from 'react';
import axios from "axios";
import patientsApiUrl from './api';

function Patients (props) {

    const [error, setError] = useState('');
    const [patients, setPatients] = useState([]);

    useEffect(() => {
        axios.get(patientsApiUrl)
            .then(response => {
                setPatients(response.data);
                console.log(response.data);
                if (response.data.length === 0) setError('Database is empty !');
                else setError('');
            })
            .catch( error => {
                    if (error.response) {
                        setError(error.status + " " + error.response);
                    } else {
                        setError(error.message + " : check that the server is up and running !");
                    }
                }
            );
    }, []);

    function displayError() {
        if (! error) return null;
        return (
            <div>
                <p>{error}</p>
            </div>
        );
    }

    return (
        <div>
            <h1>Patients management</h1>
            <table>
                <tr>
                    <th>Patient id</th>
                    <th>Family name</th>
                    <th>Date of birth</th>
                </tr>
                {patients.map(patient => (
                    <tr>
                        <td>{patient.id}</td>
                        <td>{patient.family}</td>
                        <td>{patient.dob}</td>
                    </tr>
                ))}
            </table>
            {displayError()}
        </div>
    );
}

export default Patients;