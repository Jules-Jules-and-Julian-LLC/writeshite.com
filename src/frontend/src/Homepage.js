import React from "react";
import {withRouter} from "react-router";

class Homepage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            lobbyId: null
        };

        this.joinGame = this.joinGame.bind(this);
        this.handleLobbyIdChange = this.handleLobbyIdChange.bind(this);
    }

    joinGame(event) {
        event.preventDefault();
        if (this.state.lobbyId && this.state.lobbyId !== "") {
            this.props.history.push("/lobby/" + this.state.lobbyId);
        }
    }

    handleLobbyIdChange(event) {
        event.preventDefault();
        this.setState({lobbyId: event.target.value});
    }

    render() {
        return (
            <div id="homepage-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                <div class="centered">
                    <input type="text" name="lobbyId" placeholder="Lobby ID" onChange={this.handleLobbyIdChange} />
                </div>
                <form class="centered" onSubmit={this.joinGame}>
                    <input type="submit" value="Join game" />
                </form>
            </div>
        );
    }
}

export default withRouter(Homepage);
