What I learnt through this project:
1. Socket Server and how it communicates - Browser -> Server -> InputStream/ OutputStream
2. HTTP Parsing - We manually read the HTTP request and extracted the method (GET,POST) and path from it
3. Request object - Replaced multiple parameters by creating Request class
4. Response object - Replaced writing .write() repeatedly by sendHtml() and sendJson()
5. Routing System - handleRoute() which is equivalent to @GetMapping in Spring
6. Query Parameters- query parameters to get name by id , equivalent to @RequestParam
7. POST Request-body goes from form to requestBody
8. JSON API- learnt difference btw HTML and JSON response
9. CRUD Operations- Implemented create,read , update and delete operations
