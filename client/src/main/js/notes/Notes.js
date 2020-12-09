import React from "react";
import Note from "./Note";
import {useHistory} from "react-router";

function Notes() {

    const history = useHistory();
    history.push('/notes/patient/1/new');

    return (
        <div>
            <h1>Note list</h1>
            <Note />
        </div>
    );
}

export default Notes;