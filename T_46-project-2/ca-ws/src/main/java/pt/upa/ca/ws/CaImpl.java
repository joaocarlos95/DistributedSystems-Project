package pt.upa.ca.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.Ca")
public class CaImpl implements Ca {

	public String sayHello(String name) {
		return "Hello " + name + "!";
	}
	
	public byte[] getCertificates(String nome){

		final String CERTIFICATE_QWERTy = "keys/" + nome + ".cer";
		Certificate certificate = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutput out = null;
				
		try {
			certificate = readCertificateFile(CERTIFICATE_QWERTy);
			out = new ObjectOutputStream(baos);
			out.writeObject(certificate);
			byte[] cert = baos.toByteArray();
			return cert;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	public static Certificate readCertificateFile(String certificateFilePath) throws Exception {
		FileInputStream fis;

		try {
			fis = new FileInputStream(certificateFilePath);
		} catch (FileNotFoundException e) {
			System.err.println("Certificate file <" + certificateFilePath + "> not fount.");
			return null;
		}
		BufferedInputStream bis = new BufferedInputStream(fis);

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		if (bis.available() > 0) {
			Certificate cert = cf.generateCertificate(bis);
			return cert;
			// It is possible to print the content of the certificate file:
			// System.out.println(cert.toString());
		}
		bis.close();
		fis.close();
		return null;
	}

}
