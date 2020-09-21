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
                <div>
                    <input type="text" name="lobbyId" placeholder="Lobby ID" onChange={this.handleLobbyIdChange} />
                </div>
                <form onSubmit={this.joinGame}>
                    <input class="button" type="submit" value="Join Lobby" />
                </form>
                <div>
                    <button
                        class="button"
                        type="button"
                        onClick={() =>
                            this.state.lobbyId &&
                            this.state.lobbyId !== "" &&
                            window.open("../gallery/" + this.state.lobbyId + "?fromHomepage=true", "_blank")
                        }
                    >
                        Open Gallery
                    </button>
                </div>
            </div>
        );
    }
}

export default withRouter(Homepage);
