import java.io.IOException;
import java.io.OutputStream;

public class Response {
    OutputStream output;
    public Response(OutputStream output)
    {
        this.output=output;
    }
    public void sendHtml(String html) throws IOException { //response to the GET request of html
        byte[] body = html.getBytes();

        String header =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        output.write(header.getBytes());
        output.write(body);
        output.flush();
    }
    /*
            sendJson is a response to the GET request of api/user.
            Implementing REST API support through JSON Acceptance
     */
    public void sendJson(String json) throws IOException {
        byte[] body = json.getBytes();

        String header =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        output.write(header.getBytes());
        output.write(body);
        output.flush();
    }
}


