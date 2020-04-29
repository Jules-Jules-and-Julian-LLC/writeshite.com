import React from "react";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: props.username,
            lobbyId: props.lobbyId,
            gameState: "lobby"
        };

        this.startGame = this.startGame.bind(this);
    }

    componentDidMount() {
        this.state.stompClient.subscribe("/topic/lobby/" + this.state.lobbyId, function(response) {
            //break out the different responses we get from the lobby
            console.log(response);
            var responseObj = JSON.parse(response.body);
        });
    }

    startGame(event) {
        event.preventDefault();
        this.state.stompClient.send("/app/lobby/" + this.state.lobbyId + "/startGame", {}, {})
    }

    render() {
        return (
            <div id="lobby-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                This is the lobby, it will have a player list some day.
                <form onSubmit={this.startGame}>
                    <input type="submit" value="Start game" />
                </form>
            </div>
        );
    }
}
