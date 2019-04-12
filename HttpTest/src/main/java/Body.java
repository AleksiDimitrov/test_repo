import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Body {

    private String version;
    private String host;
   private String short_message;


    public Body(String version,String host,String short_message){
        this.version = version;
        this.host = host;
        this.short_message = short_message;
    }



}
