# ServEase - Local Service Booking System

This is the Vue starter project for ServEase. This document walks you through setting up and running the project, including Vue Router, Vuex, and authentication.

## 🚀 Project Setup

First, install dependencies:
```sh
npm install
```

Next, review the `.env` file located in the project root. Update the API URL if needed:
```sh
VITE_REMOTE_API=http://localhost:9000
```

Start the Vue application:
```sh
npm run dev
```

## 🔐 Authentication

When you first visit the application, you'll be redirected to the login page since the home route (`/`) requires authentication.

### 🔄 Navigation Guards

Vue Router prevents unauthorized access with navigation guards:
```js
router.beforeEach((to) => {
  const requiresAuth = to.matched.some(x => x.meta.requiresAuth);
  if (requiresAuth && store.state.token === '') {
    return {name: "login"};
  }
});
```

### 📌 Vuex State Management
ServEase stores authentication state in Vuex.
```js
const currentToken = localStorage.getItem('token');
if (currentToken) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${currentToken}`;
}
```

## 🔑 Login & Registration

Authentication is handled through `AuthService.js`:
```js
import axios from 'axios';

const http = axios.create({
  baseURL: import.meta.env.VITE_REMOTE_API
});

export default {
  login(user) {
    return http.post('/login', user);
  },
  register(user) {
    return http.post('/register', user);
  }
};
```

Upon login, Vuex updates state:
```js
this.$store.commit("SET_AUTH_TOKEN", response.data.token);
this.$store.commit("SET_USER", response.data.user);
this.$router.push("/");
```

## 🔓 Logout

Logout clears the stored token and redirects to login:
```js
this.$store.commit("LOGOUT");
this.$router.push("/login");
```

## 🏗 Next Steps
- ✅ Implement user roles (Admin, Customer, Service Provider)
- ✅ Design the Home and Service pages
- ✅ Add appointment booking functionality

💡 Stay tuned for updates as ServEase evolves! 🚀

