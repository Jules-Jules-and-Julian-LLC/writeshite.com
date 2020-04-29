import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import Lobby from "./Lobby";
import Game from "./Game";
import JoinGame from "./JoinGame";
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
            clientId: "",
            stompClient: null
        }
    }

    componentDidMount() {
        this.connect();
    }

    connect() {
        //TODO JJ error when connect fails, retry connection
        let socket = new SockJS("/websocket");
        let stompClient = Stomp.over(socket);
        let me = this;
        stompClient.connect({}, function(frame) {
            console.log("Connected: " + frame);
            stompClient.subscribe("/topic/getClientId", function(response) {
                me.setState({
                    clientId: JSON.parse(response.body).clientId,
                    stompClient: stompClient
                });
            });
            stompClient.send("/app/getClientId", {}, {});
        });
    }

    render() {
        if (!this.state.clientId) {
            return (
                <div>
                    Loading, please wait...
                    <div style={{position:'relative', 'padding-bottom':'calc(75.00% + 44px)'}}>
                        <iframe src='https://gfycat.com/ifr/IllWearyAntipodesgreenparakeet' frameborder='0'
                            scrolling='no' width='25%' height='25%' style={{position:'absolute', top:0, left:0}} allowfullscreen>
                        </iframe>
                    </div>
                    <p>
                        <a href="https://gfycat.com/illwearyantipodesgreenparakeet-animal-crossing-dessin-anime-nintendo" />
                    </p>
                </div>
            );
        }

        //TODO create a common object for "game info" or whatever
        //TODO join doesn't have username. Really should figure out state provider :/
        return (
             <Router>
                 <Switch>
                    <Route path="/lobby/:lobbyId">
                        <Lobby clientId={this.state.clientId} stompClient={this.state.stompClient} />
                    </Route>
                    <Route path="/joinGame">
                        <JoinGame clientId={this.state.clientId} stompClient={this.state.stompClient} />
                    </Route>
                    <Route path="/game/:gameId">
                        <Game clientId={this.state.clientId} stompClient={this.state.stompClient} />
                    </Route>
                    <Route path="/">
                        <Homepage clientId={this.state.clientId} stompClient={this.state.stompClient}/>
                    </Route>
                 </Switch>
            </Router>
        );
    }
}
