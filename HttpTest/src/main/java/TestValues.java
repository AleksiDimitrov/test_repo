public class TestValues {

    private int attempts;
    private int statusCode;

    public TestValues(int attempts, int statusCode){
        this.attempts = attempts;
        this.statusCode = statusCode;
    }
    public int getAttempts() {
        return attempts;
    }
    public int getStatusCode() {
        return statusCode;
    }
}
