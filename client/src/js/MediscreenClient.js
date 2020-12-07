// inspiré de https://github.com/vijitail/react-router-demo/tree/master/src

import React from 'react';
import { BrowserRouter, Switch, Route, NavLink, useParams } from "react-router-dom";
import Patients from "./patients/Patients";
import Patient from "./patients/Patient";
import "../css/MediscreenClient.css";

const Home = () => (
    <div>
        <h1>Mediscreen home page</h1>
    </div>
);



const Doctor = () => {
    const { name } = useParams();
    return (
        <div>
            <h1 >This is the Doctor Page</h1>
            <p>We expect to deliver this page on early January 2021 latest. Thank you for your understanding.</p>
        </div>
    );
};

const MediscreenMenu = () => {

    return (
        <nav className="mediscreen-menu">
            <NavLink className="mediscreen-link" to="/" exact>
                Home
            </NavLink>
            <NavLink className="mediscreen-link" to="/patients">
                Patients
            </NavLink>
            <NavLink className="mediscreen-link" to="/doctor">
                Doctor
            </NavLink>
        </nav>
    );
};

function MediscreenClient() {
    return (
        <BrowserRouter>
            <MediscreenMenu />
            <div>
                <Switch>
                    <Route exact path="/">
                        <Home />
                    </Route>
                    <Route exact path="/patients">
                        <Patients />
                    </Route>
                    <Route exact path="/patients/new">
                        <Patient />
                    </Route>
                    <Route path="/patients">
                        <Patient />
                    </Route>
                    <Route path="/doctor">
                        <Doctor />
                    </Route>
                </Switch>
            </div>
        </BrowserRouter>
    );
}

export default MediscreenClient;