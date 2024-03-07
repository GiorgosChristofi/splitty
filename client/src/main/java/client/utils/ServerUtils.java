/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import commons.Event;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;


public class ServerUtils {

	@Inject
	@Named("connection.URL")
	private String serverURL;

	public void getQuotesTheHardWay() throws IOException, URISyntaxException {
		var url = new URI("http://localhost:8080/api/quotes").toURL();
		var is = url.openConnection().getInputStream();
		var br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	public List<Quote> getQuotes() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(serverURL).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
	}

	public Quote addQuote(Quote quote) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(serverURL).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
	}

	/**
	 * Gets the event from the server based on the invite code
	 * @param inviteCode the invite code of the event
	 * @return the event
	 */
	public Event getEvent(String inviteCode){
		String path = "api/events/" + inviteCode;
		return ClientBuilder.newClient(new ClientConfig())
				.target(serverURL).path(path) //invite code is the ID
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(Event.class);
	}

	/**
	 * Creates an event with the title given
	 * @param title the title of the event
	 * @return the event created
	 */
	public Event createEvent(String title){
		return ClientBuilder.newClient(new ClientConfig())
				.target(serverURL).path("api/events/")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(title, APPLICATION_JSON),Event.class);
	}

	/**
	 * Sends a PUT request to change the event with the given ID the given title.
	 * @param id Invitation code of the event
	 * @param title Title of the event
	 * @return Event with a new title
	 */
	public Event editTitle(String id, String title) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(serverURL).path("api/events/" + id) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.put(Entity.entity(title, APPLICATION_JSON), Event.class);
	}
}