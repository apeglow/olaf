package de.mobile.olaf.server.communication.out.udp;

import de.mobile.olaf.server.communication.out.PartnerNotifier;
import de.mobile.olaf.server.domain.PartnerSite;

public class UdpPartnerNotifier implements PartnerNotifier {
	
	private final PartnerSite site;
	
	public UdpPartnerNotifier(PartnerSite site){
		this.site = site;
	}
	
	@Override
	public PartnerSite getSite() {
		return site;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
