import React, {useState} from 'react';
import axios from "axios";
import patientsApiUrl from './api';
import ToggleButton from 'react-toggle-button';

const patientFields = [
    {field : "id", label : "Patient id", readonly : true},
    {field : "family", label : "Family name"},
    {field : "given", label : "Given name"},
    {field : "dob", label : "Date of birth"},
    {field : "sex", label : "Sex"},
    {field : "address", label : "Address"},
    {field : "phone", label : "Phone"}
];

function Patient() {

    const [input, setInput] = useState(true);
    const [error, setError] = useState('');
    const [patient, setPatient] = React.useState({ id : window.location.pathname.split("/").pop(), family : '', given : '', dob : '', sex : '', address : '', phone : ''});
    const [modify, setModify] = useState(window.location.href.includes('new'));

    function displayError() {
        if (! error) return null;
        return (
            <footer>
                {error}
            </footer>
        );
    }

    React.useEffect(() => {
        if (patient.id === 'new') return;
        if (isNaN(parseInt(patient.id))) {
            setError('Patient id must have a numeric value !');
            setInput(false);
        } else
        {
            axios.get(patientsApiUrl + "/" + patient.id)
                .then(response => {
                    setPatient(response.data);
                    setError('');
                })
                .catch( error => {
                    setInput(false);
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " : check that the server is up and running !");
                    }
                });
        }
    }, [patient.id]);

    function onClickSave(event) {
        event.preventDefault();

        const dobHasCorrectFormat = patient.dob.valueOf().match(/^\d{4}-\d{2}-\d{2}$/);
        if (!dobHasCorrectFormat) {
            setError("Please enter date of birth with format YYYY-MM-DD ("+ patient.dob + " is invalid).");
            return;
        }

        const body = {...patient};
        if (patient.id === 'new') {
            body.id=0;
            axios.post(patientsApiUrl, body)
                .then(response => {
                    body.id=response.data.id;
                    setInput(false);
                    setError("Patient created successfully with id=" + body.id);
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " : check that the server is up and running !");
                    }
                });
        } else
        {
            axios.put(patientsApiUrl + "/" + patient.id, body)
                .then(response => {
                    setPatient(response.data);
                    setError('Patient has been saved successfully !');
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " : check that the server is up and running !");
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

    function onToggleModify() {
        setModify(!modify);
        setError('');
    }

    function displayModifyButton() {
        if (!input || window.location.href.includes('new')) {
            return null;
        }
        return(
            <div key={"toggle-readOnly"} class="toggle-div">
                <label>Modify client</label>
                <div class="toggle-button">
                    <ToggleButton value={modify} onToggle={onToggleModify} />
                </div>
            </div>
        );
    }

    function displaySaveButton() {
        if (!input || !modify) return null;
        return(
            <button onClick={onClickSave}>Save</button>
        );
    }

    function displayTitle() {
        const title = patient.id === 'new' ? 'creation' : 'edit';
        return(
            <h1>Patient {title}</h1>
        );
    }

    return (
        <div>
            {displayTitle()}
            <form>
                {patientFields.map(fieldSpec => displayField(fieldSpec.field, fieldSpec.label, fieldSpec.readonly))}
                {displayModifyButton()}
                {displaySaveButton()}
                {displayError()}
            </form>
        </div>
    );
}

export default Patient;