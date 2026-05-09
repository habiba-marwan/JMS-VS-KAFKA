// package com.jms;

// import javax.jms.*;
// import org.apache.activemq.ActiveMQConnectionFactory;

// public class ConsumerThroughput {

//     public static void main(String[] args) throws Exception {

//         int X = 80000; //messages/sec

//         //interval time T
//         double T = 1000.0 / X; //ms

//         //sleep time = 0.8T
//         long sleepTime =
//                 (long)(T - (0.2 * T));

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
//                         Session.CLIENT_ACKNOWLEDGE
//                 );

//         Queue queue =
//                 session.createQueue(
//                         "throughput-queue"
//                 );

//         MessageConsumer consumer =
//                 session.createConsumer(queue);

//         int totalMessages = 1000;

//         int failed = 0;

//         long startTime =
//                 System.nanoTime();

//         for (int i = 0; i < totalMessages; i++) {

//             try {

//                 Message message =
//                         consumer.receive(5000);

//                 if (message != null) {

//                     message.acknowledge();

//                 } else {

//                     failed++;
//                 }

//             } catch (Exception e) {

//                 failed++;
//             }

//             Thread.sleep(sleepTime);
//         }

//         long endTime =
//                 System.nanoTime();

//         double totalSeconds =
//                 (endTime - startTime)
//                 / 1_000_000_000.0;

//         double throughput =
//                 totalMessages / totalSeconds;

//         System.out.println(
//                 "\nTarget Throughput = "
//                 + X
//                 + " msg/sec"
//         );

//         System.out.println(
//                 "Actual Throughput = "
//                 + throughput
//                 + " msg/sec"
//         );

//         System.out.println(
//                 "Failed Requests = "
//                 + failed
//         );

//         connection.close();
//     }
// }




package com.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class FastConsumer {

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory =
                new ActiveMQConnectionFactory(
                        "tcp://localhost:61616"
                );

        Connection connection =
                factory.createConnection();

        connection.start();

        Session session =
                connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE
                );

        Queue queue =
                session.createQueue(
                        "throughput-queue1"
                );

        MessageConsumer consumer =
                session.createConsumer(queue);

        while (true) {

            Message message =
                    consumer.receive();

            if (message != null) {

                System.out.println(
                        "Consumed"
                );
            }
        }
    }
}