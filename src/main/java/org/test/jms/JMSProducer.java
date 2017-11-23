package org.test.jms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Sample consumer to demonstrate JMS 2.0 feature :
 * Message Delivery Delay
 * Classic API is used
 */

public class JMSProducer {
    public static final String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
    public static final String QUEUE_PREFIX = "queue.";
    private static final String DEFAULT_CONNECTION_FACTORY = "QueueConnectionFactory";
    private static final String DEFAULT_DESTINATION = "MyObjQueue";
    private static final String INITIAL_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private static final String PROVIDER_URL = "tcp://localhost:61616";

    public static void runExample() throws Exception {
        Connection connection = null;
        Context context = null;

        Properties properties = new Properties();
        properties.put(NAMING_FACTORY_INITIAL, INITIAL_CONTEXT_FACTORY);
        properties.put(PROVIDER_URL, PROVIDER_URL);
        properties.put(QUEUE_PREFIX + DEFAULT_DESTINATION, DEFAULT_DESTINATION);

        context = new InitialContext(properties);

        Destination destination = (Destination) context.lookup(DEFAULT_DESTINATION);

        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);

        connection = connectionFactory.createConnection();

        Session session = null;
        MessageProducer producer = null;

        try {
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            producer = session.createProducer(null);

            BytesMessage message = session.createBytesMessage();
            message.writeBytes(serialize(new Person("Eranda", "124154138v", 27)));

            producer.send(destination, message);

        } finally {
            if (producer != null)
                producer.close();
            if (session != null)
                session.close();
            connection.close();
        }
    }

    public static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

}
