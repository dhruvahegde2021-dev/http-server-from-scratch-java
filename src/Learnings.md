Building HTTP Server from scratch without any framework.

DAY-1:
Built a server
Learnt Difference b/w ServiceSocket and Socket
Clarity on InputStream ,InputStreamReader,BufferedReader and OutputStrean
Wrote a response to the server
Understood the workflow
Browser sends HTTP request to localhost:8080 and localhost:8080-> sends a text to the server
server -> receives in form of bytes
InputStreamReader & BufferedReader -> converts bytes to readable lines
Response stored as a string is converted to bytes and sent as a response to the browser in form of HTTP format
Browser recognises this as HTTP and prints text on screen

DAY-1 concluded here.



DAY-2:
Extracted path from the http using split()
If path was favicon.ico , we ignore it and move on
Introduced status and updated body of each path
Integrated HTML into the response rather than  fixed String
Created new HTML files for each path and connected the body to that file using Files.readAllBytes()
body=new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("URL")))
Paths.get() -> finds the file
Files.readAllBytes() -> reads entire file as bytes
new String() -> converts bytes to String
Now , body of each path of localhost:8080/(path) shows HTML content when we hit enter.

DAY-2 concluded here.

DAY-3
Replaced manual rooting by  dynamic file mapping
Routed "/" -> "/index.html"
Used substring() to extract only path 
Built dynamic file path -> "public/" + string
Used try catch block and handled response body and status
Final workflow:
Browser → sends request
Server → extracts path
Path → converted to filename
Filename → mapped to public/ folder
Server → reads file
Server → sends HTTP response
Browser → renders page

DAY-3 concluded here.


DAY-4
Converted the content type to accept HTML,CSS and JS files
Used .endsWith() to configure contentType as html, css or js or plain text
Created a css file having blue bg and font style and linked it with index.html
Run localhost:8080 -> gives blue screen of intro page

DAY-4 concluded here.