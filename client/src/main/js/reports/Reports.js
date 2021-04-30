import React, {useEffect, useRef, useState} from "react";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import {Paging} from "@axa-fr/react-toolkit-table";
import {useHistory} from "react-router";
import {reportUrl, reportsApiUrl} from "../api/URLs";
import axios from "axios";
import ModalError from "../modal/error";

function getPatients(inputData) {
    const {
        pageNumber, rowsPerPage, orderField, orderDirection,
        filterId, filterFamily,
        setPatients, setUpdateRequired, setError
    } = inputData;
    let url = reportsApiUrl + "/patients"
        + "?page=" + pageNumber + "&size=" + rowsPerPage
        + "&sort=" + orderField + "," + orderDirection;
    if (filterId !== '') {
        url = url + "&filterId=" + filterId;
    }
    if (filterFamily !== '') {
        url = url + "&filterFamily=" + filterFamily;
    }
    axios.get(url)
        .then(response => {
            if (response.data.numberOfElements === 0) {
                setError('Your selection criteria match no patient. Database may also be empty.');
            }
            setPatients(response.data);
        })
        .catch(exception => {
            setError("Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message);
        });
    setUpdateRequired(false);
}

export function getRiskColorClassName(risk) {
    if (risk.includes('None')) {
        return "risk-none";
    }
    if (risk.includes('Borderline')) {
        return "risk-borderline";
    }
    if (risk.includes('In danger')) {
        return "risk-in-danger";
    }
    if (risk.includes('Early onset')) {
        return "risk-early-onset";
    }
}

function PatientList({patients, setPatients, updateRequired, setUpdateRequired, setError, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [orderField, setOrderField] = React.useState('id');
    const [orderDirection, setOrderDirection] = React.useState('asc');
    const [filterId, setFilterId] = React.useState('');
    const [filterFamily, setFilterFamily] = React.useState('');

    useEffect(() => {
        if (updateRequired) {
            const inputData = {
                pageNumber, rowsPerPage, orderField, orderDirection,
                filterId, filterFamily, setPatients, setUpdateRequired, setError
            };
            getPatients(inputData);
        }
    });

    const handleSortByFamily = (event) => {
        const isAsc = orderField === 'family' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('family');
        setUpdateRequired(true);
    };

    if (patients.length === 0) return null;

    const handleSortById = (event) => {
        const isAsc = orderField === 'id' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('id');
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

    function submitFilterFamily(event) {
        event.preventDefault();
        setFilterFamily(document.getElementById('input-filter-family').value);
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
                    <th rowSpan="2">Risk level<br/>&nbsp;</th>
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
                </tr>
                </thead>
                <tbody>
                {patients.content.map(patient => (
                    <tr key={patient.id} onClick={() => history.push("/reports/patients/" + patient.id)}>
                        <td>{patient.id}</td>
                        <td>{patient.family}</td>
                        <td className={getRiskColorClassName(patient.risk)}>{patient.risk}</td>
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

function Reports() {
    const [patients, setPatients] = useState([]);
    const error = useRef('');
    const [, setModal] = useState(false);
    const [updateRequired, setUpdateRequired] = useState('false');
    const history = useHistory();

    function setError (message) {
        error.current = message;
        setModal(message.length > 0);
    }

    function closeErrorModal() {
        setError('');
    }

    return (
        <>
            <h1>Report list</h1>
            <PatientList patients={patients} setPatients={setPatients} updateRequired={updateRequired}
                         setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <ModalError message={error.current} closureAction={closeErrorModal}/>
            <a className="swagger-url" href={reportUrl + "/swagger-ui/"}>Swagger</a>
        </>
    );
}

export default Reports;