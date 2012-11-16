package org.ow2.play;

import org.ow2.play.platform.api.bean.Subscription;
import org.ow2.play.platform.api.bean.SubscriptionResult;
import org.ow2.play.platform.api.bean.Topic;
import org.ow2.play.platform.client.ws.PlatformClient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    
    public void testMe() throws Exception {
		PlatformClient client = new PlatformClient();
		client.connect("http://localhost:8080/play/api/v1/", null);
		
		Subscription subscription = new Subscription();
		subscription.subscriber = "http://localhost:3001/";
		Topic topic = new Topic();
		topic.name = "FacebookStatusFeed";
		topic.ns = "http://streams.event-processing.org/ids/";
		topic.prefix = "s";
		
		subscription.topic = topic;
		SubscriptionResult result = client.getSubscriptionManager().subscribe(subscription);
		System.out.println("" + result.subscriptionID);
	}
    
    public void testUnsubscribe() throws Exception {
    	PlatformClient client = new PlatformClient();
		client.connect("http://localhost:8080/play/api/v1/", null);
		
		boolean result = client.getSubscriptionManager().unsubscribe("4d7ddc57-f426-454c-a738-625a7621a852");;
		System.out.println(result);
	}
    
    
    public void testSubscribeThenUnsubscribe() throws Exception {
    	PlatformClient client = new PlatformClient();
		client.connect("http://localhost:8080/play/api/v1/", null);
		
		Subscription subscription = new Subscription();
		subscription.subscriber = "http://localhost:3001/";
		Topic topic = new Topic();
		topic.name = "FacebookStatusFeed";
		topic.ns = "http://streams.event-processing.org/ids/";
		topic.prefix = "s";
		
		subscription.topic = topic;
		SubscriptionResult result = client.getSubscriptionManager().subscribe(subscription);
		System.out.println("Got subscription : " + result.subscriptionID);
		
		Thread.sleep(20000L);
		
		client.getSubscriptionManager().unsubscribe(result.subscriptionID);
	}
}
