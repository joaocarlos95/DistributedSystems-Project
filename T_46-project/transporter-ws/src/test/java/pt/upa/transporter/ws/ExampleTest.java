package pt.upa.transporter.ws;

import org.junit.*;

import mockit.Mocked;
import pt.upa.transporter.ws.TransporterPort.MyTimerTask;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class ExampleTest {

    // static members


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members

    private TransporterPort port;
    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	port = new TransporterPort();
    	// Create a hash map
	      HashMap<String, String> regioes = new HashMap<String, String>();
	      // Put elements to the map
	      regioes.put("Porto", "RegiaoNorte");
	      regioes.put("Braga", "RegiaoNorte");
	      regioes.put("Viana do Castelo", "RegiaoNorte");
	      regioes.put("Vila Real", "RegiaoNorte");
	      regioes.put("Bragança", "RegiaoNorte");
	      regioes.put("Lisboa", "RegiaoCentro");
	      regioes.put("Leiria", "RegiaoCentro");
	      regioes.put("Santarém", "RegiaoCentro");
	      regioes.put("Castelo Branco", "RegiaoCentro");
	      regioes.put("Coimbra", "RegiaoCentro");
	      regioes.put("Aveiro", "RegiaoCentro");
	      regioes.put("Viseu", "RegiaoCentro");
	      regioes.put("Guarda", "RegiaoCentro");
	      regioes.put("Setúbal", "RegiaoSul");
	      regioes.put("Évora", "RegiaoSul");
	      regioes.put("Portalegre", "RegiaoSul");
	      regioes.put("Beja", "RegiaoSul");
	      regioes.put("Faro", "RegiaoSul");
	      
	      TransporterPort.regioes = regioes;
	      TransporterPort.transport = "UpaTransporter1";
	      port.clearJobs();
    	
    }

    @After
    public void tearDown() {
    	port = null;
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
    	TransporterPort.transport = "UpaTransporter1";
    	TransporterPort.transporter = 1;
		assertNull(port.requestJob("Porto", "Coimbra", 10));
		
    }
    
    @Test
    public void testRequestJob5() throws Exception{
    	TransporterPort.transport = "UpaTransporter2";
    	TransporterPort.transporter = 2;
		assertNull(port.requestJob("Faro", "Coimbra", 10));
		
    }
    @Test
    public void testRequestJob6() throws Exception{
    	TransporterPort.transport = "UpaTransporter1";
    	TransporterPort.transporter = 1;
		assertNull(port.requestJob("Coimbra", "Porto", 10));
		
    }
    @Test
    public void testRequestJob7() throws Exception{
    	TransporterPort.transport = "UpaTransporter2";
    	TransporterPort.transporter = 2;
		assertNull(port.requestJob("Coimbra", "Faro", 10));
		
    }
    @Test
    public void testRequestJob8() throws Exception{
    	TransporterPort.transport = "UpaTransporter1";
    	TransporterPort.transporter = 1;
		assertNull(port.requestJob("Faro", "Porto", 10));
		
    }
    @Test
    public void testRequestJob9() throws Exception{
    	TransporterPort.transport = "UpaTransporter2";
    	TransporterPort.transporter = 2;
		assertNull(port.requestJob("Faro", "Porto", 10));
		
    }
    @Test
    public void testRequestJob10() throws Exception{
    	TransporterPort.transport = "UpaTransporter1";
    	TransporterPort.transporter = 1;
		assertNull(port.requestJob("Porto", "Faro", 10));
		
    }
    @Test
    public void testRequestJob11() throws Exception{
    	TransporterPort.transport = "UpaTransporter2";
    	TransporterPort.transporter = 2;
		assertNull(port.requestJob("Porto", "Faro", 10));
		
    }
    @Test
    public void testRequestJob12() throws Exception{
    	int price = port.requestJob("Lisboa", "Coimbra", 10).getJobPrice();
    	assertTrue(price < 10);
		
    }
    @Test
    public void testRequestJob13() throws Exception{
    	TransporterPort.transport = "UpaTransporter1";
    	TransporterPort.transporter = 1;
    	int price = port.requestJob("Lisboa", "Coimbra", 35).getJobPrice();
    	assertTrue(price < 35);
		
    }
    @Test
    public void testRequestJob14() throws Exception{
    	TransporterPort.transport = "UpaTransporter2";
    	TransporterPort.transporter = 2;
    	int price = port.requestJob("Lisboa", "Coimbra", 35).getJobPrice();
    	assertTrue(price > 35);
		
    }
    @Test
    public void testRequestJob15() throws Exception{
    	TransporterPort.transport = "UpaTransporter1";
    	TransporterPort.transporter = 1;
    	int price = port.requestJob("Lisboa", "Coimbra", 30).getJobPrice();
    	assertTrue(price > 30);
		
    }
    @Test
    public void testRequestJob16() throws Exception{
    	TransporterPort.transport = "UpaTransporter2";
    	TransporterPort.transporter = 2;
    	int price = port.requestJob("Lisboa", "Coimbra", 30).getJobPrice();
    	assertTrue(price < 30);
		
    }
    
    @Test
    public void testRequestJob17() throws Exception{
    	TransporterPort.transporter = 1;
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
    	TransporterPort.transporter = 1;
    	port.requestJob("Lisboa", "Coimbra", 20);
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
		
    }
    
   
}