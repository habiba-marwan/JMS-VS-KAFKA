error id: file://<WORKSPACE>/src/main/java/com/example/Consumer.java
file://<WORKSPACE>/src/main/java/com/example/Consumer.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[66,25]

error in qdox parser
file content:
```java
offset: 2834
uri: file://<WORKSPACE>/src/main/java/com/example/Consumer.java
text:
```scala
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
            //     // Record Poll Response Time (for the first 1000 valid polls)
            //     if (pollResponseTimes.size() < 1000) {
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
            }
        }

        // Calculate and Print Medians
        Collections.sort(@@pollResponseTimes);
        // Collections.sort(latencies);

        System.out.println("--- Kafka Consumer Results ---");
        System.out.println(
                "Median Consumer Response Time: " + pollResponseTimes.get(pollResponseTimes.size() / 2) + " ms");
        // System.out.println("Median End-to-End Latency (10k msgs): " +
        // latencies.get(latencies.size() / 2) + " ms");

        consumer.close();
    }}
```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.metals.SemanticdbDefinition$.foreachWithReturnMtags(SemanticdbDefinition.scala:99)
	scala.meta.internal.metals.Indexer.indexSourceFile(Indexer.scala:560)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3(Indexer.scala:691)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3$adapted(Indexer.scala:688)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.Indexer.reindexWorkspaceSources(Indexer.scala:688)
	scala.meta.internal.metals.MetalsLspService.$anonfun$onChange$2(MetalsLspService.scala:940)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:691)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:500)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	java.base/java.lang.Thread.run(Thread.java:840)
```
#### Short summary: 

QDox parse error in file://<WORKSPACE>/src/main/java/com/example/Consumer.java