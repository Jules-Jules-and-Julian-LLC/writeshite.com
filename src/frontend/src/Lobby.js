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
            roundTime: null,
            messageTime: null,
            minWordsPerMessage: null,
            maxWordsPerMessage: null,
            message: "",
            showGallery: true
        };

        this.startGame = this.startGame.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.completeStory = this.completeStory.bind(this);
        this.setUsername = this.setUsername.bind(this);
        this.getCurrentStory = this.getCurrentStory.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleSimpleStateChange = this.handleSimpleStateChange.bind(this);
        this.convertMessagesToStory = this.convertMessagesToStory.bind(this);
        this.calculateRoundTimeLeft = this.calculateRoundTimeLeft.bind(this);
        this.toggleGallery = this.toggleGallery.bind(this);
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

                stompClient.subscribe("/topic/lobby." + me.state.lobbyId, function(response) {
                    let responseObj = JSON.parse(response.body);
                    const responseType = responseObj.responseType;
                    if (responseType === "START_GAME") {
                        me.setState({
                            gameState: responseObj.gameState,
                            stories: responseObj.game.stories,
                            gallery: responseObj.gallery,
                            endTime: responseObj.game.endTime && new Date(responseObj.game.endTime)
                        });
                        //re-render once a second to update timer
                        window.setInterval(me.forceUpdate.bind(me), 1000);
                    } else if (responseType === "JOIN_GAME") {
                        let stories = responseObj.lobby.game.stories;
                        me.setState({
                            joined: me.state.clickedSetUsername,
                            gameState: responseObj.lobby.gameState,
                            lobby: responseObj.lobby,
                            stories: stories,
                            completedStories: responseObj.lobby.game.completedStories,
                            gallery: responseObj.lobby.gallery,
                            endTime: responseObj.lobby.game.endTime && new Date(responseObj.lobby.game.endTime)
                        });
                        //re-render once a second to update timer
                        window.setInterval(me.forceUpdate.bind(me), 1000);
                    } else if (responseType === "STORY_CHANGE") {
                        me.setState({
                            stories: responseObj.stories,
                            completedStories: responseObj.completedStories,
                            gameState: responseObj.gameState
                        });
                    } else {
                        console.log("ERROR: Unhandled responseType: " + responseType);
                    }
                });
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
            JSON.stringify({roundTimeMinutes: parseInt(this.state.roundTime)})
        );
    }

    sendMessage(event) {
        event.preventDefault();
        let currentStory = this.getCurrentStory();

        if (currentStory && this.state.message && this.state.message !== "") {
            this.state.stompClient.send(
                "/app/lobby." + this.state.lobbyId + ".newMessage",
                {},
                JSON.stringify({
                    message: this.state.message,
                    storyId: currentStory.id
                })
            );
            this.setState({message: ""});
        }
    }

    completeStory() {
        let currentStory = this.getCurrentStory();

        if (currentStory) {
            this.state.stompClient.send("/app/lobby." + this.state.lobbyId + ".completeStory", {}, currentStory.id);
        }
    }

    getCurrentStory() {
        let stories = this.state.stories;
        let myStories = stories && stories[this.state.username];
        return myStories && myStories[0];
    }

    setUsername(event) {
        event.preventDefault();
        if (this.state.username !== "") {
            this.setState({clickedSetUsername: true});
            this.state.stompClient.send("/app/lobby." + this.state.lobbyId + ".joinGame", {}, this.state.username);
        }
    }

    handleUsernameChange(event) {
        this.handleSimpleStateChange(event, "username");
        localStorage.setItem("username", event.target.value);
    }

    handleSimpleStateChange(event, propertyName) {
        let newState = {};
        newState[propertyName] = event.target.value;
        this.setState(newState);
    }

    convertMessagesToStory(messages) {
        let joined = messages.map(m => m.text).join(" ");
        return joined.split("\n").map((item, key) => {
            return (
                <span key={key}>
                    {item}
                    <br />
                </span>
            );
        });
    }

    calculateRoundTimeLeft() {
        const difference = +this.state.endTime - +new Date();
        let timeLeft = {};

        if (difference > 0) {
            timeLeft = {
                days: Math.floor(difference / (1000 * 60 * 60 * 24)),
                hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
                minutes: Math.floor((difference / 1000 / 60) % 60),
                seconds: Math.floor((difference / 1000) % 60)
            };
        }

        return timeLeft;
    }

    toggleGallery() {
        this.setState({showGallery: !this.state.showGallery});
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
                    <div class="centered">
                        <input
                            type="text"
                            name="username"
                            placeholder="Name"
                            onChange={this.handleUsernameChange}
                            value={this.state.username}
                        />
                    </div>
                    <form class="centered" onSubmit={this.setUsername}>
                        <input type="submit" value="Set username" />
                    </form>
                </div>
            );
        } else if (this.state.lobby && this.state.gameState === "GATHERING_PLAYERS") {
            let players = this.state.lobby.players.map(player => <li key={player.username}>{player.username}</li>);
            let gallery = this.state.gallery.map(story =>
                <li key={story.creatingPlayer.username}>{this.convertMessagesToStory(story.messages)}</li>
            );
            return (
                <div id="lobby-content" style={{"width": "700px"}}>
                    <div id="logo">
                        <img src="../../logo.svg" alt="logo" />
                    </div>
                    <span class="section-header">Players</span>
                    <div>
                        <ul>{players}</ul>
                    </div>
                    {this.state.gallery && this.state.gallery.length > 0 && this.state.showGallery && (
                        <div>
                            <span class="section-header">Gallery</span> <br />
                            <ul>{gallery}</ul>
                        </div>
                    )}
                    {this.state.lobby.creator.username === this.state.username && (
                        <div>
                            { this.state.gallery && this.state.gallery.length > 0 && (
                                <button type="button" onClick={this.toggleGallery}>
                                    {this.state.showGallery && (<span>Hide</span>)}{!this.state.showGallery && (<span>Show</span>)} Gallery
                                </button>
                            )}
                            <div id="settings">
                                <span class="section-header">Settings</span>
                                <div class="setting">
                                    <span class="bold-text">Minutes per round</span> <br />
                                    <input
                                        type="text"
                                        name="roundTime"
                                        class="settings-input"
                                        onChange={(e) => this.handleSimpleStateChange(e, "roundTime")}
                                        value={this.state.roundTime}
                                    />
                                </div>
                                <div class="setting">
                                    <span class="bold-text">Seconds per message</span> <br />
                                    <input
                                        type="text"
                                        name="messageTime"
                                        class="settings-input"
                                        onChange={e => this.handleSimpleStateChange(e, "messageTime")}
                                        value={this.state.messageTime}
                                    />
                                </div>
                                <div class="setting">
                                    <span class="bold-text">Words per message</span> <br />
                                    <input
                                        type="text"
                                        name="minWordsPerMessage"
                                        class="settings-input"
                                        onChange={e => this.handleSimpleStateChange(e, "minWordsPerMessage")}
                                        value={this.state.minWordsPerMessage}
                                    />
                                    <span style={{"margin-left": "10px"}}>-</span>
                                    <input
                                        type="text"
                                        name="maxWordsPerMessage"
                                        class="settings-input"
                                        onChange={e => this.handleSimpleStateChange(e, "maxWordsPerMessage")}
                                        value={this.state.maxWordsPerMessage}
                                    />
                                </div>
                                Leave any field blank to ignore it.
                            </div>
                            <form onSubmit={this.startGame}>
                                <input type="submit" value="Start game" />
                            </form>
                        </div>
                    )}
                </div>
            );
        } else if (this.state.gameState === "PLAYING") {
            let lobby = this.state.lobby;
            let players = lobby.players.map(player => (
                <li key={player.username}>
                    {player.username} ({this.state.stories[player.username].length} in queue)
                </li>
            ));
            let stories = this.state.stories[this.state.username];
            let currentStory = stories && stories[0] && this.convertMessagesToStory(stories[0].messages);
            let timeLeft = this.calculateRoundTimeLeft();
            let roundOver = !timeLeft.hasOwnProperty("seconds");

            return (
                <div id="game-content" style={{width: "700px"}}>
                    You are playing a game with:
                    <div>
                        <ul>{players}</ul>
                    </div>
                    {this.state.endTime && (
                        <div>
                            {roundOver && <span>Round is over! You may send one last message.</span>}
                            {!roundOver && (
                                <span>
                                    There is {timeLeft.days > 0 && <span>{timeLeft.days}:</span>}
                                    {timeLeft.hours > 0 && <span>{timeLeft.hours}:</span>}
                                    <span>
                                        {timeLeft.minutes}:{timeLeft.seconds.toString().padStart(2, '0')}
                                    </span>{" "}
                                    left in the round.
                                </span>
                            )}
                        </div>
                    )}
                    <div>You have {stories.length} stories in queue.</div>
                    <div>
                        Current story:
                        <div>{currentStory}</div>
                    </div>
                    <div>
                        <textarea
                            name="message"
                            onChange={e => this.handleSimpleStateChange(e, "message")}
                            value={this.state.message}
                            style={{width: "700px"}}
                        />
                        <form onSubmit={this.sendMessage}>
                            <input type="submit" value="Send" />
                        </form>
                        <button type="button" onClick={this.completeStory}>
                            This story is done
                        </button>
                    </div>
                </div>
            );
        } else if (this.state.gameState === "READING") {
            let myCreatedStory = this.state.completedStories.find(
                el => el.creatingPlayer.username === this.state.username
            );
            let myReadableStory = myCreatedStory && this.convertMessagesToStory(myCreatedStory.messages);
            return (
                <div id="reading-content" style={{width: "700px"}}>
                    You are now reading:
                    <div>{myReadableStory}</div>
                    {this.state.lobby.creator.username === this.state.username && (
                        <button type="button" onClick={this.startGame}>
                            Start New Game
                        </button>
                    )}
                </div>
            );
        }
    }
}
