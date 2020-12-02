package com.binu.microprofile.bookstore.rest;

import com.binu.microprofile.bookstore.entity.Book;
import com.binu.microprofile.bookstore.service.BookService;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.persistence.annotations.Properties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Path("books")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class BookStoreEndpoint {
    @Inject
    BookService bookService;

    //from microprofile-config.properties(fixed filename)
    @Inject
    @ConfigProperty(name="username", defaultValue="admin")
    private String username;

    @Inject
    Config config;

    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "404",
                            description = "We could not find anything",
                            content = @Content(mediaType = "text/plain"))
                    ,
                    @APIResponse(
                            responseCode = "200",
                            description = "We have a list of books",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Properties.class)))})
    @Operation(summary = "Outputs a list of books",
            description = "This method outputs a list of books")
    @Timed(name = "getAllBooks",
            description = "Monitor the time getAll Method takes",
            unit = MetricUnits.MILLISECONDS,
            absolute = true)
    @GET
    public Response getAll() {
        return Response.ok(bookService.getAll()).build();
    }

    @Counted(unit = MetricUnits.NONE,
            name = "getBook",
            absolute = true,
            monotonic = true,
            displayName = "get single book",
            description = "Monitor how many times getBook method was called")
    @GET
    @Path("{id}")
    @APIResponse(
            responseCode = "200",
            description = "Book retrieved for given id",
            content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = Book.class, ref = "Book")))
    public Response getBook( @Parameter(name = "id", description = "Id of the book, which is numeric", required = true, allowEmptyValue = false, example = "1")
                                 @PathParam("id") Long id) {
        Book book = bookService.findById(id);
        return Response.ok(book).build();
    }

    @Metered(name = "create-books",
            unit = MetricUnits.MILLISECONDS,
            description = "Monitor the rate events occured",
            absolute = true)
    @POST
    @Operation(
            operationId = "create",
            summary = "Add a Book"
    )
    @RequestBody(
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = Book.class, ref = "Book")),
            description = "The new Book object to create",
            required = true
    )
    public Response create(Book book) {
        bookService.create(book);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Book book) {
        Book updateBook = bookService.findById(id);
        updateBook.setIsbn(book.getIsbn());
        updateBook.setDescription(book.getDescription());
        updateBook.setLanguage(book.getLanguage());
        updateBook.setPages(book.getPages());
        updateBook.setPrice(book.getPrice());
        updateBook.setPublisher(book.getPublisher());
        updateBook.setTitle(book.getTitle());
        bookService.update(updateBook);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Book getBook = bookService.findById(id);
        bookService.delete(getBook);
        return Response.ok().build();
    }

    @GET
    @Path("/get-int-value")
    @Gauge(unit = MetricUnits.NONE, name = "intValue", absolute = true)
    public int getIntValue() {
        return 3;
    }

    @GET
    @Path("mp-config")
    @Produces(APPLICATION_JSON)
    public Response mpConfig() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put("username", username);
        configProperties.put("password", config.getValue("password", String.class));
        configProperties.put("microprofile-apis", config.getValue("microprofile.apis",
                String[].class));
        return Response.ok(configProperties).build();
    }
}
