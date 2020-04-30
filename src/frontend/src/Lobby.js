import React from "react";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/");
        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: props.username,
            lobbyId: pathname[pathname.length - 1],
            gameState: "lobby",
            players: [props.username]
        };

        this.startGame = this.startGame.bind(this);
    }

    componentDidMount() {
        this.state.stompClient.subscribe("/user/lobby/" + this.state.lobbyId, function(response) {
            //break out the different responses we get from the lobby
            console.log(response);
            var responseObj = JSON.parse(response.body);

            //TODO join lobby, add to list of players
        });
        const body = JSON.stringify({
            player: {clientId: this.state.clientId, username: this.state.username},
            lobbyId: this.state.lobbyId
        });
        this.state.stompClient.send("/app/joinLobby", body);
    }

    startGame(event) {
        event.preventDefault();
        this.state.stompClient.send("/app/lobby/" + this.state.lobbyId + "/startGame", {}, {})
    }

    render() {
    //TODO if username not set, require them to set then re-render view (and reconnect?)
        return (
            <div id="lobby-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                This is the lobby, it will have a player list some day.
                <div>
                    <ul>
                        {this.state.players}
                    </ul>
                </div>
                <form onSubmit={this.startGame}>
                    <input type="submit" value="Start game" />
                </form>
            </div>
        );
    }
}
