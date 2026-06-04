import java.util.HashMap;
import java.util.Map;

 class Request {
    String method;
    String path;
    Map<String,String> map=new HashMap<>();

    public Request(String method, String path, Map<String, String> map) {
        this.method = method;
        this.path = path;
        this.map = map;
    }
}


