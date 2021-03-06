package me.escoffier.demo;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import rx.Single;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Shopping {

    public static void writeProductLine(HttpServerResponse response, JsonObject product) {
        // All entries are string
        response.write(" * " + product.getString("name")
            + " x "  + product.getString("quantity")
            + " = " + product.getString("total") + "\n");
    }

    public static JsonObject getFallbackPrice(Map.Entry<String, Object> entry) {
        return new JsonObject().put("name", entry.getKey())
            .put("quantity", entry.getValue()) // String
            .put("total", "NaN"); // String
    }

    public static void retrievePrice(WebClient pricer, Map.Entry<String, Object> entry, Future<JsonObject> future) {
        pricer.post("/prices")
            .rxSendJson(new JsonObject()
                .put("name", entry.getKey())
                .put("quantity", entry.getValue())
            ).subscribe(
            resp -> future.complete(resp.bodyAsJsonObject()),
            future::fail
        );
    }

    public static Single<JsonObject> retrievePrice(
        WebClient pricer, Map.Entry<String, Object> entry) {
        return pricer.post("/prices")
            .rxSendJson(new JsonObject()
                .put("name", entry.getKey())
                .put("quantity", entry.getValue())
            )
            .map(HttpResponse::bodyAsJsonObject);
    }
}
