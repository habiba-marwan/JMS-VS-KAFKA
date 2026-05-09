habiba marwan 8855<br>shahd yasser 8748

<h1 align="center"> JMS VS KAFKA lab</h1>



# Apache kafka

## Usability
 - i downloaded kafa (around 30 mins)
 - i ran 2 shell scripts to start the application
**To start the zookeeper**

```sh

bin/zookeeper-server-start.sh config/zookeeper.properties

```

**To start kafka**

```sh

bin/kafka-server-start.sh config/server.properties

```
 - i created a test topic inside kafka called lab-topic
 - created a maven project inside VScode 
 - added the kafka dependencies inside the pom.xml

## Performance Comparison

### Response Time

     
- we measured the response time by sending a 1KB message from the producer a 1000 times , waited till the kafka broker acks the reception of the msg ( made the call sync.) and measured this waiting time 
  
- Got the median response time of the 1000 runs as the producer response time

**Code to test producer's response time**

```java
package com.example;

import org.apache.kafka.clients.producer.*;
import java.util.*;

public class Producer {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // to tell kafka how to convert the message string in java into bytes
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // Using ByteArraySerializer to handle the raw 1KB payload
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);

        // we will store the response time of each message we write
        List<Long> responseTimes = new ArrayList<>();

        System.out.println("Starting 1000 runs for Response Time...");
        for (int i = 0; i < 1000; i++) {
            byte[] message = new byte[1024];

            // Capture the current time as a String
            long emitTime = System.currentTimeMillis();
            String timeString = String.valueOf(emitTime);
            byte[] timeBytes = timeString.getBytes();

            // Copy the timestamp bytes into the start of our 1KB payload
            System.arraycopy(timeBytes, 0, message, 0, timeBytes.length);

            ProducerRecord<String, byte[]> record = new ProducerRecord<>("lab-topic", "key", message);

            long start = System.currentTimeMillis();
            producer.send(record).get(); // to make the call sync as the kafka default is async
            long end = System.currentTimeMillis();

            responseTimes.add(end - start);
        }

        // sorting to find the median
        Collections.sort(responseTimes);
        System.out.println("Median Producer Response Time: " + responseTimes.get(500) + " ms");
        producer.close();
    }
}
```
**Output**

![Results](producerResponse.png)

**Code to test consumer's response time**

```java
package com.example;

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.*;

public class Consumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // as in kafka each consumer must belong to a group
        props.put("group.id", "lab-group-unique"); // Unique group to start from beginning

        // to deserialize the message back into a string
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        // setting the consumer offset
        props.put("auto.offset.reset", "earliest"); // Read from the very first message

        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("lab-topic")); // subscribe to topic

        List<Long> pollResponseTimes = new ArrayList<>();
        // List<Long> latencies = new ArrayList<>();

        System.out.println("Actively consuming to measure metrics...");

        // 10K messages for latency
        // and report median of 1000 runs for response time
        while (pollResponseTimes.size() < 1000) {
            long pollStart = System.currentTimeMillis();
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
            long pollEnd = System.currentTimeMillis();

            // if (!records.isEmpty()) {
                // Record Poll Response Time (for the first 1000 valid polls)
                // if (pollResponseTimes.size() < 1000) {
                    pollResponseTimes.add(pollEnd - pollStart);
                // }

                // for (ConsumerRecord<String, byte[]> record : records) {
                // byte[] payload = record.value();

                // Extract Timestamp from the 1KB payload
                // We read the bytes until we hit a non-digit or reasonable limit
                // String timeStr = new String(payload).trim();
                // Note: trim() works because the rest of the 1KB array is empty bytes (0)

                // try {
                // long producerTimestamp = Long.parseLong(timeStr.split("[^0-9]")[0]);
                // long currentTimestamp = System.currentTimeMillis();

                // // Calculate Latency
                // latencies.add(currentTimestamp - producerTimestamp);
                // } catch (Exception e) {
                // // Skip if the bytes aren't a valid timestamp
                // }

                // if (latencies.size() >= 10000)
                // break;
                // }
            // }
        }

        // Calculate and Print Medians
        Collections.sort(pollResponseTimes);
        // Collections.sort(latencies);

        System.out.println("--- Kafka Consumer Results ---");
        System.out.println(
                "Median Consumer Response Time: " + pollResponseTimes.get(pollResponseTimes.size() / 2) + " ms");
        // System.out.println("Median End-to-End Latency (10k msgs): " +
        // latencies.get(latencies.size() / 2) + " ms");

        consumer.close();
    }
}
```

**Output**
![Results](consumerResponse.png)

### Latency

- we measured the latency by sending a 1KB message from the producer  10,000 times , added the current Time stamp to the msg at the producer , so that we can measure the time it took to get to the consumer
  
- Got the median latency of the 10000 runs 

**Code after modifying producer to 10,000 runs instead of 1000**

```java
package com.example;

import org.apache.kafka.clients.producer.*;
import java.util.*;

public class Producer {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // to tell kafka how to convert the message string in java into bytes
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // Using ByteArraySerializer to handle the raw 1KB payload
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);

        // we will store the response time of each message we write
        // List<Long> responseTimes = new ArrayList<>();

        System.out.println("Starting 10000 runs for latency...");
        for (int i = 0; i < 10000; i++) {
            byte[] message = new byte[1024];

            // Capture the current time as a String
            long emitTime = System.currentTimeMillis();
            String timeString = String.valueOf(emitTime);
            byte[] timeBytes = timeString.getBytes();

            // Copy the timestamp bytes into the start of our 1KB payload
            System.arraycopy(timeBytes, 0, message, 0, timeBytes.length);

            ProducerRecord<String, byte[]> record = new ProducerRecord<>("lab-topic", "key", message);

            long start = System.currentTimeMillis();
            producer.send(record).get(); // to make the call sync as the kafka default is async
            long end = System.currentTimeMillis();

            // responseTimes.add(end - start);
        }

        // sorting to find the median
        // Collections.sort(responseTimes);
        // System.out.println("Median Producer Response Time: " + responseTimes.get(500)
        // + " ms");
        System.out.println("Finished the 10,000 runs adding the 10,000 messages with the timestamps");
        producer.close();
    }
}

```

**Output**
![Results](producerLatency.png)

**Consumer's code for latency**

```java
package com.example;

import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.*;

public class Consumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // as in kafka each consumer must belong to a group
        props.put("group.id", "lab-group-unique"); // Unique group to start from beginning

        // to deserialize the message back into a string
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        // setting the consumer offset
        props.put("auto.offset.reset", "earliest"); // Read from the very first message

        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("lab-topic")); // subscribe to topic

        List<Long> pollResponseTimes = new ArrayList<>();
        List<Long> latencies = new ArrayList<>();

        System.out.println("Actively consuming to measure metrics...");

        // 10K messages for latency
        // and report median of 1000 runs for response time
        while (pollResponseTimes.size() < 10000) {
            long pollStart = System.currentTimeMillis();
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
            long pollEnd = System.currentTimeMillis();

            if (records.isEmpty()) {
                // If we have already received SOME messages but now it's empty,
                // the producer is likely done. Break so we can see our results!
                if (latencies.size() > 0) {
                    System.out.println("No more messages incoming. Ending test...");
                    break;
                }
                continue;
            }
            for (ConsumerRecord<String, byte[]> record : records) {
                byte[] payload = record.value();

                // Extract Timestamp from the 1KB payload
                // We read the bytes until we hit a non-digit or reasonable limit
                String timeStr = new String(payload).trim();
                // Note: trim() works because the rest of the 1KB array is empty bytes (0)

                try {
                    long producerTimestamp = Long.parseLong(timeStr.split("[^0-9]")[0]);
                    long currentTimestamp = System.currentTimeMillis();

                    // Calculate Latency
                    latencies.add(currentTimestamp - producerTimestamp);
                } catch (Exception e) {
                    // Skip if the bytes aren't a valid timestamp
                }

                if (latencies.size() >= 10000)
                    break;
            }

        }

        // Calculate and Print Medians
        Collections.sort(pollResponseTimes);
        Collections.sort(latencies);

        System.out.println("--- Kafka Consumer Results ---");
        // System.out.println(
        // "Median Consumer Response Time: " +
        // pollResponseTimes.get(pollResponseTimes.size() / 2) + " ms");
        System.out.println("Median End-to-End Latency (10k msgs): " +
                latencies.get(latencies.size() / 2) + " ms");

        consumer.close();
    }
}

```

**Output**
![Results](consumerLatency.png)