package com.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Producer2 {

    public static void main(String[] args) throws Exception {

        //setup
        ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://localhost:61616"); //creates object responsible for creating broker connections

        Connection connection = factory.createConnection(); //Actually opens connection to broker

        connection.start(); //activate connection

        Session session= connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //not transactional(non atomic), no need to acknowledge

        Queue queue= session.createQueue("latency-queue3");// where producer sends events

        //create producer
        MessageProducer producer=session.createProducer(queue);

        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); //avoid disk persistence overhead to avoid extra delays

        //Create Message
        String text = Files.readString(Paths.get("message.txt"));

        for (int i = 0; i < 10000; i++) {

            TextMessage message =session.createTextMessage(text);
            long sendTime =System.currentTimeMillis(); //timestamp before sending
            message.setLongProperty(
                    "sendTime",
                    sendTime       ); //attach timestamp to message

            producer.send(message);
            if (i % 1000 == 0) {System.out.println("Sent " + i);}
        }
        connection.close();
        System.out.println("\n10K messages sent");
    }
}