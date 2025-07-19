package ru.tbank.safedeckteam.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.service.RoleService;
import ru.tbank.safedeckteam.safedeck.web.controller.RoleController;
import ru.tbank.safedeckteam.safedeck.web.dto.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private final Principal testPrincipal = new UsernamePasswordAuthenticationToken("user@mail.com", "password");
    private final Long testBoardId = 1L;
    private final Long testRoleId = 1L;

    // ==================== GET /roles/{boardId} ====================
    @Test
    void getRoles_shouldReturnRolesWithCards() {
        // Arrange
        RoleWithCardsDTO role = new RoleWithCardsDTO(1L, "Admin", List.of(new CardDTO()));
        when(roleService.findRoles(eq(testBoardId), eq(testPrincipal.getName())))
                .thenReturn(List.of(role));

        // Act
        ResponseEntity<List<RoleWithCardsDTO>> response =
                roleController.getRoles(testBoardId, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Admin", response.getBody().get(0).getRoleName());
        assertNotNull(response.getBody().get(0).getCards());
    }

    @Test
    void getRoles_shouldReturnEmptyListWhenNoRoles() {
        when(roleService.findRoles(eq(testBoardId), eq(testPrincipal.getName())))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<RoleWithCardsDTO>> response =
                roleController.getRoles(testBoardId, testPrincipal);

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getRoles_shouldVerifyServiceCall() {
        roleController.getRoles(testBoardId, testPrincipal);

        verify(roleService).findRoles(testBoardId, testPrincipal.getName());
    }

    @Test
    void getRoles_shouldThrowExceptionWhenServiceFails() {
        when(roleService.findRoles(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () ->
                roleController.getRoles(testBoardId, testPrincipal));
    }

    // ==================== POST /roles/{boardId} ====================
    @Test
    void createRole_shouldReturnCreatedStatus() {
        // Arrange
        CreatedRoleDTO request = new CreatedRoleDTO("Moderator");
        RoleDTO mockResponse = new RoleDTO(1L, "Moderator");
        when(roleService.createRole(eq(testBoardId), eq("Moderator"), eq(testPrincipal.getName())))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<RoleDTO> response =
                roleController.createRole(testBoardId, request, testPrincipal);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Moderator", response.getBody().getRoleName());
    }

    @Test
    void createRole_shouldValidateRoleNameLength() {
        CreatedRoleDTO request = new CreatedRoleDTO("A");
        RoleDTO mockResponse = new RoleDTO(1L, "A");
        when(roleService.createRole(anyLong(), anyString(), anyString()))
                .thenReturn(mockResponse);

        ResponseEntity<RoleDTO> response =
                roleController.createRole(testBoardId, request, testPrincipal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("A", response.getBody().getRoleName());
    }

    @Test
    void createRole_shouldVerifyServiceParameters() {
        CreatedRoleDTO request = new CreatedRoleDTO("Test");
        roleController.createRole(testBoardId, request, testPrincipal);

        verify(roleService).createRole(testBoardId, "Test", testPrincipal.getName());
    }

    @Test
    void createRole_shouldHandleDuplicateRoleName() {
        CreatedRoleDTO request = new CreatedRoleDTO("Duplicate");
        when(roleService.createRole(anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Role name exists"));

        assertThrows(RuntimeException.class, () ->
                roleController.createRole(testBoardId, request, testPrincipal));
    }

    // ==================== DELETE /roles/{boardId}/{roleId} ====================
    @Test
    void deleteRole_shouldReturnOkStatus() {
        ResponseEntity<Void> response =
                roleController.deleteRole(testBoardId, testRoleId, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteRole_shouldVerifyServiceCall() {
        roleController.deleteRole(testBoardId, testRoleId, testPrincipal);

        verify(roleService).deleteRole(testBoardId, testRoleId, testPrincipal.getName());
    }

    @Test
    void deleteRole_shouldHandleNonexistentRole() {
        doThrow(new RuntimeException("Role not found"))
                .when(roleService).deleteRole(anyLong(), anyLong(), anyString());

        assertThrows(RuntimeException.class, () ->
                roleController.deleteRole(testBoardId, 999L, testPrincipal));
    }

    @Test
    void deleteRole_shouldValidatePermissions() {
        doThrow(new SecurityException("Access denied"))
                .when(roleService).deleteRole(anyLong(), anyLong(), anyString());

        assertThrows(SecurityException.class, () ->
                roleController.deleteRole(testBoardId, testRoleId, testPrincipal));
    }

    // ==================== PATCH /roles/{boardId}/{roleId} ====================
    @Test
    void addCardsToRole_shouldReturnUpdatedRoleWithCards() {
        // Arrange
        AddedCardDTO card = new AddedCardDTO(1L, "Task 1");
        RoleWithCardsDTO mockResponse = new RoleWithCardsDTO(1L, "Dev", List.of(new CardDTO()));
        when(roleService.updateRole(eq(testRoleId), eq(testBoardId), anyList(), eq(testPrincipal.getName())))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<RoleWithCardsDTO> response =
                roleController.addCardsToRole(testRoleId, testBoardId, List.of(card), testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dev", response.getBody().getRoleName());
        assertFalse(response.getBody().getCards().isEmpty());
    }

    @Test
    void addCardsToRole_shouldHandleEmptyCardsList() {
        RoleWithCardsDTO mockResponse = new RoleWithCardsDTO(1L, "Dev", Collections.emptyList());
        when(roleService.updateRole(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(mockResponse);

        ResponseEntity<RoleWithCardsDTO> response =
                roleController.addCardsToRole(testRoleId, testBoardId, Collections.emptyList(), testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getCards().isEmpty());
    }

    @Test
    void addCardsToRole_shouldVerifyServiceParameters() {
        AddedCardDTO card = new AddedCardDTO(1L, "Task 1");
        roleController.addCardsToRole(testRoleId, testBoardId, List.of(card), testPrincipal);

        verify(roleService).updateRole(testRoleId, testBoardId, List.of(card), testPrincipal.getName());
    }

    @Test
    void addCardsToRole_shouldHandleInvalidCards() {
        AddedCardDTO invalidCard = new AddedCardDTO(null, null);
        when(roleService.updateRole(anyLong(), anyLong(), anyList(), anyString()))
                .thenThrow(new RuntimeException("Invalid cards"));

        assertThrows(RuntimeException.class, () ->
                roleController.addCardsToRole(testRoleId, testBoardId, List.of(invalidCard), testPrincipal));
    }

    // ==================== PATCH /roles/{boardId}/rename/{roleId} ====================
    @Test
    void renameRole_shouldReturnUpdatedRole() {
        // Arrange
        RenamedRoleDTO request = new RenamedRoleDTO("NewName");
        RoleDTO mockResponse = new RoleDTO(testRoleId, "NewName");
        when(roleService.renameRole(eq(testRoleId), eq(testBoardId), eq("NewName"), eq(testPrincipal.getName())))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<RoleDTO> response =
                roleController.renameRole(testRoleId, testBoardId, request, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("NewName", response.getBody().getRoleName());
    }

    @Test
    void renameRole_shouldHandleEmptyName() {
        RenamedRoleDTO request = new RenamedRoleDTO("");
        RoleDTO mockResponse = new RoleDTO(testRoleId, "");
        when(roleService.renameRole(anyLong(), anyLong(), anyString(), anyString()))
                .thenReturn(mockResponse);

        ResponseEntity<RoleDTO> response =
                roleController.renameRole(testRoleId, testBoardId, request, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody().getRoleName());
    }

    @Test
    void renameRole_shouldVerifyServiceCall() {
        RenamedRoleDTO request = new RenamedRoleDTO("NewName");
        roleController.renameRole(testRoleId, testBoardId, request, testPrincipal);

        verify(roleService).renameRole(testRoleId, testBoardId, "NewName", testPrincipal.getName());
    }

    @Test
    void renameRole_shouldHandleDuplicateName() {
        RenamedRoleDTO request = new RenamedRoleDTO("Duplicate");
        when(roleService.renameRole(anyLong(), anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Name exists"));

        assertThrows(RuntimeException.class, () ->
                roleController.renameRole(testRoleId, testBoardId, request, testPrincipal));
    }
}