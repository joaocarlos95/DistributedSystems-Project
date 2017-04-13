package pt.upa.broker;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;

import example.ws.handler.SignatureHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class BrokerApplication {
	
	public static int vida = 1;

	public static void main(String[] args) throws Exception {
		
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
	      
	      BrokerPort.regioes = regioes;
		
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		String name2 = "UpaTransporter%";
		SignatureHandler.SENDER_NAME = "UpaBroker1";
		BrokerPort.TOKEN = "UpaBroker1";
		
		Thread t = null;
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		
		try {
			
			BrokerPort port = new BrokerPort();
			endpoint = Endpoint.create(port);

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);
			BrokerPort.uddiNaming= uddiNaming;
			
			System.out.println("Creating stub ...");
			TransporterService service1 = new TransporterService();
			TransporterPortType port3 = service1.getTransporterPort();
			//1
			BrokerPort.port2 = port3;
			
			System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port3;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			//2
			BrokerPort.requestContext = requestContext;
			
			System.out.printf("Looking for '%s'%n", name2);
			Collection<String> x = uddiNaming.list(name2);
			BrokerPort.x = x;
			
			if(name.equals("UpaBroker1")){
				
				System.out.printf("Looking for '%s'%n", "UpaBroker2");
				String endpointAddress = uddiNaming.lookup("UpaBroker2");

				if (endpointAddress == null) {
					System.out.println("Not found!");
					return;
				} else {
					System.out.printf("Found %s%n", endpointAddress);
				}
				System.out.println("Creating stub ...");
				BrokerService service = new BrokerService();
				BrokerPortType port2 = service.getBrokerPort();

				System.out.println("Setting endpoint address ...");
				BindingProvider bindingProvider2 = (BindingProvider) port2;
				Map<String, Object> requestContext2 = bindingProvider2.getRequestContext();
				requestContext2.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
				BrokerPort.port = port2;
				t = new Thread(new Runnable() {
		            @Override
		            public void run() {
		                while(true) {
		                    System.out.println("Thread is doing something");
		                    try {
		                    	port2.provaDeVida(name);
								Thread.sleep(2500);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								return;
							}
		                }
		            }
		        });
				t.start();
			}
			else{
				while(true) {
					if(vida == 1){
						vida = 0;
						Thread.sleep(5000);
					}
					else{
						break;
					}
				}
				// publish to UDDI
				System.out.printf("Publishing '%s' to UDDI at %s%n", "UpaBroker1", uddiURL);
				uddiNaming = new UDDINaming(uddiURL);
				uddiNaming.rebind("UpaBroker1", url);
				BrokerPort.uddiNaming= uddiNaming;
			}
			
			
			
			for(int i = 0 ; i < x.size() ; i++){
				String endpointAddress = ( String ) x.toArray()[i];
				requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
				requestContext.put(SignatureHandler.REQUEST_PROPERTY, BrokerPort.TOKEN);
				System.out.println(port3.ping("friend"));
	            System.out.println(endpointAddress);
	        }

			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			//e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", url);
					if(name.equals("UpaBroker1")){
						t.interrupt();
					}
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					System.out.printf("Deleted '%s' from UDDI%n", name);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}
	}

	
	
	
	
	
	
}
