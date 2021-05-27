package io.beka.controller.test;

import io.beka.controller.api.BaseApiController;
import io.beka.dto.CourseDto;
import io.beka.dto.ResponseListDto;
import io.beka.dto.StudentCourseDto;
import io.beka.dto.StudentDto;
import io.beka.model.Course;
import io.beka.model.Student;
import io.beka.properties.AppProperties;
import io.beka.repository.CourseRepository;
import io.beka.repository.StudentRepository;
import io.beka.specification.SearchSpecification;
import io.beka.util.AppUtil;
import io.beka.util.ConstantData;
import io.beka.util.DateUtil;
import io.beka.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping(path = "/test")
@RestController
@RequiredArgsConstructor
public class TestApiController extends BaseApiController {

    Logger logger = LoggerFactory.getLogger(TestApiController.class);

    //    private final MailProperties mailProperties;
    private final AppProperties appProperties;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Object> testGet() {
        logger.info("testGet");
        return this.responseEntity(new HashMap<String, Object>() {{
            put("camelToSnake", AppUtil.camelToSnake("ApiClient"));
        }}, HttpStatus.OK);
    }

    @GetMapping("/properties")
    public ResponseEntity<Object> testProperties() {
        return this.responseEntity(new HashMap<String, Object>() {{
            put("normal-properties", appProperties != null ? appProperties.getVersion() : null);
            put("object-properties", appProperties != null ? appProperties.getMailConfig() : null);
            put("list-properties", appProperties != null ? appProperties.getDefaultRecipients() : null);
            put("map-Properties", appProperties != null ? appProperties.getAdditionalHeaders() : null);
            put("object-list-properties", appProperties != null ? appProperties.getMenus() : null);
            put("getTestProp", appProperties != null ? appProperties.getDefaults() : null);
        }}, HttpStatus.OK);
    }

    //Spring Data JPA Many To Many Relationship Mapping Example
    @PostMapping("/createCourse")
    public ResponseEntity<Object> createCourse(@Valid @RequestBody Course course) {


        // create three courses batch
//        Course course1 = new Course("Machine Learning", "ML", 12, 1500);
//        Course course2 = new Course("Database Systems", "DS", 8, 800);
//        Course course3 = new Course("Web Basics", "WB", 10, 0);
//        courseRepository.saveAll(Arrays.asList(course1, course2, course3));

        courseRepository.save(course);
        return this.responseEntity(course, HttpStatus.CREATED);
    }

    @GetMapping("/findAllCourse")
    public ResponseEntity<Object> findAllCourse(Pageable pageable) {

        this.initSearchParam();

        SearchSpecification<Course> specification = new SearchSpecification<>();
//        specification.add(new SearchCriteria("title", "web", SearchOperation.MATCH));
        Page<Course> result = courseRepository.findAll(specification, pageable);

        ResponseListDto<CourseDto> list = new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertCourseEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
        return this.responseEntity(list, HttpStatus.OK);
    }

    @PostMapping("/createStudent")
    public ResponseEntity<Object> createStudent(@Valid @RequestBody Student student) {
        studentRepository.save(student);
        return this.responseEntity(student, HttpStatus.CREATED);
    }

    @PostMapping("/createStudentCourse")
    public ResponseEntity<Object> createStudentCourse(@Valid @RequestBody StudentCourseDto dto) {

        Optional<Student> student = studentRepository.findById(dto.getStudentId());

        if (student.isEmpty()) {
            throw this.responseErrorNotfound();
        }

        Set<Course> coursesList = new HashSet<>();
        // add courses to the student
        if (dto.getSelectdCourses().length > 0) {
            Optional<Course> course;
            for (long courseId : dto.getSelectdCourses()) {
                course = courseRepository.findById(courseId);
                course.ifPresent(coursesList::add);
            }
        }

        student.get().setCourses(coursesList);
        studentRepository.save(student.get());
        return this.responseEntity("createStudentCourse", HttpStatus.CREATED);
    }


    @GetMapping("/studentCourseList/{studentId}")
    public ResponseEntity<Object> studentCourseList(@PathVariable("studentId") long studentId, Pageable pageable) {
        Page<Course> result = courseRepository.courseAllByStudentId(studentId, pageable);
        ResponseListDto<CourseDto> list = new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertCourseEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
        return this.responseEntity(list, HttpStatus.OK);
    }

    @GetMapping("/getStudentListByCourse/{courseId}")
    public ResponseEntity<Object> getStudentListByCourse(@PathVariable("courseId") long courseId, Pageable pageable) {
        Page<Student> result = studentRepository.studentAllByCourseId(courseId, pageable);
        ResponseListDto<StudentDto> list = new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertStudentEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
        return this.responseEntity(list, HttpStatus.OK);
    }

    public CourseDto convertCourseEntityToDto(Course course) {
        return modelMapper.map(course, CourseDto.class);
    }

    public StudentDto convertStudentEntityToDto(Student student) {
        return modelMapper.map(student, StudentDto.class);
    }


    //test upload file
    @PostMapping("/upload")
    public ResponseEntity<Object> singleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, null, "Please select a file to upload");
        }
        String yearMonthImages = FileUtil.getImagesYearMonthDirectory();
        String uploadPath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), yearMonthImages);
        String newName = FileUtil.generateFileName(FileUtil.getMultipartFileName(file));
//        try {
//            // Get the file and save it somewhere
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get(uploadPath + newName);
//            Files.write(path, bytes);
//        } catch (IOException e) {
//            throw this.responseError(HttpStatus.BAD_REQUEST, null, e.getMessage());
//        }

        //resize image
//        thumbnailatorResize(uploadPath, newName);
//        if (appProperties.getUploadImage().isCreateThumbnail()) {
//            thumbnailatorCreateThumnail(uploadPath, newName);
//        }

        String pathInDb = yearMonthImages + newName;
        return this.responseEntity(new HashMap<String, Object>() {{
            put("getFileSize", FileUtil.getFileSize(file));
            put("humanReadableByteCountSI", FileUtil.humanReadableByteCountSI(FileUtil.getFileSize(file)));
            put("getMimeType", FileUtil.getMimeType(file));
            put("originalName", FileUtil.getMultipartFileName(file));
            put("pathInDb", pathInDb);
            put("uploadPath", uploadPath);
            put("imageCdnUrl", FileUtil.generateCdnPath(appProperties.getCdnForPublic(), pathInDb, null));
            put("imageThumbnailCdnUrl", FileUtil.generateCdnPath(appProperties.getCdnForPublic(), FileUtil.generateThumbnailName(pathInDb, appProperties.getUploadImage().getThumbnailExname()), null));
        }}, HttpStatus.OK);
    }

    private void thumbnailatorResize(String uploadFile, String fileName) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));
            BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getLimitWidth(), appProperties.getUploadImage().getLimitHeight(), 0.90);
            ImageIO.write(outputImage, "jpg", new File(uploadFile + fileName));
        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }

    private void thumbnailatorCreateThumnail(String uploadFile, String fileName) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(uploadFile + fileName));
            BufferedImage outputImage = FileUtil.thumbnailatorResizeImage(originalImage, appProperties.getUploadImage().getThumbnailWidth(), appProperties.getUploadImage().getThumbnailWidth(), 0.90);
            ImageIO.write(outputImage, "jpg", new File(uploadFile + FileUtil.generateThumbnailName(fileName, appProperties.getUploadImage().getThumbnailExname())));
        } catch (IOException e) {
            throw this.responseError(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        }
    }
}
