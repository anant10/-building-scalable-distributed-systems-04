package mqconsumer;


import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.LiftRideDao;
import model.LiftRideDetail;

public class ProcessorWithFanout implements Runnable {

  private static Gson gson  = new Gson();
  private final static String EXCHANGE_NAME = "dbQueries";
  //  private final static String QUEUE_NAME = "threadExQ";
  private Connection connection;
  private Map<Integer, List<String>> mapOfLiftRides;
  private LiftRideDao liftRideDao;

  public ProcessorWithFanout(Connection conn, Map<Integer, List<String>> mapOfLiftRides, LiftRideDao liftRideDao) {
    this.liftRideDao = liftRideDao;
    this.connection = conn;
    this.mapOfLiftRides = mapOfLiftRides;
  }

  public void run() {
    try {
      final Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
      String queueName = channel.queueDeclare().getQueue();
      channel.queueBind(queueName, EXCHANGE_NAME, "");

//      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      // max one message per receiver
      channel.basicQos(1);
      System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        LiftRideDetail liftRideDetail = gson.fromJson(message, LiftRideDetail.class);
//        String[] split = message.split(",");
//        String[] time = split[0].split(":");
//        String[] liftId = split[1].split(":");
        this.liftRideDao.createLiftRide(liftRideDetail);

//        mapOfLiftRides.putIfAbsent(liftRideDetail.getLiftId(), new ArrayList<String>());
//        mapOfLiftRides.get(liftRideDetail.getLiftId()).add(String.valueOf(liftRideDetail.getTime()));
        System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
      };
      // process messages
      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    } catch (IOException ex) {
      Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}