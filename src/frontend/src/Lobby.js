import React from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import PaperStack from "./PaperStack";
import LinedPaper from "./LinedPaper";
import Toaster from "./Toaster";
import InputValidator from "./InputValidator";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/");

        this.state = {
            username: localStorage.getItem("username"),
            lobbyId: pathname[pathname.length - 1],
            joined: false,
            roundTime: null,
            minWordsPerMessage: "",
            maxWordsPerMessage: "",
            message: "",
            players: [],
            warnedAboutSendButton: false,
            readingOrder: null
        };

        this.startGame = this.startGame.bind(this);
        this.endRound = this.endRound.bind(this);
        this.sendMessage = this.sendMessage.bind(this);
        this.completeStory = this.completeStory.bind(this);
        this.setUsername = this.setUsername.bind(this);
        this.getCurrentStory = this.getCurrentStory.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleSimpleStateChange = this.handleSimpleStateChange.bind(this);
        this.convertMessagesToStory = this.convertMessagesToStory.bind(this);
        this.calculateRoundTimeLeft = this.calculateRoundTimeLeft.bind(this);
        this.onPassStyleChange = this.onPassStyleChange.bind(this);
        this.messageAreaKeyDown = this.messageAreaKeyDown.bind(this);
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

                stompClient.subscribe("/user/queue/overrideUsername", function(message) {
                    me.setState({username: message.body});
                    localStorage.setItem("username", message.body);
                });

                stompClient.subscribe("/topic/lobby." + me.state.lobbyId, function(response) {
                    let responseObj = JSON.parse(response.body);
                    const responseType = responseObj.responseType;
                    const eventReceivedDatetime = responseObj.eventReceivedDatetime;
                    if (
                        !me.state.lastEventReceivedDatetime ||
                        eventReceivedDatetime > me.state.lastEventReceivedDatetime
                    ) {
                        me.setState({
                            lastEventReceivedDatetime: responseObj.eventReceivedDatetime
                        });
                        if (responseType === "START_GAME") {
                            me.setState({
                                message: "",
                                lobbyState: responseObj.lobbyState,
                                stories: responseObj.game.stories,
                                previousRoundStories: responseObj.previousRoundStories,
                                settings: responseObj.game.settings,
                                endTime: responseObj.game.endTime && new Date(responseObj.game.endTime),
                                players: responseObj.players
                            });
                            //re-render once a second to update timer
                            window.setInterval(me.forceUpdate.bind(me), 1000);
                        } else if (responseType === "JOIN_GAME") {
                            let stories = responseObj.lobby.game.stories;
                            me.setState({
                                joined: me.state.clickedSetUsername,
                                lobbyState: responseObj.lobby.lobbyState,
                                lobby: responseObj.lobby,
                                stories: stories,
                                completedStories: responseObj.lobby.game.completedStories,
                                previousRoundStories: responseObj.lobby.previousRoundStories,
                                endTime: responseObj.lobby.game.endTime && new Date(responseObj.lobby.game.endTime),
                                settings: me.state.settings || responseObj.lobby.game.settings,
                                roundTime: responseObj.lobby.game.settings.roundTimeMinutes,
                                players: responseObj.lobby.players
                            });
                            //re-render once a second to update timer
                            window.setInterval(me.forceUpdate.bind(me), 1000);
                        } else if (responseType === "STORY_CHANGE") {
                            me.setState({
                                stories: responseObj.stories,
                                completedStories: responseObj.completedStories,
                                lobbyState: responseObj.lobbyState,
                                players: responseObj.players,
                                readingOrder: responseObj.readingOrder
                            });
                            if(responseObj.lobbyState === "GATHERING_PLAYERS" || responseObj.lobbyState === "READING") {
                                me.setState({message: ""});
                            }
                        } else if (responseType === "ERROR") {
                            InputValidator.warnBasedOnErrorType(responseObj.errorType);
                        } else {
                            console.log("ERROR: Unhandled responseType: " + responseType);
                        }
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
        let minWordsPerMessage = parseInt(this.state.minWordsPerMessage);
        let maxWordsPerMessage = parseInt(this.state.maxWordsPerMessage);
        let roundTime = parseInt(this.state.roundTime);
        if (
            (isNaN(minWordsPerMessage) ||
                isNaN(maxWordsPerMessage) ||
                (minWordsPerMessage > 0 && maxWordsPerMessage > 0 && minWordsPerMessage <= maxWordsPerMessage)) &&
            (isNaN(roundTime) || roundTime > 0)
        ) {
            this.state.stompClient.send(
                "/app/lobby." + this.state.lobbyId + ".startGame",
                {},
                JSON.stringify({
                    roundTimeMinutes: roundTime,
                    minWordsPerMessage: minWordsPerMessage,
                    maxWordsPerMessage: maxWordsPerMessage,
                    passStyle: this.state.settings.passStyle
                })
            );
        }
    }

    endRound(event) {
        event.preventDefault();
        this.state.stompClient.send("/app/lobby." + this.state.lobbyId + ".endRound", {}, {});
    }

    sendMessage(event, warnAboutSendButton) {
        event && event.preventDefault();
        let currentStory = this.getCurrentStory();

        if (currentStory &&
            InputValidator.validateMessage(this.state.message, this.state.settings.minWordsPerMessage,
                this.state.settings.maxWordsPerMessage, true)) {

            if(this.state.warnedAboutSendButton === false && warnAboutSendButton === true) {
                Toaster.toast("info", "You may also send messages with Ctrl+Enter, Alt+Enter, or ⌘+Enter");
                this.setState({warnedAboutSendButton: true});
            }

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
        if (InputValidator.validateUsername(this.state.username)) {
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
        let joined = messages.map(m => m.text).join(" ").split("\n");
        let story = joined.map((item, key) => {
            return (
                <span key={key}>
                    {item}
                    {key !== joined.length - 1 &&
                        <br />
                    }
                </span>
            );
        });

        if(this.state.message && this.state.message !== "") {
            let messageSplit = this.state.message.split("\n");
            let messagePreview = messageSplit.map((item, key) => {
                return <span key={"message-preview-" + key} className="message-preview">
                    {" " + item}
                    {key !== messageSplit.length - 1 &&
                        <br />
                    }
                    </span>
            });
            story = story.concat(messagePreview);
        }
        return story;
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

    onPassStyleChange(event) {
        let newSettings = this.state.settings;
        newSettings["passStyle"] = event.target.value;
        this.setState({settings: newSettings});
    }

    messageAreaKeyDown(event) {
        if ((event.altKey || event.ctrlKey || event.metaKey) && event.keyCode === 13) {
            this.sendMessage();
        }
    }

    /*
        You must use at least N words.
        You must use at most N words.
        You must use at least N words and at most M words.
    */
    getWordRangeSentence() {
        let sentence = null;
        let minWordsPerMessage = this.state.settings.minWordsPerMessage;
        let maxWordsPerMessage = this.state.settings.maxWordsPerMessage;

        if (minWordsPerMessage || maxWordsPerMessage) {
            sentence = "You must use ";
            if (minWordsPerMessage) {
                sentence += "at least " + minWordsPerMessage + " word" + (minWordsPerMessage > 1 ? "s" : "");
                if (maxWordsPerMessage) {
                    sentence += " and ";
                }
            }
            if (maxWordsPerMessage) {
                sentence += "at most " + maxWordsPerMessage + " word" + (maxWordsPerMessage > 1 ? "s" : "");
            }
            sentence += ".";
        }

        return sentence;
    }

    render() {
        if (!this.state.stompClient) {
            return <div id="connecting-to-lobby">Connecting to lobby, please wait...</div>;
        } else if (!this.state.joined) {
            return (
                <div id="set-user-info-content">
                    <div>
                        <input
                            type="text"
                            name="username"
                            placeholder="Username"
                            onChange={this.handleUsernameChange}
                            value={this.state.username}
                        />
                    </div>
                    <form onSubmit={this.setUsername}>
                        <input className="button" type="submit" value="Set Username" />
                    </form>
                </div>
            );
        } else if (this.state.lobby && this.state.lobbyState === "GATHERING_PLAYERS") {
            let players = this.state.players.map(player => <li key={player.username}>{player.username}</li>);
            let previousRoundStories = this.state.previousRoundStories.map(story => (
                <li key={story.creatingPlayer.username}>
                    <LinedPaper
                        text={this.convertMessagesToStory(story.messages)}
                        title={story.creatingPlayer.username}
                        shorten={true}
                    />
                </li>
            ));
            return (
                <div id="lobby-content">
                    <span className="section-header">Players</span>
                    <div>
                        <ul>{players}</ul>
                    </div>
                    {this.state.lobby.creator.username === this.state.username && (
                        <div>
                            <div id="settings">
                                <span className="section-header">Optional Settings</span>
                                <div className="setting">
                                    <span className="bold-text">Minutes per round</span> <br />
                                    <input
                                        type="text"
                                        name="roundTime"
                                        className="settings-input"
                                        onChange={e => this.handleSimpleStateChange(e, "roundTime")}
                                        value={this.state.roundTime}
                                    />
                                </div>
                                <div className="setting">
                                    <span className="bold-text">Words per message</span> <br />
                                    <input
                                        type="text"
                                        name="minWordsPerMessage"
                                        className="settings-input"
                                        onChange={e => this.handleSimpleStateChange(e, "minWordsPerMessage")}
                                        value={this.state.minWordsPerMessage}
                                    />
                                    <span style={{marginLeft: "10px"}}>-</span>
                                    <input
                                        type="text"
                                        name="maxWordsPerMessage"
                                        className="settings-input"
                                        onChange={e => this.handleSimpleStateChange(e, "maxWordsPerMessage")}
                                        value={this.state.maxWordsPerMessage}
                                    />
                                </div>
                                <div className="setting">
                                    <span className="bold-text">Pass Stories</span> <br />
                                    <label>
                                        <input
                                            type="radio"
                                            value="MINIMIZE_WAIT"
                                            checked={this.state.settings.passStyle === "MINIMIZE_WAIT"}
                                            onChange={this.onPassStyleChange}
                                        />
                                        To Minimize Wait Time
                                    </label>{" "}
                                    <br />
                                    <label>
                                        <input
                                            type="radio"
                                            value="ORDERED"
                                            checked={this.state.settings.passStyle === "ORDERED"}
                                            onChange={this.onPassStyleChange}
                                        />
                                        In Order
                                    </label>{" "}
                                    <br />
                                    <label>
                                        <input
                                            type="radio"
                                            value="RANDOM"
                                            checked={this.state.settings.passStyle === "RANDOM"}
                                            onChange={this.onPassStyleChange}
                                        />
                                        Randomly
                                    </label>
                                </div>
                            </div>
                            <form onSubmit={this.startGame}>
                                <input className="button start-game-button" type="submit" value="Start game" />
                            </form>
                        </div>
                    )}
                    <div>
                        <button
                            className="button"
                            type="button"
                            onClick={() => window.open("../gallery/" + this.state.lobbyId, "_blank")}
                        >
                            Open Gallery
                        </button>
                    </div>
                    {this.state.previousRoundStories && this.state.previousRoundStories.length > 0 && (
                        <div className="previous-round-stories">
                            <span className="section-header">Previous Round Stories</span> <br />
                            <ul>{previousRoundStories}</ul>
                        </div>
                    )}
                </div>
            );
        } else if (this.state.lobbyState === "PLAYING") {
            let players = this.state.players.map(player => (
                <div key={player.username} className="player-name">
                    <li className={player.username === this.state.username ? "bold-text" : undefined}>
                        {player.username} <PaperStack count={this.state.stories[player.username] && this.state.stories[player.username].length} />
                    </li>
                </div>
            ));
            let stories = this.state.stories[this.state.username];
            let currentStory = stories && stories[0] && this.convertMessagesToStory(stories[0].messages);
            let timeLeft = this.calculateRoundTimeLeft();
            let roundOver = !timeLeft.hasOwnProperty("seconds");
            let wordRangeSentence = this.getWordRangeSentence();
            let validInput =
                !this.state.message ||
                this.state.message === "" ||
                InputValidator.validateMessage(
                    this.state.message,
                    this.state.settings.minWordsPerMessage,
                    this.state.settings.maxWordsPerMessage,
                    false
                );

            return (
                <div id="game-content">
                    {this.state.endTime && (
                        <div id="timer">
                            {roundOver && (
                                <span>
                                    Round is over! <br /> You may send one last message.
                                </span>
                            )}
                            {!roundOver && (
                                <span>
                                    {timeLeft.days > 0 && <span>{timeLeft.days}:</span>}
                                    {timeLeft.hours > 0 && <span>{timeLeft.hours}:</span>}
                                    <span>
                                        {timeLeft.minutes}:{timeLeft.seconds.toString().padStart(2, "0")}
                                    </span>
                                </span>
                            )}
                        </div>
                    )}
                    <div>
                        <ul>{players}</ul>
                    </div>

                    {!currentStory && <div className="waiting-for-story">Waiting to have a story passed to you...</div>}
                    {currentStory && (
                        <div>
                            <LinedPaper text={currentStory} />

                            <div>{wordRangeSentence && wordRangeSentence}</div>

                            <div>
                                <textarea
                                    name="message"
                                    id="message-input"
                                    onChange={e => this.handleSimpleStateChange(e, "message")}
                                    value={this.state.message}
                                    className={!validInput ? "warning-text" : undefined}
                                    style={{width: "100%"}}
                                    onKeyDown={this.messageAreaKeyDown}
                                    placeholder="Write Shite here..."
                                />

                                <form id="game-buttons" onSubmit={(event) => this.sendMessage(event, true)}>
                                    <button
                                        id="complete-story-button"
                                        className="button"
                                        type="button"
                                        title="Does not add current message to the story"
                                        onClick={this.completeStory}
                                        onMouseEnter={() => document.getElementById("message-input").classList.add("complete-story-hovered")}
                                        onMouseLeave={() => document.getElementById("message-input").classList.remove("complete-story-hovered")}
                                    >
                                        Complete Story
                                    </button>
                                    <input className="button" type="submit" value="Send" />
                                </form>
                            </div>
                        </div>
                    )}
                    <div>
                        {this.state.lobby.creator.username === this.state.username && (
                            <button className="button end-round-button" type="button" onClick={this.endRound}>
                                End Round
                            </button>
                        )}
                    </div>
                </div>
            );
        } else if (this.state.lobbyState === "READING") {
            let myCreatedStory = this.state.completedStories.find(
                el => el.creatingPlayer.username === this.state.username
            );
            let myReadableStory = myCreatedStory && this.convertMessagesToStory(myCreatedStory.messages);
            if(!myReadableStory) {
                return <div className="waiting-for-story">Waiting for others to finish reading...</div>;
            } else {
                let readingOrder;
                if(this.state.readingOrder) {
                    readingOrder = this.state.readingOrder.map(player => <li key={player.username}>{player.username}</li>);
                }
                return (
                    <div id="reading-content">
                        <LinedPaper text={myReadableStory} />
                        {readingOrder &&
                            <div>
                                <div className="bold-text">Suggested Reading Order</div>
                                <ul>{readingOrder}</ul>
                            </div>
                        }
                        {this.state.lobby.creator.username === this.state.username && (
                            <button className="button" type="button" onClick={this.startGame}>
                                Start New Game
                            </button>
                        )}
                    </div>
                );
            }
        } else {
            return (<div>ERROR unhandled lobby state: {this.state.lobbyState}</div>);
        }
    }
}
