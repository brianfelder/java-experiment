Topics: like queues. Similar to how we would use queues in Rabbit.
ConsumerGroup: each process that's consuming from the same collection belongs to the same consumer group. Used by all Consumers for the same Topic across servers.
Partitions: Provides a way to parallelize. One per server per ConsumerGroup. Though it appears we can override this with the `partitionAssigner` param, it looks like Camel uses `org.apache.kafka.clients.consumer.RangeAssignor` by default. That might be something we can tune at a later date.

Producer specifies a Topic.
Consumer specifies a Topic + a ConsumerGroup. For any given Message on a Topic, only one Consumer within the ConsumerGroup will get the Message.
Consumer might also have to specify the Partition, though I have not seen how to specify that yet in Camel.

Note however that there cannot be more consumer instances in a consumer group than partitions.

Extract:
tar -xzf kafka_2.12-0.10.2.1.tgz
Add to server.properties:
host.name: 0.0.0.0
advertised.host.name: localhost

Cleanup everything:
rm -rf /tmp/zookeeper
rm -rf /tmp/kafka-logs
rm -rf logs

Start server:
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

Create topic:
bin/kafka-topics.sh --zookeeper localhost:2181 --create --replication-factor 1 --partitions 1 --topic kafkaFirst
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic kafkaFirst

Useful commands:
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
bin/kafka-topics.sh --zookeeper localhost:2181 --list



Java producer/consumer (both from camel-experiment folder):
mvn exec:java -Dexec.mainClass="net.felder.CamelKafkaProducerRunner"
mvn exec:java -Dexec.mainClass="net.felder.CamelKafkaConsumerRunner"
mvn exec:java -Dexec.mainClass="net.felder.RawKafkaConsumerRunner"


Consume from the console:
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kafkaFirst --from-beginning



Our constructs:
* Job: One per event-integration. (~1000 per month? And how many concurrently?)
** Path: One per job per Type. (~3 per Job) CamelContext lives at this level.
*** Route: Three per path (from, mid, to)

Options for mapping:
One Topic per Job
** Consumers would filter by Type and Route.

One Topic per Type
** Consumers would filter by Job and Route.

One Topic per Route (three total, system-wide: From, Mid, To)
** Consumers would filter by Job and Type.

One Topic, system-wide
** Consumers would filter by Job, Path, and Route.

Look into CQRS


Partition per Job
Topic per Path per Route (Job + Type + 2)A


Partitions are created when you create the topic. You specify how many partitions are on the topic.
Partitions are assigned to specific consumers within a consumer group by Zookeeper.
Consumer group polls a topic.
Zookeeper assigns one or more partitions to the particular consumer within the group.
No more than one consumer within the group will get the same partition.

Analogy:
Topic is a directory. Contains log files, one partition.
Consumer subscribes to the topic as part of a consumer group. The Consumer gets assigned a set of pointers to a set of files.
Only one consumer gets


Message Decider (within a Job):
Kafka:whatever
direct:fromStart

From:
direct:fromStart
direct:fromEnd

------------

Scheduler + resource manager:
mesos
yarn

Scheduler:
nirmata

Kafka Streams:
Flink

kstreams library


