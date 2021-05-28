import React, {useEffect, useState} from "react";
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
import {getPatientList} from '../patients/Patients';
import {postNote} from './Note';
import {useDispatch, useSelector} from "react-redux";
import {
    ACTION_DISPLAY_MODAL_ERROR,
    ACTION_DISPLAY_MODAL_SUCCESS,
    ACTION_SET_FILTER_E,
    ACTION_SET_PAGE_NUMBER,
    ACTION_SET_ROWS_PER_PAGE,
    ACTION_SET_DOCTOR_PATIENT_ID,
    STATE_DOCTOR
} from "../reducers/reducerConstants";
import Modal from "../modal/modal";

function PatientIdSwitch({report}) {

    const dispatch = useDispatch();
    const history = useHistory();
    const doctorState = useSelector(state => state[STATE_DOCTOR]);

    if (!report === false) {
        return null;
    }

    function onChangePatientIdSwitch(checked) {
        if (checked) {
            dispatch({type: ACTION_SET_DOCTOR_PATIENT_ID, payload: 0});
        } else {
            dispatch({type: ACTION_SET_DOCTOR_PATIENT_ID, payload: -1});
            history.push('/notes');
        }
    }

    return (
        <div key={"switch-patient-id"} className="switch-patient-id">
            <Switch checked={doctorState.patientId >= 0} onChange={onChangePatientIdSwitch}
                    checkedIcon={false} uncheckedIcon={false} height={15} width={30} handleDiameter={13}/>
        </div>
    );
}

function NotesRandom({report, setAddedNotes}) {

    const dispatch = useDispatch();
    const doctorState = useSelector(state => state[STATE_DOCTOR]);

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

        if (doctorState.patientId >= 0) {
            url = url + "/patients/" + doctorState.patientId;
        }

        axios.post(url + "/random/" + inputFieldRandomVolume.value)
            .then(response => {
                setAddedNotes(true);
                dispatch({
                    type: ACTION_DISPLAY_MODAL_SUCCESS,
                    payload: response.data.length + " random notes have been generated successfully !"
                });
            })
            .catch(exception => {
                if (exception.response) {
                    dispatch({type: ACTION_DISPLAY_MODAL_ERROR, payload: exception.response.data});
                } else {
                    dispatch({
                        type: ACTION_DISPLAY_MODAL_ERROR,
                        payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                    });
                }
            });
    }

    return (
        <form className="form-random" onSubmit={generateRandomNotes}>
            <button>Add</button>
            <input id="input-random-volume" className="input-narrow" defaultValue={5}/>
            <label>
                random note(s) to database
            </label>
        </form>
    );
}

function PatientNotes({branch, expanded, setExpanded}) {

    const history = useHistory();
    const dispatch = useDispatch();

    function stripHtml(html) {
        const temporaryElement = document.createElement("div");
        temporaryElement.innerHTML = html;
        return temporaryElement.textContent || temporaryElement.innerText || "";
    }

    const handleSelect = () => {
        dispatch({type: ACTION_SET_DOCTOR_PATIENT_ID, payload: branch.patId})
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

function NoteList({notes, setNotes, addedNotes, setAddedNotes}) {

    const doctorState = useSelector(state => state[STATE_DOCTOR]);
    const [expanded, setExpanded] = useState([doctorState.patientId.toString()]);
    const dispatch = useDispatch();
    const history = useHistory();

    function getNoteList(patientId, setNotes) {
        const url = doctorState.getNoteListUrl;
        const defaultResponse = {
            data: {
                content: {},
                noteDTOList: []
            }
        };
        axios.get(url)
            .then((response = defaultResponse) => {
                if (!!response.data.content && response.data.content.length === 0) {
                        dispatch({
                            type: ACTION_DISPLAY_MODAL_ERROR,
                            payload: 'Your selection criteria match no note. Database may also be empty.'
                        });
                } else {
                    setNotes(response.data);
                }
            })
            .catch(exception => {
                dispatch({
                    type: ACTION_DISPLAY_MODAL_ERROR,
                    payload: "Please ask your IT support : it seems that the server or the database is unavailable ! " + exception.message
                });
            });
    }

    useEffect(() => {
        setAddedNotes(false);
        getNoteList(doctorState.patientId, setNotes);
    }, [history.location.pathname, addedNotes, doctorState.paging, doctorState.filter, doctorState.patientId]);

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
        dispatch({
            type: ACTION_SET_FILTER_E,
            payload: document.getElementById('input-filter').value
        })
        dispatch({type: ACTION_SET_PAGE_NUMBER, payload: 0})
    }

    function onChangePaging(event) {
        const {numberItems, page} = event;
        let pageNumber = page - 1;
        if (page > (notes.totalElements / numberItems)) {
            pageNumber = Math.floor(notes.totalElements / numberItems);
        }
        dispatch({type: ACTION_SET_PAGE_NUMBER, payload: pageNumber})
        dispatch({type: ACTION_SET_ROWS_PER_PAGE, payload: numberItems})
    }

    return (
        <div className="div-note-list">
            <form className="form-filter" onSubmit={submitFilter}>
                <label hidden={doctorState.patientId >= 0}>Expected note content :&nbsp;</label>
                <input className="filter-input" id="input-filter" type="text"
                       onBlur={submitFilter} hidden={doctorState.patientId >= 0}/>
            </form>
            <p/>
            <TreeView className="tree-view" expanded={activeBranches}
                      defaultCollapseIcon={<ExpandMoreIcon/>} defaultExpandIcon={<ChevronRightIcon/>}>
                {notesTree.map(branch => (
                    <PatientNotes key={branch.patId} branch={branch}
                                  expanded={expanded} setExpanded={setExpanded}/>
                ))}
            </TreeView>
            <p/>
            <div hidden={doctorState.patientId >= 0}>
                <Paging
                    currentPage={doctorState.paging.pageNumber + 1}
                    numberPages={notes.totalPages}
                    numberItems={doctorState.paging.rowsPerPage}
                    displayLabel=""
                    elementsLabel=" notes per page"
                    previousLabel="« Previous"
                    nextLabel="Next »"
                    onChange={onChangePaging}
                />
            </div>
        </div>
    );
}

function NoteListTitleWithPatientSelector({report}) {

    const dispatch = useDispatch();
    const doctorState = useSelector(state => state[STATE_DOCTOR]);
    const history = useHistory();

    function onChangeDoctorPatientId() {
        const inputFieldPatientId = document.getElementById('input-patient-id-given');
        const controlledPatientId = inputFieldPatientId.value ? parseInt(inputFieldPatientId.value.toString()) : 0;
        dispatch({type: ACTION_SET_DOCTOR_PATIENT_ID, payload: controlledPatientId});
        history.push('/notes/patients/' + controlledPatientId);
    }

    return (
        <h1 className="title-note-list">Note list
            <PatientIdSwitch report={report}/>
            <div hidden={doctorState.patientId >= 0}>
                <label>for all patients</label>
            </div>
            <div hidden={doctorState.patientId < 0 || !report === false}>
                    <label>for patient with id</label>
                    <input id="input-patient-id-given" className="input-narrow input-with-parent-font"
                           value={doctorState.patientId >= 0 ? doctorState.patientId : 0}
                           onChange={onChangeDoctorPatientId}/>
            </div>
        </h1>
    );
}

function getPatientIdByUrl(rawUrl) {
    const url = rawUrl.split("?").shift();
    return url.includes('patients') ? parseInt(url.split("/").pop()) : -1;
}

let numberOfNotesPosted;
let numberOfNotesAdded;
let numberOfPatientsChecked;
let numberOfPatientsFound;

function postPatientNoteByPatientId(results, noteContent) {
    const patientId = results.content[0].id;
    numberOfNotesPosted++;
    numberOfNotesAdded++;
    const body = {noteId: '', e: noteContent};
    postNote(body, patientId, () => {
    }, () => {
    });
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
    const getPatientListInputData = {
        pageNumber: 0, rowsPerPage: 10,
        orderField: 'id', orderDirection: 'asc',
        filterId: '', filterFamily: family, filterDob: '',
        setPatients: () => {
            numberOfPatientsFound++;
            numberOfPatientsChecked++
        },
        dispatch: () => {
            numberOfPatientsChecked++
        }
    }
    getPatientList(getPatientListInputData);
}

function NotesUpload({report, setAddedNotes}) {

    const dispatch = useDispatch();

    if (!report === false) {
        return null;
    }

    function waitAllNotesPosted(numberOfNotesToPost, setAddedNotes) {
        if (numberOfNotesPosted < numberOfNotesToPost) {
            setTimeout(waitAllNotesPosted, 1000, numberOfNotesToPost, setAddedNotes);
        } else if (numberOfNotesAdded === numberOfNotesPosted) {
            setAddedNotes(true);
            dispatch({
                type: ACTION_DISPLAY_MODAL_SUCCESS,
                payload: numberOfNotesToPost + " patient notes have been uploaded successfully !"
            });
        }
    }

    function postPatientNote(line) {
        const family = line[0];
        const setPatients = (results) => {
            postPatientNoteByPatientId(results, line[1])
        };
        const getPatientListInputData = {
            pageNumber: 0, rowsPerPage: 10,
            orderField: 'id', orderDirection: 'asc',
            filterId: '', filterFamily: family, filterDob: '',
            setPatients: setPatients,
            dispatch: (text) => {
                dispatch({type: ACTION_DISPLAY_MODAL_ERROR, payload: text});
                numberOfNotesPosted++
            }
        };
        getPatientList(getPatientListInputData);
    }

    function waitAllPatientsCheckedAndPostNotes(results, setAddedNotes) {
        if (numberOfPatientsChecked < results.data.length) {
            setTimeout(waitAllPatientsCheckedAndPostNotes, 1000, results, setAddedNotes);
            return;
        }
        if (numberOfPatientsFound < numberOfPatientsChecked) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "CSV file contains " + (numberOfPatientsChecked - numberOfPatientsFound) +
                    " note(s) for unknown or ambiguous patient(s). Aborting upload."
            });
            return;
        }
        results.data.forEach(line => postPatientNote(line));
        waitAllNotesPosted(results.data.length, setAddedNotes);
    }

    function checkAllPatientsFoundAndPostNotes(results, setAddedNotes) {
        results.data.forEach(results => checkPatientByFamily(results[0]));
        waitAllPatientsCheckedAndPostNotes(results, setAddedNotes);
    }

    function addPatientNotes(content, setAddedNotes) {
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
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "File parsing has encountered errors. Please check and try again or ask your IT"
            });
            return;
        }
        const numberOfLinesWithWrongFormat = countLinesWithFormatError(results);
        if (numberOfLinesWithWrongFormat > 0) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "CSV file parsing has found " + numberOfLinesWithWrongFormat + " line(s) with wrong format. Aborting upload."
            });
            return;
        }
        checkAllPatientsFoundAndPostNotes(results, setAddedNotes);
    }

    function uploadPatientNoteFile(values, setAddedNotes) {
        if (values.length === 0) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: "You selected an invalid file format. Please check and try again or ask your IT"
            });
            return;
        }
        // TODO setError("Uploading " + values[0].file.name + " ...");
        fetch(values[0].file.preview)
            .then(response => response.blob())
            .then(blob => blob.arrayBuffer())
            .then(content => addPatientNotes(content, setAddedNotes));
    }

    return (
        <File
            label="Browse file"
            placeholder="You can upload a CSV note file with drag and drop here"
            id="file-to-be-uploaded"
            name="file-upload"
            accept=".csv"
            onChange={(values) => uploadPatientNoteFile(values.values, setAddedNotes)}
        />
    );
}

function Notes({report}) {

    const [notes, setNotes] = useState([]);
    const [addedNotes, setAddedNotes] = useState(false);
    const history = useHistory();
    const dispatch = useDispatch();
    const doctorState = useSelector(state => state[STATE_DOCTOR]);

    useEffect(() => {
        const patientId = getPatientIdByUrl(history.location.pathname);
        if (isNaN(patientId)) {
            dispatch({
                type: ACTION_DISPLAY_MODAL_ERROR,
                payload: 'It looks like you entered an invalid URL. Patient id must have a numeric value. ' +
                    'Please check your request or ask your IT support !'
            });
        } else {
            dispatch({type: ACTION_SET_DOCTOR_PATIENT_ID, payload: patientId})
            if (patientId >= 0) {
                history.push('/notes/patients/' + patientId);
            }
        }
    }, [history.location.pathname]);

    function newNote() {
        history.push('/notes/patients/' + doctorState.patientId + '/new');
    }

    function closeErrorModal() {
        // TODO user experience when registering a new note for a given patient
        if (history.location.pathname.includes('/notes/patients/') && doctorState.patientId < 0) {
            history.push('/notes');
        }
    }

    const isNewNoteButtonHidden = doctorState.patientId < 0 || !report === false;

    return (
        <div>
            <NoteListTitleWithPatientSelector report={report}/>
            <button hidden={isNewNoteButtonHidden} className="button-new" onClick={newNote}>
                Register new note
            </button>
            <NoteList notes={notes} setNotes={setNotes}
                      addedNotes={addedNotes} setAddedNotes={setAddedNotes}/>
            <NotesRandom report={report} setAddedNotes={setAddedNotes}/>
            <NotesUpload report={report} setAddedNotes={setAddedNotes}/>
            <Modal errorClosureAction={closeErrorModal}/>
            <a className="swagger-url" href={doctorUrl + "/swagger-ui/"}>Swagger</a>
        </div>
    );
}

export default Notes;