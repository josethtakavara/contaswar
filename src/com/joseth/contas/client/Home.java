package com.joseth.contas.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Home implements EntryPoint   
{
	//CellList<String> classificacaoList = new CellList<String>(new TextCell());
	//ListDataProvider<String> classificacaoDataProvider = new ListDataProvider<String>();
	
	public static ServiceBusAsync serviceBus;
	
	@Override
	public void onModuleLoad() 
	{
		serviceBus = GWT.create(ServiceBus.class);
		BarraBotoes barraBotoes = new BarraBotoes();
		RootPanel.get("barraBotoes").add(barraBotoes);
		GWT.setUncaughtExceptionHandler(
	        new GWT.UncaughtExceptionHandler() 
	        {
	            public void onUncaughtException(Throwable e) 
	            {
	                e = unwrap(e);
	                String msg=e.getMessage()+"\n";
	                for( StackTraceElement s: e.getStackTrace())
	                    msg += s.toString()+"\n";
                    Window.alert(msg);
	            }
	        }
        );
	}
	
	public Throwable unwrap(Throwable e) 
	{
	    if(e instanceof UmbrellaException) 
	    {
	      UmbrellaException ue = (UmbrellaException) e;
	      if(ue.getCauses().size() == 1) {
	        return unwrap(ue.getCauses().iterator().next());
	      }
	    }
	    return e;
	}
}