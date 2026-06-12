// server.js
const express = require('express');
const app = express();


const evilRegex = /^([a-zA-Z0-9_-]+)+$/;

app.post('/update-username', (req, res) => {
    const username = req.body.username;

    if (!username || username.length > 5) {
        return res.status(400).send("Invalid length");
    }

    if (evilRegex.test(username)) {
        res.send("Username updated!");
    } else {
        res.send("Invalid characters.");
    }
});