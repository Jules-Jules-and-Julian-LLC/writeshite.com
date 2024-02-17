import React from "react";
import { useNavigate } from "react-router-dom";
import InputValidator from "./InputValidator";

function Homepage() {
    const navigate = useNavigate(); // Use the hook directly

    const [lobbyId, setLobbyId] = React.useState(null);

    function joinGame(event) {
        event.preventDefault();
        if (InputValidator.validateLobbyId(lobbyId)) {
            navigate("/lobby/" + lobbyId);
        }
    }

    function openGallery(event) {
        event.preventDefault();
        if (InputValidator.validateLobbyId(lobbyId)) {
            window.open(
                "../gallery/" + lobbyId + "?fromHomepage=true",
                "_blank"
            );
        }
    }

    function handleLobbyIdChange(event) {
        event.preventDefault();
        setLobbyId(event.target.value);
    }

    return (
        <div id="homepage-content">
            <div>
                <input
                    type="text"
                    name="lobbyId"
                    placeholder="Lobby Name"
                    onChange={handleLobbyIdChange}
                />
            </div>
            <form onSubmit={joinGame}>
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
                    onClick={openGallery}
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

export default Homepage;
