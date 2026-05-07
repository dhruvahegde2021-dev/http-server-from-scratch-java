
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Sever started on port 8080");
        while(true)
        {
            Socket client=server.accept();
            System.out.println("Client Connected");
            var input=client.getInputStream();
            var reader=new BufferedReader(new InputStreamReader(input));
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
            String contentType = "";
            if(path.equals("/"))
            {
                path="/index.html";
            }
            String filename=path.substring(1);
            String fullPath="public/"+filename;
            System.out.println(fullPath);
            try{
                if(filename.endsWith(".html"))
                {
                    contentType="text/html";
                }
                else if(filename.endsWith(".css"))
                {
                    contentType="text/css";
                } else if (filename.endsWith(".js"))
                {
                    contentType="application/javascript";
                }
                else {
                    contentType="text/plain";
                }
                body=new String(Files.readAllBytes(Paths.get(fullPath)));
                status="200 OK";
            }
            catch (Exception e) {
                body=new String(Files.readAllBytes(Paths.get("public/error.html")));
                status="404 Not Found";
            }
            String response =
                    "HTTP/1.1 "+ status +"\r\n" +
                    "Content-Type:"+contentType+"\r\n" +
                    "Content-Length:"+body.length()+"\r\n"+
                    "\r\n"+
                    body;

            output.write(response.getBytes());
            client.close();
        }
    }
}