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
            joined: false,
        };

        this.startGame = this.startGame.bind(this);
        this.joinGame = this.joinGame.bind(this);
    }

    componentDidMount() {
        let me = this;
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
                var responseObj = JSON.parse(response.body);
                me.setState({ joined: true,
                              gameState: responseObj.gameState,
                              lobby: responseObj.lobby
                            });

                //TODO join lobby, add to list of players
                //TODO joined = true when join lobby
            });
        }, function(frame) {
            console.log("error connecting! " + JSON.stringify(frame))
        });
    }

    startGame(event) {
        event.preventDefault();
        this.state.stompClient.send("/app/lobby/" + this.state.lobbyId + "/startGame", {},
            JSON.stringify({username: localStorage.getItem("username")}));
    }

    joinGame(event) {
        event.preventDefault()
        this.state.stompClient.send("/app/lobby." + this.state.lobbyId + ".joinGame", {}, localStorage.getItem("username"));
    }

    handleUsernameChange(event) {
        event.preventDefault();
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
                        <img src="logo.svg" alt="logo" />
                    </div>
                    <div>
                        <input type="text" name="username" placeholder="Name" onChange={this.handleUsernameChange} value={this.state.username} />
                    </div>
                    <form onSubmit={this.joinGame}>
                        <input type="submit" value="Join game" />
                    </form>
                </div>
            );
        }
        const playersList = this.state.lobby.players.map((p) => <li>{p.username}</li>);
        return (
            <div id="lobby-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                This is the lobby, it will have a player list some day.
                <div>
                    <ul>
                        {playersList}
                    </ul>
                </div>
                <form onSubmit={this.startGame}>
                    <input type="submit" value="Start game" />
                </form>
            </div>
        );
    }
}
