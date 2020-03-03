import React from 'react';
import './App.css';
import $ from 'jquery';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import Homepage from './Homepage';


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

function connect() {
//TODO JJ error when connect fails, retry connection
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/topic/clientId', function (response) {
            console.log(response.clientId);
            //TODO JJ set clientId to JSON.parse(response.body).clientId
        });
        stompClient.send('/app/clientId', {}, {});
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

function App() {
    connect();
  return (
  <Homepage />
  );
}

export default App;
