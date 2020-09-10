import React from "react";
import {withRouter} from "react-router";
import LogoPage from "./LogoPage";
import PaperStack from "./PaperStack";

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
                <LogoPage>
                    <div class="centered">
                        <input type="text" name="lobbyId" placeholder="Lobby ID" onChange={this.handleLobbyIdChange} />
                    </div>
                    <form class="centered" onSubmit={this.joinGame}>
                        <input class="button" type="submit" value="Join game" />
                    </form>
                    <PaperStack count={5} />
                </LogoPage>
            </div>
        );
    }
}

export default withRouter(Homepage);
