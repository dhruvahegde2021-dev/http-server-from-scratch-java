 Java Socket REST API 

A mini REST API framework built from scratch using raw Java Sockets  — no Spring, no frameworks, no magic.
Just pure Java.

## What This Is

Most developers use Spring Boot and never think about what happens underneath.
This project peels back those layers and builds the whole thing manually:

- A TCP server that listens for browser connections
- An HTTP parser that reads raw request strings
- A routing system that maps paths to handlers
- A full CRUD API backed by an in-memory user store


## Features

- `GET /users` — fetch all users
- `GET /users/{id}` — fetch a user by ID
- `POST /users` — add a new user (via HTML form)
- `DELETE /users/{id}` — delete a user (via Postman)
- `GET /hello?name=...` — query parameter demo
- `GET /api/user` — JSON response demo
- Static file serving (HTML, CSS, JS, images)
- Multi-threaded — each connection runs on its own thread



## Project Structure

```
├── Main.java       # Server entry point, routing logic, HTTP parsing
├── Request.java    # Encapsulates HTTP method, path, query params, body
├── Response.java   # Helpers for sending HTML and JSON responses
├── User.java       # Simple User model (id + name)
└── public/         # Static files (HTML pages, CSS, etc.)
```



## How to Run

1. Open the project in **IntelliJ IDEA**
2. Run the `Main` class
3. Server starts on `http://localhost:8080`

### Try it out

| View all users | Browser → `http://localhost:8080/users` |
| View user by ID | Browser → `http://localhost:8080/users/1` |
| Add a user | Browser → `http://localhost:8080/form` → fill form |
| Delete a user | Postman → `DELETE http://localhost:8080/users/2` |



## How It Works

### 1. Socket Server
`ServerSocket` listens on port 8080. Each incoming connection is handed off to a new `Thread`, so multiple clients can connect simultaneously.

### 2. HTTP Parsing
The raw HTTP request looks like:
```
GET /users?name=Alice HTTP/1.1
Host: localhost:8080
...
```
The server reads this line-by-line, splits out the method, path, and query parameters manually.

### 3. Request & Response Objects
Instead of passing around raw strings and streams, the code wraps everything into clean objects:
- `Request` — holds method, path, query params, and POST body
- `Response` — exposes `sendHtml()` and `sendJson()` to write properly formatted HTTP responses

### 4. Routing
`handleRoute()` checks the request path and method and dispatches to the right handler — the same idea as `@GetMapping` in Spring, but written by hand.

### 5. Static Files
If no route matches, the server tries to serve a file from the `public/` folder, with proper `Content-Type` headers for HTML, CSS, JS, and images.



## What I Learnt

- How a browser actually talks to a server over TCP
- What an HTTP request looks like as raw text — and how to parse it
- Why frameworks like Spring exist (and what they're doing for you)
- How routing, query parameters, and request bodies work at the protocol level
- The difference between an HTML response and a JSON API response
- How to implement CRUD without a database



## Tech Stack

- **Java** (no external dependencies)
- **Java Sockets** (`ServerSocket`, `Socket`)
- **BufferedReader / OutputStream** for HTTP I/O
- Tested with **IntelliJ IDEA** and **Postman**