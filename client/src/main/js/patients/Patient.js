import React, {useEffect} from 'react';
import axios from "axios";
import {patientsApiUrl} from '../api/URLs';
import Switch from "react-switch";
import moment from 'moment'
import {useHistory} from "react-router";
import {useDispatch, useSelector} from "react-redux";
import {
    ACTION_DISPLAY_MODAL_ERROR,
    ACTION_DISPLAY_MODAL_SUCCESS,
    ACTION_SET_ALL_PATIENT_FIELDS,
    ACTION_SET_MODIFY_ALLOWED,
    ACTION_SET_PATIENT_FIELD,
    STATE_PATIENT
} from "../reducers/reducerConstants";
import Modal from "../modal/modal";

const patientFieldListSpecification = [
    {field: "id", label: "Patient id", readOnly: true},
    {field: "family", label: "Family name"},
    {field: "given", label: "Given name"},
    {field: "dob", label: "Date of birth"},
    {field: "sex", label: "Sex"},
    {field: "address", label: "Address"},
    {field: "phone", label: "Phone"}
];

export const NUMBER_OF_PATIENT_FIELDS = 7;

function ModifySwitch({report}) {

    const patientState = useSelector(state => state[STATE_PATIENT]);
    const dispatch = useDispatch();

    if (window.location.pathname.includes('new') || !report === false) {
        return null;
    }

    function onChangeModify() {
        dispatch({type: ACTION_SET_MODIFY_ALLOWED, payload: !patientState.isModifyAllowed});
    }

    return (
        <div key={"div-read-only"} className="div-read-only">
            <label>View</label>
            <div className="switch-read-only">
                <Switch checked={patientState.isModifyAllowed} onChange={onChangeModify} checkedIcon={false}
                        uncheckedIcon={false}/>
            </div>
            <label>Edit</label>
        </div>
    );
}

function PatientField({field, label, readOnly}) {

    const patientState = useSelector(state => state[STATE_PATIENT]);
    const dispatch = useDispatch();

    if (field === 'id' && patientState.patientFields[field] === 'new') {
        return null;
    }

    function onChangeField(field) {
        field.persist(); // make input available asynchronously
        patientState.patientFields[field.target.name] = field.target.value;
        dispatch({
            type: ACTION_SET_PATIENT_FIELD,
            payload: {field: field.target.name, value: field.target.value}
        });
    }

    const disabled = !!readOnly;
    return (<div key={field}>
        <label>{label}
            <input value={patientState.patientFields[field]} name={field} onChange={onChangeField}
                   disabled={disabled || patientState.isModifyAllowed === false}/>
        </label>
    </div>);
}

function PatientFields() {
    return patientFieldListSpecification.map(fieldSpec => {
        return (
            <PatientField key={fieldSpec.field} field={fieldSpec.field}
                          label={fieldSpec.label} readOnly={fieldSpec.readOnly}
            />);
    });
}

function SaveButton({onClickSave}) {

    const patientState = useSelector(state => state[STATE_PATIENT]);

    if (!patientState.isModifyAllowed) return null;
    return (
        <button className="button-save" onClick={onClickSave}>Save</button>
    );
}

function Patient({report}) {
    const patientId = window.location.pathname.split("/").pop();
    const baseUri = window.location.pathname.split("/").slice(-2).shift();
    const history = useHistory();
    const dispatch = useDispatch();
    const patientState = useSelector(state => state[STATE_PATIENT]);

    useEffect(() => {
        dispatch({type: ACTION_SET_MODIFY_ALLOWED, payload: window.location.pathname.includes('new')});
        if (patientId === 'new') {
            dispatch({type: ACTION_SET_ALL_PATIENT_FIELDS, payload: { id : patientId}});
        } else if (isNaN(parseInt(patientId))) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: 'It looks like you entered an invalid URL. Patient id must have a numeric value. Please check your request or ask your IT support !'
            });
        } else {
            axios.get(patientsApiUrl + "/" + patientId)
                .then(response => {
                    dispatch({type: ACTION_SET_ALL_PATIENT_FIELDS, payload: response.data});
                })
                .catch(exception => {
                    dispatch({
                        type: ACTION_SET_PATIENT_FIELD,
                        payload: {field: 'id', value: 'not-found'}
                    });
                    dispatch({
                        type: ACTION_DISPLAY_MODAL_ERROR,
                        payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                    });
                });
        }
    }, []);

    if (baseUri !== 'patients') {
        dispatch({
            type: ACTION_DISPLAY_MODAL_ERROR,
            payload: 'It looks like you entered an invalid URL. Please check your request or ask your IT support !'
        });
    }

    function onClickSave(event) {
        event.preventDefault();
        const body = {...patientState.patientFields};
        const givenDate = moment(body.dob, "YYYY-MM-DD", true).toDate();
        const givenTime = givenDate.getTime();

        if (isNaN(givenTime) || givenTime < -5000000000000) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "Please enter a valid date of birth with format YYYY-MM-DD (" + body.dob + " is invalid)."
            });
        } else if (body.id === 'new') {
            body.id = 0;
            axios.post(patientsApiUrl, body)
                .then(response => {
                    body.id = response.data.id;
                    dispatch({
                        type: ACTION_DISPLAY_MODAL_SUCCESS,
                        payload: "Patient created successfully with id=" + body.id
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
        } else {
            axios.put(patientsApiUrl + "/" + patientState.patientFields.id, body)
                .then(response => {
                    dispatch({type: ACTION_SET_ALL_PATIENT_FIELDS, payload: response.data});
                    dispatch({
                        type: ACTION_DISPLAY_MODAL_SUCCESS,
                        payload: 'Patient has been saved successfully !'
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
    }

    function displayTitle() {
        const view = patientState.isModifyAllowed ? 'edit' : 'view';
        const title = patientState.patientFields.id === 'new' ? 'creation' : view;
        return (
            <h1>Patient {title}</h1>
        );
    }

    function closeErrorModal() {
        if ((!window.location.pathname.includes('new') && !patientState.isModifyAllowed)
            || baseUri !== 'patients') {
            history.push('/patients');
        }
    }

    function closeSuccessModal() {
        dispatch({type: ACTION_SET_MODIFY_ALLOWED, payload: false});
        if (window.location.pathname.includes('new')) {
            history.push('/patients')
        }
    }

    return (
        <div>
            {displayTitle()}
            <form>
                <PatientFields/>
                <ModifySwitch report={report}/>
                <SaveButton onClickSave={onClickSave}/>
                <Modal errorClosureAction={closeErrorModal} successClosureAction={closeSuccessModal}/>
            </form>
        </div>
    );
}

export default Patient;