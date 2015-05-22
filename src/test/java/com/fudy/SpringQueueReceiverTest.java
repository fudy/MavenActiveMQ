package com.fudy;

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

public class SpringQueueReceiverTest {
    @Test
    public void testJmsTemplateReceiveTextMessage() throws JMSException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/ApplicationContext.xml");
        JmsTemplate template = (JmsTemplate) ctx.getBean("jmsTemplate");
        
        Message msg = template.receive();
        if (msg instanceof TextMessage) {
        	TextMessage txtmsg = (TextMessage)msg;
	        System.out.println("receive a message: " + txtmsg.getText());
        } else {
        	System.out.println("the message is not text message");
        }
    }
    
    @Test
    public void testJmsTemplateReceiveObjectMessage() throws JMSException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/ApplicationContext.xml");
        JmsTemplate template = (JmsTemplate) ctx.getBean("jmsTemplate");
        Message msg = template.receive();
        if (msg instanceof ObjectMessage) {
        	ObjectMessage objMsg = (ObjectMessage) msg;
        	Student obj = (Student)objMsg.getObject();
            if (null != obj) {
                System.out.println("receive a object message: " + obj.getName());
            }
        }
    }
    
    @Test
    /* request response model
     */
    public void testSendCorrelationIDRespMsg() throws JMSException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/ApplicationContext.xml");
        JmsTemplate template = (JmsTemplate) ctx.getBean("jmsTemplate");
        final Message msg = template.receive("queue_request");
        if (msg instanceof TextMessage) {
        	TextMessage tm = (TextMessage)msg;
        	System.out.println(tm.getText());
        }
        template.send("queue_response",new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage("hello world response");
                message.setJMSCorrelationID(msg.getJMSCorrelationID());
                return message;
            }
        });
    }


}
