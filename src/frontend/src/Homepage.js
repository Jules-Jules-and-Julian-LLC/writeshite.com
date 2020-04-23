import React, {useContext} from "react";

export default class Homepage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: props.clientId,
            stompClient: props.stompClient,
            username: ""
        };
        console.log("props" + JSON.stringify(this.props));

        this.joinGame = this.joinGame.bind(this);
        this.createGame = this.createGame.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
    }

    joinGame(event) {
        alert("Joining game: " + event);
        event.preventDefault();
    }

    createGame(event) {
        event.preventDefault();
        const body = JSON.stringify({clientId: this.state.clientId, username: this.state.username});
        console.log("Body: " + body);
        this.state.stompClient.send("/app/createGame", {}, body);
    }

    handleUsernameChange(event) {
        event.preventDefault();
        this.setState({username: event.target.value})
    }

    render() {
        return (
            <div id="homepage-content">
                <div id="logo">
                    <img src="logo.svg" alt="logo" />
                </div>
                <div>
                    <input type="text" name="username" placeholder="Name" onChange={this.handleUsernameChange} />
                </div>
                <form onSubmit={this.joinGame}>
                    <input type="submit" value="Join game" />
                </form>
                <form onSubmit={this.createGame}>
                    <input type="submit" value="Create game" />
                </form>
            </div>
        );
    }
}
