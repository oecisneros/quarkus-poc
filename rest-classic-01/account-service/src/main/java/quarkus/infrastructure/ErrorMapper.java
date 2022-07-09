package quarkus.infrastructure;

import java.util.Optional;

import javax.json.Json;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class ErrorMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        // var code = 500;
        // if (exception instanceof WebApplicationException wae) {
        // code = wae.getResponse().getStatus();
        // }

        var code = as(WebApplicationException.class, exception)
                .map(x -> x.getResponse().getStatus())
                .orElse(500);

        var entityBuilder = Json.createObjectBuilder()
                .add("exceptionType", exception.getClass().getName())
                .add("code", code)
                .add("error", Optional.ofNullable(exception.getMessage()).orElse("N/A"));

        return Response
                .status(code)
                .entity(entityBuilder.build())
                .build();
    }

    public static <T> Optional<T> as(Class<T> type, Object o) {
        if (type.isInstance(o)) {
            return Optional.of(type.cast(o));
        }
        return Optional.empty();
    }
}