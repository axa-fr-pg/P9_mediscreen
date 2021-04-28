import React, {useState} from 'react';
import axios from "axios";
import {patientsApiUrl} from '../api/URLs';
import Switch from "react-switch";
import moment from 'moment'
import ModalError from "../modal/error";
import ModalSuccess from "../modal/success";
import {useHistory} from "react-router";

const patientFields = [
    {field : "id", label : "Patient id", readonly : true},
    {field : "family", label : "Family name"},
    {field : "given", label : "Given name"},
    {field : "dob", label : "Date of birth"},
    {field : "sex", label : "Sex"},
    {field : "address", label : "Address"},
    {field : "phone", label : "Phone"}
];

export const NUMBER_OF_PATIENT_FIELDS = 7;

function Patient({report}) {

    const [input, setInput] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [patient, setPatient] = useState({ id : window.location.pathname.split("/").pop(), family : '', given : '', dob : '', sex : '', address : '', phone : ''});
    const [modify, setModify] = useState(window.location.href.includes('new'));

    React.useEffect(() => {
        if (patient.id === 'new') return;
        if (isNaN(parseInt(patient.id))) {
            setError('It looks like you entered an invalid URL. Patient id must have a numeric value. Please check your request or ask your IT support !');
            setInput(false);
        } else {
            axios.get(patientsApiUrl + "/" + patient.id)
                .then(response => {
                    setPatient(response.data);
                })
                .catch( error => {
                    setInput(false);
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }, [patient.id]);

    function onClickSave(event) {
        event.preventDefault();
        setError('');
        setSuccess('');

        const givenDate = moment(patient.dob, "YYYY-MM-DD", true).toDate();
        const givenTime = givenDate.getTime();

        if (isNaN(givenTime) || givenTime < -5000000000000) {
            setError("Please enter a valid date of birth with format YYYY-MM-DD ("+ patient.dob + " is invalid).");
            return;
        }

        const body = {...patient};
        if (patient.id === 'new') {
            body.id=0;
            axios.post(patientsApiUrl, body)
                .then(response => {
                    body.id=response.data.id;
                    setInput(false);
                    setSuccess("Patient created successfully with id=" + body.id);
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        } else
        {
            axios.put(patientsApiUrl + "/" + patient.id, body)
                .then(response => {
                    setPatient(response.data);
                    setSuccess('Patient has been saved successfully !');
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }

    function onChange (field) {
        field.persist();
        const newPatient = {...patient};
        newPatient[field.target.name] = field.target.value;
        setPatient(newPatient);
    }

    function displayField(field, label, readonly) {
        if (!input) return null;
        const disabled = !!readonly;
        if (field === 'id' && patient.id === 'new') return null;
        return (<div key={field}>
            <label>{label}
                <input value={patient[field]} name={field} onChange={onChange} disabled={disabled||modify===false} />
            </label>
        </div>);
    }

    function onChangeModify() {
        setModify(!modify);
    }

    function displayModifySwitch() {
        if (!input || window.location.href.includes('new') || !report===false) {
            return null;
        }
        return(
            <div key={"div-read-only"} className="div-read-only">
                <label>View</label>
                <div className="switch-read-only">
                    <Switch checked={modify} onChange={onChangeModify} checkedIcon={false} uncheckedIcon={false} />
                </div>
                <label>Edit</label>
            </div>
        );
    }

    function displaySaveButton() {
        if (!input || !modify) return null;
        return(
            <button className="button-save" onClick={onClickSave}>Save</button>
        );
    }

    function displayTitle() {
        const view = modify ? 'edit' : 'view';
        const title = patient.id === 'new' ? 'creation' : view;
        return(
            <h1>Patient {title}</h1>
        );
    }

    return (
        <div>
            {displayTitle()}
            <form>
                {patientFields.map(fieldSpec => displayField(fieldSpec.field, fieldSpec.label, fieldSpec.readonly))}
                {displayModifySwitch()}
                {displaySaveButton()}
                <ModalError message={error} closureAction={()=>setError('')}/>
                <ModalSuccess message={success} closureAction={() => setModify(false)}/>
            </form>
        </div>
    );
}

export default Patient;