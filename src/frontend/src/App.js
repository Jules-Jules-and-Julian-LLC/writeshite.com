import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import Lobby from "./Lobby";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";

export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: null,
            stompClient: null,
            username: null,
            connected: false
        }

        this.connect = this.connect.bind(this);
        this.getStompClient = this.getStompClient.bind(this);
    }

    async connect() {
        //TODO JJ error when connect fails, retry connection
        return new Promise((resolve, reject) => {
            let socket = new SockJS("/websocket");
            let stompClient = Stomp.over(socket);
            let me = this;
            stompClient.connect({}, function(frame) {
                me.setState({stompClient: stompClient});
                resolve("success!");
            },
            function(frame) {
                reject("error! " + JSON.stringify(frame))
            });
        });
    }

    async getStompClient() {
        if(!this.state.stompClient) {
            await this.connect();
        }

        return this.state.stompClient;
    }

    setClientIdCookie(clientId) {
        document.cookie = "clientId=" + clientId + "; HttpOnly"
    }

    render() {
        //TODO create a common object for "game info" or whatever
        //TODO join doesn't have username. Really should figure out state provider :/
        return (
             <Router>
                 <Switch>
                    <Route path="/lobby/:lobbyId">
                        <Lobby getStompClient={this.getStompClient} />
                    </Route>
                    <Route path="/">
                        <Homepage getStompClient={this.getStompClient} />
                    </Route>
                 </Switch>
            </Router>
        );
    }
}
