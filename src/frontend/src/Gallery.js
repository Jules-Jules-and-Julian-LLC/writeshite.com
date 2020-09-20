import React from "react";
import LinedPaper from "./LinedPaper";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/");

        this.state = {
            lobbyId: pathname[pathname.length - 1],
            entries: []
        };
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
            if (this.state.entries.length > 0) {
                let entries = this.state.entries
                    .sort((a, b) => b.createDatetime - a.createDatetime)
                    .map(entry => (
                        <li key={entry.creatorUsername + "" + entry.createDatetime}>
                            <LinedPaper text={entry.text} title={entry.creatorUsername} shorten={true} />
                        </li>
                    ));
                return (
                    <div>
                        <div>
                            <button
                                class="button"
                                type="button"
                                onClick={() => window.open("../lobby/" + this.state.lobbyId, "_self")}
                            >
                                Go To Lobby
                            </button>
                        </div>
                        <ul>{entries}</ul>
                    </div>
                );
            } else {
                return (
                    <div>
                        <button
                            class="button"
                            type="button"
                            onClick={() => window.open("../lobby/" + this.state.lobbyId, "_self")}
                        >
                            Go To Lobby
                        </button>
                        <div>
                            No stories for that lobby yet. <br />
                            Go Write some Shite!
                        </div>
                    </div>
                );
            }
        }
    }
}
