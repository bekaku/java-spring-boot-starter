package io.beka.repository;

import io.beka.model.entity.Permissions;
import io.beka.model.Page;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class PermissionRepositoryCustomImpl implements PermissionRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Permissions> findCustomAllByCrudTableAndActive(String curdTable, Boolean status) {
		Query query = this.entityManager.createNativeQuery("select * from permissions p where p.crud_table = :tableName AND p.status=:status ", Permissions.class);
		query.setParameter("tableName", curdTable);
		query.setParameter("status", status);
		return query.getResultList();
    }

    @Override
    public List<Permissions> findAllPaging(Page page) {
        Query query = this.entityManager.createNativeQuery("select * from permissions p limit :offset, :limit ", Permissions.class);
        query.setParameter("offset", page.getOffset());
        query.setParameter("limit", page.getLimit());
        System.out.println("getResultList : "+query.getResultList().size());
        return query.getResultList();
    }

    @Override
    public List<Object[]> findAllByCustomObject() {
        List<Object[]> list = this.entityManager.createNativeQuery("SELECT * FROM permissions p WHERE p.status=:status ")
                .setParameter("status", true) //positional parameter binding
                .getResultList();
        /*
         for (Object[] objects : list) {
            Integer id=(Integer)objects[0];
            String name=(String)objects[1];
            String designation=(String)objects[2];
            System.out.println("Employee["+id+","+name+","+designation+"]");
         }
         */
        return list;
    }
}
