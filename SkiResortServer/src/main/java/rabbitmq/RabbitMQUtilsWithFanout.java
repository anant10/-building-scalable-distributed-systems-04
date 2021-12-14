package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;


public class RabbitMQUtilsWithFanout {
  private static Connection connection;
  private final static String EXCHANGE_NAME = "dbQueries";
  private final static String QUEUE_NAME = "threadExQ";
  private static final String DOMAIN = "54.175.96.218";
  private static final Integer PORT = 5672;
  private static final String USERNAME = "guest";
  private static final String PASSWORD = "guest";
  private static final String VHOST = "/";
  private static BlockingQueue<Channel> queue;

  public RabbitMQUtilsWithFanout(int numberOfChannels){
    queue = new ArrayBlockingQueue<Channel>(numberOfChannels);
    try{
      ConnectionFactory factory = new ConnectionFactory();

      factory.setHost(DOMAIN);
      factory.setPort(PORT);
      factory.setVirtualHost(VHOST);
      factory.setUsername(USERNAME);
      factory.setPassword(PASSWORD);

      connection = factory.newConnection();
      for(int i=0; i<numberOfChannels; i++){
        Channel channel = connection.createChannel();
//        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        queue.put(connection.createChannel());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
  }

  public void publish(String message) {
    try {
      Channel channel = queue.take();
      channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
      queue.put(channel);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void releaseConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void releaseResources() {
    try {
      while(!queue.isEmpty()){
        Channel channel = queue.take();
        channel.close();
      }
      if (connection != null)  connection.close();
    } catch (IOException | TimeoutException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}