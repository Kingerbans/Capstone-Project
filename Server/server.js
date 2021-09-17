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

var sessions = [];

io.on('connection', function (client) {

    console.log('one user connected : ' + client.id);

    client.on('call', function (fullname) {
        client.broadcast.emit('call', {fullname: fullname});
    });

    client.on('call-accept', function (){
        client.broadcast.emit('call-accept');
    });

    client.on('call-receive', function (){
        client.broadcast.emit('createoffer', {});
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
