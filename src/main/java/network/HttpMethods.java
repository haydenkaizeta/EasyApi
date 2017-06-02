package network;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Invocation.Builder;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HttpMethods {

    private static Logger log = Logger.getLogger(HttpMethods.class.getName());

    private static HttpMethods httpMethods;

    private Client client;

    public static HttpMethods getInstance() {
        if (httpMethods == null) {
            httpMethods = new HttpMethods();
        }
        return httpMethods;
    }

    protected HttpMethods() {
        ClientConfig cfg = new ClientConfig(JacksonJsonProvider.class);
        client = JerseyClientBuilder.createClient(cfg);
    }

    public Client getClient() {
        return client;
    }

    /*
	 * simplified HTTP methods
	 */

    protected static void verifyRequestResult(int currentResponseCode,
                                              int[] acceptableResponseCodes) {
        if (acceptableResponseCodes.length > 0) {
            boolean isResponseCodeAcceptable = false;
            for (int code : acceptableResponseCodes) {
                if (code == currentResponseCode) {
                    isResponseCodeAcceptable = true;
                    break;
                }
            }
            if (!isResponseCodeAcceptable) {
                String.format(
                        "Backend request failed. Request return code is: %d. Expected codes are: %s",
                        currentResponseCode,
                        Arrays.toString(acceptableResponseCodes));
            }
        }
    }

    public String post(Builder postTarget, Object entity, MediaType mediaType) {
        Response response = postTarget.post(Entity.entity(entity, mediaType));

        int statusCode = response.getStatus();

        return response.readEntity(String.class);
    }

    public String put(Builder putTarget, Object entity, MediaType mediaType) {
        Response response = putTarget.put(Entity.entity(entity, mediaType));

        int statusCode = response.getStatus();

        return response.readEntity(String.class);
    }

    public String get(Builder postTarget, Object entity, MediaType mediaType) {
        Response response = postTarget.put(Entity.entity(entity, mediaType));

        int statusCode = response.getStatus();

        return response.readEntity(String.class);
    }

}
