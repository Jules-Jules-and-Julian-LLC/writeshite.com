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
            navigate(
                "../gallery/" + lobbyId,
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
