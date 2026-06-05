import java.io.IOException;
import java.io.OutputStream;

public class Response {
    OutputStream output;
    public Response(OutputStream output)
    {
        this.output=output;
    }
    public void sendHtml(String html) throws IOException {
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
}
