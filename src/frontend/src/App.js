import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import Lobby from "./Lobby";
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";

export default class App extends React.Component {
    render() {
        return (
            <div id="content">
                <Router>
                    <Switch>
                        <Route path="/lobby/:lobbyId">
                            <Lobby />
                        </Route>
                        <Route path="/">
                            <Homepage />
                        </Route>
                    </Switch>
                </Router>
            </div>
        );
    }
}
