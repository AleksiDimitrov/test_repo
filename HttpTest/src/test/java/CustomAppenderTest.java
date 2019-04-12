import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


public class CustomAppenderTest {
    private static final int PORT = 12201;
    CustomAppender appender = new CustomAppender();

    @Rule
    public MockServerRule rule = new MockServerRule(this, true, PORT);
    public MockServerClient mockServerClient;

    @Test
    public void postMessage() {
        HttpResponse resp = HttpResponse.response().withStatusCode(202);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST")).respond(resp);
        appender.setRetryattempts(3);
        appender.setTimemills(1000);
        assertEquals(2, appender.postMessage("127.0.0.1", "12201", "http", "local", "1.1", "blalbal"));

    }

    @Test
    public void postMessageAsync() {
        HttpResponse resp = HttpResponse.response().withStatusCode(304);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST")).respond(resp);
        appender.setRetryattempts(3);
        appender.setTimemills(5000);
        assertEquals(2, appender.postMessageAsync("127.0.0.1", "12201", "http", "local", "1.1", "blalbal"));

    }

}