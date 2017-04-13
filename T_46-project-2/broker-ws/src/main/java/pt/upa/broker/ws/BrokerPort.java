package pt.upa.broker.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import example.ws.handler.SignatureHandler;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.BrokerApplication;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;

@WebService(
	endpointInterface="pt.upa.broker.ws.BrokerPortType",
	wsdlLocation="broker.1_0.wsdl",
	name="Broker",
	portName="BrokerPort",
	targetNamespace="http://ws.broker.upa.pt/",
	serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType{

	static public TransporterPortType port2;
	static public UDDINaming uddiNaming;
	static public Map<String, Object> requestContext;
	static public HashMap<String, String> regioes;
	public static int id2 = 0;
	static public Collection<String> x;
	public static List<TransportView> listTransporters = new ArrayList<TransportView>();
	public static HashMap<String, String> ids = new HashMap<String, String>();
	public static BrokerPortType port = null;
	public static String TOKEN;
	
	@Override
	public String ping(String name) {
		if(name.equals("kill")){
			System.exit(0);
		}
		String text = null;
		for(int i = 0 ; i < x.size() ; i++){
			String endpointAddress = ( String ) x.toArray()[i];
			SignatureHandler.SENDER_NAME = TOKEN;
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
			requestContext.put(SignatureHandler.REQUEST_PROPERTY, TOKEN);
			text = port2.ping(name);
			System.out.println(text);
            System.out.println(endpointAddress);
        }
		return text;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		Map<String, HashMap<String, Integer>> precos = new HashMap<String, HashMap<String, Integer>>();
		String id = null;
		if(price <= 0){
			InvalidPriceFault faultInfo = new InvalidPriceFault();
			faultInfo.setPrice(price);
			throw new InvalidPriceFault_Exception("error in server", faultInfo);
		}
		else if(regioes.containsKey(origin) == false){
			UnknownLocationFault faultInfo = new UnknownLocationFault();
			faultInfo.setLocation(origin);
			throw new UnknownLocationFault_Exception("error in server", faultInfo);
		}
		else if(regioes.containsKey(destination) == false){
			UnknownLocationFault faultInfo = new UnknownLocationFault();
			faultInfo.setLocation(destination);
			throw new UnknownLocationFault_Exception("error in server", faultInfo);
		}
		else if((regioes.get(origin) == "RegiaoNorte" && regioes.get(destination) == "RegiaoSul") ||
				(regioes.get(origin) == "RegiaoSul" && regioes.get(destination) == "RegiaoNorte")){
			UnavailableTransportFault faultInfo = new UnavailableTransportFault();
			faultInfo.setDestination(destination);
			throw new UnavailableTransportFault_Exception("error in server", faultInfo);
		}
		else if(price > 100){
			UnavailableTransportFault faultInfo = new UnavailableTransportFault();
			faultInfo.setDestination(destination);;
			throw new UnavailableTransportFault_Exception("error in server", faultInfo);
		}
		
		else{
			int var = 0;
			int lower = 100;
			try{
				TransportView tv = new TransportView();
				tv.setDestination(destination);
				tv.setOrigin(origin);
				tv.setId(String.valueOf(id2));
				tv.setPrice(price);
				tv.setState(TransportStateView.REQUESTED);
				ids.put(String.valueOf(id2), null);
				listTransporters.add(tv);
				//transport view
				for(int i = 0 ; i < x.size() ; i++){
					//String name2 = "UpaTransporter";
					String endpointAddress = ( String ) x.toArray()[i];
					requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
					requestContext.put(SignatureHandler.REQUEST_PROPERTY, TOKEN);
					JobView job = port2.requestJob(origin, destination, price);
					listTransporters.get(id2).setState(TransportStateView.BUDGETED);
					if(job != null){
						int price2 = job.getJobPrice();
						if(price2 >= price){
							var = 1;
							port2.decideJob(job.getJobIdentifier(), false);
						}
						else if(price2 < price){
							HashMap<String, Integer> idprice = new HashMap<String, Integer>();
							idprice.put(job.getJobIdentifier(), price2);
							precos.put(endpointAddress, idprice);
						}
					}
					//id2++;
				}
				if(precos.size() != 0){
					String endpoint = null;
					for (String key: precos.keySet()) {
						for(String key2: precos.get(key).keySet()){
							if(precos.get(key).get(key2) < lower){
								id = key2;
								lower = precos.get(key).get(key2);
								endpoint = key;
								listTransporters.get(id2).setPrice(lower);
								requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpoint);
								requestContext.put(SignatureHandler.REQUEST_PROPERTY, TOKEN);
							}
						}
					}
					String name2 = "UpaTransporter";
					String number = String.valueOf(endpoint.charAt(20));
					name2 = name2.concat(number);
					listTransporters.get(id2).setState(TransportStateView.BOOKED);
					ids.put(String.valueOf(id2), id);
					listTransporters.get(id2).setTransporterCompany(name2);
					port2.decideJob(id, true);
					precos.remove(endpoint);
					if(precos.size() != 0){
						for (String key: precos.keySet()) {
							for(String key2: precos.get(key).keySet()){
								String id4 = key2;
								port2.decideJob(id4, false);
							}
						}
						precos.clear();
					}
				}
				else{
					listTransporters.get(id2).setState(TransportStateView.FAILED);
				}
				
			}catch (BadLocationFault_Exception e){
				UnknownLocationFault faultInfo = new UnknownLocationFault();
				throw new UnknownLocationFault_Exception("error in server", faultInfo);
				
			}catch (BadPriceFault_Exception e) {
				UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
				faultInfo.setBestPriceFound(price);
				throw new UnavailableTransportPriceFault_Exception("error in server", faultInfo);
			} catch (BadJobFault_Exception e) {

			}
			id2++;
			if(var == 1 && lower == 100){
				UnavailableTransportPriceFault faultInfo = new UnavailableTransportPriceFault();
				faultInfo.setBestPriceFound(price);
				throw new UnavailableTransportPriceFault_Exception("error in server", faultInfo);
			}
			if(port != null){
				port.update(listTransporters.get(id2-1), id2, id);
			}
			return String.valueOf(id2-1);
		}
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		for(int i = 0 ; i < listTransporters.size() ; i++){
			if(listTransporters.get(i).getId().equals(id)){
				try {
					if (listTransporters.get(i).getState() == TransportStateView.REQUESTED ||
							listTransporters.get(i).getState() == TransportStateView.BUDGETED ||
							listTransporters.get(i).getState() == TransportStateView.FAILED){
						
					}
					else{
						String endpointAddress = uddiNaming.lookup(listTransporters.get(i).getTransporterCompany());
						requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
						requestContext.put(SignatureHandler.REQUEST_PROPERTY, TOKEN);
						JobView job = port2.jobStatus(ids.get(id));
						JobStateView state = job.getJobState();
						if(state == JobStateView.HEADING){
							listTransporters.get(i).setState(TransportStateView.HEADING);
						}
						else if(state == JobStateView.ONGOING){
							listTransporters.get(i).setState(TransportStateView.ONGOING);
						}
						else if(state == JobStateView.COMPLETED){
							listTransporters.get(i).setState(TransportStateView.COMPLETED);
						}
					}
					return listTransporters.get(i);
				} catch (JAXRException e) {
					e.printStackTrace();
				}
			}
		}
		UnknownTransportFault faultInfo = new UnknownTransportFault();
		faultInfo.setId(id);
		throw new UnknownTransportFault_Exception("error in server", faultInfo);
	}

	@Override
	public List<TransportView> listTransports() {
		return listTransporters;
	}

	@Override
	public void clearTransports() {
		for(int i = 0 ; i < x.size() ; i++){
			String endpointAddress = ( String ) x.toArray()[i];
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
			requestContext.put(SignatureHandler.REQUEST_PROPERTY, TOKEN);
			port2.clearJobs();
        }
		listTransporters.clear();
		id2 = 0;
		if(port != null){
			port.clearTransports();
		}
		
	}
	
	public String provaDeVida(String name) {
		BrokerApplication.vida = 1;
		return "Ainda estou vivo (" + name +")!";
	}
	
	public void update(TransportView tv, int id3, String id){
		listTransporters.add(tv);
		ids.put(String.valueOf(id2), id);
		id2=id3;
	}
	
}
