
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Sever started on port 8080");
        while(true)
        {
            Socket client=server.accept();
            System.out.println("Client Connected");
            var input=client.getInputStream();
            var reader=new java.io.BufferedReader(new java.io.InputStreamReader(input));
            String firstLine=reader.readLine();
            String[] words=firstLine.split(" ");
            String method=words[0];
            String path=words[1];
            if(path.equals("/favicon.ico"))
            {
                client.close();
                continue;
            }
            System.out.println(method);
            System.out.println(path);



            String line;
            while((line= reader.readLine())!=null && !line.isEmpty()) {
                System.out.println(line);
            }
            var output=client.getOutputStream();
            String body;
            String status;
            if(path.equals("/")) {
                path="src/intro.html";
                status="200 OK";
                body=new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/intro.html")));
            }

            else if(path.equals("/hello")){
                path="src/index.html";
                status="200 OK";
                body= new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/index.html")));
            }

            else if(path.equals("/about")){
                path="src/about.html";
                status="200 OK";
                body=new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/about.html")));
            }

            else {
                path="src/error.html";
                status="404 Not Found";
                body=new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/error.html")));;
            }

            String response =
                    "HTTP/1.1 "+ status +"\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length:"+body.length()+"\r\n"+
                    "\r\n"+
                    body;

            output.write(response.getBytes());
            client.close();
        }
    }
}