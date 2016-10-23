package com.joseth.contas.client.resumo;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.NumberLabel;
import com.google.gwt.user.client.ui.Widget;
import com.joseth.contas.client.Home;

public class ResumoPainel extends Composite 
{
	interface MyUiBinder extends UiBinder<Widget, ResumoPainel> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
    @UiField Element usuariosCnt;
    @UiField Element mesDe;  
    @UiField Element mesAte;
    @UiField Element cadastradosCnt;
    @UiField Element classificadosCnt;
    @UiField Element movPerClass;
    @UiField Element tdbar;
    @UiField(provided=true) NumberLabel<Double> valor = new NumberLabel<Double>(NumberFormat.getFormat("#,###.00"));
	
	public ResumoPainel() 
	{
	    initWidget(uiBinder.createAndBindUi(this));
	    Home.serviceBus.getResumo(
            new AsyncCallback<ResumoDTO>()
            {
                public void onFailure(Throwable caught){}
                public void onSuccess(ResumoDTO r)
                {
                    usuariosCnt.setInnerText(""+r.usuariosCnt);
                    mesDe.setInnerText(r.mesDe);
                    mesAte.setInnerText(r.mesAte);
                    
                    cadastradosCnt.setInnerText(""+r.movCnt);
                    classificadosCnt.setInnerText(""+r.movClassCnt);
                    movPerClass.setInnerText(""+r.movPercentClass);
                    
                    tdbar.setAttribute("width",""+r.movPercentClass+"%" );
                    valor.setValue(r.valor);
                }
            } 
        );    
	}
	



}