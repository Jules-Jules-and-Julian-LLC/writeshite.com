import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import Lobby from "./Lobby";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

export default class App extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
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
        );
    }
}
