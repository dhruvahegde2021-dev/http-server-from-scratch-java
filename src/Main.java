
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
            filename = filename + ".html";
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

    public static boolean handleRoute(String path, Socket client, OutputStream  output, String value) throws IOException {
        if(path.equals("/hello"))
        {
                String message="<h1>Hello"+value+"</h1>";
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
                    System.out.println(Thread.currentThread().getName());
                    System.out.println("Client Connected");
                    var input = client.getInputStream();
                    var reader = new BufferedReader(
                            new InputStreamReader(input)
                    );
                    String firstLine = reader.readLine();
                    String[] words = firstLine.split(" ");
                    String method = words[0];
                    String path = words[1];
                    String query="";
                    String value="";
                    if(path.contains("?"))
                    {
                        String[] parts=path.split("\\?");
                        path=parts[0];
                        query=parts[1];
                        System.out.println("Path:"+path);
                        System.out.println("Query:"+query);
                        String[] queryParts=query.split("=");
                        String key=queryParts[0];
                        value=queryParts[1];
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
                    byte[] body;
                    String status;
                    String contentType = "";
                    String fullPath = getFilePath(path);
                    String filename =
                            Paths.get(fullPath)
                                    .getFileName()
                                    .toString();

                    System.out.println(fullPath);

                    try {
                        contentType = getContentType(filename);
                        body = Files.readAllBytes(
                                Paths.get(fullPath)
                        );

                        status = "200 OK";
                    } catch (Exception e) {
                        body = Files.readAllBytes(
                                Paths.get("public/error.html")
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