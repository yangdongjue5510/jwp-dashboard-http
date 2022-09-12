package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.ControllerContainer;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class ControllerContainerTest {

    @Test
    void findController() throws IOException {
        // given
        final String requestLine = RequestFixture.create(HttpMethod.GET, "/login.html", "");
        final Request request = Request.of(new ByteArrayInputStream(requestLine.getBytes()));

        // when
        final Controller actual =
                new ControllerContainer(new RequestMapping(), List.of())
                        .findController(request);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }
}
