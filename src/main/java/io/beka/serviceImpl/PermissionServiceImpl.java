package io.beka.serviceImpl;

import io.beka.dto.PermissionDto;
import io.beka.dto.ResponseListDto;
import io.beka.mapper.PermissionMapper;
import io.beka.model.Permission;
import io.beka.repository.PermissionRepository;
import io.beka.service.PermissionService;
import io.beka.vo.Paging;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final ModelMapper modelMapper;

    Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<PermissionDto> findAllWithPaging(Pageable pageable) {
        Page<Permission> result = permissionRepository.findAll(pageable);
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission update(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public void delete(Permission permission) {
        permissionRepository.delete(permission);
    }

    @Override
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public PermissionDto convertEntityToDto(Permission permission) {
        return modelMapper.map(permission, PermissionDto.class);
    }

    @Override
    public Permission convertDtoToEntity(PermissionDto permissionDto) {
        return modelMapper.map(permissionDto, Permission.class);
    }

    //My Batis
    @Transactional(readOnly = true)
    @Override
    public List<Permission> findAllViaMapper(Paging page) {
        return permissionMapper.findAllWithPaging(page);
    }


    @Override
    public boolean isHasPermission(long userId, String permissionCode) {
        List<String> permissionList = permissionRepository.findPermissionsByUserIdAndPermissionCode(userId, permissionCode);
        return !permissionList.isEmpty();
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return permissionRepository.findByCode(code);
    }

    //custom JPA query
//    public List<Permission> findAllNativeByCrudTableAndActive(String curdTable, Boolean status) {
//        Query query = this.entityManager.createNativeQuery("select * from permission p where p.crud_table = :tableName AND p.status=:status ", Permission.class);
//        query.setParameter("tableName", curdTable);
//        query.setParameter("status", status);
//        return query.getResultList();
//    }
//    public List<Permission> findAllNativePaging(Page page) {
//        Query query = this.entityManager.createNativeQuery("select * from permission p limit :offset, :limit ", Permission.class);
//        query.setParameter("offset", page.getOffset());
//        query.setParameter("limit", page.getLimit());
//        System.out.println("getResultList : "+query.getResultList().size());
//        return query.getResultList();
//    }

//    public List<Object[]> findAllNativeByCustomObject() {
//        List<Object[]> list = this.entityManager.createNativeQuery("SELECT * FROM permission p WHERE p.status=:status ")
//                .setParameter("status", true) //positional parameter binding
//                .getResultList();
        /*
         for (Object[] objects : list) {
            Integer id=(Integer)objects[0];
            String name=(String)objects[1];
            String designation=(String)objects[2];
            System.out.println("Employee["+id+","+name+","+designation+"]");
         }
         */
//        return list;
//    }
}
