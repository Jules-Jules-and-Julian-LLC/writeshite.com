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
            joined: false
        };

        this.startGame = this.startGame.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.setUsername = this.setUsername.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleMessageChange = this.handleMessageChange.bind(this);
    }

    componentDidMount() {
        this.connect();
    }

    connect() {
        let socket = new SockJS("/websocket");
        let stompClient = Stomp.over(socket);
        let me = this;

        stompClient.connect(
            {},
            function(frame) {
                me.setState({stompClient: stompClient});
                let disconnect = e => stompClient.disconnect();
                window.addEventListener("beforeunload", disconnect.bind(me));

                stompClient.subscribe(
                    "/topic/lobby." + me.state.lobbyId,
                    function(response) {
                        let responseObj = JSON.parse(response.body);
                        const responseType = responseObj.responseType;
                        if (responseType === "START_GAME") {
                            me.setState({
                                gameState: responseObj.gameState,
                                stories: responseObj.game.stories
                            });
                        } else if (responseType === "JOIN_GAME") {
                            let stories = responseObj.lobby.game.stories;
                            me.setState({
                                joined: me.state.clickedSetUsername,
                                gameState: responseObj.lobby.gameState,
                                lobby: responseObj.lobby,
                                stories: stories
                            });
                        } else if(responseType === "NEW_MESSAGE") {
                            me.setState({stories: responseObj.stories});
                        }
                    }
                );
            },
            function(frame) {
                console.log("error connecting! " + JSON.stringify(frame));
            }
        );
    }

    startGame(event) {
        event.preventDefault();
        this.state.stompClient.send(
            "/app/lobby." + this.state.lobbyId + ".startGame",
            {},
            JSON.stringify({username: localStorage.getItem("username")})
        );
    }

    sendMessage(event) {
        event.preventDefault();
        let stories = this.state.stories;
        let myStories = stories && stories[this.state.username];
        let currentStory = myStories && myStories[0];
        
        if(currentStory && this.state.message && this.state.message !== "") {
            this.state.stompClient.send(
                "/app/lobby." + this.state.lobbyId + ".newMessage",
                {},
                JSON.stringify({message: this.state.message, storyId: currentStory.id})
            );
            this.setState({message:""});
        }
    }

    setUsername(event) {
        event.preventDefault();
        if(this.state.username !== "") {
            this.setState({clickedSetUsername: true});
            this.state.stompClient.send(
                "/app/lobby." + this.state.lobbyId + ".joinGame",
                {},
                this.state.username
            );
        }
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
        localStorage.setItem("username", event.target.value);
    }

    handleMessageChange(event) {
        this.setState({message: event.target.value});
    }

    render() {
        if (!this.state.stompClient) {
            return <div>Connecting to lobby, please wait...</div>;
        } else if (!this.state.joined) {
            return (
                <div id="set-user-info-content">
                    <div id="logo">
                        <img src="../../logo.svg" alt="logo" />
                    </div>
                    <div>
                        <input
                            type="text"
                            name="username"
                            placeholder="Name"
                            onChange={this.handleUsernameChange}
                            value={this.state.username}
                        />
                    </div>
                    <form onSubmit={this.setUsername}>
                        <input type="submit" value="Set username" />
                    </form>
                </div>
            );
        } else if ( this.state.lobby && this.state.gameState === "GATHERING_PLAYERS" ) {
            let players = this.state.lobby.players.map(player => (
                <li key={player.username}>{player.username}</li>
            ));
            return (
                <div id="lobby-content">
                    <div id="logo">
                        <img src="../../logo.svg" alt="logo" />
                    </div>
                    Players:
                    <div>
                        <ul>{players}</ul>
                    </div>
                    {this.state.lobby.creator.username === this.state.username && (
                        <form onSubmit={this.startGame}>
                            <input type="submit" value="Start game" />
                        </form>
                    )}
                </div>
            );
        } else if (this.state.gameState === "PLAYING") {
            let lobby = this.state.lobby;
            let players = lobby.players.map(player => <li key={player.username}>{player.username} ({this.state.stories[player.username].length} in queue)</li>);
            let stories = this.state.stories[this.state.username];
            let currentStory = stories && stories[0] && stories[0].messages.map(m => m.text).join(" ");

            return (
                <div id="game-content">
                    You are playing a game with:
                    <div>
                        <ul>{players}</ul>
                    </div>
                    <div>You have {stories.length} stories in queue.</div>
                    <div>
                        Current story: {currentStory}
                    </div>
                    <div>
                        <input
                            type="text"
                            name="message"
                            onChange={this.handleMessageChange}
                            value={this.state.message}
                        />
                        <form onSubmit={this.sendMessage}>
                            <input type="submit" value="Send" />
                        </form>
                    </div>
                </div>
            );
        }
    }
}
