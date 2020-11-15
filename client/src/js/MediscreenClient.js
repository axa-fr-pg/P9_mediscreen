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
            <article className="message is-dark" style={{ marginTop: 40 }}>
                <div className="message-header">
                    <p>{name}</p>
                </div>
                <div className="message-body">
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit.{" "}
                    <strong>Pellentesque risus mi</strong>, tempus quis placerat ut, porta
                    nec nulla. Vestibulum rhoncus ac ex sit amet fringilla. Nullam gravida
                    purus diam, et dictum <a>felis venenatis</a> efficitur. Aenean ac{" "}
                    <em>eleifend lacus</em>, in mollis lectus. Donec sodales, arcu et
                    sollicitudin porttitor, tortor urna tempor ligula, id porttitor mi
                    magna a neque. Donec dui urna, vehicula et sem eget, facilisis sodales
                    sem.
                </div>
            </article>
        </div>
    );
};

const MediscreenMenu = () => {

    return (
        <nav className="mediscreen-menu">
            <NavLink class="mediscreen-link" to="/" exact>
                Home
            </NavLink>
            <NavLink class="mediscreen-link" to="/patients">
                Patients
            </NavLink>

            <NavLink class="mediscreen-link" to="/doctor">
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