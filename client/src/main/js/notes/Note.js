import React, {useState} from "react";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";
import ReactQuill from "react-quill";

function NoteSaveButton({input, modify, onClick}) {
    if (!input || !modify) return null;
    return (
        <button className="button-save" onClick={onClick}>Save</button>
    );
}

function NoteTitleWithModeSelector({note, input, modify, onChangeSwitch}) {
    const view = modify ? 'edit' : 'view';
    const title = note.noteId === 'new' ? 'creation' : view;
    let switchHidden = false;
    if (!input || window.location.href.includes('new')) {
        switchHidden = true;
    }
    return (
        <h1 className="title-note">Note {title}
            <div hidden={switchHidden}>
                &nbsp;
                <Switch checked={modify} onChange={onChangeSwitch}
                        checkedIcon={false} uncheckedIcon={false}
                        height={15} width={30} handleDiameter={13}/>
            </div>
        </h1>
    );
}

function Note() {
    const currentUrl = window.location.pathname.split("/");
    const [note, setNote] = useState({noteId: currentUrl.pop(), patId: currentUrl.pop(), e: ''});
    const [modify, setModify] = useState(window.location.href.includes('new'));
    const [input, setInput] = useState(true);
    const [error, setError] = useState('');

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
                    setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
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
                        setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
                    } else {
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
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
                        setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }

    function DisplayError() {
        if (!error) return null;
        return (<h4>{error}</h4>);
    }

    function onChangeNote(content) {
        const newNote = {...note};
        newNote['e'] = content;
        setNote(newNote);
    }

    function onChangeSwitch() {
        setModify(!modify);
        setError('');
    }

    return (
        <div>
            <NoteTitleWithModeSelector note={note} input={input} modify={modify} onChangeSwitch={onChangeSwitch}/>
            <link rel="stylesheet" href="//cdn.quilljs.com/1.2.6/quill.snow.css"/>
            <DisplayError/>
            <NoteSaveButton input={input} modify={modify} onClick={onClickSave}/>
            <ReactQuill className="quill-note-content" key={note.noteId} value={note.e}
                        readOnly={modify === false} onChange={onChangeNote}/>
        </div>
    );
}

export default Note;