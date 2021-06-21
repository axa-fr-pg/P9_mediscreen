import React, {useEffect, useState} from "react";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import {Paging} from "@axa-fr/react-toolkit-table";
import {useHistory} from "react-router";
import {reportUrl, reportsApiUrl} from "../api/URLs";
import axios from "axios";
import Modal from "../modal/modal";
import {useDispatch} from "react-redux";
import {ACTION_DISPLAY_MODAL_ERROR} from "../reducers/reducerConstants";

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

function PatientList({patients, setPatients, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [orderField, setOrderField] = useState('id');
    const [orderDirection, setOrderDirection] = useState('asc');
    const [filterId, setFilterId] = useState('');
    const [filterFamily, setFilterFamily] = useState('');
    const dispatch = useDispatch();

    useEffect(() => {
        const getPatientsInputData = {
            pageNumber, rowsPerPage, orderField, orderDirection,
            filterId, filterFamily, setPatients
        };
        getPatients(getPatientsInputData);
    }, [pageNumber, rowsPerPage, orderField, orderDirection, filterId, filterFamily]);

    function getPatients(getPatientsInputData) {
        const {
            pageNumber, rowsPerPage, orderField, orderDirection,
            filterId, filterFamily, setPatients
        } = getPatientsInputData;
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
                    dispatch({type: ACTION_DISPLAY_MODAL_ERROR,
                        payload: 'Your selection criteria match no report. Patient database may also be empty.'});
                }
                setPatients(response.data);
            })
            .catch(exception => {
                dispatch({type: ACTION_DISPLAY_MODAL_ERROR,
                    payload: "Please ask your IT support : it seems that the server or the database is unavailable ! "
                        + exception.message});
            });
    }

    const handleSortByFamily = (event) => {
        const isAsc = orderField === 'family' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('family');
    };

    if (patients.length === 0) return null;

    const handleSortById = (event) => {
        const isAsc = orderField === 'id' && orderDirection === 'asc';
        setOrderDirection(isAsc ? 'desc' : 'asc');
        setOrderField('id');
    };

    function submitFilterId(event) {
        event.preventDefault();
        const inputField = document.getElementById('input-filter-id');
        const expectedId = inputField.value;
        setFilterId(expectedId);
        setPageNumber(0);
    }

    function submitFilterFamily(event) {
        event.preventDefault();
        setFilterFamily(document.getElementById('input-filter-family').value);
        setPageNumber(0);
    }

    function onChangePaging(event) {
        const {numberItems, page} = event;
        setPageNumber(page - 1);
        if (page > (patients.totalElements / numberItems)) {
            setPageNumber(Math.floor(patients.totalElements / numberItems));
        }
        setRowsPerPage(numberItems);
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
                            onChange={onChangePaging}
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
    const history = useHistory();

    return (
        <>
            <h1>Report list</h1>
            <PatientList patients={patients} setPatients={setPatients} history={history}/>
            <Modal/>
            <a className="swagger-url" href={reportUrl + "/swagger-ui/"}>Swagger</a>
        </>
    );
}

export default Reports;