var express    = require('express');        // call express
var app        = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var port = process.env.PORT || 3000;
http.listen(port, function () {
    console.log('Server listening at port %d', port);
});

app.get('/',function(req,res){
    res.send("Welcome to my socket");
});

var onlineUsers = {};

io.on('connection', function (client) {
    
    console.log('one user connected : ' + client.id);

    client.on('login', function (fullname) {
        onlineUsers[client.id] = fullname;
        client.emit('login', client.id);
    });

    client.on('call', function (id) {
        if (id != client.id && onlineUsers[id]) {
            client.broadcast.emit('call', onlineUsers[client.id]);
        } else {
            client.emit('callFail');
        }
    });

    client.on('callReject', function (){
        client.broadcast.emit('callReject');
    });

    client.on('callEnd', function (){
        client.broadcast.emit('callEnd');
    });

    client.on('callAccept', function (){
        client.broadcast.emit('callAccept');
    });

    client.on('callReceive', function (){
        client.broadcast.emit('createOffer', {});
    });

    client.on('offer', function (details) {
        client.broadcast.emit('offer', details);
        console.log('offer: ' + JSON.stringify(details));
    });

    client.on('answer', function (details) {
        client.broadcast.emit('answer', details);
        console.log('answer: ' + JSON.stringify(details));
    });

    client.on('candidate', function (details) {
        client.broadcast.emit('candidate', details);
        console.log('candidate: ' + JSON.stringify(details));
    });

    // when the client emits 'new message', this listens and executes

});
