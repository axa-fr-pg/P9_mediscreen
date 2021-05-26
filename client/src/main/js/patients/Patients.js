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
import {useDispatch} from "react-redux";
import {
    ACTION_DISPLAY_ERROR_MODAL,
    ACTION_DISPLAY_SUCCESS_MODAL
} from "../reducers/reducerConstants";
import Modal from "../modal/modal";

export function getPatients(getPatientsInputData) {
    const {
        pageNumber, rowsPerPage, orderField, orderDirection, filterId,
        filterFamily, filterDob, setPatients, dispatch
    } = getPatientsInputData;
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
                dispatch({
                    type: ACTION_DISPLAY_ERROR_MODAL,
                    payload: 'Your selection criteria match no patient. Database may also be empty.'
                });
            }
        })
        .catch(exception => {
            dispatch({
                type: ACTION_DISPLAY_ERROR_MODAL,
                payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
            });
        });
}

function PatientList({patients, setPatients, addedPatients, setAddedPatients, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [orderField, setOrderField] = useState('id');
    const [orderDirection, setOrderDirection] = useState('asc');
    const [filterId, setFilterId] = useState('');
    const [filterFamily, setFilterFamily] = useState('');
    const [filterDob, setFilterDob] = useState('');
    const dispatch = useDispatch();

    useEffect(() => {
        setAddedPatients(false);
        const getPatientsInputData = {
            pageNumber, rowsPerPage, orderField, orderDirection, filterId,
            filterFamily, filterDob, setPatients, dispatch
        };
        getPatients(getPatientsInputData);
    }, [addedPatients, pageNumber, rowsPerPage, orderField, orderDirection, filterId, filterFamily, filterDob]);

    if (patients.length === 0) return null;

    const handleSortById = (event) => {
        const isAsc = orderField === 'id' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('id');
    };

    const handleSortByFamily = (event) => {
        const isAsc = orderField === 'family' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('family');
    };

    const handleSortByDob = (event) => {
        const isAsc = orderField === 'dob' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('dob');
    };

    function submitFilterId(event) {
        event.preventDefault();
        const inputField = document.getElementById('input-filter-id');
        const expectedId = inputField.value;
        setFilterId(expectedId);
        setPageNumber(0);
    }

    function submitFilterDob(event) {
        event.preventDefault();
        setFilterDob(document.getElementById('input-filter-dob').value);
        setPageNumber(0);
    }

    function onChange(event) {
        const {numberItems, page} = event;
        setPageNumber(page - 1);
        if (page > (patients.totalElements / numberItems)) {
            setPageNumber(Math.floor(patients.totalElements / numberItems));
        }
        setRowsPerPage(numberItems);
    }

    function submitFilterFamily(event) {
        event.preventDefault();
        setFilterFamily(document.getElementById('input-filter-family').value);
        setPageNumber(0);
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

function convertSlashDateToDashDate(slashDate, dispatch) {
    const numbers = slashDate.split('/');
    if (numbers.length !== 3) {
        dispatch({type: ACTION_DISPLAY_ERROR_MODAL, payload: "File contains at least one date with wrong format !"});
    }
    const day = numbers[0];
    const month = numbers[1];
    const year = numbers[2];
    const date = new Date(year, month, day);
    return moment(date).format('YYYY-MM-DD');
}

let numberOfPatientsPosted;
let numberOfPatientsAdded;

function postPatient(line, dispatch) {
    const patient = {
        id: 0,
        family: line[0],
        given: line[1],
        dob: line[2],
        sex: line[3].replace(/ /g, ''),
        address: line[4],
        phone: line[5]
    };
    if (patient.dob.includes('/')) {
        patient.dob = convertSlashDateToDashDate(patient.dob, dispatch);
    }
    axios.post(patientsApiUrl, patient)
        .then(() => {
            numberOfPatientsPosted++;
            numberOfPatientsAdded++;
        })
        .catch(exception => {
            numberOfPatientsPosted++;
            if (exception.response) {
                dispatch({type: ACTION_DISPLAY_ERROR_MODAL, payload: exception.response.data});
            } else {
                dispatch({
                    type: ACTION_DISPLAY_ERROR_MODAL,
                    payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                });
            }
        });
}

function PatientsRandom({setAddedPatients}) {
    const dispatch = useDispatch();

    function generateRandomPatients(event, setAddedPatients) {
        event.preventDefault();
        const inputField = document.getElementById('input-expected-number-of-patients');
        inputField.blur();
        const randomVolume = inputField.value;
// TODO    SetError("Processing request...");

        axios.post(patientsApiUrl + "/random/" + randomVolume)
            .then(response => {
                setAddedPatients(true);
                dispatch({
                    type: ACTION_DISPLAY_SUCCESS_MODAL,
                    payload: response.data.length + " random patients have been generated successfully !"
                });
            })
            .catch(exception => {
                if (exception.response) {
                    dispatch({type: ACTION_DISPLAY_ERROR_MODAL, payload: exception.response.data});
                } else {
                    dispatch({
                        type: ACTION_DISPLAY_ERROR_MODAL,
                        payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                    });
                }
            });
    }

    return (
        <form className="form-random">
            <button
                onClick={(event) => generateRandomPatients(event, setAddedPatients)}>Add
            </button>
            <input id="input-expected-number-of-patients" className="input-narrow" defaultValue={5}/>
            <label>
                random patient(s) to database
            </label>
        </form>
    );
}

function PatientsUpload({setAddedPatients}) {

    const dispatch = useDispatch();

    function waitAllPatientsPostedAndRefreshDisplay(numberOfPatientsToPost, setAddedPatients) {
        if (numberOfPatientsPosted < numberOfPatientsToPost) {
            setTimeout(waitAllPatientsPostedAndRefreshDisplay, 1000, numberOfPatientsToPost, setAddedPatients);
        } else {
            if (numberOfPatientsAdded === numberOfPatientsPosted) {
                setAddedPatients(true);
                dispatch({
                    type: ACTION_DISPLAY_SUCCESS_MODAL,
                    payload: numberOfPatientsToPost + " patients have been uploaded successfully !"
                });
            }
        }
    }

    function addPatients(text, setAddedPatients) {
        numberOfPatientsPosted = 0;
        numberOfPatientsAdded = 0;
        const csvConfig = {
            delimiter: ";",
            skipEmptyLines: true
        };
        const results = readString(text, csvConfig);
        if (results.errors.length > 0) {
            dispatch({
                type: ACTION_DISPLAY_ERROR_MODAL,
                payload: "File parsing has encountered errors. Please check and try again or ask your IT"
            });
            return;
        }
        let numberOfLinesWithWrongFormat = 0;
        results.data.forEach(line => {
            if (line.length !== NUMBER_OF_PATIENT_FIELDS - 1) {
                numberOfLinesWithWrongFormat++;
            }
        });
        if (numberOfLinesWithWrongFormat > 0) {
            dispatch({
                type: ACTION_DISPLAY_ERROR_MODAL,
                payload: "CSV file parsing has found " + numberOfLinesWithWrongFormat + " line(s) with wrong format. Aborting upload."
            });
            return;
        }
        results.data.forEach(line => postPatient(line, dispatch));
        waitAllPatientsPostedAndRefreshDisplay(results.data.length, setAddedPatients);
    }

    function uploadPatientFile(values, setAddedPatients) {
        if (values.length === 0) {
            dispatch({
                type: ACTION_DISPLAY_ERROR_MODAL,
                payload: "You selected an invalid file format. Please check and try again or ask your IT"
            });
            return;
        }
// TODO   SetError("Uploading " + values[0].file.name + " ...");
        fetch(values[0].file.preview)
            .then(response => response.blob())
            .then(blob => blob.text())
            .then(content => addPatients(content, setAddedPatients));
    }

    return (
        <File
            label="Browse file"
            placeholder="You can upload a CSV patient file with drag and drop here"
            id="file-to-be-uploaded"
            name="file-upload"
            accept=".csv"
            onChange={(values) => uploadPatientFile(values.values, setAddedPatients)}
        />
    );
}

function Patients() {
    const [patients, setPatients] = useState([]);
    const [addedPatients, setAddedPatients] = useState(false);
    const history = useHistory();

    return (
        <div>
            <h1>Patient list</h1>
            <PatientList patients={patients} setPatients={setPatients} addedPatients={addedPatients}
                         setAddedPatients={setAddedPatients} history={history}/>
            <button className="button-new" onClick={() => history.push('/patients/new')}>Register new patient</button>
            <PatientsRandom setAddedPatients={setAddedPatients}/>
            <PatientsUpload setAddedPatients={setAddedPatients}/>
            <Modal/>
            <a className="swagger-url" href={patientUrl + "/swagger-ui/"}>Swagger</a>
        </div>
    );
}

export default Patients;