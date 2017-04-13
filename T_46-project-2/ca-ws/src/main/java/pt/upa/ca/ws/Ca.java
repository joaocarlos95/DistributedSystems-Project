package pt.upa.ca.ws;

import java.security.cert.Certificate;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface Ca {
	
	@WebMethod
	String sayHello(String name);
	@WebMethod
	byte[] getCertificates(String nome);
}
