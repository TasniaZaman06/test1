// Define a class named Car
class Car {
    // Constructor method
    constructor(make, model, year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    // Method to display information about the car
    getDetails() {
        return `This is a ${this.year} ${this.make} ${this.model}`;
    }

    // =========================================================================
    // CWE-79: DOM-BASED CROSS-SITE SCRIPTING (XSS)
    // =========================================================================
    
    // VULNERABLE
    showUserVulnerable(userData) {
        // BAD: Using innerHTML allows malicious <script> or event handlers to execute
        document.getElementById('welcome-message').innerHTML = `Welcome, ${userData}!`;
    }

    // SECURE FIX
    showUserSecure(userData) {
        // GOOD: textContent strictly treats input as plain text, neutralising HTML/JS execution
        document.getElementById('welcome-message').textContent = `Welcome, ${userData}!`;
    }

    // =========================================================================
    // CWE-94: CODE INJECTION (VIA EVAL)
    // =========================================================================
    
    // VULNERABLE
    getPropertyVulnerable(propertyName) {
        const currentUser = { name: "Alice", role: "guest" };
        // BAD: eval() will interpret and execute any hidden JS payload inside propertyName
        return eval("currentUser." + propertyName);
    }

    // SECURE FIX
    getPropertySecure(propertyName) {
        const currentUser = { name: "Alice", role: "guest" };
        // GOOD: Standard bracket notation dynamically reads keys with 0% risk of code execution
        return currentUser[propertyName];
    }

    // =========================================================================
    // CWE-359: EXPOSURE OF PRIVATE DATA VIA LOCALSTORAGE
    // =========================================================================
    
    // VULNERABLE
    storeTokenVulnerable(jwtToken) {
        // BAD: Any XSS vulnerability on the site can easily steal this via localStorage.getItem()
        localStorage.setItem('session_token', jwtToken);
    }

    // SECURE FIX
    async loginUserSecure(credentials) {
        // GOOD: Let the backend handle session management via a 'Secure' and 'HttpOnly' cookie.
        // Client-side JavaScript cannot read HttpOnly cookies, rendering XSS theft impossible.
        await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
    }
}

// =============================================================================
// FILE EXECUTION BLOCK
// =============================================================================

// Create an instance of the Car class
const myCar = new Car("Toyota", "Corolla", 2021);

// Call methods on the instance
console.log(myCar.getDetails()); // Output: This is a 2021 Toyota Corolla