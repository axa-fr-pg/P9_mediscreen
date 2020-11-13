import React from 'react';
import '../client.css';
import axios from 'axios';

const patientApiUrl = "http://patient:8081/patients";

function PatientDetailLine(props) {

    return (<div>
        <label>{props.label}
            <input defaultValue={props.input} name={props.name} onChange={props.onChange} />
        </label>
    </div>);
}

function PatientDetail (props) {

    const [patient, setPatient] = React.useState({ id : '', family : '', given : '', dob : '', sex : '', address : '', phone : ''});

    React.useEffect(() => {
        axios.get(patientApiUrl + "/" + props.id)
            .then(response => {
                setPatient(response.data);
                console.log("Je lance une requête GET qui me retourne : ", response.data);
            })
            .catch(exception => { console.error("Error in GET request : ", exception) });
    }, [props.id]);

    if (props.id === 0) return null;

    function onClick(event) {
        console.log("Bouton cliqué avec : ", event);
        event.preventDefault();
        const body = {...patient};
        axios.put(patientApiUrl + "/" + patient.id, body)
            .then(response => {
                setPatient(response.data);
                console.log("Je lance une requête PUT qui me retourne : ", response.data);
            })
            .catch(exception => { console.error("Error in PUT request : ", exception) });
        console.log("Je lance une requête PUT avec : ", body);
    }

    function onChange (field) {
        console.log("Patient mis à jour pour ", field.target.name, ":", field.target.value);
        field.persist();
        const newPatient = {...patient};
        newPatient[field.target.name] = field.target.value;
        console.log("Patient donné pour mise à jour ", newPatient);
        setPatient(newPatient);
        console.log("Patient résultant de la mise à jour ", patient);
    }

    return (<form>
        <PatientDetailLine label="Patient id" name="id" onChange={onChange} input={patient.id} />
        <PatientDetailLine label="Family name" name="family" onChange={onChange} input={patient.family} />
        <PatientDetailLine label="Given name" name="given" onChange={onChange} input={patient.given} />
        <PatientDetailLine label="Date of birth" name="dob" onChange={onChange} input={patient.dob} />
        <PatientDetailLine label="Sex" name="sex" onChange={onChange} input={patient.sex} />
        <PatientDetailLine label="Address" name="address" onChange={onChange} input={patient.address} />
        <PatientDetailLine label="Phone" name="phone" onChange={onChange} input={patient.phone} />
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
      </div>
  );
}

export default PatientClient;
