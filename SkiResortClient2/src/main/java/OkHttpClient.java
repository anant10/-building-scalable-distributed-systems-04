import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import io.swagger.client.model.LiftRide;

public class OkHttpClient {

  private static Gson gson  = new Gson();
  private static String BASE_URL = "http://lb-skier-server-461963516.us-east-1.elb.amazonaws" +
          ".com/SkiResortServer/";
  private static final HttpClient httpClient = HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .connectTimeout(Duration.ofSeconds(10))
          .build();

  public int sendPost(LiftRide liftRide, int resortId, String seasonId, String dayId, int skierId) throws IOException, InterruptedException {
    String json = gson.toJson(liftRide);
//    String url = BASE_URL + "skiers/"+resortId+"/seasons/"+seasonId+"/days/"+dayId+"/skiers/"+skierId;
    String url = "http://lb-skier-server-461963516.us-east-1.elb.amazonaws" +
            ".com/SkiResortServer/skiers/"+11+"/seasons/"+2223+"/days/"+21+"/skiers/"+22;

    HttpRequest request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .uri(URI.create(url))
            .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
            .header("Content-Type", "application/json")
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if(response.statusCode()!=200){
      throw new InterruptedException("Could not send the request");
    }
    return response.statusCode();
  }

  public static void main(String[] args) throws IOException, InterruptedException {

//    // json formatted data
//    String json = new StringBuilder()
//            .append("{")
//            .append("\"time\":\"2\",")
//            .append("\"liftId\":\"6\"")
//            .append("}").toString();
//
//    // add json header
//    HttpRequest request = HttpRequest.newBuilder()
//            .POST(HttpRequest.BodyPublishers.ofString(json))
//            .uri(URI.create(url))
//            .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
//            .header("Content-Type", "application/json")
//            .build();

//    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//    // print status code
//    System.out.println(response.statusCode());
//
//    // print response body
//    System.out.println(response.body());



  }
}
