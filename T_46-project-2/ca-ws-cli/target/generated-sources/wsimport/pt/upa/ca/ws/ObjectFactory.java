
package pt.upa.ca.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the pt.upa.ca.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SayHelloResponse_QNAME = new QName("http://ws.ca.upa.pt/", "sayHelloResponse");
    private final static QName _GetCertificates_QNAME = new QName("http://ws.ca.upa.pt/", "getCertificates");
    private final static QName _SayHello_QNAME = new QName("http://ws.ca.upa.pt/", "sayHello");
    private final static QName _GetCertificatesResponse_QNAME = new QName("http://ws.ca.upa.pt/", "getCertificatesResponse");
    private final static QName _GetCertificatesResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pt.upa.ca.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SayHello }
     * 
     */
    public SayHello createSayHello() {
        return new SayHello();
    }

    /**
     * Create an instance of {@link GetCertificatesResponse }
     * 
     */
    public GetCertificatesResponse createGetCertificatesResponse() {
        return new GetCertificatesResponse();
    }

    /**
     * Create an instance of {@link GetCertificates }
     * 
     */
    public GetCertificates createGetCertificates() {
        return new GetCertificates();
    }

    /**
     * Create an instance of {@link SayHelloResponse }
     * 
     */
    public SayHelloResponse createSayHelloResponse() {
        return new SayHelloResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SayHelloResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ca.upa.pt/", name = "sayHelloResponse")
    public JAXBElement<SayHelloResponse> createSayHelloResponse(SayHelloResponse value) {
        return new JAXBElement<SayHelloResponse>(_SayHelloResponse_QNAME, SayHelloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCertificates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ca.upa.pt/", name = "getCertificates")
    public JAXBElement<GetCertificates> createGetCertificates(GetCertificates value) {
        return new JAXBElement<GetCertificates>(_GetCertificates_QNAME, GetCertificates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SayHello }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ca.upa.pt/", name = "sayHello")
    public JAXBElement<SayHello> createSayHello(SayHello value) {
        return new JAXBElement<SayHello>(_SayHello_QNAME, SayHello.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCertificatesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ca.upa.pt/", name = "getCertificatesResponse")
    public JAXBElement<GetCertificatesResponse> createGetCertificatesResponse(GetCertificatesResponse value) {
        return new JAXBElement<GetCertificatesResponse>(_GetCertificatesResponse_QNAME, GetCertificatesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = GetCertificatesResponse.class)
    public JAXBElement<byte[]> createGetCertificatesResponseReturn(byte[] value) {
        return new JAXBElement<byte[]>(_GetCertificatesResponseReturn_QNAME, byte[].class, GetCertificatesResponse.class, ((byte[]) value));
    }

}
