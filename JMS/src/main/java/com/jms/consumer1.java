package com.jms;
import java.util.ArrayList;
import java.util.Collections;

// import jakarta.jms.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class consumer1 {
    public static void main(String[] args) throws Exception {

        //setup
        ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://localhost:61616"); //creates object responsible for creating broker connections
        Connection connection = factory.createConnection(); //Actually opens connection to broker
        connection.start(); //activate connection
        Session session= connection.createSession(false, Session.CLIENT_ACKNOWLEDGE); //not transactional(non atomic), consumer acknowledge
        Queue queue= session.createQueue("broker-queue");// where produces sens events

        MessageConsumer consumer =session.createConsumer(queue);

        ArrayList<Long> times = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            long start =System.currentTimeMillis();
            Message message =consumer.receive();
            message.acknowledge();
            long responseTime = (System.currentTimeMillis() - start)  ;
            times.add(responseTime);
            System.out.println("Receive " + i +" = " + responseTime + " ms");
        }

        // Calculate median
        Collections.sort(times);
        long median =times.get(times.size() / 2);
        System.out.println("\nMedian Consume Response Time = "+ median + "ms");

        connection.close();
    }
}
