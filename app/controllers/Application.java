package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result chat()
    {
        ObjectNode result = Json.newObject();
        result.put("messageId", 1);
        result.put("userName", "Mihai");
        result.put("time", new Date().toString());


        List<ObjectNode> results = Arrays.asList(result, result, result);

        return ok(chat.render("Hello world", results));
    }

    public Result api()
    {
        ObjectNode result = Json.newObject();
        result.put("messageId", 1);
        result.put("userName", "Mihai");
        result.put("time", new Date().toString());
        return ok(result);
    }
}


