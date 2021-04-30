import React, {useState, useEffect, useRef} from 'react';
import axios from "axios";
import {patientUrl, patientsApiUrl} from '../api/URLs';
import {useHistory} from "react-router";
import TableSortLabel from '@material-ui/core/TableSortLabel';
import {Paging} from "@axa-fr/react-toolkit-table";
import {File} from '@axa-fr/react-toolkit-form-input-file';
import '@axa-fr/react-toolkit-form-input-file/dist/file.scss';
import {readString} from 'react-papaparse';
import {NUMBER_OF_PATIENT_FIELDS} from './Patient';
import moment from 'moment';
import ModalError from "../modal/error";
import ModalSuccess from "../modal/success";

export function getPatients(inputData) {
    const {
        pageNumber, rowsPerPage, orderField, orderDirection,
        filterId, filterFamily, filterDob,
        setPatients, setUpdateRequired, setError
    } = inputData;
    let url = patientsApiUrl
        + "?page=" + pageNumber + "&size=" + rowsPerPage
        + "&sort=" + orderField + "," + orderDirection;
    if (filterId !== '') {
        url = url + "&id=" + filterId;
    }
    if (filterFamily !== '') {
        url = url + "&family=" + filterFamily;
    }
    if (filterDob !== '') {
        url = url + "&dob=" + filterDob;
    }
    axios.get(url)
        .then(response => {
            setPatients(response.data);
            if (response.data.numberOfElements === 0) {
                setError('Your selection criteria match no patient (or the database is empty).');
            }
        })
        .catch(exception => {
            if (exception.response) {
                setError(exception.response.status + " " + exception.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
            } else {
                setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
    setUpdateRequired(false);
}

function PatientList({patients, setPatients, updateRequired, setUpdateRequired, setError, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [orderField, setOrderField] = React.useState('id');
    const [orderDirection, setOrderDirection] = React.useState('asc');
    const [filterId, setFilterId] = React.useState('');
    const [filterFamily, setFilterFamily] = React.useState('');
    const [filterDob, setFilterDob] = React.useState('');

    useEffect(() => {
        if (updateRequired) {
            const inputData = {
                pageNumber, rowsPerPage, orderField, orderDirection,
                filterId, filterFamily, filterDob,
                setPatients, setUpdateRequired, setError
            };
            getPatients(inputData);
        }
    });

    if (patients.length === 0) return null;

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

    function submitFilterId(event) {
        event.preventDefault();
        const inputField = document.getElementById('input-filter-id');
        const expectedId = inputField.value;
        setFilterId(expectedId);
        setPageNumber(0);
        setUpdateRequired(true);
    }

    function submitFilterDob(event) {
        event.preventDefault();
        setFilterDob(document.getElementById('input-filter-dob').value);
        setPageNumber(0);
        setUpdateRequired(true);
    }

    function onChange(event) {
        const {numberItems, page} = event;
        setPageNumber(page - 1);
        if (page > (patients.totalElements / numberItems)) {
            setPageNumber(Math.floor(patients.totalElements / numberItems));
        }
        setRowsPerPage(numberItems);
        setUpdateRequired(true);
    }

    function submitFilterFamily(event) {
        event.preventDefault();
        setFilterFamily(document.getElementById('input-filter-family').value);
        setPageNumber(0);
        setUpdateRequired(true);
    }

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
                <tr>
                    <th>
                        <form className="form-filter" onSubmit={submitFilterId}>
                            <input className="filter-input" id="input-filter-id" type="text"
                                   onBlur={submitFilterId}/>
                        </form>
                    </th>
                    <th>
                        <form className="form-filter" onSubmit={submitFilterFamily}>
                            <input className="filter-input" id="input-filter-family" type="text"
                                   onBlur={submitFilterFamily}/>
                        </form>
                    </th>
                    <th>
                        <form className="form-filter" onSubmit={submitFilterDob}>
                            <input className="filter-input" id="input-filter-dob" type="text"
                                   onBlur={submitFilterDob}/>
                        </form>
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
                    <td colSpan={3}>
                        <Paging
                            currentPage={pageNumber + 1}
                            numberPages={patients.totalPages}
                            numberItems={rowsPerPage}
                            displayLabel=""
                            elementsLabel=" patients per page"
                            previousLabel="« Previous"
                            nextLabel="Next »"
                            onChange={onChange}
                        />
                    </td>
                </tr>
                </tfoot>
            </table>
        </nav>
    );
}

function generateRandomPatients(event, setUpdateRequired, setSuccess, setError) {
    event.preventDefault();
    const inputField = document.getElementById('input-expected-number-of-patients');
    inputField.blur();
    const randomVolume = inputField.value;
// TODO    setError("Processing request...");

    axios.post(patientsApiUrl + "/random/" + randomVolume)
        .then(response => {
            setUpdateRequired(true);
            setSuccess(response.data.length + " random patients have been generated successfully !");
        })
        .catch(exception => {
            if (exception.response) {
                setError(exception.response.status + " " + exception.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
            } else {
                setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
}

function convertSlashDateToDashDate(slashDate, setError) {
    const numbers = slashDate.split('/');
    if (numbers.length !== 3) {
        setError("File contains at least one date with wrong format !")
    }
    const day = numbers[0];
    const month = numbers[1];
    const year = numbers[2];
    const date = new Date(year, month, day);
    return moment(date).format('YYYY-MM-DD');
}

let numberOfPatientsPosted;
let numberOfPatientsAdded;

function postPatient(line, setError) {
    const patient = {
        id : 0,
        family : line[0],
        given : line[1],
        dob : line[2],
        sex : line[3].replace(/ /g,''),
        address : line[4],
        phone : line[5]
    };
    if (patient.dob.includes('/')) {
        patient.dob = convertSlashDateToDashDate(patient.dob, setError);
    }
    axios.post(patientsApiUrl, patient)
        .then(() => {numberOfPatientsPosted++; numberOfPatientsAdded++;})
        .catch(exception => {
            numberOfPatientsPosted++;
            if (exception.response) {
                setError(exception.response.status + " " + exception.response.data);
            } else {
                setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
}

function waitAllPatientsPostedAndRefreshDisplay(numberOfPatientsToPost, setUpdateRequired, setSuccess, setError) {
    if (numberOfPatientsPosted < numberOfPatientsToPost) {
        setTimeout(waitAllPatientsPostedAndRefreshDisplay, 1000, numberOfPatientsToPost, setUpdateRequired, setSuccess, setError);
    } else {
        setUpdateRequired(true);
        if (numberOfPatientsAdded === numberOfPatientsPosted) {
            setSuccess(numberOfPatientsToPost +" patients have been uploaded successfully !")
        }
    }
}

function addPatients(text, setUpdateRequired, setSuccess, setError) {
    numberOfPatientsPosted = 0;
    numberOfPatientsAdded = 0;
    const csvConfig = {
        delimiter: ";",
        skipEmptyLines: true
    };
    const results = readString(text, csvConfig);
    if (results.errors.length > 0) {
        setError("File parsing has encountered errors. Please check and try again or ask your IT");
        return;
    }
    let numberOfLinesWithWrongFormat = 0;
    results.data.forEach(line => {
        if (line.length !== NUMBER_OF_PATIENT_FIELDS - 1) {
            numberOfLinesWithWrongFormat++;
        }
    });
    if (numberOfLinesWithWrongFormat > 0) {
        setError("CSV file parsing has found " + numberOfLinesWithWrongFormat + " line(s) with wrong format. Aborting upload.");
        return;
    }
    results.data.forEach(line => postPatient(line, setError));
    waitAllPatientsPostedAndRefreshDisplay(results.data.length, setUpdateRequired, setSuccess, setError);
}

function uploadPatientFile(values, setUpdateRequired, setSuccess, setError) {
    if (values.length === 0) {
        setError("You selected an invalid file format. Please check and try again or ask your IT");
        return;
    }
// TODO   setError("Uploading " + values[0].file.name + " ...");
    fetch(values[0].file.preview)
        .then(response => response.blob())
        .then(blob => blob.text())
        .then(content => addPatients(content, setUpdateRequired, setSuccess, setError));
}

function PatientsRandom({setUpdateRequired, setSuccess, setError}) {
    return (
            <form className="form-random">
                <button
                    onClick={(event) => generateRandomPatients(event, setUpdateRequired, setSuccess, setError)}>Add
                </button>
                <input id="input-expected-number-of-patients" className="input-narrow" defaultValue={5}/>
                <label>
                    random patient(s) to database
                </label>
            </form>
    );
}

function PatientsUpload({setUpdateRequired, setSuccess, setError}) {
    return (
        <File
            label="Browse file"
            placeholder="You can upload a CSV patient file with drag and drop here"
            id="file-to-be-uploaded"
            name="file-upload"
            accept=".csv"
            onChange={(values) => uploadPatientFile(values.values, setUpdateRequired, setSuccess, setError)}
        />
    );
}

function Patients() {
    const error = useRef('');
    const success = useRef('');
    const [, setModal] = useState(false);
    const [patients, setPatients] = useState([]);
    const [updateRequired, setUpdateRequired] = useState('false');
    const history = useHistory();

    function setError (message) {
        error.current = message;
        setModal(message.length > 0);
    }

    function setSuccess (message) {
        success.current = message;
        setModal(message.length > 0);
    }

    function closeErrorModal() {
        setError('');
    }

    function closeSuccessModal() {
        setSuccess('');
    }

    return (
        <div>
            <h1>Patient list</h1>
            <PatientList patients={patients} setPatients={setPatients} updateRequired={updateRequired}
                         setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <button className="button-new" onClick={() => history.push('/patients/new')}>Register new patient</button>
            <PatientsRandom setUpdateRequired={setUpdateRequired} setSuccess={setSuccess} setError={setError}/>
            <PatientsUpload setUpdateRequired={setUpdateRequired} setSuccess={setSuccess} setError={setError}/>
            <ModalError message={error.current} closureAction={closeErrorModal}/>
            <ModalSuccess message={success.current} closureAction={closeSuccessModal}/>
            <a className="swagger-url" href={patientUrl + "/swagger-ui/"}>Swagger</a>
        </div>
    );
}

export default Patients;