import React from "react";
import "./App.css";
import Homepage from "./Homepage";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            clientId: "",
            stompClient: null
        }
    }

    componentDidMount() {
        this.connect();
    }

    connect() {
        //TODO JJ error when connect fails, retry connection
        let socket = new SockJS("/websocket");
        let stompClient = Stomp.over(socket);
        let me = this;
        stompClient.connect({}, function(frame) {
            console.log("Connected: " + frame);
            me.state.stompClient = stompClient;
            stompClient.subscribe("/topic/getClientId", function(response) {
                me.setState({
                    clientId: JSON.parse(response.body).clientId,
                    stompClient: stompClient
                });
                console.log(me.state);
            });
            stompClient.send("/app/getClientId", {}, {});
        });
    }

    render() {
        if (!this.state.clientId) {
            return (
                <div>
                    Loading, please wait...
                    <div style={{position:'relative', 'padding-bottom':'calc(75.00% + 44px)'}}>
                        <iframe src='https://gfycat.com/ifr/IllWearyAntipodesgreenparakeet' frameborder='0'
                            scrolling='no' width='25%' height='25%' style={{position:'absolute', top:0, left:0}} allowfullscreen>
                        </iframe>
                    </div>
                    <p>
                        <a href="https://gfycat.com/illwearyantipodesgreenparakeet-animal-crossing-dessin-anime-nintendo" />
                    </p>
                </div>
            );
        }

        return (
            <Homepage clientId={this.state.clientId} stompClient={this.state.stompClient}/>
        );
    }
}
