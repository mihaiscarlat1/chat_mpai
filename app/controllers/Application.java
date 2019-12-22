package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.libs.Json;
import play.mvc.*;

import views.html.*;

import java.util.Date;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result chat()
    {
        return ok(hello.render("Hello world"));
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
