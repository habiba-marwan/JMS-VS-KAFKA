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

            // long start = System.currentTimeMillis();
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Send failed: " + exception.getMessage());
                }
            });

            // long end = System.currentTimeMillis();

            // responseTimes.add(end - start);
        }
        producer.flush();
        // sorting to find the median
        // Collections.sort(responseTimes);
        // System.out.println("Median Producer Response Time: " + responseTimes.get(500)
        // + " ms");
        System.out.println("Finished the 10,000 runs adding the 10,000 messages with the timestamps");
        producer.close();
    }
}