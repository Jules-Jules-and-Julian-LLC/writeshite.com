import React from 'react';
import './App.css';
import $ from 'jquery';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import Homepage from './Homepage';
import { StateProvider } from './store.js';

export default class App extends React.Component {
    connect() {
        //TODO JJ error when connect fails, retry connection
        var socket = new SockJS('/websocket');
        let stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/clientId', function (response) {
                console.log(response.clientId);
                //TODO JJ set clientId to JSON.parse(response.body).clientId
            });
            stompClient.send('/app/clientId', {}, {});
        });
    }

    showGreeting(message) {
        $("#chat-room").append("<tr><td>" + message + "</td></tr>");
    }

    render() {
        this.connect();
        return (
            <StateProvider>
                <Homepage />
            </StateProvider>
        );
    }
}
