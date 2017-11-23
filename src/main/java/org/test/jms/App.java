package org.test.jms;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Thread consumer = new Thread(new JMSConsumer());
        consumer.start();

        try {
            JMSProducer.runExample();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
