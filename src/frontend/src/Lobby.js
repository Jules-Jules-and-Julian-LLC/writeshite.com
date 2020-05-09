import React from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/");

        this.state = {
            username: localStorage.getItem("username"),
            lobbyId: pathname[pathname.length - 1],
        };

        this.startGame = this.startGame.bind(this);
        this.setUsername = this.setUsername.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
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
            me.setState({stompClient: stompClient});
            let disconnect = e => stompClient.disconnect();
            window.addEventListener("beforeunload", disconnect.bind(me));

            stompClient.subscribe("/topic/lobby." + me.state.lobbyId, function(response) {
                //break out the different responses we get from the lobby
                console.log(response);
                let responseObj = JSON.parse(response.body);
                const responseType = responseObj.responseType;
                if(responseType === "START_GAME") {
                    me.setState({ gameState: responseObj.gameState });
                } else if(responseType === "JOIN_GAME") {
                    me.setState({ joined: true,
                                  gameState: responseObj.gameState,
                                  lobby: responseObj.lobby,
                                  creator: responseObj.creator
                                });
                }
            });
        }, function(frame) {
            console.log("error connecting! " + JSON.stringify(frame))
        });
    }

    startGame(event) {
        event.preventDefault();
        this.state.stompClient.send("/app/lobby." + this.state.lobbyId + ".startGame", {},
            JSON.stringify({username: localStorage.getItem("username")}));
    }

    setUsername(event) {
        event.preventDefault()
        this.state.stompClient.send("/app/lobby." + this.state.lobbyId + ".joinGame", {}, this.state.username);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
        localStorage.setItem("username", event.target.value);
    }

    render() {
        //TODO if username not set, require them to set then re-render view (and reconnect?)
        //TODO on join, connect to lobby and get initial info
        if(!this.state.stompClient) {
            return (
                <div>
                    Connecting to lobby, please wait...
                </div>
            );
        }
        if(!this.state.joined) {
            return (
                <div id="set-user-info-content">
                    <div id="logo">
                        <img src="../../logo.svg" alt="logo" />
                    </div>
                    <div>
                        <input type="text" name="username" placeholder="Name" onChange={this.handleUsernameChange} value={this.state.username} />
                    </div>
                    <form onSubmit={this.setUsername}>
                        <input type="submit" value="Set username" />
                    </form>
                </div>
            );
        }
        if(this.state.lobby.gameState === "PLAYING") {
            return (
                <div id="lobby-content">
                    You are playing a game with:
                    <div>
                        <ul>
                            {this.state.lobby.players}
                        </ul>
                    </div>
                </div>
            );
        }
        if(this.state.lobby.gameState === "GATHERING_PLAYERS") {
            return (
                <div id="lobby-content">
                    <div id="logo">
                        <img src="../../logo.svg" alt="logo" />
                    </div>
                    Players:
                    <div>
                        <ul>
                            {this.state.lobby.players}
                        </ul>
                    </div>
                    <form onSubmit={this.startGame}>
                        <input type="submit" value="Start game" />
                    </form>
                </div>
            );
        }
    }
}
