import React, {useState, useEffect} from 'react';
import axios from "axios";
import patientsApiUrl from './api';
import Patient from './Patient';
import {BrowserRouter, NavLink, Route, Switch} from "react-router-dom";

function Patients () {

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
            <footer>
                {error}
            </footer>
        );
    }

    function displayPatients() {
        if (patients.length === 0) return null;
        return (
            <nav className="patients-menu">
                <table>
                    <tr>
                        <th>Patient id</th>
                        <th>Family name</th>
                        <th>Date of birth</th>
                    </tr>
                    {patients.map(patient => (
                        <tr>
                            <td>
                                <a class="patients-link" href={"/patients/"+patient.id}>
                                    {patient.id}
                                </a>
                            </td>
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
            <h1>Patients management</h1>
            {displayPatients()}
            {displayError()}
        </div>
    );
}

export default Patients;