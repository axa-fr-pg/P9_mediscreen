import React, {useEffect, useRef, useState} from "react";
import {useHistory} from "react-router";
import axios from "axios";
import {doctorUrl, notesApiUrl} from "../api/URLs";
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
import ModalError from "../modal/error";
import ModalSuccess from "../modal/success";

function PatientIdSwitch({patientIdGiven, setPatientIdGiven, report, history}) {

    if (!report === false) {
        return null;
    }

    function onChangePatientIdSwitch() {
        if (patientIdGiven < 0) {
            setPatientIdGiven(0);
        } else {
            setPatientIdGiven(-1);
            history.push('/notes');
        }
    }

    return (
        <div key={"switch-patient-id"} className="switch-patient-id">
            <Switch checked={patientIdGiven >= 0} onChange={onChangePatientIdSwitch}
                    checkedIcon={false} uncheckedIcon={false} height={15} width={30} handleDiameter={13}/>
        </div>
    );
}

function NotesRandom({patientIdGiven, setSuccess, setError, report, setAddedNotes}) {

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
// TODO         setError("Processing request...");

        if (patientIdGiven >= 0) {
            url = url + "/patients/" + patientIdGiven;
        }

        axios.post(url + "/random/" + inputFieldRandomVolume.value)
            .then(response => {
                setAddedNotes(true);
                setSuccess(response.data.length + " random notes have been generated successfully !");
            })
            .catch(exception => {
                if (exception.response) {
                    setError(exception.response.data);
                } else {
                    setError("Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message);
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

function getNotes(pageNumber, rowsPerPage, filter, patientIdGiven, setNotes, setError) {
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
            setNotes(response.data);
            if ((response.data.content !== undefined && response.data.content.length === 0)
                || ((response.data.noteDTOList !== undefined && response.data.noteDTOList.length === 0) && patientIdGiven > 0)) {
                setError('Your selection criteria match no note. Database may also be empty.');
            }
        })
        .catch(exception => {
            setError("Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message);
        });
}

function PatientNotes({branch, history, expanded, setExpanded, setPatientIdGiven}) {

    function stripHtml(html) {
        const temporaryElement = document.createElement("div");
        temporaryElement.innerHTML = html;
        return temporaryElement.textContent || temporaryElement.innerText || "";
    }

    const handleSelect = () => {
        setPatientIdGiven(branch.patId);
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

function NoteList({patientIdGiven, setPatientIdGiven, notes, setNotes, addedNotes, setAddedNotes, setError, history}) {

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [filter, setFilter] = useState('');
    const [expanded, setExpanded] = useState([patientIdGiven.toString()]);

    useEffect(() => {
        setAddedNotes(false);
        getNotes(pageNumber, rowsPerPage, filter, patientIdGiven, setNotes, setError);
    }, [addedNotes, pageNumber, rowsPerPage, filter, patientIdGiven]);

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
    }

    function onChange(event) {
        const {numberItems, page} = event;
        setPageNumber(page - 1);
        if (page > (notes.totalElements / numberItems)) {
            setPageNumber(Math.floor(notes.totalElements / numberItems));
        }
        setRowsPerPage(numberItems);
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
                                  setPatientIdGiven={setPatientIdGiven} expanded={expanded} setExpanded={setExpanded}/>
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

function NoteListTitleWithPatientSelector({patientIdGiven, setPatientIdGiven, report, setError, history}) {

    function onSubmitPatientIdGivenField() {
        const inputFieldPatientId = document.getElementById('input-patient-id-given');
        history.push('/notes/patients/' + inputFieldPatientId.value);
        setPatientIdGiven(inputFieldPatientId.value);
    }

    function onChangePatientIdGiven() {
        const inputFieldPatientId = document.getElementById('input-patient-id-given');
        setPatientIdGiven(inputFieldPatientId.value);
    }

    return (
        <h1 className="title-note-list">Note list
            <PatientIdSwitch patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                             report={report} history={history}/>
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

function waitAllNotesPosted(numberOfNotesToPost, setSuccess, setAddedNotes) {
    if (numberOfNotesPosted < numberOfNotesToPost) {
        setTimeout(waitAllNotesPosted, 1000, numberOfNotesToPost, setSuccess, setAddedNotes);
    } else if (numberOfNotesAdded === numberOfNotesPosted) {
        setAddedNotes(true);
        setSuccess(numberOfNotesToPost + " patient notes have been uploaded successfully !")
    }
}

function postPatientNoteByPatientId(results, noteContent) {
    const patientId = results.content[0].id;
    numberOfNotesPosted++;
    numberOfNotesAdded++;
    const body = {noteId: '', e: noteContent};
    postNote(body, patientId, () => {
    }, () => {
    });
}

function postPatientNote(line, setError) {
    const family = line[0];
    const setPatients = (results) => {
        postPatientNoteByPatientId(results, line[1])
    };
    const inputData = {
        pageNumber: 0, rowsPerPage: 10, orderField: 'id', orderDirection: 'asc',
        filterId: '', filterFamily: family, filterDob: '',
        setPatients: setPatients,
        setError: (text) => {
            setError(text);
            numberOfNotesPosted++
        }
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
    const getPatientsInputData = {
        pageNumber: 0, rowsPerPage: 10, orderField: 'id', orderDirection: 'asc', filterId: '',
        filterFamily: family, filterDob: '',
        setPatients: () => {
            numberOfPatientsFound++;
            numberOfPatientsChecked++
        },
        setError: () => {
            numberOfPatientsChecked++
        }
    }
    getPatients(getPatientsInputData);
}

function waitAllPatientsCheckedAndPostNotes(results, setSuccess, setError, setAddedNotes) {
    if (numberOfPatientsChecked < results.data.length) {
        setTimeout(waitAllPatientsCheckedAndPostNotes, 1000, results, setSuccess, setError, setAddedNotes);
        return;
    }
    if (numberOfPatientsFound < numberOfPatientsChecked) {
        setError("CSV file contains " + (numberOfPatientsChecked - numberOfPatientsFound) +
            " note(s) for unknown or ambiguous patient(s). Aborting upload.");
        return;
    }
    results.data.forEach(line => postPatientNote(line, setError));
    waitAllNotesPosted(results.data.length, setSuccess, setAddedNotes);
}

function checkAllPatientsFoundAndPostNotes(results, setSuccess, setError, setAddedNotes) {
    results.data.forEach(results => checkPatientByFamily(results[0]));
    waitAllPatientsCheckedAndPostNotes(results, setSuccess, setError, setAddedNotes);
}

function addPatientNotes(content, setSuccess, setError, setAddedNotes) {
    const text = new Buffer(content).toString('latin1');
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
    checkAllPatientsFoundAndPostNotes(results, setSuccess, setError, setAddedNotes);
}

function uploadPatientNoteFile(values, setSuccess, setError, setAddedNotes) {
    if (values.length === 0) {
        setError("You selected an invalid file format. Please check and try again or ask your IT");
        return;
    }
    // TODO setError("Uploading " + values[0].file.name + " ...");
    fetch(values[0].file.preview)
        .then(response => response.blob())
        .then(blob => blob.arrayBuffer())
        .then(content => addPatientNotes(content, setSuccess, setError, setAddedNotes));
}

function NotesUpload({setSuccess, setError, report, setAddedNotes}) {
    if (!report === false) {
        return null;
    }
    return (
        <File
            label="Browse file"
            placeholder="You can upload a CSV note file with drag and drop here"
            id="file-to-be-uploaded"
            name="file-upload"
            accept=".csv"
            onChange={(values) => uploadPatientNoteFile(values.values, setSuccess, setError, setAddedNotes)}
        />
    );
}

function Notes({report}) {
    const [notes, setNotes] = useState([]);
    const [, setModal] = useState(false);
    const [patientIdGiven, setPatientIdGiven] = useState(-1);
    const [addedNotes, setAddedNotes] = useState(false);
    const error = useRef('');
    const success = useRef('');
    const history = useHistory();

    useEffect(() => {
        const patientId = getPatientIdByUrl(window.location.href);
        if (isNaN(parseInt(patientId))) {
            setError('It looks like you entered an invalid URL. Patient id must have a numeric value. Please check your request or ask your IT support !');
        } else {
            setPatientIdGiven(patientId);
        }
    }, [history.location.pathname]);

    function newNote() {
        history.push('/notes/patients/' + patientIdGiven + '/new');
    }

    function setSuccess(message) {
        success.current = message;
        setModal(message.length > 0);
    }

    function setError(message) {
        if (!report === true) {
            error.current = message;
            setModal(message.length > 0);
        }
    }

    function closeErrorModal() {
        setError('');
        // TODO user experience when registering a new note for a given patient
        if (window.location.href.includes('/notes/patients/') && patientIdGiven < 0) {
            history.push('/notes');
        }
    }

    function closeSuccessModal() {
        setSuccess('');
    }

    return (
        <div>
            <NoteListTitleWithPatientSelector patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                                              report={report} history={history} setError={setError}/>
            <button hidden={patientIdGiven < 0 || !report === false} className="button-new" onClick={newNote}>
                Register new note
            </button>
            <NoteList patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven} notes={notes} setNotes={setNotes}
                      addedNotes={addedNotes} setAddedNotes={setAddedNotes} setError={setError} history={history}/>
            <NotesRandom patientIdGiven={patientIdGiven} setSuccess={setSuccess} setError={setError}
                         report={report} setAddedNotes={setAddedNotes}/>
            <NotesUpload setSuccess={setSuccess} setError={setError} report={report} setAddedNotes={setAddedNotes}/>
            <ModalError message={error.current} closureAction={closeErrorModal}/>
            <ModalSuccess message={success.current} closureAction={closeSuccessModal}/>
            <a className="swagger-url" href={doctorUrl + "/swagger-ui/"}>Swagger</a>
        </div>
    );
}

export default Notes;