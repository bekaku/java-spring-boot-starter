package com.bekaku.api.spring.dto;

import com.bekaku.api.spring.enumtype.AiDocumentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/*
@GenSourceableTable()
@Getter
@Setter
@Entity
@Table(
        name = "knowledge_masters_tags",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"knowledge_masters", "knowledge_tags"})
        },
        indexes = {
                @Index(columnList = "knowledge_masters"),
                @Index(columnList = "knowledge_tags")
        }


 )
@SQLDelete(sql = "UPDATE knowledge_masters SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class KnowledgeMasters extends SoftDeletedAuditableUpdated<Long>
public class DtoTemplate extends Id {
public class DtoTemplate extends Auditable<Long> {
*/
@NoArgsConstructor
@JsonRootName("dtoName")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtoTemplate extends DtoId {
    /* https://www.baeldung.com/java-validation
    @NotNull validates that the annotated property value isn’t null.
    @AssertTrue validates that the annotated property value is true.
    @Size validates that the annotated property value has a size between the attributes min and max. We can apply it to String, Collection, Map, and array properties.
    @Min validates that the annotated property has a value no smaller than the value attribute.
    @Max validates that the annotated property has a value no larger than the value attribute.
    @Email validates that the annotated property is a valid email address.
    Some annotations accept additional attributes, but the message attribute is common to all of them. This is the message that will usually be rendered when the value of the respective property fails validation.

    Here are some additional annotations we can find in the JSR:

    @NotEmpty validates that the property isn’t null or empty. We can apply it to String, Collection, Map or Array values.
    @NotBlank can be applied only to text values, and validates that the property isn’t null or whitespace.
    @Positive and @PositiveOrZero apply to numeric values, and validate that they’re strictly positive, or positive including 0.
    @Negative and @NegativeOrZero apply to numeric values, and validate that they’re strictly negative, or negative including 0.
    @Past and @PastOrPresent validate that a date value is in the past, or the past including the present. We can apply it to date types, including those added in Java 8.
    @Future and @FutureOrPresent validate that a date value is in the future, or in the future including the present.
     */
    @NotEmpty(message = "{error.NotEmpty}")
    private String code;

    @NotEmpty(message = "{error.validateRequire}")
    private String name;

    @DecimalMax(value = "999999999999.0", message = "{error.DecimalMax.message}")
    private BigDecimal target;

    @DecimalMax(value = "999999999999.0", message = "{error.DecimalMax.message}")
    @DecimalMin(value = "0.0", message = "{error.DecimalMin.message}")
    private BigDecimal parentInputResult;

    @Size(max = 100, message = "{error.SizeLimitMaxFormat}")
    private String apiName;

    @Positive(message = "{error.NotEmpty}")
    private Long companyTypeId;

    @Email(message = "{error.validateEmail}")
    @Size(min = 3, max = 100, message = "{error.SizeLimitMinMaxFormat}")
    private String email;

    @Min(value = 18, message = "{error.min.message}")
    @Max(value = 150, message = "{error.max.message}")
    private int age;

    @NotBlank(message = "{error.validateRequire}")
    private String emailOrUsername;

    @NotNull(message = "{error.NotEmpty}")
    private String shareTopic;

    @Enumerated(EnumType.STRING)
    private AiDocumentType documentType;

    /*
    @Column(columnDefinition = "boolean default true")
    private boolean active;

    @Column(columnDefinition = "bit default 0")
    private boolean calculateUseScore;

    @Column(columnDefinition = "int default 0")
    private int starLevel;

    @Column(columnDefinition = "bigint default 0")
    long totalNewMessage;

   @Column(columnDefinition = "longtext")
    String longText;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "post_datetime", columnDefinition = "datetime")
    private LocalDateTime postDatetime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "post_datetime", columnDefinition = "datetime", updatable = false)
    private LocalDateTime postDatetime;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "start_date", columnDefinition = "date")
    private LocalDate startDate;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "groupName");
    }
   public static Sort getSort() {
        return Sort.by(
                Sort.Order.desc("pin"),
                Sort.Order.desc("groupChat.latestUpdate"),
                Sort.Order.desc("groupChat.id")
        );
    }

    update enum type if add or remove type
    ALTER TABLE company_notice MODIFY COLUMN notice_type ENUM('GENERAL', 'WI');
     */
}
