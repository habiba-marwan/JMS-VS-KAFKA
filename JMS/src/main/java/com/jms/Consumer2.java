package com.jms;

import java.util.ArrayList;
import java.util.Collections;

// import jakarta.jms.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer2 {

    public static void main(String[] args) throws Exception {

        //setup
        ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://localhost:61616"); //creates object responsible for creating broker connections
        Connection connection = factory.createConnection(); //Actually opens connection to broker
        connection.start(); //activate connection

        Session session= connection.createSession(false, Session.CLIENT_ACKNOWLEDGE); //not transactional(non atomic), consumer acknowledge

        Queue queue= session.createQueue("latency-queue3");// where producer sends events
        MessageConsumer consumer =session.createConsumer(queue);

        ArrayList<Double> times = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {

            Message message = consumer.receive();
            long receiveTime = System.currentTimeMillis(); //timestamp at message consumption
            message.acknowledge();
            long sendTime = message.getLongProperty("sendTime"); //retrieve producer timestamp

            double latency =
                    (receiveTime - sendTime); 

            times.add(latency);

            if (i % 1000 == 0) {System.out.println("Processed " + i);
}
        }

        // Calculate median
        Collections.sort(times);
        double median =
                times.get(times.size() / 2);

        System.out.println("\nMedian Latency = "+ median + " ms" );

        connection.close();
    }
}