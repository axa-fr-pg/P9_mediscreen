import React, {useState, useEffect} from 'react';
import axios from "axios";
import {patientsApiUrl} from '../api/URLs';
import {useHistory} from "react-router";
import TablePagination from "@material-ui/core/TablePagination";
import TableSortLabel from '@material-ui/core/TableSortLabel';

function getPatients(pageNumber, rowsPerPage, orderField, orderDirection, setPatients, setUpdateRequired, setError) {
    axios.get(patientsApiUrl + "?page=" + pageNumber + "&size=" + rowsPerPage
        + "&sort=" + orderField + "," + orderDirection)
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

function PatientList({patients, setPatients, updateRequired, setUpdateRequired, setError, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [orderField, setOrderField] = React.useState('id');
    const [orderDirection, setOrderDirection] = React.useState('asc');

    useEffect(() => {
        if (updateRequired) getPatients(pageNumber, rowsPerPage, orderField,
            orderDirection, setPatients, setUpdateRequired, setError);
    });

    function onChangePageNumber(event, pageIndex) {
        setPageNumber(pageIndex);
        setUpdateRequired(true);
    }

    const handleSortById = (event) => {
        const isAsc = orderField === 'id' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('id');
        setUpdateRequired(true);
    };

    const handleSortByFamily = (event) => {
        const isAsc = orderField === 'family' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('family');
        setUpdateRequired(true);
    };

    const handleSortByDob = (event) => {
        const isAsc = orderField === 'dob' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('dob');
        setUpdateRequired(true);
    };

    function onChangeRowsPerPage(event) {
        const pageSize = event.target.value;
        setPageNumber(Math.floor(rowsPerPage * pageNumber / pageSize));
        setRowsPerPage(pageSize);
        setUpdateRequired(true);
    }

    if (patients.length === 0) return null;

    return (
        <nav>
            <table>
                <thead>
                <tr>
                    <th>
                        <TableSortLabel
                            active={orderField === 'id'}
                            direction={orderDirection}
                            onClick={handleSortById}>
                            Patient id
                        </TableSortLabel>
                    </th>
                    <th>
                        <TableSortLabel
                            active={orderField === 'family'}
                            direction={orderDirection}
                            onClick={handleSortByFamily}>
                            Family name
                        </TableSortLabel>
                    </th>
                    <th>
                        <TableSortLabel
                            active={orderField === 'dob'}
                            direction={orderDirection}
                            onClick={handleSortByDob}>
                            Date of birth
                        </TableSortLabel>
                    </th>
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
                <tfoot>
                <tr>
                    <TablePagination
                        page={pageNumber}
                        rowsPerPage={patients.pageable.pageSize}
                        count={patients.totalElements}
                        onChangePage={onChangePageNumber}
                        onChangeRowsPerPage={onChangeRowsPerPage}
                        labelRowsPerPage="Patients per page"
                    />
                </tr>
                </tfoot>
            </table>
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