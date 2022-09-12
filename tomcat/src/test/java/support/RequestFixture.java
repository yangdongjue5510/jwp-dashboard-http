package support;

import static org.apache.coyote.http11.request.Request.SPACE_DELIMITER;
import static org.apache.coyote.http11.request.RequestHeaders.HEADER_KEY_VALUE_DELIMITER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;

public class RequestFixture {

    private RequestFixture() {
    }

    public static String createLine(final HttpMethod method, final String url, final String body) {
        return createLine(method, url, Map.of("Host", "localhost:8080", "Connection", "keep-alive", "Content-Length", String.valueOf(body.getBytes().length)),body);
    }

    public static String createLine(final HttpMethod method, final String url, final Map<String, String> headers, final String body) {
        return String.format("%s %s HTTP/1.1 \r\n%s\r\n\r\n%s", method.getMethodName(), url, headerToString(headers), body);
    }

    private static String headerToString(final Map<String, String> headers) {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(HEADER_KEY_VALUE_DELIMITER + SPACE_DELIMITER).append(entry.getValue()).append(" \r\n");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
    public static Request create(final HttpMethod method, final String url, final String body) throws IOException {
        final String line = createLine(method, url, body);
        return Request.of(new ByteArrayInputStream(line.getBytes()));
    }

    public static Request create(final HttpMethod method, final String url, final Map<String, String> headers, final String body)
            throws IOException {
        final String line = createLine(method, url, headers, body);
        return Request.of(new ByteArrayInputStream(line.getBytes()));
    }
}
