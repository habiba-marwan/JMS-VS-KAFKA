package com.jms;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ProducerThroughput {

    public static void main(String[] args) throws Exception {

        int X = 900000; // messages/sec

        //interval time T
        double T = 1000.0 / X; //ms

        //sleep time = 0.8T
        long sleepTime =(long)(T - (0.2 * T));

        //setup
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");

        Connection connection =factory.createConnection();

        connection.start();

        Session session =
                connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE
                );

        Queue queue =session.createQueue("throughput-queue1" );

        MessageProducer producer =
                session.createProducer(queue);

        producer.setDeliveryMode(
                DeliveryMode.NON_PERSISTENT
        );

        String text =Files.readString( Paths.get("message.txt")
                );

        int totalMessages = 1000;
        int failed = 0;
        long startTime =
                System.nanoTime();
        for (int i = 0; i < totalMessages; i++) {

            try {
                TextMessage message =
                        session.createTextMessage(text);

                producer.send(message);

            } catch (Exception e) {

                failed++;
            }

            Thread.sleep(sleepTime);
        }

        long endTime =
                System.nanoTime();

        double totalSeconds =
                (endTime - startTime)
                / 1_000_000_000.0;

        double throughput =
                totalMessages / totalSeconds;

        System.out.println(
                "\nTarget Throughput = "
                + X
                + " msg/sec"
        );

        System.out.println(
                "Actual Throughput = "
                + throughput
                + " msg/sec"
        );

        System.out.println(
                "Failed Requests = "
                + failed
        );

        connection.close();
    }
}


// public class ProducerFillQueue {

//     public static void main(String[] args) throws Exception {

//         //setup
//         ConnectionFactory factory =
//                 new ActiveMQConnectionFactory(
//                         "tcp://localhost:61616"
//                 );

//         Connection connection =
//                 factory.createConnection();

//         connection.start();

//         Session session =
//                 connection.createSession(
//                         false,
//                         Session.AUTO_ACKNOWLEDGE
//                 );

//         Queue queue =
//                 session.createQueue(
//                         "throughput-queue"
//                 );

//         MessageProducer producer =
//                 session.createProducer(queue);

//         producer.setDeliveryMode(
//                 DeliveryMode.NON_PERSISTENT
//         ); //avoid disk persistence overhead

//         //Create Message
//         String text =
//                 Files.readString(
//                         Paths.get("message.txt")
//                 );

//         int totalMessages = 100000;

//         for (int i = 0; i < totalMessages; i++) {

//             TextMessage message =
//                     session.createTextMessage(text);

//             producer.send(message);
//         }

//         System.out.println(
//                 "\nQueue filled with "
//                 + totalMessages
//                 + " messages"
//         );

//         connection.close();
//     }
// }