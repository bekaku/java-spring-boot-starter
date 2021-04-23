package io.beka.controller.develop;

import io.beka.annotation.TableSerializable;
import io.beka.configuration.I18n;
import io.beka.configuration.MetadataExtractorIntegrator;
import io.beka.controller.api.BaseApiController;
import io.beka.util.ConstantData;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.Metadata;
//import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

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
            className = getSimpleClassName(persistentClass.getClassName());
            packageClassName = persistentClass.getClassName();
            Table table = persistentClass.getTable();

            System.out.println("--------------------------");
            logger.info("Entity: {} is mapped to table: {}",
                    packageClassName,
                    table.getName()
            );

//            TableSerializable tableSerializable = LoginLog.class.getAnnotation(TableSerializable.class);

            Class<?> aClass = getClassFromName(packageClassName);
            if (aClass != null) {
                TableSerializable tableSerializable = aClass.getAnnotation(TableSerializable.class);
                if (tableSerializable != null) {

                    if (tableSerializable.createDto()) {
                        logger.error("  createDto : {} ", className);
                    }
                    if (tableSerializable.createRepository()) {
                        generateRepository(className);
                    }
                    if (tableSerializable.createService()) {
                        generateService(className);
                    }
                    if (tableSerializable.createServiceImpl()) {
                        generateServiceImpl(className);
                    }
                    if (tableSerializable.createController()) {
                        generateController(className);
                    }
                    if (tableSerializable.createMapper()) {
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
        String className = "io.beka.dto." + fileName;

        boolean isExist = getClassFromName(className) != null;
        logger.error("Class : {} " + (isExist ? "FOUND " : "NOT FOUND"), className);
    }
    private void generateRepository(String className) {
        //package io.beka.repository
        String fileName = className + "Repository";
        String name = "io.beka.repository." + fileName;
        boolean isExist = getClassFromName(name) != null;
        logger.error("Class : {} " + (isExist ? "FOUND " : "NOT FOUND"), name);
    }

    private void generateService(String className) {
        //package io.beka.service
        String name = "io.beka.service." + className + "Service";
        boolean isExist = getClassFromName(name) != null;
        logger.error("Class : {} " + (isExist ? "FOUND " : "NOT FOUND"), name);
    }

    private void generateServiceImpl(String className) {
        //package io.beka.serviceImpl
        String name = "io.beka.serviceImpl." + className + "ServiceImpl";
        boolean isExist = getClassFromName(name) != null;
        logger.error("Class : {} " + (isExist ? "FOUND " : "NOT FOUND"), name);
    }

    private void generateController(String entityName) {
        //package io.beka.controller.api
        String fileName = entityName + "Controller";
        String className = "io.beka.controller.api." + fileName;

        boolean isExist = getClassFromName(className) != null;
        logger.error("Class : {} " + (isExist ? "FOUND " : "NOT FOUND"), className);

        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_SRC_PATH + "/controller/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_SRC_PACKAGE + ".controller").append("; \n");
                writer.append(" \n");
                writer.append("import lombok.RequiredArgsConstructor; \n");
                writer.append("import org.springframework.web.bind.annotation.RestController; \n");
                writer.append(" \n");
                writer.append("@RequiredArgsConstructor \n");
                writer.append("@RestController \n");
                writer.append("public class ").append(fileName).append("{ \n");
                writer.append("} \n");
                writer.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private String getSimpleClassName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    private Class<?> getClassFromName(String className) {
        try {
            //            logger.error("Class : {} found ", className);
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
//            logger.error("Class : {} not found ", className);
            return null;
        }
    }
}
