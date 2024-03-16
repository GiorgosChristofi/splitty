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
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import commons.Event;
import commons.Participant;
import commons.Expense;
import jakarta.ws.rs.core.Response;
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

	/**
	 * Requests the server to add a participant.
	 * @param id ID of the event
	 * @param participantName Name of the participant to add
	 * @return The participant added by the server
	 */
	public Participant addParticipant(String id, String participantName) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(serverURL).path("api/events/" + id) //
				.request(APPLICATION_JSON) //
				.accept(APPLICATION_JSON) //
				.post(Entity.entity(participantName, APPLICATION_JSON), Participant.class);
	}

	/**
	 * Sends a POST request to add an expense to a specific event
	 * @param eventId the id of the specific event
	 * @param expense the expense to be added
	 */
	public void addExpense(String eventId, Expense expense) {
		ClientBuilder.newClient()
				.target(serverURL)
				.path("api/events/" + eventId + "/expenses")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
	}

	/**
	 * Sends a GET request to get a list of expenses for a specific event
	 * @param eventId the id of the event for which we want to get the expenses
	 * @return the list of expenses for the specific event
	 */
	public Set<Expense> getExpensesForEvent(String eventId) {
		return ClientBuilder.newClient()
				.target(serverURL).path("api/events/" + eventId + "/expenses")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<Set<Expense>>() {});
	}

	/**
	 * Sends a GET request to get the sum of all the expenses
	 * @param eventId the event for which we want to retrieve the
	 * total expenses
	 * @return a double representing the total expenses
	 */
	public double getTotalExpensesForEvent(String eventId) {
		return ClientBuilder.newClient()
				.target(serverURL).path("api/events/" + eventId + "/total-expenses")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(Double.class);
	}

	/**
	 * Sends a DELETE request to delete the specific expense
	 * @param eventId the id of the event to delete the Expense from
	 * @param expenseId the id of the expense we want to delete
	 */
	public void deleteExpenseForEvent(String eventId, String expenseId) {
		Response response = ClientBuilder.newClient()
				.target(serverURL)
				.path("api/events/" + eventId + "/expenses/" + expenseId)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.delete();
		if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
			System.out.println("Expense deleted successfully.");
		} else {
			System.out.println("Failed to delete expense. Status code: " + response.getStatus());
		}
	}

	/**
	 * Sends a PUT request to modify an existing expense
	 * @param eventId the id of the Event tied to the expense
	 * @param expense the expense we want the current expense to be updated to
	 * @return the new expense
	 */
	public Expense editExpense(String eventId, Expense expense) {
		return ClientBuilder.newClient()
				.target(serverURL)
				.path("api/events/" + eventId + "/expenses/" + expense.getId())
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.put(Entity.entity(expense, APPLICATION_JSON), Expense.class);
	}

	//TODO Test weather or not the methods actually work in cae of problems like(expense doesn't exist)
	/**
	 * checks if a password matches with the one randomly generated
	 * @param inputPassword the password the user inputs to log in to the management overview
	 * @return a boolean, true or false whether the password matches or not
	 */
	public Boolean checkPassword(String inputPassword){
		return ClientBuilder.newClient(new ClientConfig())
				.target(serverURL).path("api/password/" + inputPassword)
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(Boolean.class);
	}

	/**
	 * retrieves all events from the server
	 * @return all the events from the server
	 */
	public List<Event> retrieveAllEvents(){
		List<Event> events = ClientBuilder.newClient(new ClientConfig())
				.target(serverURL).path("api/events/all")
				.request(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.get(new GenericType<List<Event>>(){});
		return events;
	}
}
