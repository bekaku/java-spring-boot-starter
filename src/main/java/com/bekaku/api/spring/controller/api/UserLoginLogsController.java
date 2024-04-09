package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.enumtype.LoginLogType;
import com.bekaku.api.spring.service.UserService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.model.User;
import com.bekaku.api.spring.model.UserLoginLogs;
import com.bekaku.api.spring.service.UserLoginLogsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/userLoginLogs")
@RestController
@RequiredArgsConstructor
public class UserLoginLogsController extends BaseApiController {

    private final UserLoginLogsService userLoginLogsService;
    private final UserService userService;
    private final I18n i18n;
    Logger logger = LoggerFactory.getLogger(UserLoginLogsController.class);

    @PreAuthorize("isHasPermission('user_login_logs_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<UserLoginLogs> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(userLoginLogsService.findAllWithSearch(specification, getPageable(pageable, UserLoginLogs.getSort())), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@AuthenticationPrincipal UserDto auth, @RequestParam(name = "loginFrom") LoginLogType loginFrom) {
        LocalDateTime nowDate = DateUtil.getLocalDateTimeNow();
        String dateString = DateUtil.getLocalDateByForMat(nowDate, DateUtil.DATE_FORMAT);
        String amOrPm = DateUtil.getLocalDateByForMat(nowDate, DateUtil.AM_PM_FORMAT);
        List<UserLoginLogs> userLoginLogsList = userLoginLogsService.findByUserAndLoginDate(auth.getId(), dateString);
        if (userLoginLogsList.isEmpty()) {
            createLog(auth.getId(), nowDate, loginFrom);
        } else {
            UserLoginLogs logs = userLoginLogsList.get(0);
            boolean isThesamePeriod = amOrPm.equalsIgnoreCase(DateUtil.getLocalDateByForMat(logs.getLoginDate(), DateUtil.AM_PM_FORMAT));
            if (!isThesamePeriod) {
                createLog(auth.getId(), nowDate, loginFrom);
            }
        }

        return responseEntity(new HashMap<String, Object>() {{
            put("dateString", dateString);
            put("am", amOrPm.equalsIgnoreCase(ConstantData.AM));
            put("pm", amOrPm.equalsIgnoreCase(ConstantData.PM));
        }}, HttpStatus.OK);
    }

    private void createLog(Long userId, LocalDateTime dateTime, LoginLogType loginFrom) {
        Optional<User> user = userService.findById(userId);
        //            queueService.calculateUserScoreLogs(null, user.get(), ScoreLogType.LOGIN, new UserScoreLogOptions(false, false, false));
        user.ifPresent(value -> userLoginLogsService.save(new UserLoginLogs(value, dateTime, loginFrom)));

    }

    @PreAuthorize("isHasPermission('user_login_logs_manage')")
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody UserLoginLogs userLoginLogs) {
        Optional<UserLoginLogs> oldData = userLoginLogsService.findById(userLoginLogs.getId());
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        userLoginLogsService.update(userLoginLogs);
        return this.responseEntity(userLoginLogs, HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_login_logs_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") long id) {
        Optional<UserLoginLogs> userLoginLogs = userLoginLogsService.findById(id);
        if (userLoginLogs.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(userLoginLogs.get(), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('user_login_logs_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        Optional<UserLoginLogs> userLoginLogs = userLoginLogsService.findById(id);
        if (userLoginLogs.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        userLoginLogsService.delete(userLoginLogs.get());
        return this.responseDeleteMessage();
    }
}
