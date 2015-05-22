package com.fudy;

import org.junit.Test;

public class QueueTest {
	@Test
	public void testSendAndReceive() throws Exception {
		String url = "vm://localhost";
		String queueName = "Queue1";
		Producer producer = new Producer(url, queueName);
		producer.setText("hello world!");
		Consumer consumer = new Consumer(url, queueName);
		new Thread(consumer).start();
		new Thread(producer).start();
		Thread.sleep(1000);
	}
}
