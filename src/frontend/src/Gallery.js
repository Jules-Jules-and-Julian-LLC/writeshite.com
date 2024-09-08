import React from "react";
import LinedPaper from "./LinedPaper";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/").filter(segment => segment !== "");

        this.state = {
            lobbyId: pathname[pathname.length - 1],
            entries: undefined,
            loaded: false
        };

        this.handleGoToLobbyClick = this.handleGoToLobbyClick.bind(this);
        this.handleCloseGalleryClick = this.handleCloseGalleryClick.bind(this);
    }

    handleGoToLobbyClick(event) {
        event && event.preventDefault();
        window.open("../lobby/" + this.state.lobbyId, "_self");
    }

    handleCloseGalleryClick(event) {
        event && event.preventDefault();
        window.open(window.location.origin, "_self");
    }

    componentDidMount() {
        try {
            fetch(`${window.location.origin}/rawJson/${this.state.lobbyId}`, {cache: "no-cache"})
                .then(async res => res.json())
                .then(
                    result => {
                        this.setState({entries: result.entries, loaded: true});
                    },
                    error => {
                        this.setState({loaded: true});
                        console.log(error);
                    }
                );
        } catch (error) {
            console.log(error);
        }
    }

    render() {
        if (!this.state.entries && !this.state.loaded) {
            return (
                <div className="please-wait-text">
                    Loading gallery, please wait...
                </div>
            );
        } else {
            let body;
            if (this.state.entries && this.state.entries.length > 0) {
                let entries = this.state.entries
                    .sort(
                        (a, b) =>
                            new Date(b.createDatetime) -
                            new Date(a.createDatetime)
                    )
                    .map(entry => (
                        <li key={entry.creatorUsername + entry.createDatetime}>
                            <LinedPaper
                                text={entry.text}
                                title={entry.creatorUsername}
                                shorten={true}
                            />
                        </li>
                    ));
                body = (
                    <div>
                        <div className="gallery-header">
                            Only The Finest Shite, Courtesy Of The{" "}
                            {this.state.lobbyId} Lobby
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
                        <button
                            className="button"
                            type="button"
                            onClick={this.handleGoToLobbyClick}
                        >
                            Go To Lobby
                        </button>
                        <button
                            className="button space-left"
                            type="button"
                            onClick={this.handleCloseGalleryClick}
                        >
                            Close Gallery
                        </button>
                    </div>
                    <div>{body}</div>
                </div>
            );
        }
    }
}
