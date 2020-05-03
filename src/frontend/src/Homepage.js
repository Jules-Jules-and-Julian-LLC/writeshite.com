import React from "react";
import { withRouter } from 'react-router';

class Homepage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: props.username
        };

        this.joinGame = this.joinGame.bind(this);
        this.createGame = this.createGame.bind(this);
        this.handleUsernameChange = props.handleUsernameChange;
        this.getUsername = props.getUsername;
        this.getStompClient = props.getStompClient
    }

    joinGame(event) {
        event.preventDefault();
        this.props.history.push('/joinGame');
    }

    async createGame(event) {
        event.preventDefault();
        //TODO setup stomp connection, create lobby, set stomp client for parent as well so we can pass it down to lobby
        //TODO probably better if we instead pass a "create stomp client" method from parent, or a "get stomp client" that creates if need be?
        //TODO can I make the connect give a client ID?
        let me = this;
        (await this.getStompClient()).subscribe("/topic/createLobby", function(response) {
            var responseObj = JSON.parse(response.body);
            me.props.history.push('/lobby/' + responseObj.lobby.lobbyId);
        });
        (await this.getStompClient()).send("/app/createLobby", {}, {});
    }

    render() {
        return (
            <div id="homepage-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                <form onSubmit={this.createGame}>
                    <input type="submit" value="Create game" />
                </form>
            </div>
        );
    }
}

export default withRouter(Homepage)
