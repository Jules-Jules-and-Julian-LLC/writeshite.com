import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import Lobby from "./Lobby";
import Gallery from "./Gallery";
import LogoPage from "./LogoPage";
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";

export default class App extends React.Component {
    render() {
        return (
            <div id="content">
                <Router>
                    <Switch>
                        <Route path="/lobby/:lobbyId">
                            <LogoPage>
                                <Lobby />
                            </LogoPage>
                        </Route>
                        <Route path="/gallery/:lobbyId">
                            <LogoPage>
                                <Gallery />
                            </LogoPage>
                        </Route>
                        <Route path="/">
                            <LogoPage>
                                <Homepage />
                            </LogoPage>
                        </Route>
                    </Switch>
                </Router>
            </div>
        );
    }
}
