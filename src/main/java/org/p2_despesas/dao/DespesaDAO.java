package org.p2_despesas.dao;

import org.p2_despesas.Main;
import org.p2_despesas.model.Despesa;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DespesaDAO {

    public void salvarDespesa(Despesa despesa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Se o ID for nulo, o persist faz INSERT.
            // Se tiver ID, o merge faz UPDATE se necessário (mas para garantir insert usamos persist em novos)
            if (despesa.getId() == null) {
                em.persist(despesa);
            } else {
                em.merge(despesa); // Atualiza se já existir
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e; // Relança para o Controller tratar
        } finally {
            em.close();
        }
    }

    public void excluirDespesa(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            // Primeiro busca o objeto para garantir que ele está gerenciado
            Despesa despesa = em.find(Despesa.class, id);
            if (despesa != null) {
                em.remove(despesa);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Despesa> listarDespesas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL: Seleciona o objeto Despesa, não a tabela
            String jpql = "SELECT d FROM Despesa d";
            return em.createQuery(jpql, Despesa.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Método extra para o exercício (calcular total) usando JPQL
    public double calcularTotalDespesas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT SUM(d.valor) FROM Despesa d";
            Double total = em.createQuery(jpql, Double.class).getSingleResult();
            return total != null ? total : 0.0;
        } finally {
            em.close();
        }
    }
}