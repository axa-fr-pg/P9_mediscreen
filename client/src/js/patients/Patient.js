import React, {useState, useEffect} from 'react';

function Patient() {
    const [patient, setPatient] = React.useState({ id : window.location.pathname.split("/").pop(), family : '', given : '', dob : '', sex : '', address : '', phone : ''});

    return (
        <h1>Patient {patient.id}</h1>
    );
}

export default Patient;