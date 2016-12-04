package bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Stateless
public class ImageBean {

    private QueueConnection queueConnection;
    private MessageProducer messageProducer;
    private QueueSession queueSession;

    @Resource(name = "ImageSplitterBean")
    private Queue questionQueue;

    @PostConstruct
    public void ads() {
        System.out.println("created");
        try {
            Context ctx = new InitialContext();

            QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) ctx.lookup("openejb:Resource/myQCF");
            queueConnection = queueConnectionFactory.createQueueConnection();

            queueConnection.start();
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = queueSession.createProducer(questionQueue);
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void asd() {
        System.out.println("destroyed");
        try {
            queueConnection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void putInMS(long id) throws JMSException {
        messageProducer.send(queueSession.createTextMessage(String.valueOf(id)));
    }
}
