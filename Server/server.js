const express = require('express');
const socket = require('socket.io');
const app = express();
var PORT = process.env.PORT || 3000;
const server = app.listen(PORT);

app.use(express.static('public'));
console.log('Server is running');
const io = socket(server);

io.on('connection', (socket) =>{
    console.log("someone connected");
});