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
import TablePagination from "@material-ui/core/TablePagination";

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
        setPageNumber(page-1);
        if (page > (notes.totalElements/numberItems)) {
            setPageNumber(Math.floor(notes.totalElements/numberItems));
        }
        setRowsPerPage(numberItems);
        setUpdateRequired(true);
    }

    return (
        <div className="div-note-list">
            <form className="form-filter" onSubmit={submitFilter}>
                <label>Expected note content :&nbsp;</label>
                <input className="filter-input" id="input-filter" type="text"
                       onBlur={submitFilter}/>
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
            <Paging
                currentPage={pageNumber+1}
                numberPages={notes.totalPages}
                numberItems={rowsPerPage}
                displayLabel=""
                elementsLabel=" notes per page"
                previousLabel="« Previous"
                nextLabel="Next »"
                onChange={onChange}
            />
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

const getPatIdFromUrl = (url) => url.includes('patients') ? url.split("/").pop() : -1;

function Notes() {
    const [notes, setNotes] = useState([]);
    const [updateRequired, setUpdateRequired] = useState(false);
    const [error, setError] = useState('');
    const [patientIdGiven, setPatientIdGiven] = useState(getPatIdFromUrl(window.location.href));
    const [inputFieldPatientId, setInputFieldPatientId] = useState(null);
    const history = useHistory();

    useEffect(() => {
        setPatientIdGiven(getPatIdFromUrl(window.location.href));
        setUpdateRequired(true);
    }, [history.location.pathname]);

    function newNote() {
        history.push('/notes/patients/' + patientIdGiven + '/new');
    }

    return (
        <div>
            <NoteListTitleWithPatientSelector patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                                              setUpdateRequired={setUpdateRequired} history={history}
                                              setInputFieldPatientId={setInputFieldPatientId} setError={setError}/>
            <button hidden={patientIdGiven < 0} className="button-new" onClick={newNote}>Register new note</button>
            <NoteList patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                      notes={notes} setNotes={setNotes} updateRequired={updateRequired}
                      setUpdateRequired={setUpdateRequired} setError={setError} history={history}/>
            <NotesRandom patientIdGiven={patientIdGiven} inputFieldPatientId={inputFieldPatientId}
                         setUpdateRequired={setUpdateRequired} setError={setError}/>
            <NotesError patientIdGiven={patientIdGiven} setPatientIdGiven={setPatientIdGiven}
                        setInputFieldPatientId={setInputFieldPatientId} error={error}/>
        </div>
    );
}

export default Notes;