/*
    Main objective of project: Created a mini REST API framework with Java Socket , which supports CRUD Ops
    Using only java and no Spring or any other framework involved
    How to run:
    Run Main class in IntelliJ
    1. to fetch all users - go to browser and type http://localhost:8080/users
    2. to fetch user by id- go to browser and type http://localhost:8080/users/id
    3. to add user
                    -> open browser and type  http://localhost:8080/form
                    -> enter user name and press add user
    4. to delete a user -
                        -> open postman
                        -> select DELETE and enter http://localhost:8080/users/id  ( id to be deleted)
                        -> Hit Send
     5. to see updated list again : go to browser and type http://localhost:8080/users
    This is the entirety of the project.

    *************************************************************************************************************
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static List<User> users=new ArrayList<>();// List of users
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

        Server sends response to browser with help of header.
        header contains HTTP response , content type , length and status code
     */

    //request here is blank , it works in POST method and not for GET as of now
    public static boolean handleRoute( Request request,Socket client, OutputStream  output) throws IOException { // introducing request obj
        System.out.println("REQUEST PATH = [" + request.path + "]");
        System.out.println(request.path.equals("/api/user"));
        if (request.path.equals("/hello")) {
            Response response = new Response(output);
            String name = request.queryParam.get("name");//using obj of Request class
            response.sendHtml("<h1>Hello \t" + name + "</h1>");
            return true;
        }
        if (request.path.equals("/api/user"))// only for one user Dhruva
        {
            Response response = new Response(output);
            response.sendJson("{\"name\":\"Dhruva\"}");// sending JSON data as request
            return true;
        }
//adding new user with id and name
        if (request.path.equals("/users") && request.method.equals("POST")) {
            Response response = new Response(output);
            String name = request.map.get("name");        //obtained from the form
    /*
        When we hit add user in form , POST request is sent and requestBody conatins name
        reuqest.map contains (name,Bob)
        Key:name , Value:Bob
    */
            int id = users.size() + 1;          //id of new user
            users.add(new User(id, name));       //new user added into the list of users

            response.sendJson(
                    "{\"message\":\"User added\"}"
            );      //response sent in JSON saying user was added

            return true;
        }


//deleting user by id
        if (request.path.startsWith("/users/")
                && request.method.equals("DELETE")) {
            Response response = new Response(output);

            String[] parts = request.path.split("/"); // ex:http://localhost:8080/users/3 -> 3 will be in parts[2]
            int userId = Integer.parseInt(parts[2]);

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id == userId) {   // if  id in route matches id in List
                    users.remove(i);            // we remove that user from the list

                    response.sendJson(
                            "{\"message\":\"User deleted\"}"    // message displayed after deletion
                    );

                    return true;
                }
            }

            response.sendJson(
                    "{\"error\":\"User not found\"}"    //if invalid user entered
            );

            return true;
        }


//fetching user by id
        if (request.path.startsWith("/users/")
                && request.method.equals("GET")) {
            Response response = new Response(output);

            String[] parts = request.path.split("/"); //example: http://localhost:8080/users/1
            int userId = Integer.parseInt(parts[2]); // userId=1

            System.out.println(userId);

            for (User user : users) {
                if (user.id == userId)                 //if id in request and user id is matching
                {                                   // JSON in form of {id: 1 , name:} is sent as response

                    String json =
                            "{\"id\":" + user.id +
                                    ",\"name\":\"" + user.name + "\"}";

                    response.sendJson(json);

                    return true;
                }
            }

            response.sendJson(
                    "{\"error\":\"User " + userId + " not found\"}"
            );

            return true;
        }


//Fetch each user from list
        if (request.path.equals("/users")
                && request.method.equals("GET")) {
            Response response = new Response(output);

            String json = "[";

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);

                json += "{\"id\":" + user.id +
                        ",\"name\":\"" + user.name + "\"}"; //send their JSON as ex: {id:1},{name:Dhruva}

                if (i < users.size() - 1) {
                    json += ",";
                }
            }

            json += "]";
            response.sendJson(json); // sending JSON as data for GET request
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        users.add(new  User(1,"Dhruva"));// user 1
        users.add(new  User(2,"Alice"));//user 2

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
                    if(firstLine == null)
                    {
                        client.close();
                        return;
                    }
                    String[] words = firstLine.split(" ");
                    String method = words[0];
                    String path = words[1];
                    Map<String ,String> queryParam=new HashMap<>();//query parameters
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
                        String[] parts=path.split("\\?",2);// ex:/hello?name=dhruva&city=bangalore
                        path=parts[0];//hello
                        query=parts[1];//name=dhruva&city=bangalore
                        System.out.println("Path:"+path);
                        System.out.println("Query:"+query);
                        String[] pairs=query.split("&",2); //name=dhruva and city=bangalore
                        for(String pair:pairs)
                        {
                            String[] queryParts=pair.split("=",2);//name and city , dhruva and banglore
                            String key=queryParts[0];//name and city
                            value=queryParts[1];//dhruva and banglore
                            queryParam.put(key,value);//map name to dhruva and city to banglore
                            System.out.println("Path:"+path);
                            System.out.println("Key:"+key);
                            System.out.println("Value:"+queryParam.get("name"));//dhruva
                            System.out.println("Value:"+queryParam.get("city"));//banglore
                        }
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
                                new Request(method, path,queryParam, mpp);//object of Request class

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
                    Request request =
                            new Request(method, path,queryParam, mpp);//object of Request class
                    if(handleRoute(request,client,output))// Replacing path by request object
                    {
                        return;
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