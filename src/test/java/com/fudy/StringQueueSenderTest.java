package com.fudy;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class StringQueueSenderTest {
    @Test
    public void testJmsTemplateSendTextMessage() {
    	final String text = "Hello World!";
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/ApplicationContext.xml");
        JmsTemplate template = (JmsTemplate) ctx.getBean("jmsTemplate");
        template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
              Message msg = session.createTextMessage(text);
              return msg;
            }
        });
        System.out.println("sent a message: " + text);
	}
    
    @Test
    public void testJmsTemplateSendObjectMessage() {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("/ApplicationContext.xml");
        JmsTemplate template = (JmsTemplate) ctx.getBean("jmsTemplate");
        template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objMsg = session.createObjectMessage();
                Student std = new Student();
                std.setId(1);
                std.setName("lifudong");
                objMsg.setObject(std);
              return objMsg;
            }
        });
        System.out.println("sent a object message");
    }
    
    @Test
    /**
     * request response model
     * @throws JMSException
     */
    public void testSendCorrelationIDReqMsg() throws JMSException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/ApplicationContext.xml");
        JmsTemplate template = (JmsTemplate) ctx.getBean("jmsTemplate");
        final String messageID = "xxxefsdfefsdf";
        template.send("queue_request", new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage("hello world");
                message.setJMSCorrelationID(messageID);
                System.out.println("sent message, message id: " + messageID);
                return message;
            }
        });
       template.setReceiveTimeout(30000);
       TextMessage msg = (TextMessage)template.receiveSelected("queue_response", "JMSCorrelationID='"+messageID.toString() +"'");
       
       if (null == msg) {
           System.out.println("message is null");
       } else {
           System.out.println("get response : " + msg.getText());
        }
    }
}
