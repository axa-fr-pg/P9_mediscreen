import React, {useEffect, useState} from "react";
import {useHistory} from "react-router";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";
import {TreeView, TreeItem} from '@material-ui/lab';
import ReactQuill from "react-quill";

function PatientIdSwitch({patientIdGiven, setPatientIdGiven, setError, history, setUpdateRequired}) {

    function onChangePatientIdSwitch() {
        setUpdateRequired(true);
        if (patientIdGiven < 0) {
            setPatientIdGiven(0);
        } else {
            setPatientIdGiven(-1);
            history.push('/notes');
        }
        setError('');
    }

    return (
        <div key={"switch-patient-id"} className="switch-patient-id">
            <Switch checked={patientIdGiven >= 0} onChange={onChangePatientIdSwitch}
                    checkedIcon={false} uncheckedIcon={false} height={15} width={30} handleDiameter={13}/>
        </div>
    );
}

function NotesRandom({patientIdGiven, inputFieldPatientId, setUpdateRequired, setError}) {
    const [randomVolume, setRandomVolume] = useState(5);
    const [inputFieldRandomVolume, setInputFieldRandomVolume] = useState(null);

    function generateRandomNotes(event) {

        let url = notesApiUrl;
        event.preventDefault();
        if (!!inputFieldRandomVolume) {
            inputFieldRandomVolume.blur();
        }
        if (!!inputFieldPatientId) {
            inputFieldPatientId.blur();
        }
        setError("Processing request...");

        if (patientIdGiven >= 0) {
            url = url + "/patients/" + patientIdGiven;
        }

        axios.post(url + "/random/" + randomVolume)
            .then(response => {
                setUpdateRequired(true);
                setError(response.data.length + " random notes have been generated successfully !");
            })
            .catch(error => {
                if (error.response) {
                    setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
                } else {
                    setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
                }
            });
    }

    function onChangeRandomVolume(field) {
        setRandomVolume(field.target.value);
        setInputFieldRandomVolume(field.target);
    }

    return (
        <form>
            <div className="div-random">
                <button onClick={generateRandomNotes}>Add</button>
                <input className="input-narrow" value={randomVolume} onChange={onChangeRandomVolume}/>
                <label>
                    random note(s) to database
                </label>
            </div>
        </form>
    );
}

function getNotes(patientIdGiven, setNotes, setUpdateRequired, setError) {
    let url = notesApiUrl;
    if (patientIdGiven >= 0) {
        url = url + "/patients/" + patientIdGiven;
    }
    axios.get(url)
        .then(response => {
            setUpdateRequired(false);
            setNotes(response.data);
            if (response.data.length === 0) {
                setError('It seems that the database is empty : please generate some random patients or ask your IT support.');
            }
        })
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it seems that the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it seems that the server or the database is unavailable !");
            }
        });
}

function PatientNotes({branch, history}) {

    return (
        <TreeItem nodeId={branch.patId.toString()} label={"Patient " + branch.patId}>
            <table className="table-patient-notes" key={branch.patId}>
                {branch.noteDTOList.map(note => (
                    <tr className="tr-note-overview" key={note.noteId}
                        onClick={() => history.push('/notes/' + note.noteId)}>
                        {note.e}
                    </tr>
                ))}
            </table>
        </TreeItem>
    );
}

function NoteList({patientIdGiven, notes, setNotes, updateRequired, setUpdateRequired, setError, history}) {

    const [expanded, setExpanded] = useState([patientIdGiven.toString()]);

    const handleToggle = (event, nodeIds) => {
        setExpanded(nodeIds);
    };

    useEffect(() => {
        if (updateRequired) {
            console.log("get notes effect");
            getNotes(patientIdGiven, setNotes, setUpdateRequired, setError);
        }
    });

    if (notes.length === 0) return null;

    let notesTree = notes;
    let activeBranches = expanded;
    if (notes.patId >= 0) {
        activeBranches = [notes.patId.toString()];
        notesTree = [notes];
    }

    return (
        <nav>
            <link rel="stylesheet" href="//cdn.quilljs.com/1.2.6/quill.snow.css"/>
            <TreeView className="tree-view" expanded={activeBranches} onNodeToggle={handleToggle}>
                {notesTree.map(branch => (
                    <PatientNotes key={branch.patId} branch={branch} history={history}/>
                ))}
            </TreeView>
        </nav>
    );
}

function NotesError({error}) {

    if (!error) return null;
    return (
        <footer>
            {error}
        </footer>
    );
}

function NoteListTitleWithPatientSelector({patientIdGiven, setPatientIdGiven, setUpdateRequired, setInputFieldPatientId, setError, history}) {

    function onChangePatientIdGivenField(field) {
        setPatientIdGiven(field.target.value);
        setInputFieldPatientId(field.target);
    }

    function onSubmitPatientIdGivenField() {
        setUpdateRequired(true);
        history.push('/notes/patients/' + patientIdGiven);
    }

    return (
        <h1 className="title-note-list">Note list
            <PatientIdSwitch patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                             setError={setError} history={history} setUpdateRequired={setUpdateRequired}/>
            <div hidden={patientIdGiven >= 0}>
                <label>for all patients</label>
            </div>
            <div hidden={patientIdGiven < 0}>
                <label>for patient with id</label>
                <input className="input-narrow input-with-parent-font" value={patientIdGiven}
                       onChange={onChangePatientIdGivenField}/>
                <button className="button-submit" onClick={onSubmitPatientIdGivenField}>Submit</button>
            </div>
        </h1>
    );
}

function Notes() {
    const [notes, setNotes] = useState([]);
    const [updateRequired, setUpdateRequired] = useState('false');
    const [error, setError] = useState('');
    const [patientIdGiven, setPatientIdGiven] = useState(
        window.location.href.includes('patient') ?
            window.location.pathname.split("/").pop() : -1);
    const [inputFieldPatientId, setInputFieldPatientId] = useState(null);
    const history = useHistory();

    function newNote() {
        history.push('/notes/patients/' + patientIdGiven + '/new');
    }

    return (
        <div>
            <NoteListTitleWithPatientSelector patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                                              setUpdateRequired={setUpdateRequired}
                                              setInputFieldPatientId={setInputFieldPatientId} setError={setError}
                                              history={history}/>
            <button hidden={patientIdGiven < 0} className="button-new" onClick={newNote}>Register new note</button>
            <NoteList patientIdGiven={patientIdGiven} notes={notes} setNotes={setNotes} updateRequired={updateRequired}
                      setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <NotesRandom patientIdGiven={patientIdGiven} inputFieldPatientId={inputFieldPatientId}
                         setUpdateRequired={setUpdateRequired} setError={setError}/>
            <NotesError patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                        setInputFieldPatientId={setInputFieldPatientId} error={error}/>
        </div>
    );
}

export default Notes;