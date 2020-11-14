import React from 'react';
import '../client.css';
import axios from 'axios';

const patientApiUrl = "http://patient:8081/patients";

function PatientDetailLine(props) {

    const disabled = props.disabled ? true : false;

    return (<div>
        <label>{props.label}
            <input value={props.input} name={props.name} onChange={props.onChange} disabled={disabled} />
        </label>
    </div>);
}

function PatientDetail (props) {

    const [patient, setPatient] = React.useState({ id : '', family : '', given : '', dob : '', sex : '', address : '', phone : ''});

    React.useEffect(() => {
        if (props.id !== 0) {
            axios.get(patientApiUrl + "/" + props.id)
                .then(response => {
                    setPatient(response.data);
                    props.displayError('');
                })
                .catch(exception => props.displayError(exception.response));
        }
    }, [props.id]);

    if (props.id === 0) return null;

    function onClick(event) {
        event.preventDefault();
        const body = {...patient};
        axios.put(patientApiUrl + "/" + patient.id, body)
            .then(response => {
                setPatient(response.data);
                props.displayError('');
            })
            .catch(exception => props.displayError(exception.response));
    }

    function onChange (field) {
        field.persist();
        const newPatient = {...patient};
        newPatient[field.target.name] = field.target.value;
        setPatient(newPatient);
    }

    return (<form>
        <PatientDetailLine label="Patient id" name="id" onChange={onChange} input={patient.id} disabled="true"/>
        <PatientDetailLine label="Family name" name="family" onChange={onChange} input={patient.family} />
        <PatientDetailLine label="Given name" name="given" onChange={onChange} input={patient.given} />
        <PatientDetailLine label="Date of birth" name="dob" onChange={onChange} input={patient.dob} />
        <PatientDetailLine label="Sex" name="sex" onChange={onChange} input={patient.sex} />
        <PatientDetailLine label="Address" name="address" onChange={onChange} input={patient.address} />
        <PatientDetailLine label="Phone" name="phone" onChange={onChange} input={patient.phone} />
        <br />
        <button onClick={onClick}>Save</button>
    </form>);
}

function PatientLink (props) {

    function changeId(id) {
        props.setId(props.id);
    }

    return (<a href="#" onClick={changeId}>{props.id}</a>);
}

function PatientTable (props) {

    const [patients, setPatients] = React.useState([]);

    React.useEffect(() => {
        axios.get(patientApiUrl)
            .then(response => {
                setPatients(response.data);
                props.displayError('');
            })
            .catch( error => {
                if (error.response) {
                    props.displayError(error.status + " " + error.response);
                } else {
                    props.displayError(error.message + " : check that the server is up and running !");
                }
            }
        );
    }, []);

    return (
        <table>
            <tr>
                <th>Patient id</th>
                <th>Family name</th>
                <th>Date of birth</th>
            </tr>
            {patients.map(patient => (
                <tr>
                    <td><PatientLink setId={props.setId} id={patient.id}/></td>
                    <td>{patient.family}</td>
                    <td>{patient.dob}</td>
                </tr>
            ))}
        </table>
    );
}

function PatientError(props) {
    if (props.message === '') return null;
    return (<div>
        <h1>Error :</h1>
        <p>{props.message}</p>
    </div>);
}

function PatientClient() {

    const [id, setId] = React.useState(0);
    const [error, setError] = React.useState('');

    return (
      <div>
          <PatientTable setId={setId} displayError={setError} />
          <br />
          <PatientDetail id={id} displayError={setError} />
          <br />
          <PatientError message={error} />
      </div>
  );
}

export default PatientClient;
