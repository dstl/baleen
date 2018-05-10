// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerThread extends Thread {

  private String topic;
  private Producer<String, String> producer;

  public ProducerThread(String topic, Producer<String, String> producer) {
    this.producer = producer;
    this.topic = topic;
  }

  @Override
  @SuppressWarnings("squid:S2925" /* Sleep to spread messages */)
  public void run() {
    int messageNo = 1;
    while (messageNo < 1000) {
      String messageStr = new String("Message_" + messageNo);
      producer.send(new ProducerRecord<>(topic, Integer.toString(messageNo), messageStr));
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // IGNORE
      }
      messageNo++;
    }
  }
}
