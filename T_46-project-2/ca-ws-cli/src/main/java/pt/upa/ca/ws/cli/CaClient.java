package pt.upa.ca.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.security.cert.Certificate;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
// classes generated from WSDL
import pt.upa.ca.ws.Ca;
import pt.upa.ca.ws.CaImplService;



public class CaClient {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", CaClient.class.getName());
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
		CaImplService service = new CaImplService();
		Ca port = service.getCaImplPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

		System.out.println("Remote call ...");
		String result = port.sayHello("friend");
		byte[] result1 = port.getCertificates("UpaBroker");
		System.out.println(result1);
		ByteArrayInputStream bis = new ByteArrayInputStream(result1);
		ObjectInput in = null;
		in = new ObjectInputStream(bis);
		Certificate certificate = (Certificate) in.readObject();
		System.out.println(certificate);
		
		// wait
		System.out.println("Awaiting connections");
		System.out.println("Press enter to shutdown");
		System.in.read();
	}

}
