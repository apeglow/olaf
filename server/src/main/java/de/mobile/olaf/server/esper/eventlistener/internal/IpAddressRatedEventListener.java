package de.mobile.olaf.server.esper.eventlistener.internal;

import java.util.HashSet;
import java.util.Set;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import de.mobile.olaf.server.OlafWebsocketServer;
import de.mobile.olaf.server.communication.out.PartnersNotificationService;
import de.mobile.olaf.server.communication.out.websocket.WebsocketMessage;
import de.mobile.olaf.server.domain.RatedIpAddress;


/**
 * Listens to the event "ip address rated" and calls the partners notification service.
 * 
 * @author andre
 * 
 */
public class IpAddressRatedEventListener implements UpdateListener {

    /**
     * Register at the service provider.
     * 
     * @param epServiceProvider
     * @param partnersNotificationService
     */
    public static void register(EPServiceProvider epServiceProvider,
        PartnersNotificationService partnersNotificationService, OlafWebsocketServer websocketServer) {
        String query = "select * from " + RatedIpAddress.class.getName();
        EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(query);
        statement.addListener(new IpAddressRatedEventListener(partnersNotificationService, websocketServer));
    }

    private final PartnersNotificationService notificationService;

    private final OlafWebsocketServer websocketServer;

    private IpAddressRatedEventListener(PartnersNotificationService notificationService,
            OlafWebsocketServer websocketServer) {
        this.notificationService = notificationService;
        this.websocketServer = websocketServer;
    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        Set<RatedIpAddress> changedIpStatuses = new HashSet<RatedIpAddress>();

        String wsBody = "";

        for (EventBean eventBean : newEvents) {
            RatedIpAddress ipStatus = (RatedIpAddress) eventBean.getUnderlying();
            changedIpStatuses.add(ipStatus);
            wsBody = ipStatus.getAddress() + " - " + ipStatus.getStatus().name() + ",";
        }

        notificationService.ipStatusChanged(changedIpStatuses);

        WebsocketMessage wms = new WebsocketMessage();
        wms.setType("IP-RATING");
        wms.setHeader("IP-Rating changed");
        wms.setBody(wsBody);
        websocketServer.addEventMessage(wms);
    }

}
