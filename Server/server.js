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

    client.on('disconnect', function() {
        delete onlineUsers[client.id];
    });
    
    client.on('call', function (toId) {
        if (toId != client.id && onlineUsers[toId]) {
            io.to(toId).emit('call', {'fullName': onlineUsers[client.id], 'fromId': client.id});
        }
    });

    client.on('callReject', function (toId){
        io.to(toId).emit('callReject');
    });

    client.on('callEnd', function (toId){
        io.to(toId).emit('callEnd');
    });

    client.on('callAccept', function (toId){
        io.to(toId).emit('callAccept');
    });

    client.on('callReceive', function (toId){
        io.to(toId).emit('createOffer', {'fromId': client.id});
    });

    client.on('offer', function (details) {
        userId = details['fromId'];
        details['fromId'] = client.id;
        io.to(userId).emit('offer', details);
    });

    client.on('answer', function (details) {
        userId = details['fromId'];
        details['fromId'] = client.id;
        io.to(userId).emit('answer', details);
    });

    client.on('candidate', function (details) {
        userId = details['fromId'];
        details['fromId'] = client.id;
        io.to(userId).emit('candidate', details);
    });

    // when the client emits 'new message', this listens and executes

});
