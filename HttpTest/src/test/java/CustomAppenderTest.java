import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.matchers.Times;
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
        HttpResponse resp = HttpResponse.response().withStatusCode(404);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"), Times.exactly(2)).respond(resp);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"), Times.exactly(1)).respond(HttpResponse.response().withStatusCode(202));
        appender.setRetryattempts(5);
        appender.setTimemills(1000);
        assertEquals(2, appender.postMessage("127.0.0.1", "12201", "http", "local", "1.1", "blalbal").getAttempts());

    }

    @Test
    public void postMessageSuccess() {
        HttpResponse resp = HttpResponse.response().withStatusCode(202);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"), Times.exactly(2)).respond(resp);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"), Times.exactly(1)).respond(HttpResponse.response().withStatusCode(404));
        appender.setRetryattempts(5);
        appender.setTimemills(1000);
        assertEquals(4, appender.postMessage("127.0.0.1", "12201", "http", "local", "1.1", "blalbal").getAttempts());

    }
    @Test
    public void postMessageFail() {
        HttpResponse resp = HttpResponse.response().withStatusCode(404);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST")).respond(resp);
        appender.setRetryattempts(5);
        appender.setTimemills(1000);
        assertEquals(0, appender.postMessage("127.0.0.1", "12201", "http", "local", "1.1", "blalbal").getAttempts());

    }

    @Test
    public void postMessageAsync() {
        HttpResponse resp = HttpResponse.response().withStatusCode(202);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST")).respond(resp);
        appender.setRetryattempts(3);
        appender.setTimemills(100);
        assertEquals(2, appender.postMessageAsync("127.0.0.1", "12201", "http", "local", "1.1", "blalbal").getAttempts());

    }
    @Test
    public void postMessageAsyncSuccess() {
        HttpResponse resp = HttpResponse.response().withStatusCode(404);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"),Times.exactly(2)).respond(resp);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"), Times.exactly(1)).respond(HttpResponse.response().withStatusCode(202));
        appender.setRetryattempts(4);
        appender.setTimemills(500);
        assertEquals(1, appender.postMessageAsync("127.0.0.1", "12201", "http", "local", "1.1", "blalbal").getAttempts());

    }
    @Test
    public void postMessageAsyncSuccessTwo() {
        HttpResponse resp = HttpResponse.response().withStatusCode(202);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"),Times.exactly(1)).respond(resp);
        mockServerClient.when(HttpRequest.request("/gelf").withMethod("POST"), Times.exactly(3)).respond(HttpResponse.response().withStatusCode(404));
        appender.setRetryattempts(4);
        appender.setTimemills(500);
        assertEquals(3, appender.postMessageAsync("127.0.0.1", "12201", "http", "local", "1.1", "blalbal").getAttempts());

    }
    @Test
    public void testAsyncCall() {
        ILoggingEvent loggingEvent = new LoggingEvent();
        CustomAppender appenderCurrent = new CustomAppender();
        CustomAppender current = spy(appenderCurrent);
        current.setAsynchronous(true);
        when(current.postMessageAsync(null,null,null,null,null,null)).thenReturn(null);
        current.append(loggingEvent);
        verify(current, times(1)).postMessageAsync(null,null,null,null,null,null);
        verify(current, times(0)).postMessage(null,null,null,null,null,null);

    }
    @Test
    public void testSyncCall() {
        ILoggingEvent loggingEvent = new LoggingEvent();
        CustomAppender appenderCurrent = new CustomAppender();
        CustomAppender current = spy(appenderCurrent);
        current.setAsynchronous(false);
        current.setPort("12201");
        current.setProtocol("http");
        current.setServer("127.0.0.1");
        current.setVersion("1.1");
        current.append(loggingEvent);
        verify(current, times(1)).postMessage("127.0.0.1", "12201", "http", null, "1.1", null);

    }

}