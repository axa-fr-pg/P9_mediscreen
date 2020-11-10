import React from 'react';
import '../client.css';
import axios from 'axios';

const patientApiUrl = "http://patient:8081/patients";

function PatientLink (props) {
    const target = patientApiUrl + "/" + props.id;
    console.log("je crée un PatientLink pour ", target);
    return (<a href={target}> {props.id}</a>);
}

function PatientTable () {
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
                <th>Given name</th>
                <th>Date of birth</th>
                <th>Sex</th>
                <th>Address</th>
                <th>Phone</th>
            </tr>
            {patients.map(patient => (
                <tr>
                    <td><PatientLink id={patient.id}/></td>
                    <td>{patient.family}</td>
                    <td>{patient.given}</td>
                    <td>{patient.dob}</td>
                    <td>{patient.sex}</td>
                    <td>{patient.address}</td>
                    <td>{patient.phone}</td>
                </tr>
            ))}
        </table>
    );
}

function PatientClient() {
  return (
      <div>
          <PatientTable />
          <button type="Search">Bouton à implémenter</button>
      </div>
  );
}

export default PatientClient;
