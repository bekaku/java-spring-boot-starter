package io.beka.controller.develop;

import io.beka.annotation.GenSourceableTable;
import io.beka.configuration.I18n;
import io.beka.configuration.MetadataExtractorIntegrator;
import io.beka.controller.api.BaseApiController;
import io.beka.util.AppUtil;
import io.beka.util.ConstantData;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;

@RequestMapping(path = "/dev")
@RestController
@RequiredArgsConstructor
public class DevelopmentContoller extends BaseApiController {
    private final I18n i18n;
    Logger logger = LoggerFactory.getLogger(DevelopmentContoller.class);

    @Value("${production}")
    boolean isProduction;

    @PostMapping("/generateSrc")
    public ResponseEntity<Object> generateSrc() {

        logger.warn("Production : {}", isProduction);
        if (isProduction) {
            throw this.responseError(HttpStatus.UNAUTHORIZED, null, i18n.getMessage("error.productionModeDetect"));
        }

        /*
        for (Namespace namespace : MetadataExtractorIntegrator.INSTANCE
                .getDatabase()
                .getNamespaces()) {

            for (Table table : namespace.getTables()) {
                logger.info("Table {} has the following columns: {}",
                        table,
                        StreamSupport.stream(
                                Spliterators.spliteratorUnknownSize(
                                        table.getColumnIterator(),
                                        Spliterator.ORDERED
                                ),
                                false
                        )
                                .collect(Collectors.toList())
                );
            }
        }
        */


        Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();
        String className;
        String packageClassName;
        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            className = AppUtil.getSimpleClassName(persistentClass.getClassName());
            packageClassName = persistentClass.getClassName();
            Table table = persistentClass.getTable();

            logger.info("capitalizeFirstLetter {} => {}", className, AppUtil.capitalizeFirstLetter(className, true));

            System.out.println("--------------------------");
            logger.info("Entity: {} is mapped to table: {}",
                    packageClassName,
                    table.getName()
            );
            Class<?> aClass = getClassFromName(packageClassName);
            if (aClass != null) {
                GenSourceableTable genSourceableTable = aClass.getAnnotation(GenSourceableTable.class);
                if (genSourceableTable != null) {

                    if (genSourceableTable.createDto()) {
                        logger.error("  createDto : {} ", className);
                        generateDto(className);
                    }
                    if (genSourceableTable.createRepository()) {
                        generateRepository(className);
                    }
                    if (genSourceableTable.createService()) {
                        generateService(className, genSourceableTable.createDto());
                    }
                    if (genSourceableTable.createServiceImpl()) {
                        generateServiceImpl(className, genSourceableTable.createDto());
                    }
                    if (genSourceableTable.createController()) {
                        generateController(className);
                    }
                    if (genSourceableTable.createMapper()) {
                        logger.error("  createMapper : {} ", className);
                    }
                }
            } else {
                logger.error("  className : {} NULL", className);
            }


//            for (Iterator propertyIterator = persistentClass.getPropertyIterator();
//                 propertyIterator.hasNext(); ) {
//                Property property = (Property) propertyIterator.next();
//
//                for (Iterator columnIterator = property.getColumnIterator();
//                     columnIterator.hasNext(); ) {
//                    Column column = (Column) columnIterator.next();
//
//                    logger.info("Property: {} is mapped on table column: {} of type: {} uniqe : {}, length : {}, propertyType : {}, isNullable :{}",
//                            property.getName(),
//                            column.getName(),
//                            column.getSqlType(),
//                            column.isUnique(),
//                            column.getLength(),
//                            property.getType().getName(),
//                            column.isNullable()
//                    );
//                }
//            }
        }


//        TableSerializable tableSerializable = LoginLog.class.getAnnotation(TableSerializable.class);
//        logger.info("CreateController : {}", tableSerializable.createController());

//
//        for (Field field : LoginLog.class.getAnnotation(TableSerializable.class)) {
//            javax.persistence.Column column = field.getAnnotation(javax.persistence.Column.class);
//            if (column != null) {
//                System.out.println("Columns: " + column);
//            }
//        }

        return this.responseEntity(HttpStatus.OK);
    }

    private void generateDto(String entityName) {
        //package io.beka.controller.api
        String fileName = entityName + "Dto";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/dto/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto").append(";\n");
                writer.append("\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonRootName;\n");
                writer.append("import lombok.*;\n");
                writer.append("import lombok.experimental.Accessors;\n");
                writer.append("\n");
                writer.append("@Data\n");
                writer.append("@JsonRootName(\"").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("\")\n");
                writer.append("@AllArgsConstructor\n");
                writer.append("@NoArgsConstructor\n");
                writer.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
                writer.append("@Accessors(chain = true)\n");
                writer.append("public class ").append(fileName).append("{\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateRepository(String entityName) {
        //package io.beka.repository
        String fileName = entityName + "Repository";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/repository/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository").append(";\n");
                writer.append("\n");
                writer.append("import org.springframework.stereotype.Repository;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("\n");
                writer.append("@Repository\n");
                writer.append("public interface ").append(fileName).append(" extends BaseRepository<").append(entityName).append(",Long> {\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateService(String entityName, boolean haveDto) {
        //package io.beka.service
        String fileName = entityName + "Service";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service." + fileName;
        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/service/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service").append(";\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                }
                writer.append("\n");
                writer.append("import io.beka.model.").append(entityName).append(";\n");
                writer.append("\n");
                writer.append("public interface ").append(entityName).append("Service extends BaseService<").append(entityName).append(", ").append(haveDto ? entityName + "Dto" : entityName).append("> {\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateServiceImpl(String entityName, boolean haveDto) {
        //package io.beka.serviceImpl
        String fileName = entityName + "ServiceImpl";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".serviceImpl." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/serviceImpl/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".serviceImpl").append(";\n");
                writer.append("\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".vo.Paging;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.ResponseListDto;\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                }
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository.").append(entityName).append("Repository;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service.").append(entityName).append("Service;\n");
                writer.append("import lombok.AllArgsConstructor;\n");
                writer.append("import org.modelmapper.ModelMapper;\n");
                writer.append("import org.springframework.data.domain.Page;\n");
                writer.append("import org.springframework.data.domain.PageRequest;\n");
                writer.append("import org.springframework.data.domain.Sort;\n");
                writer.append("import org.springframework.stereotype.Service;\n");
                writer.append("import org.springframework.transaction.annotation.Transactional;\n");
                writer.append("\n");
                writer.append("import java.util.List;\n");
                writer.append("import java.util.Optional;\n");
                writer.append("import java.util.stream.Collectors;\n");
                writer.append("\n");
                writer.append("@Transactional\n");
                writer.append("@AllArgsConstructor\n");
                writer.append("@Service\n");
                writer.append("public class ").append(entityName).append("ServiceImpl implements ").append(entityName).append("Service {\n");
                writer.append("    private final ").append(entityName).append("Repository ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository;\n");
                writer.append("    private final ModelMapper modelMapper;\n");
                //findAllWithPaging
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public ResponseListDto<").append(haveDto ? entityName + "Dto" : entityName).append("> findAllWithPaging(Paging paging, Sort sort) {\n");
                writer.append("        Page<").append(entityName).append("> resault = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findAll(PageRequest.of(paging.getPage(), paging.getLimit(), sort));\n");
                writer.append("        return new ResponseListDto<>(resault.getContent()\n");
                if (haveDto) {
                    writer.append("                .stream()\n");
                    writer.append("                .map(this::convertEntityToDto)\n");
                    writer.append("                .collect(Collectors.toList())\n");
                }
                writer.append("                , resault.getTotalPages(), resault.getNumberOfElements(), resault.isLast());\n");
                writer.append("    }\n");
                //findAll
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public List<").append(entityName).append("> findAll() {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findAll();\n");
                writer.append("    }\n");
                writer.append("\n");
                //save
                writer.append("\n");
                writer.append("    public ").append(entityName).append(" save(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.save(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("    }\n");
                //update
                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(entityName).append(" update(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.save(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("    }\n");
                //findById
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public Optional<").append(entityName).append("> findById(Long id) {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findById(id);\n");
                writer.append("    }\n");
                //delete
                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public void delete(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.delete(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("    }\n");

                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public void deleteById(Long id) {\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.deleteById(id);\n");
                writer.append("    }\n");

                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(haveDto ? entityName + "Dto" : entityName).append(" convertEntityToDto(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                if (haveDto) {
                    writer.append("        return modelMapper.map(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(", ").append(entityName).append("Dto.class);\n");
                } else {
                    writer.append("return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");

                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(entityName).append(" convertDtoToEntity(").append(haveDto ? entityName + "Dto " + AppUtil.capitalizeFirstLetter(entityName, true) + "Dto" : entityName + " " + AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                if (haveDto) {
                    writer.append("        return modelMapper.map(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Dto, ").append(entityName).append(".class);\n");
                } else {
                    writer.append("return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");
                writer.append("\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateController(String entityName) {
        //package io.beka.controller.api
        String fileName = entityName + "Controller";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".controller.api." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/controller/api/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".controller.api").append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".configuration.I18n;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".vo.Paging;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service.").append(entityName).append("Service;\n");
                writer.append("import lombok.RequiredArgsConstructor;\n");
                writer.append("import org.slf4j.Logger;\n");
                writer.append("import org.slf4j.LoggerFactory;\n");
                writer.append("import org.springframework.http.HttpStatus;\n");
                writer.append("import org.springframework.http.ResponseEntity;\n");
                writer.append("import org.springframework.web.bind.annotation.*;\n");
                writer.append("\n");
                writer.append("import javax.validation.Valid;\n");
                writer.append("import java.util.Optional;\n");
                writer.append("\n");
                writer.append("@RequestMapping(path = \"/api/").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("\")\n");
                writer.append("@RestController\n");
                writer.append("@RequiredArgsConstructor\n");
                writer.append("public class ").append(fileName).append("extends BaseApiController{\n");
                writer.append("\n");
                writer.append("    private final ").append(entityName).append("Service ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service;\n");
                writer.append("    private final I18n i18n;\n");
                writer.append(" //   Logger logger = LoggerFactory.getLogger(").append(entityName).append("Controller.class);\n");
                writer.append("\n");
                //findall
                writer.append("    @GetMapping\n");
                writer.append("    public ResponseEntity<Object> findAll(@RequestParam(value = \"page\", defaultValue = \"0\") int page,\n");
                writer.append("                                          @RequestParam(value = \"limit\", defaultValue = \"20\") int limit) {\n");
                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findAllWithPaging(new Paging(page, limit), ").append(entityName).append(".getSort()), HttpStatus.OK);\n");
                writer.append("    }\n");
                //create
                writer.append("\n");
                writer.append("    @PostMapping\n");
                writer.append("    public ResponseEntity<Object> create(@Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
                writer.append("        ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertDtoToEntity(dto);\n");

                //validator
//                writer.append("        Optional<Role> roleExist = roleService.findByName(dto.getName());\n");
//                writer.append("        if (roleExist.isPresent()) {\n");
//                writer.append("            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage(\"error.validateDuplicate\", dto.getName()));\n");
//                writer.append("        }\n");

                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.save(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("), HttpStatus.OK);\n");
                writer.append("    }\n");
                //update
                writer.append("\n");
                writer.append("    @PutMapping\n");
                writer.append("    public ResponseEntity<Object> update(@Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
                writer.append("        ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertDtoToEntity(dto);\n");
                writer.append("        Optional<").append(entityName).append("> oldData = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(dto.getId());\n");
                writer.append("        if (oldData.isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.update(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("), HttpStatus.OK);\n");
                //findOne
                writer.append("\n");
                writer.append("    @GetMapping(\"/{id}\")\n");
                writer.append("    public ResponseEntity<Object> findOne(@PathVariable(\"id\") long id) {\n");
                writer.append("        Optional<").append(entityName).append("> ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(id);\n");
                writer.append("        if (permission.isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".get()), HttpStatus.OK);\n");
                writer.append("    }\n");
                //delete
                writer.append("    @DeleteMapping(\"/{id}\")\n");
                writer.append("    public ResponseEntity<Object> delete(@PathVariable(\"id\") long id) {\n");
                writer.append("        Optional<").append(entityName).append("> ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(id);\n");
                writer.append("        if (role.isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.deleteById(id);\n");
                writer.append("        return this.responseEntity(HttpStatus.OK);\n");
                writer.append("    }\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }


    private Class<?> getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
