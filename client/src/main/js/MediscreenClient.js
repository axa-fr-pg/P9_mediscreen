import React from 'react';
import {BrowserRouter, Switch, Route, NavLink} from "react-router-dom";
import Patients from "./patients/Patients";
import Patient from "./patients/Patient";
import "../css/MediscreenClient.css";
import Notes from "./notes/Notes";
import Note from "./notes/Note";
import Reports from "./reports/Reports";
import Report from "./reports/Report";
import {useSelector, useDispatch} from "react-redux";
import {STATE_PATIENT} from "./reducers/reducerConstants";

function Button() {
    const dispatch = useDispatch();
    console.log("Render button");
    return (
        <button onClick={() => {
            console.log("clic bouton");
            dispatch({type: 'clic', payload : 'bouton'})
        }}>button
        </button>
    );
}

function Text() {
    const message = useSelector(state =>  state[STATE_PATIENT].text);
    console.log("Render text " + message);
    return (<h2>Text : {message}</h2>);
}

function Home() {
    console.log("Render home");
    return (
        <div>
            <h1>Mediscreen home page</h1>
            <br/>
            <Text/>
            <Button/>
        </div>
    );
}


const MediscreenMenu = () => {
    console.log("render MediscreenMenu")

    return (
        <nav className="mediscreen-menu">
            <NavLink className="mediscreen-link" to="/" exact>
                Home
            </NavLink>
            <NavLink className="mediscreen-link" to="/patients">
                Patients
            </NavLink>
            <NavLink className="mediscreen-link" to="/notes">
                Notes
            </NavLink>
            <NavLink className="mediscreen-link" to="/reports">
                Reports
            </NavLink>
        </nav>
    );
};

function MediscreenClient() {
    console.log("render MediscreenClient")
    return (
        <BrowserRouter>
            <MediscreenMenu/>
            <div>
                <Switch>
                    <Route exact path="/">
                        <Home/>
                    </Route>
                    <Route exact path="/patients">
                        <Patients/>
                    </Route>
                    <Route exact path="/patients/new">
                        <Patient/>
                    </Route>
                    <Route path="/patients">
                        <Patient/>
                    </Route>
                    <Route exact path="/notes">
                        <Notes/>
                    </Route>
                    <Route path="/notes/patients/*/new">
                        <Note/>
                    </Route>
                    <Route path="/notes/patients">
                        <Notes/>
                    </Route>
                    <Route path="/notes">
                        <Note/>
                    </Route>
                    <Route exact path="/reports">
                        <Reports/>
                    </Route>
                    <Route path="/reports">
                        <Report/>
                    </Route>
                </Switch>
            </div>
        </BrowserRouter>
    );
}

export default MediscreenClient;