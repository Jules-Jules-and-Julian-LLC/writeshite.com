import React from "react";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        const pathname = window.location.pathname.split("/");

        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: localStorage.getItem("username"),
            lobbyId: pathname[pathname.length - 1],
            joined: false,
        };

        this.startGame = this.startGame.bind(this);
        this.joinGame = this.joinGame.bind(this);
        this.getStompClient = props.getStompClient;
    }

    async componentDidMount() {
        let me = this;
        (await this.getStompClient()).subscribe("/topic/lobby." + this.state.lobbyId, function(response) {
            //break out the different responses we get from the lobby
            console.log(response);
            var responseObj = JSON.parse(response.body);
            me.setState({ joined: true,
                          gameState: responseObj.gameState,
                          players: responseObj.lobby.players.map(p => p.username)
                        });

            //TODO join lobby, add to list of players
            //TODO joined = true when join lobby
        });
//        const body = JSON.stringify({
//            player: {clientId: this.state.clientId, username: this.state.username},
//            lobbyId: this.state.lobbyId
//        });
//        (await this.getStompClient()).send("/app/joinLobby", body);
    }

    async startGame(event) {
        event.preventDefault();
        (await this.getStompClient()).send("/app/lobby/" + this.state.lobbyId + "/startGame", {},
            JSON.stringify({username: localStorage.getItem("username")}));
    }

    async joinGame(event) {
        event.preventDefault()
        const stompClient = await this.getStompClient();
        stompClient.send("/app/lobby." + this.state.lobbyId + ".joinGame", {}, localStorage.getItem("username"));
    }

    handleUsernameChange(event) {
        event.preventDefault();
        localStorage.setItem("username", event.target.value);
    }

    render() {
        //TODO if username not set, require them to set then re-render view (and reconnect?)
        //TODO on join, connect to lobby and get initial info
        if(!this.state.joined) {
            return (
                <div id="set-user-info-content">
                    <div id="logo">
                        <img src="logo.svg" alt="logo" />
                    </div>
                    <div>
                        <input type="text" name="username" placeholder="Name" onChange={this.handleUsernameChange} value={this.state.username} />
                    </div>
                    <form onSubmit={this.joinGame}>
                        <input type="submit" value="Join game" />
                    </form>
                </div>
            );
        }
        const playersList = this.state.players.map((p) => <li>{p}</li>);
        return (
            <div id="lobby-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                This is the lobby, it will have a player list some day.
                <div>
                    <ul>
                        {playersList}
                    </ul>
                </div>
                <form onSubmit={this.startGame}>
                    <input type="submit" value="Start game" />
                </form>
            </div>
        );
    }
}
