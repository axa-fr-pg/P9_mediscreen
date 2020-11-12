import React from 'react';
import '../client.css';
import axios from 'axios';

const patientApiUrl = "http://patient:8081/patients";

function PatientDetailLine(props) {
    return (<div>
        <label>{props.label}
            <input value={props.input} />
        </label>
    </div>);
}

function PatientDetail (props) {
    const [patient, setPatient] = React.useState(props.id);
    React.useEffect(() => {
        axios.get(patientApiUrl + "/" + props.id)
            .then(response => setPatient(response.data))
            .catch(exception => { console.error("Error in GET request : ", exception) });
    }, [props.id]);

    if (props.id === 0) return null;
    return (<form>
        <PatientDetailLine label="Patient id" input={patient.id} />
        <PatientDetailLine label="Family name" input={patient.family} />
        <PatientDetailLine label="Given name" input={patient.given} />
        <PatientDetailLine label="Date of birth" input={patient.dob} />
        <PatientDetailLine label="Sex" input={patient.sex} />
        <PatientDetailLine label="Address" input={patient.address} />
        <PatientDetailLine label="Phone" input={patient.phone} />
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
            .then(response => setPatients(response.data))
            .catch(exception => { console.error("Error in GET request : ", exception) });
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

function PatientClient() {
    const [id, setId] = React.useState(1);
    return (
      <div>
          <PatientTable setId={setId} />
          <PatientDetail id={id}/>
          <button type="Search">Futur bouton de mise à jour</button>
      </div>
  );
}

export default PatientClient;
