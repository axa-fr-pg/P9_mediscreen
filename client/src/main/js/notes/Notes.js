import React, {useEffect, useState} from "react";
import {useHistory} from "react-router";
import axios from "axios";
import {notesApiUrl} from "../api/URLs";
import Switch from "react-switch";
import {TreeView, TreeItem} from '@material-ui/lab';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import {Paging} from '@axa-fr/react-toolkit-table';
import '@axa-fr/react-toolkit-form-input-select/dist/select.scss';
import '@axa-fr/react-toolkit-table/dist/Pager/pager.scss';
import '@axa-fr/react-toolkit-table/dist/Paging/paging.scss';
import {File} from '@axa-fr/react-toolkit-form-input-file';
import '@axa-fr/react-toolkit-form-input-file/dist/file.scss';
import {readString} from 'react-papaparse';
import {getPatients} from '../patients/Patients';
import {postNote} from './Note';

function PatientIdSwitch({patientIdGiven, setPatientIdGiven, report, setError, history, setUpdateRequired}) {

    if (!report === false) {
        return null;
    }

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

function NotesRandom({patientIdGiven, setUpdateRequired, setError, report}) {

    if (!report === false) {
        return null;
    }

    function generateRandomNotes(event) {
        let url = notesApiUrl;
        event.preventDefault();
        const inputFieldRandomVolume = document.getElementById('input-random-volume');
        inputFieldRandomVolume.blur();
        const inputFieldPatientId = document.getElementById('input-patient-id-given');
        inputFieldPatientId.blur();
        setError("Processing request...");

        if (patientIdGiven >= 0) {
            url = url + "/patients/" + patientIdGiven;
        }

        axios.post(url + "/random/" + inputFieldRandomVolume.value)
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

    return (
        <form className="form-random">
            <button onClick={generateRandomNotes}>Add</button>
            <input id="input-random-volume" className="input-narrow" defaultValue={5}/>
            <label>
                random note(s) to database
            </label>
        </form>
    );
}

function getNotes(pageNumber, rowsPerPage, filter, patientIdGiven, setNotes, setUpdateRequired, setError) {
    let url = notesApiUrl;
    if (patientIdGiven >= 0) {
        url = url + "/patients/" + patientIdGiven;
    } else {
        url = url + "?page=" + pageNumber;
        url = url + "&size=" + rowsPerPage;
        url = url + "&e=" + filter;
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

function PatientNotes({branch, history, expanded, setExpanded, setUpdateRequired, setPatientIdGiven}) {

    function stripHtml(html) {
        const temporaryElement = document.createElement("div");
        temporaryElement.innerHTML = html;
        return temporaryElement.textContent || temporaryElement.innerText || "";
    }

    const handleSelect = () => {
        setPatientIdGiven(branch.patId);
        setUpdateRequired(true);
        history.push('/notes/patients/' + branch.patId);
    };

    const handleToggle = () => {
        const patId = branch.patId.toString();
        if (expanded.includes(patId)) {
            setExpanded([]);
        } else {
            setExpanded([patId]);
        }
    };

    return (
        <TreeItem nodeId={branch.patId.toString()} label={"Patient " + branch.patId}
                  onLabelClick={handleSelect} onIconClick={handleToggle}>
            {branch.noteDTOList.map(note => (
                <textarea className="text-note-overview" key={note.noteId} readOnly={true}
                          onClick={() => history.push('/notes/' + note.noteId)} value={stripHtml(note.e)}/>
            ))}
        </TreeItem>
    );
}

function NoteList({patientIdGiven, setPatientIdGiven, notes, setNotes, updateRequired, setUpdateRequired, setError, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [filter, setFilter] = useState('');
    const [expanded, setExpanded] = useState([patientIdGiven.toString()]);

    useEffect(() => {
        if (updateRequired) {
            getNotes(pageNumber, rowsPerPage, filter, patientIdGiven, setNotes, setUpdateRequired, setError);
        }
    });

    if (notes.length === 0) return null;

    let notesTree, activeBranches;
    if (!!notes.content) {
        notesTree = notes.content;
        activeBranches = expanded;
    } else {
        notesTree = [notes];
        activeBranches = [notes.patId.toString()];
    }

    function submitFilter(event) {
        event.preventDefault();
        setFilter(document.getElementById('input-filter').value);
        setPageNumber(0);
        setUpdateRequired(true);
    }

    function onChange(event) {
        const {numberItems, page} = event;
        setPageNumber(page - 1);
        if (page > (notes.totalElements / numberItems)) {
            setPageNumber(Math.floor(notes.totalElements / numberItems));
        }
        setRowsPerPage(numberItems);
        setUpdateRequired(true);
    }

    return (
        <div className="div-note-list">
            <form className="form-filter" onSubmit={submitFilter}>
                <label hidden={patientIdGiven >= 0}>Expected note content :&nbsp;</label>
                <input className="filter-input" id="input-filter" type="text"
                       onBlur={submitFilter} hidden={patientIdGiven >= 0}/>
            </form>
            <p/>
            <TreeView className="tree-view" expanded={activeBranches}
                      defaultCollapseIcon={<ExpandMoreIcon/>} defaultExpandIcon={<ChevronRightIcon/>}>
                {notesTree.map(branch => (
                    <PatientNotes key={branch.patId} branch={branch} history={history}
                                  setPatientIdGiven={setPatientIdGiven}
                                  expanded={expanded} setExpanded={setExpanded} setUpdateRequired={setUpdateRequired}/>
                ))}
            </TreeView>
            <p/>
            <div hidden={patientIdGiven >= 0}>
                <Paging
                    currentPage={pageNumber + 1}
                    numberPages={notes.totalPages}
                    numberItems={rowsPerPage}
                    displayLabel=""
                    elementsLabel=" notes per page"
                    previousLabel="« Previous"
                    nextLabel="Next »"
                    onChange={onChange}
                />
            </div>
        </div>
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

function NoteListTitleWithPatientSelector({patientIdGiven, setPatientIdGiven, report, setUpdateRequired, setError, history}) {

    function onSubmitPatientIdGivenField() {
        const inputFieldPatientId = document.getElementById('input-patient-id-given');
        history.push('/notes/patients/' + inputFieldPatientId.value);
        setPatientIdGiven(inputFieldPatientId.value);
        setUpdateRequired(true);
    }

    function onChangePatientIdGiven() {
        const inputFieldPatientId = document.getElementById('input-patient-id-given');
        setPatientIdGiven(inputFieldPatientId.value);
    }

    return (
        <h1 className="title-note-list">Note list
            <PatientIdSwitch patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} report={report}
                             setError={setError} history={history} setUpdateRequired={setUpdateRequired}/>
            <div hidden={patientIdGiven >= 0}>
                <label>for all patients</label>
            </div>
            <div hidden={patientIdGiven < 0 || !report === false}>
                <form>
                    <label>for patient with id</label>
                    <input id="input-patient-id-given" className="input-narrow input-with-parent-font"
                           value={patientIdGiven >= 0 ? patientIdGiven : 0}
                           onChange={onChangePatientIdGiven}/>
                    <button className="button-submit" onClick={onSubmitPatientIdGivenField}>Submit</button>
                </form>
            </div>
        </h1>
    );
}

function getPatientIdByUrl(rawUrl) {
    const url = rawUrl.split("?").shift();
    return url.includes('patients') ? url.split("/").pop() : -1;
}

let numberOfNotesPosted;
let numberOfNotesAdded;
let numberOfPatientsChecked;
let numberOfPatientsFound;
let noteContent;

function waitAllNotesPosted(numberOfNotesToPost, setError) {
    if (numberOfNotesPosted < numberOfNotesToPost) {
        setTimeout(waitAllNotesPosted, 1000, numberOfNotesToPost, setError);
    } else if (numberOfNotesAdded === numberOfNotesPosted) {
        setError(numberOfNotesToPost +" patient notes have been uploaded successfully !")
    }
}

function postPatientNoteByPatientId(results) {
    const patientId = results.content[0].id;
    numberOfNotesPosted++;
    numberOfNotesAdded++;
    const body = {noteId : '', e: noteContent};
    postNote(body, patientId, ()=>{}, ()=>{});
}

function postPatientNote(line, setError) {
    const family = line[0];
    noteContent = line[1];
    const inputData = {
        pageNumber : 0, rowsPerPage : 10, orderField : 'id', orderDirection : 'asc',
        filterId : '', filterFamily : family, filterDob : '',
        setPatients : postPatientNoteByPatientId, setUpdateRequired : () => {},
        setError : (text) => {setError(text); numberOfNotesPosted++}
    };
    getPatients(inputData);
}

function countLinesWithFormatError(results) {
    let numberOfLinesWithWrongFormat = 0;
    results.data.forEach(line => {
        if (line.length !== 2) {
            numberOfLinesWithWrongFormat++;
        }
    });
    return numberOfLinesWithWrongFormat;
}

function checkPatientByFamily(family) {
    const inputData = {
        pageNumber : 0, rowsPerPage : 10, orderField : 'id', orderDirection : 'asc',
        filterId : '', filterFamily : family, filterDob : '',
        setPatients : () => {numberOfPatientsFound++; numberOfPatientsChecked++},
        setUpdateRequired : () => {}, setError : () => {numberOfPatientsChecked++}
    };
    getPatients(inputData);
}

function waitAllPatientsCheckedAndPostNotes(results, setError) {
    if (numberOfPatientsChecked < results.data.length) {
        setTimeout(waitAllPatientsCheckedAndPostNotes, 1000, results, setError);
        return;
    }
    if (numberOfPatientsFound < numberOfPatientsChecked) {
        setError("CSV file contains " + (numberOfPatientsChecked - numberOfPatientsFound) +
            " note(s) for unknown or ambiguous patient(s). Aborting upload.");
        return;
    }
    results.data.forEach(line => postPatientNote(line, setError));
    waitAllNotesPosted(results.data.length, setError);
}

function checkAllPatientsFoundAndPostNotes(results, setError) {
    results.data.forEach(results => checkPatientByFamily(results[0]));
    waitAllPatientsCheckedAndPostNotes(results, setError);
}

function addPatientNotes(content, setUpdateRequired, setError) {
    const text = new Buffer(content).toString( 'latin1');
    numberOfNotesPosted = 0;
    numberOfNotesAdded = 0;
    numberOfPatientsChecked = 0;
    numberOfPatientsFound = 0;
    const csvConfig = {
        delimiter: ";",
        skipEmptyLines: true
    };
    const results = readString(text, csvConfig);
    if (results.errors.length > 0) {
        setError("File parsing has encountered errors. Please check and try again or ask your IT");
        return;
    }
    const numberOfLinesWithWrongFormat = countLinesWithFormatError(results);
    if (numberOfLinesWithWrongFormat > 0) {
        setError("CSV file parsing has found " + numberOfLinesWithWrongFormat + " line(s) with wrong format. Aborting upload.");
        return;
    }
    checkAllPatientsFoundAndPostNotes(results, setError);
}

function uploadPatientNoteFile(values, setUpdateRequired, setError) {
    if (values.length === 0) {
        setError("You selected an invalid file format. Please check and try again or ask your IT");
        return;
    }
    setError("Uploading " + values[0].file.name + " ...");
    fetch(values[0].file.preview)
        .then(response => response.blob())
        .then(blob => blob.arrayBuffer())
        .then(content => addPatientNotes(content, setUpdateRequired, setError));
}

function NotesUpload({setUpdateRequired, setError}) {
    return (
        <File
            label="Browse file"
            placeholder="You can upload a CSV note file with drag and drop here"
            id="file-to-be-uploaded"
            name="file-upload"
            accept=".csv"
            onChange={(values) => uploadPatientNoteFile(values.values, setUpdateRequired, setError)}
        />
    );
}

function Notes({report}) {
    const [notes, setNotes] = useState([]);
    const [updateRequired, setUpdateRequired] = useState(false);
    const [error, setError] = useState('');
    const [patientIdGiven, setPatientIdGiven] = useState(getPatientIdByUrl(window.location.href));
    const history = useHistory();

    useEffect(() => {
        setPatientIdGiven(getPatientIdByUrl(window.location.href));
        setUpdateRequired(true);
    }, [history.location.pathname]);

    function newNote() {
        history.push('/notes/patients/' + patientIdGiven + '/new');
    }

    return (
        <div>
            <NoteListTitleWithPatientSelector patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                                              report={report}
                                              setUpdateRequired={setUpdateRequired} history={history}
                                              setError={setError}/>
            <button hidden={patientIdGiven < 0 || !report === false} className="button-new" onClick={newNote}>Register
                new note
            </button>
            <NoteList patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                      notes={notes} setNotes={setNotes} updateRequired={updateRequired}
                      setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <NotesRandom patientIdGiven={patientIdGiven} setUpdateRequired={setUpdateRequired} setError={setError}
                         report={report}/>
            <NotesUpload setUpdateRequired={setUpdateRequired} setError={setError}/>
            <NotesError patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} error={error}/>
        </div>
    );
}

export default Notes;