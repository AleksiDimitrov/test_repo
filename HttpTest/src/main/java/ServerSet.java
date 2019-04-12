import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.HashMap;

public class ServerSet {

    private HashMap<String,String> jsonObj;
    private String endpoint;


    public ServerSet(){
        jsonObj = new HashMap<String, String>();
    }

    public void postMessage(String server,String port,String protocol,String host,String version,String short_message){
        try {
            HttpResponse<JsonNode> response = Unirest.post(setEndpoint(server,port,protocol)).header("accept", "application/json").body(getMessage(version,host,short_message)).asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private String setEndpoint(String server, String port,String protocol){
        return endpoint = protocol+"://"+server+":"+port+"/gelf";
    }


    private HashMap<String,String> getMessage(String version,String host,String short_message){
        jsonObj.put("version", version);
        jsonObj.put("host", host);
        jsonObj.put("short_message",short_message);
        return jsonObj;
    }
}
