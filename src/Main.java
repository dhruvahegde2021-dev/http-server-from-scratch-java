
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static String getFilePath(String path)
    {
        if(path.equals("/"))
        {
            path="/index.html";
        }
        String filename = path.substring(1);
        if(!filename.contains("."))
        {
            filename = filename + ".html";//dynamic routing i.e http // about -> about.html
        }
        return "public/"+filename;
    }

    public static String getContentType(String filename)
    {
        if(filename.endsWith(".html"))
        {
            return "text/html";
        }
        else if(filename.endsWith(".css"))
        {
            return "text/css";
        } else if (filename.endsWith(".js"))
        {
            return "application/javascript";
        } else if (filename.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else{
            return "text/plain";
        }
    }

    /*
        Browser sends request to server in form of GET  , POST
        GET displays available data
        POST fetches data from requestBody

        Server sends response to browser with help of header
        header contains HTTP response , content type , length and status code
     */

    //request here is blank , it works in POST method and not for GET as of now
    public static boolean handleRoute( Request request,Socket client, OutputStream  output) throws IOException { // introducing request obj
        if(request.path.equals("/hello"))
        {
                Response response=new Response(output);//using obj of Response class
                response.sendHtml("<h1>Hello \t"+request.map.get("username")+"</h1>");//request.map.get is blank here bcz map is present in POST block
                return true;
        }
        return false;

    }
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Sever started on port 8080");
        while (true) {
            Socket client = server.accept();

            Thread thread = new Thread(() ->
            {
                try {
                    System.out.println(Thread.currentThread().getName());//multithreading
                    System.out.println("Client Connected");
                    var input = client.getInputStream();
                    var reader = new BufferedReader(
                            new InputStreamReader(input)
                    );
                    String firstLine = reader.readLine();
                    String[] words = firstLine.split(" ");
                    String method = words[0];
                    String path = words[1];
                    System.out.println("Method:"+method);
                    String query="";
                    String value="";
                    /*
                        for GET:
                        Browser -> GET -> server -> Response -> displays available data
                        ex:hello?name=dhruva displays hello dhruva
                     */
                    if(path.contains("?")) //GET request from browser
                    {
                        String[] parts=path.split("\\?");// ex:hello?name=dhruva
                        path=parts[0];//hello
                        query=parts[1];//name=dhruva
                        System.out.println("Path:"+path);
                        System.out.println("Query:"+query);
                        String[] queryParts=query.split("=");//name=dhruva
                        String key=queryParts[0];//name
                        value=queryParts[1];//dhruva
                        System.out.println("Key:"+key);
                        System.out.println("Value:"+value);
                    }

                    if (path.equals("/favicon.ico")) {
                        client.close();
                        return;
                    }

                    System.out.println(method);
                    System.out.println(path);

                    String line;

                    while ((line = reader.readLine()) != null
                            && !line.isEmpty()) {
                        System.out.println(line);
                    }
                    var output = client.getOutputStream();
                    /*for POST:
                    Browser->login and password -> POST -> server -> requestBody -> response -> fetches data from requestBody
                    and displays
                    */
                    Map<String, String> mpp = new HashMap<>();

                    if(method.equals("POST"))
                    {
                        char[] bodyChar = new char[1000];
                        int length = reader.read(bodyChar);

                        String requestBody =
                                new String(bodyChar, 0, length);

                        System.out.println(requestBody);

                        String[] pairs = requestBody.split("&");// ex:username=dhruva&password=abc

                        for(String pair : pairs)
                        {
                            String[] parts = pair.split("=");

                            String key = parts[0];// username and password
                            value = parts[1];//dhruva and abc

                            mpp.put(key, value);//map the key and value
                        }

                        Request request =
                                new Request(method, path, mpp);//object of Request class

                        if(handleRoute(request,client,output))// Replacing path by request object
                        {
                            return;
                        }

                        System.out.println("Method: " + request.method);
                        System.out.println("Path: " + request.path);

                        System.out.println(
                                request.map.get("username")
                        );

                        System.out.println(
                                request.map.get("password")
                        );

                        if(request.path.equals("/login"))
                        {
                            String username =
                                    request.map.get("username");
                        /*
                            Response class contains entire code of response we are sending to HTTP
                            So we repalce the entire header , output stream , byte array and input string
                             and call function sendHtml()
                         */
                            Response response=new Response(output);
                            response.sendHtml( "<h1>Response Class Works!</h1>");

                            client.close();
                            return;
                        }
                    }

                    byte[] body;
                    String status;
                    String contentType = "";
                    String fullPath = getFilePath(path); // in built method to get path
                    String filename =
                            Paths.get(fullPath)
                                    .getFileName()
                                    .toString();

                    System.out.println(fullPath);

                    try {
                        contentType = getContentType(filename);//content type -html , css or js
                        body = Files.readAllBytes(
                                Paths.get(fullPath) // converting path to bytes for HTTP
                        );

                        status = "200 OK";
                    } catch (Exception e) {
                        body = Files.readAllBytes(
                                Paths.get("public/error.html") // for invalid , laod error.htnl
                        );

                        status = "404 Not Found";

                        contentType = "text/html";
                    }

                    String header =
                            "HTTP/1.1 " + status + "\r\n" +
                                    "Content-Type: " + contentType + "\r\n" +
                                    "Content-Length: " + body.length + "\r\n" +
                                    "\r\n";

                    output.write(header.getBytes());
                    output.write(body);
                    output.flush();
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}