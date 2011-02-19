package de.mobile.olaf.tutorial;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class Application {
	
	public static void main(String[] args) {
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
		String expression = "select avg(price) from de.mobile.olaf.tutorial.OrderEvent.win:time(30 sec)";
		EPStatement statement = epService.getEPAdministrator().createEPL(expression);
		MyListener listener = new MyListener();
		statement.addListener(listener);

		OrderEvent event = new OrderEvent("shirt", 74.50);
		epService.getEPRuntime().sendEvent(event);


	}

}
