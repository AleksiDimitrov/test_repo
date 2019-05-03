import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CustomAppender extends AppenderBase<ILoggingEvent> {
    private String server;
    private String port;
    private String protocol;
    private String version;
    private String endpoint;
    private Body bodyObj;
    private boolean asynchronous;
    private int retryattempts;
    private int timemills;
    private static Logger logger = LoggerFactory.getLogger("stdout");
    private int statusCode;
    private Client client = ClientBuilder.newClient();

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return this.server;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return this.port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public int getRetryattempts() {
        return retryattempts;
    }

    public void setRetryattempts(int retryattempts) {
        this.retryattempts = retryattempts;
    }

    public int getTimemills() {
        return timemills;
    }

    public void setTimemills(int timemills) {
        this.timemills = timemills;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }


    public synchronized TestValues postMessage(String server, String port, String protocol, String host, String version, String short_message) {
        int attempts = retryattempts;

        try {
            bodyObj = new Body(version, host, short_message);
            do {
                Response response = client.target(setEndpoint(server, port, protocol)).request(MediaType.APPLICATION_JSON).post(Entity.entity(bodyObj, MediaType.APPLICATION_JSON));
                statusCode = response.getStatus();
                attempts--;
                Thread.sleep(timemills);
            } while (statusCode != 202 && attempts > 0);
        } catch (InterruptedException e) {
            logger.debug(e.getMessage());
        }
        return new TestValues(attempts,statusCode);
    }

    public synchronized TestValues postMessageAsync(String server, String port, String protocol, String host, String version, String short_message) {

        bodyObj = new Body(version, host, short_message);
        int attempts = retryattempts;

        do {
            Future<Response> response = client.target(setEndpoint(server, port, protocol)).request().async().post(Entity.entity(bodyObj, MediaType.APPLICATION_JSON));
            attempts--;
            try {
                statusCode = response.get().getStatus();
            } catch (InterruptedException e) {
               logger.debug(e.getMessage());
            } catch (ExecutionException e) {
                logger.debug(e.getMessage());
            }
            try {
                Thread.sleep(timemills);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
        } while (statusCode != 202 && attempts > 0);
        return new TestValues(attempts,statusCode);
    }

    protected void append(ILoggingEvent iLoggingEvent) {

        LoggingEvent copy = new LoggingEvent();
        copy.setLoggerName(iLoggingEvent.getLoggerName());
        iLoggingEvent.getMessage();
        if (asynchronous) {
            postMessageAsync(server, port, protocol, iLoggingEvent.getLoggerName(), version, iLoggingEvent.getMessage());
        } else {
            postMessage(server, port, protocol, iLoggingEvent.getLoggerName(), version, iLoggingEvent.getMessage());
        }
    }

    private String setEndpoint(String server, String port, String protocol) {
        return endpoint = protocol + "://" + server + ":" + port + "/gelf";
    }
}
