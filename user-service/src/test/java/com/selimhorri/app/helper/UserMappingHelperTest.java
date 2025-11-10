package com.selimhorri.app.helper;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas Unitarias para UserMappingHelper
 * Taller 2 - Pruebas y Lanzamiento
 *
 * ✅ NUEVA PRUEBA UNITARIA 1: testMapUserToDto_ValidUser_MapsCorrectly
 * ✅ NUEVA PRUEBA UNITARIA 2: testMapDtoToUser_ValidDto_MapsCorrectly
 * ✅ NUEVA PRUEBA UNITARIA 3: testMapCredentialToDto_ValidCredential_MapsCorrectly
 * ✅ NUEVA PRUEBA UNITARIA 4: testMapUser_WithNullFirstName_HandlesGracefully
 * ✅ NUEVA PRUEBA UNITARIA 5: testMapUser_WithAllFields_MapsAllCorrectly
 */
class UserMappingHelperTest {

    private User testUser;
    private Credential testCredential;

    @BeforeEach
    void setUp() {
        testCredential = Credential.builder()
            .credentialId(1)
            .username("santiago_test")
            .password("password123")
            .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
            .isEnabled(true)
            .isAccountNonExpired(true)
            .isAccountNonLocked(true)
            .isCredentialsNonExpired(true)
            .build();

        testUser = User.builder()
            .userId(1)
            .firstName("Santiago")
            .lastName("Test")
            .email("santiago@test.com")
            .imageUrl("http://example.com/avatar.jpg")
            .credential(testCredential)
            .build();
    }

    /**
     * ✅ PRUEBA UNITARIA 1: Validar mapeo de User a UserDto
     */
    @Test
    void testMapUserToDto_ValidUser_MapsCorrectly() {
        // Act
        UserDto result = UserMappingHelper.map(testUser);

        // Assert
        assertNotNull(result, "El UserDto no debería ser nulo");
        assertEquals(1, result.getUserId(), "El userId debe coincidir");
        assertEquals("Santiago", result.getFirstName(), "El firstName debe coincidir");
        assertEquals("Test", result.getLastName(), "El lastName debe coincidir");
        assertEquals("santiago@test.com", result.getEmail(), "El email debe coincidir");
        assertEquals("http://example.com/avatar.jpg", result.getImageUrl(), "La imageUrl debe coincidir");
        assertNotNull(result.getCredentialDto(), "El CredentialDto no debería ser nulo");
        assertEquals("santiago_test", result.getCredentialDto().getUsername(), "El username debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 2: Validar mapeo de UserDto a User
     */
    @Test
    void testMapDtoToUser_ValidDto_MapsCorrectly() {
        // Arrange
        CredentialDto credentialDto = CredentialDto.builder()
            .credentialId(1)
            .username("maria_test")
            .password("password456")
            .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
            .build();

        UserDto userDto = UserDto.builder()
            .userId(2)
            .firstName("Maria")
            .lastName("Test")
            .email("maria@test.com")
            .credentialDto(credentialDto)
            .build();

        // Act
        User result = UserMappingHelper.map(userDto);

        // Assert
        assertNotNull(result, "El User no debería ser nulo");
        assertEquals(2, result.getUserId(), "El userId debe coincidir");
        assertEquals("Maria", result.getFirstName(), "El firstName debe coincidir");
        assertEquals("maria@test.com", result.getEmail(), "El email debe coincidir");
        assertNotNull(result.getCredential(), "El Credential no debería ser nulo");
        assertEquals("maria_test", result.getCredential().getUsername(), "El username debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 3: Validar que el mapping de User contiene Credential
     */
    @Test
    void testMapUserToDto_WithCredential_IncludesCredentialData() {
        // Act
        UserDto result = UserMappingHelper.map(testUser);

        // Assert
        assertNotNull(result, "El UserDto no debería ser nulo");
        assertNotNull(result.getCredentialDto(), "El CredentialDto no debería ser nulo");
        assertEquals(1, result.getCredentialDto().getCredentialId(), "El credentialId debe coincidir");
        assertEquals("santiago_test", result.getCredentialDto().getUsername(), "El username debe coincidir");
        assertTrue(result.getCredentialDto().getIsEnabled(), "El credential debe estar habilitado");
        assertTrue(result.getCredentialDto().getIsAccountNonExpired(), "La cuenta no debe estar expirada");
    }

    /**
     * ✅ PRUEBA UNITARIA 4: Validar manejo de campos nulos
     */
    @Test
    void testMapUser_WithNullFirstName_HandlesGracefully() {
        // Arrange
        User userWithNull = User.builder()
            .userId(3)
            .firstName(null)
            .lastName("Test")
            .email("test@test.com")
            .credential(testCredential)
            .build();

        // Act
        UserDto result = UserMappingHelper.map(userWithNull);

        // Assert
        assertNotNull(result, "El UserDto no debería ser nulo");
        assertNull(result.getFirstName(), "El firstName debe ser nulo");
        assertEquals("Test", result.getLastName(), "El lastName debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 5: Validar mapeo completo con todos los campos
     */
    @Test
    void testMapUser_WithAllFields_MapsAllCorrectly() {
        // Act
        UserDto result = UserMappingHelper.map(testUser);

        // Assert
        assertAll("Verificar todos los campos mapeados",
            () -> assertNotNull(result),
            () -> assertEquals(1, result.getUserId()),
            () -> assertEquals("Santiago", result.getFirstName()),
            () -> assertEquals("Test", result.getLastName()),
            () -> assertEquals("santiago@test.com", result.getEmail()),
            () -> assertEquals("http://example.com/avatar.jpg", result.getImageUrl()),
            () -> assertNotNull(result.getCredentialDto()),
            () -> assertEquals("santiago_test", result.getCredentialDto().getUsername())
        );
    }
}
