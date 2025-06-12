package com.goorno.canigo.unit.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.goorno.canigo.common.jwt.JwtAuthenticationFilter;
import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.common.util.FileValidationUtil;
import com.goorno.canigo.controller.user.UserController;
import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.mapper.user.UserMapper;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.user.UserService;

@ActiveProfiles("test")
@WebMvcTest(
    controllers = UserController.class, // 이 컨트롤러만 스캔
    // JwtAuthenticationFilter를 ComponentScan에서 제외
    // JwtAuthenticationFilter가 @Component, @Configuration 등으로 빈 등록될 경우,
    // @WebMvcTest의 스캔 범위에 포함될 수 있기 때문.
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class // JwtAuthenticationFilter 클래스 자체를 제외
    )
)
public class UserControllerUnitTest {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context; // WebApplicationContext는 필요

    @MockBean
    private UserService userService;

    @MockBean
    private FileValidationUtil fileValidationUtil;

    @MockBean
    private Base64Util base64Util;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    
    @MockBean
    private UserDetailsService userDetailService;

    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
    	mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(SecurityMockMvcConfigurers.springSecurity()) // Security MockMvc 통합 활성화
        .build();

        requestDTO = UserRequestDTO.builder()
                .email("test@example.com")
                .password("securePassword")
                .nickname("testUser")
                .build();

        responseDTO = UserResponseDTO.builder()
                .email("test@example.com")
                .nickname("testUser")
                .authProvider(AuthProviderType.LOCAL)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createUser_success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "uploadFiles",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/users/signup")
                        .file(mockFile)
                        .param("email", requestDTO.getEmail())
                        .param("password", requestDTO.getPassword())
                        .param("nickname", requestDTO.getNickname())
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        })
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF 토큰 추가
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이메일 중복 확인")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void checkEmailDuplicate_success() throws Exception {
        when(userService.checkEmailDuplicate(eq("test@example.com"))).thenReturn(true);

        mockMvc.perform(get("/api/users/check-email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    @DisplayName("닉네임 중복 확인")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void checkNicknameDuplicate_success() throws Exception {
        when(userService.checkNicknameDuplicate(eq("testUser"))).thenReturn(false);

        mockMvc.perform(get("/api/users/check-nickname")
                        .param("nickname", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    @DisplayName("전체 회원 조회")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getAllUsers_success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserByEmail_success() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testUser"));
    }

    @Test
    @DisplayName("닉네임으로 회원 조회")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserByNickname_success() throws Exception {
        when(userService.getUserByNickname("testUser")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/nickname/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("내 정보 조회")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getMyProfile_success() throws Exception {
        when(userService.findMyDataFromEmail("test@example.com")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testUser"));
    }
}