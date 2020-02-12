import React from 'react';
import './App.css';
import $ from 'jquery';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';


var stompClient = null;

function setConnected(connected) {
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect(e) {
    e.preventDefault();
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(message) {
    $("#chat-room").append("<tr><td>" + message + "</td></tr>");
}

function createGame() {
    stompClient.send("/app/create", {}, JSON.stringify({'name': $("#name").val()}));
}


function App() {
  return (
    <div id="main-content" className="container">
        <div className="row">
            <div className="col-md-6">
                <form className="form-inline">
                    <div className="form-group">
                        <label htmlFor="connect">WebSocket connection:</label>
                        <button id="connect" className="btn btn-default" type="submit" onClick={connect}>Connect</button>
                        <button id="disconnect" className="btn btn-default" type="submit" disabled="disabled">Disconnect </button>
                    </div>
                </form>
            </div>
            <div className="col-md-6">
                <form className="form-inline">
                    <div className="form-group">
                        <label htmlFor="name">What is your name?</label>
                        <input type="text" id="name" className="form-control" placeholder="Your name here..." />
                    </div>
                    <button id="send" className="btn btn-default" type="submit">Send</button>
                </form>
            </div>
        </div>
        <div className="row">
            <div className="col-md-12">
                <table id="conversation" className="table table-striped">
                    <thead>
                    <tr>
                        <th>Greetings</th>
                    </tr>
                    </thead>
                    <tbody id="greetings">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
  );
}

export default App;
