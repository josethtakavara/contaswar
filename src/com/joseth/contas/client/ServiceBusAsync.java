package com.joseth.contas.client; 

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.joseth.contas.beans.BlackDescricao;
import com.joseth.contas.beans.BlackString;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Saldo;
import com.joseth.contas.beans.Template;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.client.resumo.ResumoDTO;

public interface ServiceBusAsync 
{
	public void getClassificacoes(AsyncCallback<List<Classificacao>> callback);
	public void getClassificacoesContagem(AsyncCallback<List<Map>> callback);
	public void getUsuarios(AsyncCallback<List<Usuario>> callback);
	public void getContas(AsyncCallback<List<Conta>> callback);
	public void getTemplates(AsyncCallback<List<Template>> callback);
	public void getBlackDescricoes(AsyncCallback<List<BlackDescricao>> callback);
	public void getBlackStrings(AsyncCallback<List<BlackString>> callback);
	public void getSaldos(String contaId, AsyncCallback<List<Saldo>> callback);
	public void getAnoMesSoma(String contaId, String ano, AsyncCallback<List> callback);
	public void getMeses(AsyncCallback<List<String>> callback);
	public void getAnos(AsyncCallback<List<Integer>> callback);
	public void getMesesAnos(AsyncCallback<List<String>> callback);
	public void getMesesAnos(Conta c,AsyncCallback<List<String>> callback);
	
	public void getMovimentos(
			Boolean subMovimentos,
			String movimentoPaiId,
			String filtroDe, String filtroAte,
			String usuario, String conta,
			Collection<String> classificacoes, 
			String descricao, String comentario,  Double valor,
			Boolean pessoal,
			Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos,
			AsyncCallback<Collection<Movimento>> callback);
	
	public void getArquivoMovimentos(String pass, Boolean inverter, Conta conta, Movimento pai, AsyncCallback<List[]> callback);
	public void getTextoMovimentos(String texto, Boolean inverter, Conta conta, Movimento pai, AsyncCallback<List[]> callback);
	public void getTextoMovimentosTemplate(String template, String texto, Boolean inverter, Conta conta, Movimento pai, AsyncCallback<List[]> callback);
	public void getResumo(AsyncCallback<ResumoDTO> callback);
	
	public void atualizarMovimento(Movimento m, AsyncCallback<Movimento> callback);
	public void removerMovimento(Movimento m, AsyncCallback<Void> callback);
	
	public void atualizarTemplate(Template t, AsyncCallback<Template> callback);
	public void removerTemplate(Template t, AsyncCallback<Void> callback);

	public void atualizarBlackDescricao(BlackDescricao t, AsyncCallback<BlackDescricao> callback);
    public void removerBlackDescricao(BlackDescricao t, AsyncCallback<Void> callback);

    public void atualizarBlackString(BlackString t, AsyncCallback<BlackString> callback);
    public void removerBlackString(BlackString t, AsyncCallback<Void> callback);
	    
//	public void update(Classificacao c,AsyncCallback<Void> callback);
	public void atualizar(Collection<Movimento> mvs, AsyncCallback<Void> callback );
	
	public void atualizarSaldo(Saldo s, AsyncCallback<Void> callback);
	
	public void relatorioPeriodo(
            Boolean subMovimentos,
            String movimentoPaiId,
            String filtroMesAnoDe, String filtroMesAnoAte,
            String usuarioId, String contaId,
            Collection<String> classificacoesId, 
            String descricao, String comentario,  Double valor,
            Boolean pessoal,
            Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos, AsyncCallback<List> callback );
    
	void atualizar(Classificacao c, AsyncCallback<Classificacao> callback);
    void apagar(Classificacao c, AsyncCallback<Void> callback);	
}
