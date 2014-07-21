package io.skullabs.undertow.urouting.samples;

import io.skullabs.undertow.urouting.api.Consumes;
import io.skullabs.undertow.urouting.api.DefaultResponse;
import io.skullabs.undertow.urouting.api.Mimes;
import io.skullabs.undertow.urouting.api.POST;
import io.skullabs.undertow.urouting.api.Path;
import io.skullabs.undertow.urouting.api.Produces;
import io.skullabs.undertow.urouting.api.Response;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import trip.spi.Singleton;

@Path("todos")
@Produces(Mimes.JSON)
@Consumes(Mimes.JSON)
@Singleton
public class TodoResource {

	final Map<Long, Todo> todos = new HashMap<>();

	@POST
	public Response persistTodo(Todo user) {
		todos.put(user.getId(), user);
		return DefaultResponse.created("todos/" + user.getId());
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