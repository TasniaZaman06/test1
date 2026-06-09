class Car {
    constructor(make, model, year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }
    getDetails() {
        return `This is a ${this.year} ${this.make} ${this.model}`;
    }
    showUserVulnerable(userData) {
        document.getElementById('welcome-message').innerHTML = `Welcome, ${userData}!`;
    }
    showUserSecure(userData) {
        document.getElementById('welcome-message').textContent = `Welcome, ${userData}!`;
    }

    getPropertyVulnerable(propertyName) {
        const currentUser = { name: "Alice", role: "guest" };
        return eval("currentUser." + propertyName);
    }
    getPropertySecure(propertyName) {
        const currentUser = { name: "Alice", role: "guest" };
        return currentUser[propertyName];
    }

    storeTokenVulnerable(jwtToken) {
        localStorage.setItem('session_token', jwtToken);
    }
    async loginUserSecure(credentials) {
       
        await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
    }
}

const myCar = new Car("Toyota", "Corolla", 2021);
console.log(myCar.getDetails()); 