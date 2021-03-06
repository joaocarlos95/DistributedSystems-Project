package pt.upa.transporter;

import java.util.HashMap;

import javax.xml.ws.Endpoint;

import example.ws.handler.SignatureHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPort;

public class TransporterApplication {

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
	      
	      TransporterPort.regioes = regioes;
	      
		
		System.out.println(TransporterApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 4) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		TransporterPort.transporter = Integer.parseInt(args[3].trim());
		TransporterPort.transport = name;
		TransporterPort.TOKEN = name;
		SignatureHandler.SENDER_NAME = name;
		//System.out.println(SignatureHandler.SENDER_NAME + "llllllllsafdasssssssssssssssssssssssss");

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		try {
			TransporterPort port = new TransporterPort();
			endpoint = Endpoint.create(port);

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);

			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", url);
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
