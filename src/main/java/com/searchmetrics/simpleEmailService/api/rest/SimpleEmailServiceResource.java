package com.searchmetrics.simpleEmailService.api.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 *
 */
@Path("/")
public class SimpleEmailServiceResource {

    @GET
    @Path("helloWorld")
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld(@QueryParam("userName") final Optional<String> potentialUserName) {
            return Response.ok().entity(new HelloWorldResponse(potentialUserName, "Hello World, %s")).build();
    }

    static class HelloWorldResponse {
        private final String user;
        private final String msg;

        public HelloWorldResponse(Optional<String> user, String msg) {
            this.user = user.orElseGet(() -> "F2U");
            this.msg = String.format(msg, this.user);
        }

        @JsonProperty
        public String getUser() {
            return user;
        }

        @JsonProperty
        public String getMsg() {
            return msg;
        }
    }
}
