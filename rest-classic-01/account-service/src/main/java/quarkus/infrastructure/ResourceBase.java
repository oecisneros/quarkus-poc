package quarkus.infrastructure;

import java.text.MessageFormat;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public abstract class ResourceBase {
    protected final static WebApplicationException notFound(String message, Object... args) {
        return new WebApplicationException(MessageFormat.format(message, args), 404);
    }

    protected final static WebApplicationException badRequest(String message, Object... args) {
        return new WebApplicationException(MessageFormat.format(message, args), 400);
    }

    protected final static <T> Response created(T entity) {
        return Response.status(201).entity(entity).build();
    }

    protected final static Response noContent() {
        return Response.noContent().build();
    }
}
