var stompClient = null;
var app;

window.addEventListener('load', function() {
    app = new Vue({
        el: '#app',
        data: {
            message: 'Hello Vue.js!'
        }
    });
});

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

$(function () {
    connect();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("createGame").on("click", function() { createGame(); });
});