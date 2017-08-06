package com.joseth.contas.web;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContasBootstrap implements javax.servlet.ServletContextListener
{
    private static EntityManagerFactory emf;
//    private static EntityManager em;
    
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        sce.getServletContext().log("Contas Destruindo");
//        em.close(); 
        emf.close();
        sce.getServletContext().log("Contas Destruido ok");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        sce.getServletContext().log("Contas Inicializando ");
        emf = Persistence.createEntityManagerFactory("primary");
//        em = emf.createEntityManager();
        sce.getServletContext().log("Contas Inicializado ok ");
    }
    
    public static EntityManagerFactory getEntityManagerFactory(){return emf;}
}
