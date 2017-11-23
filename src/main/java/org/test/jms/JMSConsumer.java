package org.test.jms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Sample consumer to demonstrate JMS 2.0 feature :
 * Message Delivery Delay
 * Classic API is used
 */

public class JMSConsumer implements Runnable {
    private static final String DEFAULT_CONNECTION_FACTORY = "QueueConnectionFactory";
    private static final String DEFAULT_DESTINATION = "MyObjQueue";
    private static final String INITIAL_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private static final String PROVIDER_URL = "tcp://localhost:61616";


    public static void runExample() throws Exception {
        Connection connection = null;
        Context initialContext = null;
        try {
            // Step 1. Create an initial context to perform the JNDI lookup.
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put("java.naming.provider.url", PROVIDER_URL);
            env.put("queue." + DEFAULT_DESTINATION, DEFAULT_DESTINATION);
            initialContext = new InitialContext(env);

            // Step 2. perform a lookup on the Queue
            Queue queue = (Queue) initialContext.lookup(DEFAULT_DESTINATION);

            // Step 3. perform a lookup on the Connection Factory
            ConnectionFactory cf = (ConnectionFactory) initialContext.lookup(DEFAULT_CONNECTION_FACTORY);

            // Step 4. Create a JMS Connection
            connection = cf.createConnection();

            // Step 5. Create a JMS Session
            Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

            // Step 6. Create a JMS Message Consumer
            MessageConsumer messageConsumer = session.createConsumer(queue);

            // Step 7. Start the Connection
            connection.start();
            System.out.println("JMS consumer stated on the queue " + DEFAULT_DESTINATION + "\n");

            BytesMessage message = (BytesMessage) messageConsumer.receive();
            byte[] serializedData = new byte[(int) message.getBodyLength()];
            message.readBytes(serializedData);

            Person person = (Person) deserialize(serializedData);

            System.out.println("Consumed Object: " + person.toString());

        } finally {
            // Step 9. Close JMS resources
            if (connection != null) {
                connection.close();
            }

            // Also the initialContext
            if (initialContext != null) {
                initialContext.close();
            }
        }
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }

    @Override
    public void run() {
        try {
            runExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
