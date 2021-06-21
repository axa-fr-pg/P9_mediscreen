import React, {useEffect} from 'react';
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
import {useDispatch, useSelector} from "react-redux";
import {
    STATE_PATIENT,
    ACTION_DISPLAY_MODAL_ERROR,
    ACTION_DISPLAY_MODAL_SUCCESS,
    ACTION_SET_FILTER_DOB,
    ACTION_SET_FILTER_FAMILY,
    ACTION_SET_FILTER_ID,
    ACTION_SET_PAGE_NUMBER,
    ACTION_SET_ROWS_PER_PAGE,
    ACTION_SET_SORTING_FIELD,
    ACTION_SET_SORTING_DIRECTION,
    ACTION_SET_PATIENT_LIST,
    ACTION_SET_UPDATE_REQUIRED, ACTION_SET_ALL_PATIENT_FIELDS
} from "../reducers/reducerConstants";
import Modal from "../modal/modal";

export function getPatientList(patientState, dispatch, successCallback, errorCallback) {

    dispatch({type: ACTION_SET_UPDATE_REQUIRED, payload: false});

    axios.get(patientState.getPatientListUrl)
        .then((response = {numberOfElements: 0}) => {
            if (response.data.numberOfElements === 0) {
                if (errorCallback===undefined) {
                    dispatch({
                        type: ACTION_DISPLAY_MODAL_ERROR,
                        payload: 'Your selection criteria match no patient. Patient database may also be empty.'
                    });
                } else {
                    errorCallback();
                }
            } else {
                if (successCallback===undefined) {
                    dispatch({type: ACTION_SET_PATIENT_LIST, payload: response.data});
                } else {
                    successCallback(response.data);
                }
            }
        })
        .catch(exception => {
            if (errorCallback===undefined) {
                dispatch({
                    type: ACTION_DISPLAY_MODAL_ERROR,
                    payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                });
            } else {
                errorCallback();
            }
        });
}

function PatientList() {

    const history = useHistory();
    const dispatch = useDispatch();
    const patientState = useSelector(state => state[STATE_PATIENT]);

    useEffect(() => {
            getPatientList(patientState, dispatch);
    }, [history.location.pathname, patientState.isUpdateRequired,
        patientState.paging.pageNumber, patientState.paging.rowsPerPage,
        patientState.filter.id, patientState.filter.family, patientState.filter.dob,
        patientState.sorting]);

    if (patientState.patientList.length === 0) return null;

    const handleSortById = (event) => {
        const isAsc = patientState.sorting.field === 'id' && patientState.sorting.direction === 'asc';
        dispatch({type: ACTION_SET_SORTING_FIELD, payload: 'id'});
        dispatch({type: ACTION_SET_SORTING_DIRECTION, payload: isAsc ? 'desc' : 'asc'});
    };

    const handleSortByFamily = (event) => {
        const isAsc = patientState.sorting.field === 'family' && patientState.sorting.direction === 'asc';
        dispatch({type: ACTION_SET_SORTING_FIELD, payload: 'family'});
        dispatch({type: ACTION_SET_SORTING_DIRECTION, payload: isAsc ? 'desc' : 'asc'});
    };

    const handleSortByDob = (event) => {
        const isAsc = patientState.sorting.field === 'dob' && patientState.sorting.direction === 'asc';
        dispatch({type: ACTION_SET_SORTING_FIELD, payload: 'dob'});
        dispatch({type: ACTION_SET_SORTING_DIRECTION, payload: isAsc ? 'desc' : 'asc'});
    };

    function submitFilterId(event) {
        event.preventDefault();
        const inputField = document.getElementById('input-filter-id');
        const expectedId = inputField.value;
        dispatch({type: ACTION_SET_FILTER_ID, payload: expectedId})
        dispatch({type: ACTION_SET_PAGE_NUMBER, payload: 0})
    }

    function submitFilterDob(event) {
        event.preventDefault();
        dispatch({
            type: ACTION_SET_FILTER_DOB,
            payload: document.getElementById('input-filter-dob').value
        })
        dispatch({type: ACTION_SET_PAGE_NUMBER, payload: 0})
    }

    function onChangePaging(event) {
        const {numberItems, page} = event;
        dispatch({type: ACTION_SET_PAGE_NUMBER, payload: page - 1})
        if (page > (patientState.patientList.totalElements / numberItems)) {
            dispatch({
                type: ACTION_SET_PAGE_NUMBER,
                payload: Math.floor(patientState.patientList.totalElements / numberItems)
            })
        }
        dispatch({type: ACTION_SET_ROWS_PER_PAGE, payload: numberItems})
    }

    function submitFilterFamily(event) {
        event.preventDefault();
        dispatch({
            type: ACTION_SET_FILTER_FAMILY,
            payload: document.getElementById('input-filter-family').value
        })
        dispatch({type: ACTION_SET_PAGE_NUMBER, payload: 0})
    }

    return (
        <nav>
            <table>
                <thead>
                <tr>
                    <th>
                        <TableSortLabel
                            active={patientState.sorting.field === 'id'}
                            direction={patientState.sorting.direction}
                            onClick={handleSortById}>
                            Patient id
                        </TableSortLabel>
                    </th>
                    <th>
                        <TableSortLabel
                            active={patientState.sorting.field === 'family'}
                            direction={patientState.sorting.direction}
                            onClick={handleSortByFamily}>
                            Family name
                        </TableSortLabel>
                    </th>
                    <th>
                        <TableSortLabel
                            active={patientState.sorting.field === 'dob'}
                            direction={patientState.sorting.direction}
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
                {patientState.patientList.content.map(patient => (
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
                            currentPage={patientState.paging.pageNumber + 1}
                            numberPages={patientState.patientList.totalPages}
                            numberItems={patientState.paging.rowsPerPage}
                            displayLabel=""
                            elementsLabel=" patients per page"
                            previousLabel="« Previous"
                            nextLabel="Next »"
                            onChange={onChangePaging}
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
        dispatch({type: ACTION_DISPLAY_MODAL_ERROR, payload: "File contains at least one date with wrong format !"});
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
                dispatch({type: ACTION_DISPLAY_MODAL_ERROR, payload: exception.response.data});
            } else {
                dispatch({
                    type: ACTION_DISPLAY_MODAL_ERROR,
                    payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                });
            }
        });
}

function PatientsRandom() {
    const dispatch = useDispatch();

    function generateRandomPatients(event) {
        event.preventDefault();
        const inputField = document.getElementById('input-expected-number-of-patients');
        inputField.blur();
        const randomVolume = inputField.value;
// TODO    SetError("Processing request...");

        axios.post(patientsApiUrl + "/random/" + randomVolume)
            .then(response => {
                dispatch({type: ACTION_SET_UPDATE_REQUIRED, payload: true});
                dispatch({
                    type: ACTION_DISPLAY_MODAL_SUCCESS,
                    payload: response.data.length + " random patients have been generated successfully !"
                });
            })
            .catch(exception => {
                if (exception.response) {
                    dispatch({type: ACTION_DISPLAY_MODAL_ERROR, payload: exception.response.data});
                } else {
                    dispatch({
                        type: ACTION_DISPLAY_MODAL_ERROR,
                        payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                    });
                }
            });
    }

    return (
        <form className="form-random">
            <button
                onClick={(event) => generateRandomPatients(event)}>Add
            </button>
            <input id="input-expected-number-of-patients" className="input-narrow" defaultValue={5}/>
            <label>
                random patient(s) to database
            </label>
        </form>
    );
}

function PatientsUpload() {

    const dispatch = useDispatch();

    function waitAllPatientsPostedAndRefreshDisplay(numberOfPatientsToPost) {
        if (numberOfPatientsPosted < numberOfPatientsToPost) {
            setTimeout(waitAllPatientsPostedAndRefreshDisplay, 1000, numberOfPatientsToPost);
        } else {
            if (numberOfPatientsAdded === numberOfPatientsPosted) {
                dispatch({type: ACTION_SET_UPDATE_REQUIRED, payload: true});
                dispatch({
                    type: ACTION_DISPLAY_MODAL_SUCCESS,
                    payload: numberOfPatientsToPost + " patients have been uploaded successfully !"
                });
            }
        }
    }

    function addPatients(text) {
        numberOfPatientsPosted = 0;
        numberOfPatientsAdded = 0;
        const csvConfig = {
            delimiter: ";",
            skipEmptyLines: true
        };
        const results = readString(text, csvConfig);
        if (results.errors.length > 0) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
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
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "CSV file parsing has found " + numberOfLinesWithWrongFormat + " line(s) with wrong format. Aborting upload."
            });
            return;
        }
        results.data.forEach(line => postPatient(line, dispatch));
        waitAllPatientsPostedAndRefreshDisplay(results.data.length);
    }

    function uploadPatientFile(values) {
        if (values.length === 0) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "You selected an invalid file format. Please check and try again or ask your IT"
            });
            return;
        }
// TODO   SetError("Uploading " + values[0].file.name + " ...");
        fetch(values[0].file.preview)
            .then(response => response.blob())
            .then(blob => blob.text())
            .then(content => addPatients(content));
    }

    return (
        <File
            label="Browse file"
            placeholder="You can upload a CSV patient file with drag and drop here"
            id="file-to-be-uploaded"
            name="file-upload"
            accept=".csv"
            onChange={(values) => uploadPatientFile(values.values)}
        />
    );
}

function Patients() {
    const history = useHistory();
    const swaggerUrl = patientUrl + "/swagger-ui/";
    const dispatch = useDispatch();

    function onClickRegisterNewPatient() {
        dispatch({type: ACTION_SET_ALL_PATIENT_FIELDS, payload: {}});
        history.push('/patients/new');
    }

    return (
        <div>
            <h1>Patient list</h1>
            <PatientList/>
            <button className="button-new" onClick={onClickRegisterNewPatient}>Register new patient</button>
            <PatientsRandom/>
            <PatientsUpload/>
            <Modal/>
            <a className="swagger-url" href={swaggerUrl}>Swagger</a>
        </div>
    );
}

export default Patients;