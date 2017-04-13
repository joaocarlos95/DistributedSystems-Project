package pt.upa.broker.ws.it;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class ExampleIT {

    // static members

	private static BrokerPortType port;
	private static BrokerPortType port2;
    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
    	String uddiURL = "http://localhost:9090";
		String name = "UpaBroker1";
		String name2 = "UpaBroker2";
		
		String endpointAddress1 = null;
		String endpointAddress2 = null;
		UDDINaming uddiNaming = null;
    	
    	System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
		uddiNaming = new UDDINaming(uddiURL);
		
		
		endpointAddress1 = uddiNaming.lookup(name);
		endpointAddress2 = uddiNaming.lookup(name2);
		System.out.println("Creating stub ...");
		BrokerService service = new BrokerService();

		port = service.getBrokerPort();
		port2 = service.getBrokerPort();
			
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress1);
		bindingProvider = (BindingProvider) port2;
		requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress2);
    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	port.clearTransports();
    }

    @After
    public void tearDown() {
    	port.clearTransports();
    }


    // tests

    @Test
    public void testPing() {
        assertEquals(" ping...daqui transporter\nUpa!", port.ping("Upa"));
    
    }
    
    @Test(expected = InvalidPriceFault_Exception.class)
    public void testRequestTransport() throws Exception{
    	port.requestTransport("Lisboa", "Coimbra", -1);
	
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void testRequestTransport1() throws Exception{

			port.requestTransport("Lisboa", "mbra", 10);
		
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void testRequestTransport2() throws Exception{

			port.requestTransport("Lisb", "Coimbra", 10);
		
    }
    
    @Test
    public void testRequestTransport3() throws Exception{
			String id = port.requestTransport("Lisboa", "Coimbra", 10);
			assertEquals("0", id);
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void testViewTransport() throws Exception{
			port.viewTransport("0");
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void testViewTransport1() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 10);
			port.viewTransport("1");
    }
    
    @Test
    public void testViewTransport2() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 10);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Lisboa", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertTrue(trans.getPrice()<10);
			assertEquals("0", trans.getId());
    }
    
    @Test
    public void testViewTransport3() throws Exception{
			port.requestTransport("Porto", "Coimbra", 10);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Porto", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertEquals("UpaTransporter2", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<10);
			assertEquals("0", trans.getId());
    }
    
    @Test
    public void testViewTransport4() throws Exception{
			port.requestTransport("Coimbra", "Porto", 10);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Coimbra", trans.getOrigin());
			assertEquals("Porto", trans.getDestination());
			assertEquals("UpaTransporter2", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<10);
			assertEquals("0", trans.getId());
    }

    @Test
    public void testViewTransport5() throws Exception{
			port.requestTransport("Coimbra", "Faro", 10);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Coimbra", trans.getOrigin());
			assertEquals("Faro", trans.getDestination());
			assertEquals("UpaTransporter1", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<10);
			assertEquals("0", trans.getId());
    }
    
    @Test
    public void testViewTransport6() throws Exception{
			port.requestTransport("Faro", "Coimbra", 10);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Faro", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertEquals("UpaTransporter1", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<10);
			assertEquals("0", trans.getId());
    }
    
    @Test(expected = UnavailableTransportFault_Exception.class)
    public void testViewTransport7() throws Exception{
			port.requestTransport("Faro", "Porto", 10);
			Thread.sleep(1000);
			TransportView trans = port.viewTransport("0");
			assertEquals("Faro", trans.getOrigin());
			assertEquals("Porto", trans.getDestination());
			assertEquals(TransportStateView.FAILED, trans.getState());
			assertNull(trans.getTransporterCompany());
			assertTrue(10 == trans.getPrice());
			assertEquals("0", trans.getId());
    }
    
    @Test(expected = UnavailableTransportFault_Exception.class)
    public void testViewTransport8() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 101);
			Thread.sleep(1000);
			TransportView trans = port.viewTransport("0");
			assertEquals("Lisboa", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertEquals(TransportStateView.FAILED, trans.getState());
			assertNull(trans.getTransporterCompany());
			assertEquals("0", trans.getId());
    }
    
    @Test
    public void testViewTransport9() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 50);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Lisboa", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertEquals("UpaTransporter2", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<50);
			assertEquals("0", trans.getId());
    }
    
    @Test
    public void testViewTransport10() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 51);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Lisboa", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertEquals("UpaTransporter1", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<51);
			assertEquals("0", trans.getId());
    }
    
    @Test
    public void testViewTransport11() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 51);
			TransportView trans = port.viewTransport("0");
			assertNotNull(trans);
			assertEquals("Lisboa", trans.getOrigin());
			assertEquals("Coimbra", trans.getDestination());
			assertEquals("UpaTransporter1", trans.getTransporterCompany());
			assertTrue(trans.getPrice()<51);
			assertEquals("0", trans.getId());
    }
    
    
    //Caso de erro correr novamente, margem de erro de tempo
    @Test
    public void testViewTransport12() throws Exception{
			port.requestTransport("Lisboa", "Coimbra", 51);
			TransportView trans = port.viewTransport("0");
			long startTime = System.currentTimeMillis();
			long startTime1 = 0;
			long startTime2 = 0;
			long endTime = 0;
			long endTime1 = 0;
			long endTime2 = 0;
			int var = 1;
			while(port.viewTransport(trans.getId()).getState() != TransportStateView.COMPLETED &&
					(System.currentTimeMillis()-startTime)<16000){
				if(var == 1 && port.viewTransport(trans.getId()).getState()==TransportStateView.BOOKED){
					var++;
				}
				else if(var == 2 && port.viewTransport(trans.getId()).getState()==TransportStateView.HEADING){
					endTime = System.currentTimeMillis();
					startTime1 = System.currentTimeMillis();
					var++;
				}
				else if(var == 3 && port.viewTransport(trans.getId()).getState()==TransportStateView.ONGOING){
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
			assertEquals(TransportStateView.COMPLETED, port.viewTransport(trans.getId()).getState());
    }
    
    @Test
    public void testListTransports() throws Exception{
    	port.requestTransport("Lisboa", "Coimbra", 20);
    	assertNotNull(port.listTransports().get(0));
    	assertEquals(1, port.listTransports().size());
    	assertEquals("UpaTransporter2" ,port.listTransports().get(0).getTransporterCompany());
    	assertEquals("Coimbra" ,port.listTransports().get(0).getDestination());
    	assertEquals(Integer.toString(0) ,port.listTransports().get(0).getId());
    	assertEquals("Lisboa" ,port.listTransports().get(0).getOrigin());
    	assertTrue( port.listTransports().get(0).getPrice() < 20);
    }
    
    @Test
    public void testClearTransports() throws Exception{
    	port.requestTransport("Lisboa", "Coimbra", 20);
    	port.clearTransports();
    	assertEquals(new ArrayList<TransportView>(),port.listTransports());
    	assertEquals(0, port.listTransports().size());
		
    }
    
    @Test
    public void testUpdate() throws Exception{
    	port.requestTransport("Lisboa", "Coimbra", 20);
    	TransportView trans = port.viewTransport("0");
    	TransportView trans2 = port2.viewTransport("0");
		assertNotNull(trans);
		assertNotNull(trans2);
		assertEquals(trans.getOrigin(), trans2.getOrigin());
		assertEquals(trans.getDestination(), trans2.getDestination());
		assertEquals(trans.getTransporterCompany(), trans2.getTransporterCompany());
		assertEquals(trans.getPrice(), trans2.getPrice());
		assertEquals(trans.getId(), trans2.getId());
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void testUpdate2() throws Exception{
    	port.requestTransport("Lisboa", "Coimbra", 20);
    	port.clearTransports();
    	port2.viewTransport("0");
    }
    
    @Test
    public void testUpdate3() throws Exception{
    	port.requestTransport("Lisboa", "Coimbra", 20);
    	assertEquals(port.listTransports().size(), port2.listTransports().size());
    }
    
    /*@Test
    public void testChange() throws Exception{
    	port.ping("kill");
    	port.requestTransport(origin, destination, price)
    }*/
    
}