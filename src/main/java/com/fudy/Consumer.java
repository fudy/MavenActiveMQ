package com.fudy;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer implements Runnable {
	private String url;
	private String queueName;

	public Consumer(String url, String queueName) {
		this.url = url;
		this.queueName = queueName;
	}

	public void run() {
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = null;
		MessageConsumer consumer = null;
		Session session = null;
		try {
			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			// Create a Session
			session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(queueName);
			// Create a MessageConsumer from the Session to the Topic or Queue
			consumer = session.createConsumer(destination);
			// Wait for a message
			Message message = consumer.receive(1000);
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				System.out.println("Received: " + text);
			} else {
				System.out.println("Received: " + message);
			}

		} catch (Exception e) {
			System.out.println("Caught: " + e);
		} finally {
			close(consumer, connection, session);
		}
	}

	private void close(MessageConsumer consumer, Connection connection, Session session){
		if (null != consumer) {
			try {
				consumer.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (null != session) {
			try {
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (null != connection) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
