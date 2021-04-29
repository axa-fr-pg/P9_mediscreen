import React, {useRef, useState} from "react";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";
import ReactQuill from "react-quill";
import ModalError from "../modal/error";
import ModalSuccess from "../modal/success";
import {useHistory} from "react-router";

const NOTE_NOT_FOUND = 'note-not-found';

export function postNote(body, patId, setSuccess, setError) {
    body.noteId = '';
    axios.post(notesApiUrl + '/patients/' + patId, body)
        .then(response => {
            body.noteId = response.data.noteId;
            setSuccess("Note created successfully with id=" + body.noteId);
        })
        .catch(exception => {
            if (exception.response) {
                setError(exception.response.status + " " + exception.response.data);
            } else {
                setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
}

function NoteSaveButton({modify, onClick}) {
    if (!modify) return null;
    return (
        <button className="button-save" onClick={onClick}>Save</button>
    );
}

function NoteTitleWithModeSelector({note, modify, onChangeSwitch}) {
    const view = modify ? 'edit' : 'view';
    const title = note.current.noteId === 'new' ? 'creation' : view;
    let switchHidden = false;
    if (window.location.href.includes('new')) {
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
    const note = useRef({noteId: currentUrl.pop(), patId: currentUrl.pop(), e: ''});
    const error = useRef('');
    const success = useRef('');
    const [, setModal] = useState(false);
    const [, setNoteReady] = useState(false);
    const [modify, setModify] = useState(window.location.href.includes('new'));
    const history = useHistory();

    function setSuccess (message) {
        success.current = message;
        setModal(message.length > 0);
    }

    function setError (message) {
        error.current = message;
        setModal(message.length > 0);
    }

    function setNote(data) {
        note.current = data;
        setNoteReady(true);
    }

    React.useEffect(() => {
        if (note.current.noteId !== 'new') {
            axios.get(notesApiUrl + "/" + note.current.noteId)
                .then(response => {
                    setNote(response.data);
                })
                .catch(exception => {
                    note.current.noteId = NOTE_NOT_FOUND;
                    if (exception.response) {
                        setError(exception.response.status + " " + exception.response.data);
                    } else {
                        setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }, []);

    function onClickSave(event) {
        event.preventDefault();

        const body = {...note.current};
        if (note.current.noteId === 'new') {
            postNote(body, note.current.patId, setSuccess, setError);
        } else {
            axios.put(notesApiUrl + "/" + note.current.noteId, body)
                .then(response => {
                    setNote(response.data);
                    setSuccess('Note has been saved successfully !');
                })
                .catch(exception => {
                    if (exception.response) {
                        setError(exception.response.status + " " + exception.response.data);
                    } else {
                        setError(exception.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                    }
                });
        }
    }

    function onChangeNote(content) {
        const newNote = {...note.current};
        newNote['e'] = content;
        setNote(newNote);
    }

    function onChangeSwitch() {
        setModify(!modify);
        setError('');
    }

    function closeErrorModal() {
        setError('');
        if (!window.location.href.includes('new') && note.current.noteId === NOTE_NOT_FOUND) {
            history.push('/notes');
        }
    }

    function closeSuccessModal() {
        setModify(false);
        setSuccess('');
        if (window.location.href.includes('new')) {
            history.push(window.location.href.split('/').slice(0, -1).join('/'));
        }
    }

    return (
        <div>
            <NoteTitleWithModeSelector note={note} modify={modify} onChangeSwitch={onChangeSwitch}/>
            <link rel="stylesheet" href={"//cdn.quilljs.com/1.2.6/quill.snow.css"}/>
            <NoteSaveButton modify={modify} onClick={onClickSave}/>
            <ReactQuill className="quill-note-content" key={note.current.noteId} value={note.current.e}
                        readOnly={modify === false} onChange={onChangeNote}
                        modules={{toolbar: modify}}/>
            <ModalError message={error.current} closureAction={closeErrorModal}/>
            <ModalSuccess message={success.current} closureAction={closeSuccessModal}/>
        </div>
    );
}

export default Note;