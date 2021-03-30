import React, {useEffect, useState} from "react";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import {Paging} from "@axa-fr/react-toolkit-table";
import {useHistory} from "react-router";
import {reportsApiUrl} from "../api/URLs";
import axios from "axios";

function getPatients(inputData) {
    const {
        pageNumber, rowsPerPage, orderField, orderDirection,
        filterId, filterFamily, filterDob,
        setPatients, setUpdateRequired, setError
    } = inputData;
    let url = reportsApiUrl + "/patients"
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
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
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

    function submitFilterFamily(event) {
        event.preventDefault();
        setFilterFamily(document.getElementById('input-filter-family').value);
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
                            Risk level
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
                        <td>{patient.risk}</td>
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

function ReportsError({error}) {

    if (!error) return null;
    return (
        <footer>
            {error}
        </footer>
    );
}

function Reports() {
    const [patients, setPatients] = useState([]);
    const [error, setError] = useState('');
    const [updateRequired, setUpdateRequired] = useState('false');
    const history = useHistory();

    return (
        <>
            <h1>Report list</h1>
            <PatientList patients={patients} setPatients={setPatients} updateRequired={updateRequired}
                         setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <ReportsError error={error}/>
        </>
    );
}

export default Reports;