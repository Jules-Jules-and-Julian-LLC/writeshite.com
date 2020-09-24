import React from "react";
import {withRouter} from "react-router";
import InputValidator from "./InputValidator";

class Homepage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            lobbyId: null
        };

        this.joinGame = this.joinGame.bind(this);
        this.openGallery = this.openGallery.bind(this);
        this.handleLobbyIdChange = this.handleLobbyIdChange.bind(this);
    }

    joinGame(event) {
        event.preventDefault();
        if (InputValidator.validateLobbyId(this.state.lobbyId)) {
            this.props.history.push("/lobby/" + this.state.lobbyId);
        }
    }

    openGallery(event) {
        event.preventDefault();
        if (InputValidator.validateLobbyId(this.state.lobbyId)) {
            window.open("../gallery/" + this.state.lobbyId + "?fromHomepage=true", "_blank");
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
                    <input type="text" name="lobbyId" placeholder="Lobby Name" onChange={this.handleLobbyIdChange} />
                </div>
                <form onSubmit={this.joinGame}>
                    <input className="button" type="submit" value="Join Lobby" />
                </form>
                <div>
                    <button className="button" type="button" onClick={this.openGallery}>
                        Open Gallery
                    </button>
                </div>
            </div>
        );
    }
}

export default withRouter(Homepage);
