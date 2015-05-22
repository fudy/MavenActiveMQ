package com.fudy;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer implements Runnable {
	private String url;
	private String queueName;
	private String text; 
	
	public Producer(String url, String queueName) {
		this.url = url;
		this.queueName = queueName;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void run() {
		// Create a ConnectionFactory, vm protocol(in memory)
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = null;
		Session session = null;
		try {
			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			// Create a Session, no transacted session and auto acknowlege
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(queueName);
			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// Create a messages
			TextMessage message = session.createTextMessage(text);
			System.out.println("send message: " + text);
			producer.send(message);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			close(connection, session);
		}
	}

	private void close(Connection connection, Session session) {
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
