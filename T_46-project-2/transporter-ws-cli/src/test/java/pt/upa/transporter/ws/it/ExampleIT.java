package pt.upa.transporter.ws.it;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class ExampleIT {

    // static members
    private static TransporterPortType port;
    private static TransporterPortType port2;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception{
    	String uddiURL = "http://localhost:9090";
		String name = "UpaTransporter1";
		String name2 = "UpaTransporter2";
		
		String endpointAddress1 = null;
		String endpointAddress2 = null;
		UDDINaming uddiNaming = null;

		uddiNaming = new UDDINaming(uddiURL);
		
		endpointAddress1 = uddiNaming.lookup(name);
		System.out.println("Creating stub ...");
		TransporterService service = new TransporterService();

		port = service.getTransporterPort();
		
			
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;

		Map<String, Object> requestContext = bindingProvider.getRequestContext();

		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress1);
		
		endpointAddress2 = uddiNaming.lookup(name2);
		System.out.println("Creating stub ...");
		TransporterService service2 = new TransporterService();
		port2 = service2.getTransporterPort();
		
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider2 = (BindingProvider) port2;
		Map<String, Object> requestContext2 = bindingProvider2.getRequestContext();
			
		requestContext2.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress2);

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }
    // members

    //private static TransporterPortType port2 = null;

    // initialization and clean-up for each test

    @Before
    public void setUp() throws Exception{
    	
	}

    @After
    public void tearDown() {
    	port.clearJobs();
    	port2.clearJobs();
    }


    // tests

    
    @Test
    public void testPing() {
        assertEquals(" ping...daqui transporter\nUpa!", port.ping("Upa"));
    
    }
    
    @Test(expected = BadPriceFault_Exception.class)
    public void testRequestJob() throws Exception{

		port.requestJob("Lisboa", "Coimbra", -1);
	
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void testRequestJob1() throws Exception{

			port.requestJob("Lisboa", "mbra", 10);
		
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void testRequestJob2() throws Exception{

			port.requestJob("Lisb", "Coimbra", 10);
		
    }
    
    @Test
    public void testRequestJob3() throws Exception{
		assertNull(port.requestJob("Lisboa", "Coimbra", 101));
		
    }
    
    @Test
    public void testRequestJob4() throws Exception{
		assertNull(port.requestJob("Porto", "Coimbra", 10));
		
    }
    
    @Test
    public void testRequestJob5() throws Exception{
		assertNull(port2.requestJob("Faro", "Coimbra", 10));
		
    }
    @Test
    public void testRequestJob6() throws Exception{
		assertNull(port.requestJob("Coimbra", "Porto", 10));
		
    }
    @Test
    public void testRequestJob7() throws Exception{
		assertNull(port2.requestJob("Coimbra", "Faro", 10));
		
    }
    @Test
    public void testRequestJob8() throws Exception{
		assertNull(port.requestJob("Faro", "Porto", 10));
		
    }
    @Test
    public void testRequestJob9() throws Exception{
		assertNull(port2.requestJob("Faro", "Porto", 10));
		
    }
    @Test
    public void testRequestJob10() throws Exception{
		assertNull(port.requestJob("Porto", "Faro", 10));
		
    }
    @Test
    public void testRequestJob11() throws Exception{
		assertNull(port2.requestJob("Porto", "Faro", 10));
		
    }
    @Test
    public void testRequestJob12() throws Exception{
    	int price = port.requestJob("Lisboa", "Coimbra", 10).getJobPrice();
    	assertTrue(price < 10);
		
    }
    @Test
    public void testRequestJob13() throws Exception{
    	int price = port.requestJob("Lisboa", "Coimbra", 35).getJobPrice();
    	assertTrue(price < 35);
		
    }
    @Test
    public void testRequestJob14() throws Exception{
    	int price = port2.requestJob("Lisboa", "Coimbra", 35).getJobPrice();
    	assertTrue(price > 35);
		
    }
    @Test
    public void testRequestJob15() throws Exception{
    	int price = port.requestJob("Lisboa", "Coimbra", 30).getJobPrice();
    	assertTrue(price > 30);
		
    }
    @Test
    public void testRequestJob16() throws Exception{
    	int price = port2.requestJob("Lisboa", "Coimbra", 30).getJobPrice();
    	assertTrue(price < 30);
		
    }
    
    @Test
    public void testRequestJob17() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30).getJobPrice();
    	assertEquals("UpaTransporter1" ,port.listJobs().get(0).getCompanyName());
    	assertEquals("Coimbra" ,port.listJobs().get(0).getJobDestination());
    	assertEquals(Integer.toString(0) ,port.listJobs().get(0).getJobIdentifier());
    	assertEquals(JobStateView.PROPOSED ,port.listJobs().get(0).getJobState());
    	assertEquals("Lisboa" ,port.listJobs().get(0).getJobOrigin());
    	assertTrue( port.listJobs().get(0).getJobPrice() > 20);
		
    }
    @Test
    public void testDecideJob() throws Exception{
    	JobView jb = port.decideJob(Integer.toString(0),false);
    	assertNull(jb);
		
    }
    @Test
    public void testDecideJob1() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30);
    	JobView jb = port.decideJob(Integer.toString(1),false);
    	assertNull(jb);
		
    }
    
    @Test
    public void testDecideJob2() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30);
    	JobView jb2 = port.decideJob(Integer.toString(0),false);
    	assertTrue(jb2.getJobState()== JobStateView.REJECTED);
		
    }
    
    @Test
    public void testDecideJob3() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30);
    	JobView jb = port.decideJob(Integer.toString(0),true);
    	
    	assertTrue(jb.getJobState() == JobStateView.ACCEPTED);
    }
    
    @Test
    public void testDecideJob4() throws Exception{
    	JobView job = port.requestJob("Lisboa", "Coimbra", 10);
    	JobView alterado = port.decideJob(Integer.toString(0), true);

    	assertEquals(JobStateView.ACCEPTED,port.jobStatus(alterado.getJobIdentifier()).getJobState());
    	Thread.sleep(16000);
    	assertEquals(JobStateView.COMPLETED,port.jobStatus(alterado.getJobIdentifier()).getJobState());
  
    }
    
    @Test
    public void testDecideJob5() throws Exception{
    	JobView job = port.requestJob("Lisboa", "Coimbra", 10);
    	JobView jb1 = port.decideJob(Integer.toString(0), true);
    	long startTime = System.currentTimeMillis();
    	long startTime1 = 0;
    	long startTime2 = 0;
    	long endTime = 0;
    	long endTime1 = 0;
    	long endTime2 = 0;
    	int var = 1;
    	while(port.jobStatus(jb1.getJobIdentifier()).getJobState() != JobStateView.COMPLETED &&
    			(System.currentTimeMillis()-startTime)<16000){
    		if(var == 1 && port.jobStatus(jb1.getJobIdentifier()).getJobState()==JobStateView.ACCEPTED){
    			var++;
    		}
    		else if(var == 2 && port.jobStatus(jb1.getJobIdentifier()).getJobState()==JobStateView.HEADING){
    			endTime = System.currentTimeMillis();
    			startTime1 = System.currentTimeMillis();
    			var++;
    		}
    		else if(var == 3 && port.jobStatus(jb1.getJobIdentifier()).getJobState()==JobStateView.ONGOING){
    			endTime1 = System.currentTimeMillis();
    			startTime2 = System.currentTimeMillis();
    			var++;
    		}
    		
    	}
    	endTime2 = System.currentTimeMillis();

    	assertTrue(endTime-startTime<=5000);
    	assertTrue(endTime-startTime>=1000);
    	assertTrue(endTime1-startTime1<=5000);
    	assertTrue(endTime1-startTime1>=1000);
    	assertTrue(endTime2-startTime2<=5000);
    	assertTrue(endTime2-startTime2>=1000);
    	assertEquals(4, var);
    	assertEquals(JobStateView.COMPLETED, port.jobStatus(jb1.getJobIdentifier()).getJobState());
    }
    
    @Test
    public void testJobStatus() throws Exception{
    	JobView jb = port.jobStatus(Integer.toString(0));
    	assertNull(jb);
		
    }
    @Test
    public void testJobStatus1() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30);
    	JobView jb = port.jobStatus(Integer.toString(1));
    	assertNull(jb);
		
    }
    
    @Test
    public void testJobStatus2() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30);
    	JobView jb = port.jobStatus(Integer.toString(0));
    	assertEquals(jb.getJobState(), JobStateView.PROPOSED);
		
    }
    @Test
    public void testListJobs() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 20);
    	assertEquals(1, port.listJobs().size());
    	assertEquals("UpaTransporter1" ,port.listJobs().get(0).getCompanyName());
    	assertEquals("Coimbra" ,port.listJobs().get(0).getJobDestination());
    	assertEquals(Integer.toString(0) ,port.listJobs().get(0).getJobIdentifier());
    	assertEquals(JobStateView.PROPOSED ,port.listJobs().get(0).getJobState());
    	assertEquals("Lisboa" ,port.listJobs().get(0).getJobOrigin());
    	assertTrue( port.listJobs().get(0).getJobPrice() > 20);
    }
    @Test
    public void testClearJobs() throws Exception{
    	port.requestJob("Lisboa", "Coimbra", 30);
    	port.clearJobs();
    	assertEquals(new ArrayList<JobView>(),port.listJobs());
    	assertEquals(0, port.listJobs().size());
		
    }

}