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
        } else if (errorType === "INVALID_MESSAGE") {
            Toaster.warnInvalidMessage();
        } else if (errorType === "CANT_ADD_MESSAGE_TO_STORY") {
            Toaster.warnCantAddMessageToStory();
        } else if (errorType === "LOBBY_NOT_FOUND") {
            Toaster.warnLobbyNotFound();
        } else {
            console.error("ERROR: Unhandled errorType: " + errorType);
        }
    }

    static validateMessage(message, minWordsPerMessage, maxWordsPerMessage, toast) {
        let getWordCount = function(text) {
            let trimmed = text.trim();
            if (trimmed === "") {
                return 0;
            }
            return trimmed.split(/\s+/).length;
        };

        let shouldToast = toast === true;
        let empty = !message || message === "";
        let belowMin = minWordsPerMessage && getWordCount(message) < minWordsPerMessage;
        let aboveMax = maxWordsPerMessage && getWordCount(message) > maxWordsPerMessage;
        let tooLong = message.length > 30000;

        if (shouldToast) {
            let errorText = "";
            if (belowMin) {
                errorText += "Message too short, must be at least " + minWordsPerMessage + " words long.";
            }
            if (aboveMax) {
                if (errorText !== "") {
                    errorText += "<br/>";
                }
                errorText += "Message too long, must be at most " + maxWordsPerMessage + " words long.";
            }
            if (tooLong) {
                if (errorText !== "") {
                    errorText += "<br/>";
                }
                errorText += "Message too long, must be at most 30,000 characters long.";
            }
            if (empty) {
                if (errorText !== "") {
                    errorText += "<br/>";
                }
                errorText += "Message empty, must have some text to add.";
            }

            if (errorText !== "") {
                Toaster.toast("error", errorText);
            }
        }

        return !(belowMin || aboveMax || tooLong || empty);
    }
}

export default InputValidator;
