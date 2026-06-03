// BAD: Reading directly from the URL query string and dumping it into the HTML
const urlParams = new URLSearchParams(window.location.search);
const username = urlParams.get('name'); // If input is: <img src=x onerror=alert(1)>

// This executes the malicious script immediately

document.getElementById('welcome-message').innerHTML = `Welcome, ${username}!`;