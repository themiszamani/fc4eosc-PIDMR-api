package org.grnet.pidmr.util;

import io.quarkus.oidc.TokenIntrospection;
import lombok.Getter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonString;
import javax.ws.rs.BadRequestException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestScoped
public class RequestUserContext {

    @Getter
    private final String vopersonID;

    @Inject
    private final TokenIntrospection tokenIntrospection;

    public RequestUserContext(TokenIntrospection tokenIntrospection) {

        try {

            this.vopersonID = tokenIntrospection.getJsonObject().getString("voperson_id");
            this.tokenIntrospection = tokenIntrospection;
        } catch (Exception e) {

            String message = "The User's unique identifier voperson_id is missing from the access token.";
            throw new BadRequestException(message);
        }
    }

    public Set<String> getRoles(String clientID){

        var jsonArray =  tokenIntrospection
                .getObject("resource_access")
                .getJsonObject(clientID)
                .getJsonArray("roles");

        return IntStream
                .range(0,jsonArray.size())
                .mapToObj(jsonArray::getJsonString)
                .map(JsonString::getString)
                .collect(Collectors.toSet());
    }
}
