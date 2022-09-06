package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.AccountNotFoundException;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.RequestBody;
import org.apache.coyote.http11.Response;
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
        response.write(HttpStatus.OK, "/login.html");
    }

    private void runLogin(final Request request, final Response response)
            throws IOException, URISyntaxException {
        final RequestBody body = request.getBody();
        if (loginSuccess(body)) {
            final User loggedInUser = findUser(body);
            log.info(loggedInUser.toString());
            response.write(HttpStatus.FOUND, "/index.html");
        }
        response.write(HttpStatus.UNAUTHORIZED, "/401.html");
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
