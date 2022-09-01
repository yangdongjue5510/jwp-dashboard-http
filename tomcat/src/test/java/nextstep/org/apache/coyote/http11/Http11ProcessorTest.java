package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private StubSocket stubSocket;

    @AfterEach
    void afterEach() throws IOException {
        stubSocket.close();
    }

    @Test
    void process() {
        // given
        stubSocket = new StubSocket();
        final var processor = new Http11Processor(stubSocket);

        // when
        processor.process(stubSocket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(stubSocket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        stubSocket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(stubSocket);

        // when
        processor.process(stubSocket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assert resource != null;
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(stubSocket.output()).isEqualTo(expected);
    }
}
