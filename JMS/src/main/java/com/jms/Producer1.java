package com.jms;

// import jakarta.jms.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class Producer1 {
    public static void main(String[] args) throws Exception {
        //setup
        ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://localhost:61616"); //creates object responsible for creating broker connections
        Connection connection = factory.createConnection(); //Actually opens connection to broker
        connection.start(); //activate connection
        Session session= connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //not transactional(non atomic), no need to acknowledge
        Queue queue= session.createQueue("broker-queue");// where produces sens events

        //create producer
        MessageProducer producer=session.createProducer(queue);

        //Create Message
        String text = Files.readString(Paths.get("message.txt"));
        
        ArrayList<Long> times = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            TextMessage message = session.createTextMessage(text);
            long start =System.currentTimeMillis();
            producer.send(message);
            long responseTime = (System.currentTimeMillis() - start);
            times.add(responseTime);
            System.out.println("Msg Sent " + i +" : " + responseTime + " ms"
            );
        }
        connection.close();
        // Calculate median
        Collections.sort(times);
        long median =times.get(times.size() / 2);
        System.out.println("\nMedian Produce Response Time = "+ median + " ms");



    }
}
