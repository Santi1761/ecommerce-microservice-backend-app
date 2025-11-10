package com.selimhorri.app.service;

import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User entity1;
    private UserDto dto1;

    @BeforeEach
    void setUp() {
        entity1 = new User();
        entity1.setUserId(1);
        entity1.setFirstName("Santiago");
        entity1.setLastName("Test");
        entity1.setEmail("santiago@test.com");

        dto1 = new UserDto();
        dto1.setUserId(1);
        dto1.setFirstName("Santiago");
        dto1.setLastName("Test");
        dto1.setEmail("santiago@test.com");
    }

    @Test
    void testFindById_UserExists_ReturnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(entity1));

        try (MockedStatic<UserMappingHelper> mapping = mockStatic(UserMappingHelper.class)) {
            mapping.when(() -> UserMappingHelper.map(entity1)).thenReturn(dto1);

            UserDto result = userService.findById(1);

            assertNotNull(result);
            assertEquals("Santiago", result.getFirstName());
            assertEquals("santiago@test.com", result.getEmail());

            verify(userRepository, times(1)).findById(1);
            mapping.verify(() -> UserMappingHelper.map(entity1), times(1));
        }
    }

    @Test
    void testFindById_UserNotExists_ThrowsException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        try (MockedStatic<UserMappingHelper> mapping = mockStatic(UserMappingHelper.class)) {
            UserObjectNotFoundException ex = assertThrows(
                    UserObjectNotFoundException.class,
                    () -> userService.findById(999));
            assertTrue(ex.getMessage().contains("999"));
            verify(userRepository, times(1)).findById(999);
            // No se debe invocar el mapper si no existe
            mapping.verifyNoInteractions();
        }
    }

    @Test
    void testFindAll_ReturnsUserList() {
        User entity2 = new User();
        entity2.setUserId(2);
        entity2.setFirstName("Maria");
        entity2.setLastName("Test");
        entity2.setEmail("maria@test.com");

        UserDto dto2 = new UserDto();
        dto2.setUserId(2);
        dto2.setFirstName("Maria");
        dto2.setLastName("Test");
        dto2.setEmail("maria@test.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        try (MockedStatic<UserMappingHelper> mapping = mockStatic(UserMappingHelper.class)) {
            mapping.when(() -> UserMappingHelper.map(entity1)).thenReturn(dto1);
            mapping.when(() -> UserMappingHelper.map(entity2)).thenReturn(dto2);

            List<UserDto> result = userService.findAll();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Santiago", result.get(0).getFirstName());
            assertEquals("Maria", result.get(1).getFirstName());

            verify(userRepository, times(1)).findAll();
            mapping.verify(() -> UserMappingHelper.map(entity1), times(1));
            mapping.verify(() -> UserMappingHelper.map(entity2), times(1));
        }
    }

    @Test
    void testSave_ValidUser_SavesSuccessfully() {
        UserDto newDto = new UserDto();
        newDto.setFirstName("Nuevo");
        newDto.setLastName("Usuario");
        newDto.setEmail("nuevo@test.com");

        User newEntity = new User();
        newEntity.setFirstName("Nuevo");
        newEntity.setLastName("Usuario");
        newEntity.setEmail("nuevo@test.com");

        when(userRepository.save(any(User.class))).thenReturn(entity1);

        try (MockedStatic<UserMappingHelper> mapping = mockStatic(UserMappingHelper.class)) {
            // dto -> entity
            mapping.when(() -> UserMappingHelper.map(newDto)).thenReturn(newEntity);
            // entity -> dto (lo que devuelva el repo)
            mapping.when(() -> UserMappingHelper.map(entity1)).thenReturn(dto1);

            UserDto result = userService.save(newDto);

            assertNotNull(result);
            assertEquals("Santiago", result.getFirstName());

            mapping.verify(() -> UserMappingHelper.map(newDto), times(1));
            verify(userRepository, times(1)).save(any(User.class));
            mapping.verify(() -> UserMappingHelper.map(entity1), times(1));
        }
    }

    @Test
    void testDeleteById_ExistingUser_DeletesSuccessfully() {
        doNothing().when(userRepository).deleteById(1);

        try (MockedStatic<UserMappingHelper> mapping = mockStatic(UserMappingHelper.class)) {
            userService.deleteById(1);
            verify(userRepository, times(1)).deleteById(1);
            mapping.verifyNoInteractions();
        }
    }
}
