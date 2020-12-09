import React, {useState} from "react";
import moment from "moment";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";

function Note() {
    const [note, setNote] = React.useState({ patId : 0, noteId : window.location.pathname.split("/").pop(), e : ''});
    const [modify, setModify] = useState(window.location.href.includes('new'));
    const [input, setInput] = useState(true);
    const [error, setError] = useState('');

    function displayTitle() {
        const view = modify ? 'edit' : 'view';
        const title = note.noteId === 'new' ? 'creation' : view;
        return(
            <h1>Note {title}</h1>
        );
    }

    function onClickSave(event) {
        event.preventDefault();

        const body = {...note};
        if (note.noteId === 'new') {
            body.noteId='';
            axios.post(notesApiUrl+'/patient/'+note.patId, body)
                .then(response => {
                    body.noteId=response.data.noteId;
                    setInput(false);
                    setError("Note created successfully with id=" + body.noteId);
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
                    }
                });
        } else
        {
            axios.put(notesApiUrl + "/" + note.noteId, body)
                .then(response => {
                    setNote(response.data);
                    setError('XXXXXXXX has been saved successfully !');
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data);
                    } else {
                        setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
                    }
                });
        }
    }

    function displaySaveButton() {
        if (!input || !modify) return null;
        return(
            <button onClick={onClickSave}>Save</button>
        );
    }

    function onChange (field) {
        field.persist();
        const newNote = {...note};
        newNote[field.target.name] = field.target.value;
        setNote(newNote);
    }

    function displayField(field, label, readonly) {
        if (!input) return null;
        const disabled = !!readonly;
        if (field === 'noteId' && note.noteId === 'new') return null;
        return (<div key={field}>
            <label>{label}
                <input value={note[field]} name={field} onChange={onChange} disabled={disabled||modify===false} />
            </label>
        </div>);
    }

    return (
        <div>
            {displayTitle()}
            <form>
                {displayField('noteId', 'Note Id', true)}
                {displayField('e', 'Content', false)}
                {displaySaveButton()}
            </form>
        </div>
    );
}

export default Note;