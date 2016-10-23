package com.joseth.contas.client.movimentos;

import java.util.Collection;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.client.Home;
import com.joseth.contas.client.filtro.FiltroMovimentos;


public class FiltroMovimentosLista extends FiltroMovimentos
{  
    Movimentos movimentos;

    public void filtrar()
	{ 
	    super.filtrar();
        Home.serviceBus.getMovimentos( 
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
                
                new AsyncCallback<Collection<Movimento>>()
                {
                    public void onFailure(Throwable caught){}
                    public void onSuccess(Collection<Movimento> result)
                    {       
                        movimentos.movimentosDataProvider.getList().clear();
                        movimentos.movimentosDataProvider.getList().addAll(result);
                        ColumnSortEvent.fire(movimentos.movimentos, movimentos.movimentos.getColumnSortList());
                    }
                }
            );
	}
	
	public FiltroMovimentosLista(Movimentos movimentos) 
	{
        this.movimentos = movimentos;
	}
}