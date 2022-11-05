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
            window.open(
                "../gallery/" + this.state.lobbyId + "?fromHomepage=true",
                "_blank"
            );
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
                    <input
                        type="text"
                        name="lobbyId"
                        placeholder="Lobby Name"
                        onChange={this.handleLobbyIdChange}
                    />
                </div>
                <form onSubmit={this.joinGame}>
                    <input
                        className="button"
                        type="submit"
                        value="Join Lobby"
                    />
                </form>
                <div>
                    <button
                        className="button"
                        type="button"
                        onClick={this.openGallery}
                    >
                        Open Gallery
                    </button>
                </div>
                <div>
                    <br />
                    Friends already in a lobby? Ask them for the name, put it in
                    above and click "Join Lobby"! <br /> <br />
                    Want to make a lobby to play with your friends? Pick a name,
                    put it in above, and click "Join Lobby". A lobby will be
                    created for you to play in. <br /> <br />
                    Anyone with the lobby name can join the lobby and can access
                    the gallery of all stories that have been written in that
                    lobby, so make sure only you and your friends know the lobby
                    name!
                </div>
                <div className="help-about-links">
                    <a className="help-link" href="./help" target="_self">
                        Help
                    </a>
                    <a href="./about" target="_self">
                        About
                    </a>
                </div>
            </div>
        );
    }
}

export default withRouter(Homepage);
