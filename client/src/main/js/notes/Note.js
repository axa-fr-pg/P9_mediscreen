import React, {useState} from "react";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";

function NoteModifySwitch({input, modify, onChange}) {
    if (!input || window.location.href.includes('new')) {
        return null;
    }
    return(
        <div key={"div-read-only"} className="div-read-only">
            <label>View</label>
            <div className="switch-read-only">
                <Switch checked={modify} onChange={onChange} checkedIcon={false} uncheckedIcon={false}
                        height={15} width={30} handleDiameter={13} />
            </div>
            <label>Edit</label>
        </div>
    );
}

function NoteSaveButton({input, modify, onClick}) {
    if (!input || !modify) return null;
    return(
        <button className="button-save" onClick={onClick}>Save</button>
    );
}

function Note() {
    const currentUrl = window.location.pathname.split("/");
    const [note, setNote] = useState({noteId: currentUrl.pop(), patId: currentUrl.pop(), e: ''});
    const [modify, setModify] = useState(window.location.href.includes('new'));
    const [input, setInput] = useState(true);
    const [error, setError] = useState('');

    function displayTitle() {
        const view = modify ? 'edit' : 'view';
        const title = note.noteId === 'new' ? 'creation' : view;
        return (
            <h1>Note {title}</h1>
        );
    }

    React.useEffect(() => {
        if (note.noteId === 'new') return;
        axios.get(notesApiUrl + "/" + note.noteId)
            .then(response => {
                setNote(response.data);
                setError('');
            })
            .catch(error => {
                setInput(false);
                if (error.response) {
                    setError(error.response.status + " " + error.response.data);
                } else {
                    setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
                }
            });

    }, [note.noteId]);

    function onClickSave(event) {
        event.preventDefault();

        const body = {...note};
        if (note.noteId === 'new') {
            body.noteId = '';
            axios.post(notesApiUrl + '/patients/' + note.patId, body)
                .then(response => {
                    body.noteId = response.data.noteId;
                    setInput(false);
                    setError("Note created successfully with id=" + body.noteId);
                })
                .catch(error => {
                    if (error.response) {
                        setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it looks like the database is not ready !");
                    } else {
                        setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
                    }
                });
        } else {
            axios.put(notesApiUrl + "/" + note.noteId, body)
                .then(response => {
                    setNote(response.data);
                    setError('Note has been saved successfully !');
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

    function DisplayError() {
        if (!error) return null;
        return (
            <footer>
                {error}
            </footer>
        );
    }

    function onChangeNote(field) {
        field.persist();
        const newNote = {...note};
        newNote[field.target.name] = field.target.value;
        setNote(newNote);
    }

    function onChangeSwitch() {
        setModify(!modify);
        setError('');
    }

    return (
        <div>
            {displayTitle()}
            <div hidden={!input}>
                <form className="form-note">
                    <div className="form-note-element" key="note-content">
                        <label className="form-note-label">Content</label>
                        <textarea className="form-note-input" value={note.e} name="e" onChange={onChangeNote}
                                  disabled={modify === false}/>
                    </div>
                    <NoteModifySwitch input={input} modify={modify} onChange={onChangeSwitch} />
                    <NoteSaveButton input={input} modify={modify} onClick={onClickSave} />
                </form>
            </div>
            <DisplayError/>
        </div>
    );
}

export default Note;