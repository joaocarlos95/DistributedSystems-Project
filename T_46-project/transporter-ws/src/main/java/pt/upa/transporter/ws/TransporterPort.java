package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(
		endpointInterface="pt.upa.transporter.ws.TransporterPortType",
		wsdlLocation="transporter.1_0.wsdl",
		name="Transporter",
		portName="TransporterPort",
		targetNamespace="http://ws.transporter.upa.pt/",
		serviceName="TransporterService"
	)
public class TransporterPort implements TransporterPortType{
	
	static public HashMap<String, String> regioes;
	static public int transporter;
	static public String transport;
	public int var = 0;
	public JobView job;
	public Timer timer;
	public List<JobView> listJob = new ArrayList<JobView>();
	
	@Override
	public String ping(String name) {
		System.out.println(" ping...");
		return " ping...daqui transporter\n" + name + "!";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		int valor = 0;
		JobView job = new JobView();
		job.setCompanyName(transport);
		job.setJobOrigin(origin);
		job.setJobDestination(destination);
		job.setJobIdentifier(Integer.toString(var*10+transporter));
		var++;
		job.setJobPrice(valor);
		job.setJobState(JobStateView.PROPOSED);
		if(price <= 0){
			BadPriceFault faultInfo = new BadPriceFault();
			faultInfo.setPrice(price);
			throw new BadPriceFault_Exception("error in server", faultInfo);
		}
		else if(regioes.containsKey(origin) == false){
			BadLocationFault faultInfo = new BadLocationFault();
			faultInfo.setLocation(origin);
			throw new BadLocationFault_Exception("error in server", faultInfo);
		}
		else if(regioes.containsKey(destination) == false){
			BadLocationFault faultInfo = new BadLocationFault();
			faultInfo.setLocation(destination);
			throw new BadLocationFault_Exception("error in server", faultInfo);
		}
		else if((regioes.get(origin) == "RegiaoNorte" && transporter%2 == 1) || 
				(regioes.get(origin) == "RegiaoSul" && transporter%2 == 0) ||
				(regioes.get(destination) == "RegiaoNorte" && transporter%2 == 1) || 
				(regioes.get(destination) == "RegiaoSul" && transporter%2 == 0)){
			listJob.add(job);
			return null;
		}
		else{
			if(price > 100){
				listJob.add(job);
				return null;
			}
			else if(price <= 10){
				valor = ThreadLocalRandom.current().nextInt(0, price-1);
			}
			else{
				if(price%2==1){
					if(transporter%2 == 1){
						valor = ThreadLocalRandom.current().nextInt(0, price-1);
					}
					else{
						valor = ThreadLocalRandom.current().nextInt(price+1,102);
					}
				}
				else{
					if(transporter%2 == 0){
						valor = ThreadLocalRandom.current().nextInt(0, price-1);
					}
					else{
						valor = ThreadLocalRandom.current().nextInt(price+1,102);
					}
				}	
			}
			job.setJobPrice(valor);
			listJob.add(job);
		}
		
		return job;
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		for(int i=0; i<listJob.size(); i++){
			if(listJob.get(i).getJobIdentifier().equals(id)){
				if(accept == true){
					listJob.get(i).setJobState(JobStateView.ACCEPTED);
					Timer timer1 = new Timer();
					Timer timer2 = new Timer();
					Timer timer3 = new Timer();
					int dur1= ThreadLocalRandom.current().nextInt(1000, 5000);
					int dur2= dur1 + ThreadLocalRandom.current().nextInt(1000, 5000);
					int dur3= dur2 + ThreadLocalRandom.current().nextInt(1000, 5000);
					timer1.schedule(new MyTimerTask(timer1, listJob.get(i)), dur1);
					timer2.schedule(new MyTimerTask(timer2, listJob.get(i)), dur2);
					timer3.schedule(new MyTimerTask(timer3, listJob.get(i)), dur3);
				}
				else{
					listJob.get(i).setJobState(JobStateView.REJECTED);
				}
				System.out.println(listJob.get(i).getJobState());
				return listJob.get(i);
			}
		}
		return null;
	}

	@Override
	public JobView jobStatus(String id) {
		for(int i=0; i<listJob.size(); i++){
			if(listJob.get(i).getJobIdentifier().equals(id)){
				return listJob.get(i);
			}
		}
		return null;
	}

	@Override
	public List<JobView> listJobs() {
		return listJob;
	}

	@Override
	public void clearJobs() {
		listJob.clear();
		this.var = 0;
	}
	
	class MyTimerTask extends TimerTask {
		private JobView job;
		private Timer timer;
		
		public MyTimerTask(Timer timer, JobView job) {
			this.job=job;
			this.timer=timer;
			
		}

		@Override
		public void run() {
				if(job.getJobState() == JobStateView.ACCEPTED){
					job.setJobState(JobStateView.HEADING);
					System.out.println(job.getJobState());
					timer.cancel();
					return;
				}
				else if(job.getJobState() == JobStateView.HEADING){
					job.setJobState(JobStateView.ONGOING);
					System.out.println(job.getJobState());
					timer.cancel();
					return;
				}
				else if(job.getJobState() == JobStateView.ONGOING){
					job.setJobState(JobStateView.COMPLETED);
					System.out.println(job.getJobState());
					timer.cancel();
					return;
				}
				else{
					timer.cancel();
				}
		}
	}
}
