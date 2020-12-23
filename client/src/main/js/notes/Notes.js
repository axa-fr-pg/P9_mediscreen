import React, {useEffect, useState} from "react";
import {useHistory} from "react-router";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";
import {TreeView, TreeItem} from '@material-ui/lab';

function generateRandomNotes(event, patientIdGiven, inputFieldRandomVolume, randomVolume, inputFieldPatientId,
                             setUpdateRequired, setError) {
    let url = notesApiUrl;
    event.preventDefault();
    if (!!inputFieldRandomVolume) {
        inputFieldRandomVolume.blur();
    }
    if (!!inputFieldPatientId) {
        inputFieldPatientId.blur();
    }
    setError("Processing request...");

    if (patientIdGiven>=0) {
        url = url + "/patients/" + patientIdGiven;
    }

    axios.post(url + "/random/" + randomVolume)
        .then(response => {
            setUpdateRequired(true);
            setError(response.data.length + " random notes have been generated successfully !");
        })
        .catch(error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data+ " ! Please ask your IT support : it looks like the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
            }
        });
}

function onChangePatientIdGiven(patientIdGiven, setPatientIdGiven, setError) {
    if (patientIdGiven<0) {
        setPatientIdGiven(0);
    } else {
        setPatientIdGiven(-1);
    }
    setError('');
}

function PatientIdSwitch({patientIdGiven, setPatientIdGiven, setError}) {

    return(
        <div key={"switch-patient-id"} className="switch-patient-id">
            <Switch checked={patientIdGiven>=0} onChange={() => onChangePatientIdGiven(patientIdGiven, setPatientIdGiven, setError)}
                    checkedIcon={false} uncheckedIcon={false} height={15} width={30} handleDiameter={13} />
        </div>
    );
}

function NotesRandom({patientIdGiven, inputFieldPatientId, setUpdateRequired, setError}) {
    const [randomVolume, setRandomVolume] = useState(5);
    const [inputFieldRandomVolume, setInputFieldRandomVolume] = useState(null);

    function onChangeRandomVolume (field) {
        setRandomVolume(field.target.value);
        setInputFieldRandomVolume(field.target);
    }

    return (
        <form>
            <div className="div-random">
                <button onClick={(event) => generateRandomNotes(event, patientIdGiven, inputFieldRandomVolume, randomVolume, inputFieldPatientId, setUpdateRequired, setError)}>
                    Add
                </button>
                <input className="input-small" value={randomVolume} onChange={onChangeRandomVolume} />
                <label >
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
            setNotes(response.data);
            setUpdateRequired(false);
            if (response.data.length === 0) {
                setError('It looks like the database is empty : please generate some random patients or ask your IT support.');
            }
        })
        .catch( error => {
            if (error.response) {
                setError(error.response.status + " " + error.response.data + " ! Please ask your IT support : it looks like the database is not ready !");
            } else {
                setError(error.message + " ! Please ask your IT support : it looks like the server or the database is unavailable !");
            }
        });
}

function PatientNotes({branch, history}) {

    return(
        <TreeItem nodeId={branch.patId.toString()} label={"Patient " + branch.patId}>
            {branch.noteDTOList.map(note => (
                <TreeItem key={note.noteId} nodeId={note.noteId} label={note.e} onLabelClick={()=>history.push('/notes/'+note.noteId)}>
                    {note.e}
                </TreeItem>
            ))}
        </TreeItem>
    );
}

function NoteList({patientIdGiven, notes, setNotes, error, updateRequired, setUpdateRequired, setError, history}) {

    const [expanded, setExpanded] = React.useState([patientIdGiven.toString()]);

    const handleToggle = (event, nodeIds) => {
        setExpanded(nodeIds);
    };

    useEffect(() => {
        if (updateRequired) {
            getNotes(patientIdGiven, setNotes, setUpdateRequired, setError);
        }
    });

    if (notes.length === 0) return null;

    let notesTree = notes;
    if (patientIdGiven >= 0) {
        notesTree = [{patId : patientIdGiven, noteDTOList : notes}];
    }

    return (
        <nav>
            <TreeView className="tree-view" expanded={expanded} onNodeToggle={handleToggle}>
                {notesTree.map(branch => (
                    <PatientNotes branch={branch} history={history}/>
                ))}
            </TreeView>
        </nav>
    );
}

function NotesError({error}) {

    if (! error) return null;
    return (
        <footer>
            {error}
        </footer>
    );
}

function NotesPatientSelector({patientIdGiven, setPatientIdGiven, setInputFieldPatientId, setError}) {

    function onChangePatientIdGiven (field) {
        setPatientIdGiven(field.target.value);
        setInputFieldPatientId(field.target);
    }

    return (
        <div className="notes-patient-selector">
            <label>Work on</label>
            <PatientIdSwitch patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} setError={setError} />
            <div hidden={patientIdGiven>=0}>
                <label>all patients together</label>
            </div>
            <div hidden={patientIdGiven<0}>
                <label>patient with id</label>
                <input className="input-small"  value={patientIdGiven} onChange={onChangePatientIdGiven} />
            </div>
        </div>
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
    const [patientSelectorHidden, setPatientSelectorHidden] = useState(window.location.href.includes('patient'));
    const history = useHistory();

    return (
        <div>
            <h1>Note list</h1>
            <NoteList patientIdGiven={patientIdGiven} notes={notes} setNotes={setNotes} updateRequired={updateRequired}
                      setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <div hidden={patientSelectorHidden}>
                <NotesPatientSelector patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} setInputFieldPatientId={setInputFieldPatientId} setError={setError} />
            </div>
            <button hidden={patientIdGiven<0} className="button-new" onClick={() => history.push('/notes/patients/'+patientIdGiven+'/new')}>Register new note</button>
            <NotesRandom patientIdGiven={patientIdGiven} inputFieldPatientId={inputFieldPatientId} setUpdateRequired={setUpdateRequired} setError={setError} />
            <NotesError patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} setInputFieldPatientId={setInputFieldPatientId} error={error} />
        </div>
    );
}

export default Notes;