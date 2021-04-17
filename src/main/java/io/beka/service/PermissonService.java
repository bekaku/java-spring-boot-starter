package io.beka.service;

import io.beka.mapper.PermissionMapper;
import io.beka.dto.Paging;
import io.beka.dto.ResponseListDto;
import io.beka.dto.PermissionDto;
import io.beka.model.Permission;
import io.beka.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PermissonService {

    @Autowired
    private CoreService<Permission, PermissionDto> coreService;

    @PersistenceContext
    private EntityManager entityManager;

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ResponseListDto<PermissionDto> findAllWithPaging(int page, int size, Sort sort) {
        Page<Permission> resault = permissionRepository.findAll(PageRequest.of(page, size, sort));
//        return new ResponseListDto<>(resault.getContent()
//                .stream()
//                .map(permission -> coreService.convertEntityToDto(permission, PermissionDto.class))
//                .collect(Collectors.toList())
//                , resault.getTotalPages(), resault.getNumberOfElements(), resault.isLast());
        return new ResponseListDto<>(resault.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList())
                , resault.getTotalPages(), resault.getNumberOfElements(), resault.isLast());
    }

    @Transactional(readOnly = true)
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Transactional
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    //My Batis
    @Transactional(readOnly = true)
    public List<Permission> findAllViaMapper(Paging page) {
        return permissionMapper.findAllWithPaging(page);
    }

    public PermissionDto convertToDto(Permission permission) {
        return coreService.convertEntityToDto(permission, new PermissionDto());
//        return modelMapper.map(permission, PermissionDto.class);
    }

    public Permission convertToEntity(PermissionDto permissionDto) {
//        return modelMapper.map(permissionDto, Permission.class);
        return coreService.convertDtoToEntity(new Permission(), permissionDto);
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
