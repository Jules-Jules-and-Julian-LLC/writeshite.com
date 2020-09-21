import React from "react";
import LinedPaper from "./LinedPaper";
import InputValidator from "./InputValidator";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/");

        this.state = {
            lobbyId: pathname[pathname.length - 1],
            entries: [],
            fromHomepage: new URLSearchParams(window.location.search).get("fromHomepage") === "true"
        };

        this.handleButtonClick = this.handleButtonClick.bind(this);
    }

    handleButtonClick(event) {
        event && event.preventDefault();
        if (this.state.fromHomepage && InputValidator.validateLobbyId(this.state.lobbyId)) {
            window.open("../lobby/" + this.state.lobbyId, "_self");
        } else {
            window.close();
        }
    }

    componentDidMount() {
        try {
            fetch("../galleries/" + this.state.lobbyId + ".json", {cache: "no-cache"})
                .then(async res => res.json())
                .then(
                    result => {
                        this.setState({entries: result.entries});
                    },
                    error => console.log(error)
                );
        } catch (error) {
            console.log(error);
        }
    }

    render() {
        if (!this.state.entries) {
            return <div id="connecting-to-lobby">Loading gallery, please wait...</div>;
        } else {
            let body;
            if (this.state.entries.length > 0) {
                let entries = this.state.entries
                    .sort((a, b) => b.createDatetime - a.createDatetime)
                    .map(entry => (
                        <li key={entry.creatorUsername + "" + entry.createDatetime}>
                            <LinedPaper text={entry.text} title={entry.creatorUsername} shorten={true} />
                        </li>
                    ));
                body = (
                    <div>
                        <div class="gallery-header">
                            Only The Finest Shite, Courtesy Of The {this.state.lobbyId} Lobby
                        </div>
                        <ul>{entries}</ul>
                    </div>
                );
            } else {
                body = (
                    <div>
                        No stories for that lobby yet. <br />
                        Go Write some Shite!
                    </div>
                );
            }

            return (
                <div>
                    <div>
                        <button class="button" type="button" onClick={this.handleButtonClick}>
                            {this.state.fromHomepage ? "Go To Lobby" : "Close Gallery"}
                        </button>
                    </div>
                    <div>{body}</div>
                </div>
            );
        }
    }
}
