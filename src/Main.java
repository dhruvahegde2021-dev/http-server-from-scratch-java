
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        POST fetches data frim requestBody

        Server sends response to browser with help of header
        header contains HTTP response , content type , length and status code
     */
    public static boolean handleRoute(String path, Socket client, OutputStream  output, String value) throws IOException {
        if(path.equals("/hello"))
        {
                String message="<h1>Hello \t"+value+"</h1>";
                byte[] body=message.getBytes();
                String header =
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: " + body.length + "\r\n" +
                                "\r\n";
                output.write(header.getBytes());
                output.write(body);
                output.flush();
                client.close();
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
                    if(handleRoute(path,client,output,value))
                    {
                        return;
                    }
                    /*for POST:
                    Browser->login and password -> POST -> server -> requestBody -> response -> fetches data from requestBody
                    and displays
                    */
                    if(method.equals("POST"))
                    {
                        char[] bodyChar=new char[1000];
                        int length=reader.read(bodyChar);
                        String requestBody=new String(bodyChar,0,length);
                        System.out.println(requestBody);
                        String[] pairs=requestBody.split("&");// username=dhruva&password=abc
                        String pair1=pairs[0];//username=dhruva
                        String pair2=pairs[1];//password=abc
                        String pvalue = "";
                        for(String pair:pairs) {
                            String[] parts = pair.split("=");
                            String key = parts[0];//username and password
                            pvalue = parts[1];// dhruva and abc
                            System.out.println("Key:" + key);
                            System.out.println("Value:" + pvalue);
                        }
                        if(path.equals("/login"))
                        {
                            String message="<h1> HELLO \t"+pvalue+"</h1>";//formatting message on screen
                            byte[] body=message.getBytes();
                            String header= "HTTP/1.1 200 OK\r\n" +// header is the response to browser request
                                    "Content-Type: text/html\r\n" +
                                    "Content-Length: " + body.length + "\r\n" +
                                    "\r\n";
                            output.write(header.getBytes()); // HTTP reads only bytes
                            output.write(body);
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