package org.encryptor.simple;

import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;

/**
 * @author Ilya Tkachuk
 */
public class Dispatcher {

    private static final String ENC_ALGORITHM = "AES";
    public static final String MESSAGE_PARAM = "message";
    public static final String APPLICATION_JSON = "application/json";

    private final SimpleMessageEncryptor aesEncryptor;
    private final SimpleMessageEncryptor aesWithKeyEncryptor;
    private List<Route> routesGet = emptyList();
    private List<Route> routesPost = emptyList();

    public Dispatcher(Config config) {
        aesEncryptor = new SimpleMessageEncryptor(ENC_ALGORITHM, null);
        aesWithKeyEncryptor = config.getEncKey() != null ?
                new SimpleMessageEncryptor(ENC_ALGORITHM, config.getEncKey()) :
                null;

        if (config.getPort() != null) {
            setPort(config.getPort());
        }

        configure();
    }

    private void configure() {
        routesGet = asList(
                new Route("/encrypt/byKey/:key") {
                    @Override
                    public Object handle(final Request request, final Response response) {
                        try {
                            String message = request.queryParams(MESSAGE_PARAM);
                            String key = request.params(":key");
                            response.type(APPLICATION_JSON);
                            return aesEncryptor.encryptMessageWithKey(message, key);
                        } catch (Exception e) {
                            halt(500, e.getMessage());
                            return null;
                        }
                    }
                },
                new Route("/decrypt/byKey/:key") {
                    @Override
                    public Object handle(final Request request, final Response response) {
                        try {
                            String message = request.queryParams(MESSAGE_PARAM);
                            String key = request.params(":key");
                            response.type(APPLICATION_JSON);
                            return aesEncryptor.decryptMessageWithKey(message, key);
                        } catch (Exception e) {
                            halt(500, e.getMessage());
                            return null;
                        }
                    }
                },
                new Route("/encrypt") {
                    @Override
                    public Object handle(final Request request, final Response response) {
                        try {
                            String message = request.queryParams(MESSAGE_PARAM);
                            response.type(APPLICATION_JSON);
                            ensureDefaultKeyProvided();
                            return aesWithKeyEncryptor.encryptMessage(message);
                        } catch (Exception e) {
                            halt(500, e.getMessage());
                            return null;
                        }
                    }
                },
                new Route("/decrypt") {
                    @Override
                    public Object handle(final Request request, final Response response) {
                        try {
                            String message = request.queryParams(MESSAGE_PARAM);
                            response.type(APPLICATION_JSON);
                            return aesWithKeyEncryptor.decryptMessage(message);
                        } catch (Exception e) {
                            halt(500, e.getMessage());
                            return null;
                        }
                    }
                }
        );
    }

    private void ensureDefaultKeyProvided() {
        if (aesWithKeyEncryptor == null) {
            throw new NullPointerException("Default key not provided");
        }
    }

    public void start() {
        for (Route route : routesGet) {
            get(route);
        }
        for (Route route : routesPost) {
            post(route);
        }
    }
}
