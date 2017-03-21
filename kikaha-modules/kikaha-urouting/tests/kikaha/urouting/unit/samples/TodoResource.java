package kikaha.urouting.unit.samples;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.api.Consumes;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.POST;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Produces;
import kikaha.urouting.api.Response;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;

@Path("todos")
@Produces(Mimes.PLAIN_TEXT)
@Consumes(Mimes.PLAIN_TEXT)
@Singleton
public class TodoResource {

	final Map<Long, Todo> todos = new HashMap<>();

	@POST
	public Response persistTodo(Todo user) {
		todos.put(user.getId(), user);
		return DefaultResponse.created("todos/" + user.getId());
	}

	@GET
	@Path( "{id}" )
	public Todo getTodo( @PathParam("id") Long id ) {
		return todos.get(id);
	}

	@Getter
	@EqualsAndHashCode
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class Todo implements Serializable {

		static final long serialVersionUID = 5059268468661470631L;
		final Long id = System.currentTimeMillis();

		@NonNull
		String name;
		Date date;
	}

}