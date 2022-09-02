package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttP11StaticFile;
import org.apache.coyote.http11.Http11URLPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class HttP11StaticFileTest {

    private StubSocket stubSocket;

    @AfterEach
    void afterEach() throws IOException {
        stubSocket.close();
    }

    @Test
    void of() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(httpRequest);

        // when
        final Http11URLPath urlPath = Http11URLPath.of(stubSocket.getInputStream());
        final HttP11StaticFile staticFile = HttP11StaticFile.of(urlPath);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        final String content = new String(Files.readAllBytes(Path.of(resource.toURI())));
        assertThat(staticFile.getContent()).isEqualTo(content);
    }

    @Test
    void getContentType() throws IOException, URISyntaxException {
        // given
        final String httpCssRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        stubSocket = new StubSocket(httpCssRequest);
        final Http11URLPath urlPath = Http11URLPath.of(stubSocket.getInputStream());
        final HttP11StaticFile cssFile = HttP11StaticFile.of(urlPath);

        // when
        final String actual = cssFile.getContentType();

        // then
        assertThat(actual).isEqualTo("text/css");
    }
}
