package com.joseth.contas.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.joseth.contas.beans.BlackDescricao;
import com.joseth.contas.beans.BlackString;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Saldo;
import com.joseth.contas.beans.Template;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.client.resumo.ResumoDTO;

@RemoteServiceRelativePath("serviceBus") 
public interface ServiceBus extends RemoteService
{
	public List<Classificacao> getClassificacoes();
	public List<Usuario> getUsuarios();
	public List<Conta> getContas();
	public List<Template> getTemplates();
	public List<BlackDescricao> getBlackDescricoes();
	public List<BlackString> getBlackStrings();
	public List<Saldo> getSaldos(String contaId);
	public List<Map<String,String>> getAnoMesSoma(String contaId, String ano);
	
	public List<String> getMeses();
	public List<Integer> getAnos();
	
	public List<String> getMesesAnos();
	public List<String> getMesesAnos(Conta c);
	
	public Collection<Movimento> getMovimentos(
			Boolean subMovimentos ,
			String movimentoPaiId,
			String filtroDe, String filtroAte,
			String usuario, String conta,
			Collection<String> classificacoes, 
			String descricao, String comentario,  Double valor,
			Boolean pessoal,
			Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos
			);
	
	public List<Object>[] getArquivoMovimentos(String pass, Boolean inverter, Conta conta, Movimento pai);
	public List<Object>[] getTextoMovimentos(String texto, Boolean inverter, Conta conta, Movimento pai );
	public List<Object>[] getTextoMovimentosTemplate(String template, String texto, Boolean inverter, Conta conta, Movimento pai );
	public ResumoDTO getResumo(); 
	
	Classificacao atualizar(Classificacao c);
	public void apagar(Classificacao c);
	
	public Movimento atualizarMovimento(Movimento m);
	public void atualizar(Collection<Movimento> mvs);
	public void removerMovimento(Movimento m);
	
	public Template atualizarTemplate(Template t);
	public void removerTemplate(Template t);
	
	public BlackDescricao atualizarBlackDescricao(BlackDescricao t);
	public void removerBlackDescricao(BlackDescricao t);
	
	public BlackString atualizarBlackString(BlackString t);
    public void removerBlackString(BlackString t);
	
	public void atualizarSaldo( Saldo s);

	public List<Object> relatorioPeriodo(
            Boolean subMovimentos,
            String movimentoPaiId,
            String filtroMesAnoDe, String filtroMesAnoAte,
            String usuarioId, String contaId,
            Collection<String> classificacoesId, 
            String descricao, String comentario,  Double valor,
            Boolean pessoal,
            Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos);
    List<Map> getClassificacoesContagem();	
}
