import React from "react";
import toastr from "toastr/build/toastr.min.js";
import "toastr/build/toastr.min.css";

class Toaster extends React.Component {
    static setOptions = false;

    static toast(type, text) {
        //From: https://codeseven.github.io/toastr/demo.html
        if (!Toaster.setOptions) {
            toastr.options = {
                closeButton: false,
                debug: false,
                newestOnTop: false,
                progressBar: true,
                positionClass: "toast-bottom-center",
                preventDuplicates: true,
                onclick: null,
                showDuration: "300",
                hideDuration: "1000",
                timeOut: "7500",
                extendedTimeOut: "3000",
                showEasing: "swing",
                hideEasing: "linear",
                showMethod: "fadeIn",
                hideMethod: "fadeOut"
            };

            Toaster.setOptions = true;
        }

        toastr[type](text);
    }

    static warnInvalidLobbyId() {
        Toaster.toast("error", "Lobby name is invalid. <br />Lobby names must be 1-64 alphanumeric characters.");
    }

    static warnInvalidUsername() {
        Toaster.toast("error", "Username is invalid. <br />Usernames must be 1-64 characters long.");
    }

    static warnInvalidMessage() {
        Toaster.toast("error", "Message is invalid.");
    }

    static warnCantAddMessageToStory() {
        Toaster.toast("error", "Can't add message to story, that story is not in your queue.");
    }

    static warnLobbyNotFound() {
        Toaster.toast("error", "Lobby not found.");
    }
}

export default Toaster;
