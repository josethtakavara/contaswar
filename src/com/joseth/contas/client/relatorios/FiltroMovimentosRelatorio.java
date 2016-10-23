package com.joseth.contas.client.relatorios;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joseth.contas.client.Home;
import com.joseth.contas.client.filtro.FiltroMovimentos;


public class FiltroMovimentosRelatorio extends FiltroMovimentos
{  
    RelatorioPeriodo re;
	public void filtrar()
	{
	    super.filtrar();
        Home.serviceBus.relatorioPeriodo( 
                subMovimentos.getValue(),
                (movimentoPai==null?null:""+movimentoPai.getId()),
                filtroMesAnoDe,filtroMesAnoAte,
                usuario.getSelectedIndex() >= 0 ? usuario.getValue(usuario.getSelectedIndex()):"-1",
                conta.getSelectedIndex() >= 0 ? conta.getValue(conta.getSelectedIndex()):"-1",
                classFiltro,
                descricao.getText(),comentario.getText(),
                val,
                pessoal.getValue(),
                semComentarios.getValue(),semClassificacoes.getValue(),sinalMais.getValue(),sinalMenos.getValue(),
                
                new AsyncCallback<List>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(List result)
                    {       
                        re.resultado(result);
                    }
                }
            );
	}
	
	public FiltroMovimentosRelatorio(RelatorioPeriodo re) 
	{
	    this.re = re;
	}
}