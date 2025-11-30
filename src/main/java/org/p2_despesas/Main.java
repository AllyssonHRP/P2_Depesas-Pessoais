package org.p2_despesas;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.p2_despesas.model.Despesa;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("un-jpa");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Despesa despesa = new Despesa();

        despesa.setDescricao("PC");
        despesa.setCategoria("Lazer");
        despesa.setData(LocalDate.of(2025,1,1));
        despesa.setValor(2300);
        em.persist(despesa);
        em.getTransaction().commit();
        em.close();

    }
}
