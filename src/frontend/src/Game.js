import React from "react";

export default class Game extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: props.username,
            lobbyId: props.lobbyId,
            gameId: props.gameId
        };
        console.log("props" + JSON.stringify(this.props));
    }

    render() {
        return (
            <div id="game-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                This is the game
            </div>
        );
    }
}
