import React from "react";
import { withRouter } from 'react-router';

class Homepage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: ""
        };

        this.joinGame = this.joinGame.bind(this);
        this.createGame = this.createGame.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
    }

    joinGame(event) {
        event.preventDefault();
        this.props.history.push('/joinGame');
    }

    createGame(event) {
        event.preventDefault();
        var me = this
        this.state.stompClient.subscribe("/topic/createLobby", function(response) {
            var responseObj = JSON.parse(response.body);
            me.props.history.push('/lobby/' + responseObj.lobby.lobbyId);
        });
        const body = JSON.stringify({clientId: this.state.clientId, username: this.state.username});
        this.state.stompClient.send("/app/createLobby", {}, body);
    }

    handleUsernameChange(event) {
        event.preventDefault();
        this.setState({username: event.target.value})
    }

    render() {
        return (
            <div id="homepage-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                <div>
                    <input type="text" name="username" placeholder="Name" onChange={this.handleUsernameChange} />
                </div>
                <form onSubmit={this.joinGame}>
                    <input type="submit" value="Join game" />
                </form>
                <form onSubmit={this.createGame}>
                    <input type="submit" value="Create game" />
                </form>
            </div>
        );
    }
}

export default withRouter(Homepage)
