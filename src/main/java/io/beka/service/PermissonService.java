package io.beka.service;
import io.beka.mapper.PermissionsMapper;
import io.beka.model.Page;
import io.beka.model.entity.Permissions;
import io.beka.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PermissonService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PermissionRepository permissionRepository;
    private final PermissionsMapper permissionsMapper;

    public List<Permissions> findAll() {
        return permissionRepository.findAll();
    }

    public Optional<Permissions> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public Permissions save(Permissions permissions) {
        return permissionRepository.save(permissions);
    }

    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
    //My Batis
    public List<Permissions> findAllViaMapper(Page page){
        return permissionsMapper.findAllWithPaging(page);
    }


    //custom JPA query
    public List<Permissions> findAllNativeByCrudTableAndActive(String curdTable, Boolean status) {
        Query query = this.entityManager.createNativeQuery("select * from permissions p where p.crud_table = :tableName AND p.status=:status ", Permissions.class);
        query.setParameter("tableName", curdTable);
        query.setParameter("status", status);
        return query.getResultList();
    }
    public List<Permissions> findAllNativePaging(Page page) {
        Query query = this.entityManager.createNativeQuery("select * from permissions p limit :offset, :limit ", Permissions.class);
        query.setParameter("offset", page.getOffset());
        query.setParameter("limit", page.getLimit());
        System.out.println("getResultList : "+query.getResultList().size());
        return query.getResultList();
    }

    public List<Object[]> findAllNativeByCustomObject() {
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
