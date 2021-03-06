package pt.upa.broker;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
//classes generated from WSDL
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;

public class BrokerClientApplication {

	private static Scanner scanner;

	public static void main(String[] args) throws Exception {
		//System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClientApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		
		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		UDDINaming uddiNaming = new UDDINaming(uddiURL);

		System.out.printf("Looking for '%s'%n", name);
		String endpointAddress = uddiNaming.lookup(name);

		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");
		BrokerService service = new BrokerService();
		BrokerPortType port = service.getBrokerPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		
		scanner = new Scanner(System.in);
		
		while(true){
			try {
				System.out.println("select option:\nrequest transport\nview transport\nlist transports\nclear transports\n");
				String option = scanner.nextLine();
				uddiNaming = new UDDINaming(uddiURL);

				System.out.printf("Looking for '%s'%n", name);
				endpointAddress = uddiNaming.lookup(name);

				if (endpointAddress == null) {
					System.out.println("Not found!");
					return;
				} else {
					System.out.printf("Found %s%n", endpointAddress);
				}

				System.out.println("Creating stub ...");
				service = new BrokerService();
				port = service.getBrokerPort();

				System.out.println("Setting endpoint address ...");
				bindingProvider = (BindingProvider) port;
				requestContext = bindingProvider.getRequestContext();
				requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
				if(option.equals("request transport")){
					System.out.println("select origin");
					String origin = scanner.nextLine();
					System.out.println("select destination");
					String destination = scanner.nextLine();
					System.out.println("select price");
					int price = scanner.nextInt();
					String result2 = port.requestTransport(origin, destination, price);
					String x = "ID: ";
					x = x.concat(result2);
					System.out.println(x);
				}
				else if (option.equals("ping")){
					String result = port.ping("kill");
					System.out.println(result);
				}
				else if (option.equals("view transport")){
					System.out.println("select id");
					String id = scanner.nextLine();
					TransportView result = port.viewTransport(id);
					System.out.println(result.getState());
				}
				else if (option.equals("list transports")){
					List<TransportView> result = port.listTransports();
					String listafinal = "[";
					for(int i=0; i<result.size(); i++){
						listafinal = listafinal.concat(result.get(i).getState().toString());
						listafinal = listafinal.concat(", ");
					}
					listafinal = listafinal.concat("]");
					System.out.println(listafinal);
				}
				else if (option.equals("clear transports")){
					port.clearTransports();
				}
				else{
					System.out.println("-------------------------");
				}
				

			} catch (InvalidPriceFault_Exception pfe) {
				System.out.println("Caught: " + pfe);
			} catch (UnavailableTransportFault_Exception pfe) {
				System.out.println("Caught: " + pfe);
			} catch (UnavailableTransportPriceFault_Exception pfe) {
				System.out.println("Caught: " + pfe);
			} catch (UnknownLocationFault_Exception pfe) {
				System.out.println("Caught: " + pfe);
			} catch (Exception pfe){
				
			}
		}
	}

}
