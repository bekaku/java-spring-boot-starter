package com.grandats.api.givedeefive;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class UserAclTest {
    List<String> permissionList;

    @BeforeEach
    public void setUp() {
        permissionList = Arrays.asList("api_client_list",
                "api_client_view",
                "system_announcement_manage");
    }

    @Test
    public void should_have_permission_inpermission_list() {
//        assertThat(permissionList.contains("greeting_msg_manage"), is(true));
        Assertions.assertTrue(permissionList.contains("system_announcement_manage"));
    }
}
