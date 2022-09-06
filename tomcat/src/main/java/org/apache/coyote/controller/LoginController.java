package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Cookie;
import org.apache.coyote.Session;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.exception.AccountNotFoundException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Override
    public boolean isRunnable(final Request request) {
        return request.hasPath("/login");
    }

    @Override
    public void run(final Request request, final Response response) throws IOException, URISyntaxException {
        if (request.getMethod().equals(HttpMethod.POST)) {
            runLogin(request, response);
            return;
        }
        final String jsessionid = request.findJsessionid();
        if (Session.find(jsessionid, "user").isPresent()) {
            response.addHeader("Location", "/index.html");
            response.write(HttpStatus.FOUND);
            return;
        }
        response.write(HttpStatus.OK, "/login.html");
    }

    private void runLogin(final Request request, final Response response)
            throws IOException, URISyntaxException {
        final RequestBody body = request.getBody();
        if (loginSuccess(body)) {
            final User loggedInUser = findUser(body);
            Session.add(request.findJsessionid(), "user", loggedInUser);
            log.info(loggedInUser.toString());
            response.addHeader("Location", "/index.html");
            Cookie.createJsessionidIfNotExists(request.getHeaders(), response.getHeaders());
            response.write(HttpStatus.FOUND);
        }
        response.addHeader("Location", "/401.html");
        response.write(HttpStatus.FOUND);
    }

    private boolean loginSuccess(final RequestBody body) {
        final User foundUser = findUser(body);
        final String password = body.get("password");
        return foundUser.checkPassword(password);

    }

    private User findUser(final RequestBody body) {
        String account = body.get("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(AccountNotFoundException::new);
    }
}
