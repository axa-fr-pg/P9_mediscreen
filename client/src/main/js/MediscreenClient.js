import React from 'react';
import {BrowserRouter, Switch, Route, NavLink} from "react-router-dom";
import Patients from "./patients/Patients";
import Patient from "./patients/Patient";
import "../css/MediscreenClient.css";
import Notes from "./notes/Notes";
import Note from "./notes/Note";
import Reports from "./reports/Reports";
import Report from "./reports/Report";

function Home() {
    return (
        <div>
            <h1>Mediscreen home page</h1>
            <br/>
            <p>Welcome in our new high tech medical software !</p>
        </div>
    );
}


const MediscreenMenu = () => {

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