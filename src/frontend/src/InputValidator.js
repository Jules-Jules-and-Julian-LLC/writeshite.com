import React from "react";
import Toaster from "./Toaster";

class InputValidator extends React.Component {
    static validateLobbyId(lobbyId) {
        if (lobbyId && lobbyId !== "" && lobbyId.match(/^[A-Za-z0-9-_]{1,64}$/)) {
            return true;
        } else {
            Toaster.warnInvalidLobbyId();
            return false;
        }
    }

    static validateUsername(username) {
        if (username && username !== "" && username.length > 0 && username.length <= 64) {
            return true;
        } else {
            Toaster.warnInvalidUsername();
            return false;
        }
    }

    static warnBasedOnErrorType(errorType) {
        if (errorType === "INVALID_LOBBY_ID") {
            Toaster.warnInvalidLobbyId();
        } else if (errorType === "INVALID_USERNAME") {
            Toaster.warnInvalidUsername();
        } else {
            console.error("ERROR: Unhandled errorType: " + errorType);
        }
    }
}

export default InputValidator;
