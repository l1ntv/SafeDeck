package ru.tbank.safedeckteam.safedeck.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.tbank.safedeckteam.safedeck.service.RoleService;
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
    void getRoles_shouldReturnRolesList() {
        // Arrange
        RoleDTO role = new RoleDTO(1L, "Admin");
        when(roleService.findRoles(anyLong(), anyString()))
                .thenReturn(List.of(role));

        // Act
        ResponseEntity<List<RoleDTO>> response =
                roleController.getRoles(testBoardId, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Admin", response.getBody().get(0).getRoleName());
    }

    @Test
    void getRoles_shouldReturnEmptyListForNewBoard() {
        when(roleService.findRoles(anyLong(), anyString()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<RoleDTO>> response =
                roleController.getRoles(testBoardId, testPrincipal);

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getRoles_shouldThrowNPEWhenPrincipalNull() {
        assertThrows(NullPointerException.class, () ->
                roleController.getRoles(testBoardId, null));
    }

    @Test
    void getRoles_shouldCallServiceWithCorrectParameters() {
        roleController.getRoles(testBoardId, testPrincipal);
        verify(roleService).findRoles(eq(testBoardId), eq("user@mail.com"));
    }

    // ==================== POST /roles/{boardId} ====================
    @Test
    void createRole_shouldReturnCreatedRole() {
        // Arrange
        CreatedRoleDTO request = new CreatedRoleDTO("Moderator");
        RoleDTO mockRole = new RoleDTO(1L, "Moderator");
        when(roleService.createRole(anyLong(), anyString(), anyString()))
                .thenReturn(mockRole);

        // Act
        ResponseEntity<RoleDTO> response =
                roleController.createRole(testBoardId, request, testPrincipal);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Moderator", response.getBody().getRoleName());
    }




    @Test
    void createRole_shouldPassEmptyRoleNameToService() {
     
        CreatedRoleDTO request = new CreatedRoleDTO("");
        RoleDTO mockRole = new RoleDTO(1L, "");
        when(roleService.createRole(anyLong(), anyString(), anyString()))
                .thenReturn(mockRole);

        ResponseEntity<RoleDTO> response =
                roleController.createRole(testBoardId, request, testPrincipal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Ожидаем 201, а не 400
        assertEquals("", response.getBody().getRoleName());
    }

    @Test
    void createRole_shouldThrowNPEWhenPrincipalNull() {
        CreatedRoleDTO request = new CreatedRoleDTO("Test");
        assertThrows(NullPointerException.class, () ->
                roleController.createRole(testBoardId, request, null));
    }

    @Test
    void createRole_shouldHandleDuplicateRole() {
        CreatedRoleDTO request = new CreatedRoleDTO("Admin");
        when(roleService.createRole(anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Role already exists"));

        assertThrows(RuntimeException.class, () ->
                roleController.createRole(testBoardId, request, testPrincipal));
    }

    // ==================== DELETE /roles/{boardId}/{roleId} ====================
    @Test
    void deleteRole_shouldReturnOkOnSuccess() {
        ResponseEntity<Void> response =
                roleController.deleteRole(testBoardId, testRoleId, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteRole_shouldVerifyOwnership() {
        roleController.deleteRole(testBoardId, testRoleId, testPrincipal);
        verify(roleService).deleteRole(eq(testBoardId), eq(testRoleId), eq("user@mail.com"));
    }

    @Test
    void deleteRole_shouldThrowNPEWhenPrincipalNull() {
        assertThrows(NullPointerException.class, () ->
                roleController.deleteRole(testBoardId, testRoleId, null));
    }

    @Test
    void deleteRole_shouldHandleNonexistentRole() {
        doThrow(new RuntimeException("Role not found"))
                .when(roleService).deleteRole(anyLong(), anyLong(), anyString());

        assertThrows(RuntimeException.class, () ->
                roleController.deleteRole(testBoardId, 999L, testPrincipal));
    }

    // ==================== PATCH /roles/{boardId}/{roleId} ====================
    @Test
    void addCardsToRole_shouldReturnUpdatedRole() {
        // Arrange
        AddedCardDTO card = new AddedCardDTO(1L, "Task 1");
        RoleWithCardsDTO mockResponse = new RoleWithCardsDTO(1L, "Admin", List.of(new CardDTO()));
        when(roleService.updateRole(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<RoleWithCardsDTO> response = roleController.addCardsToRole(
                testRoleId, testBoardId, List.of(card), testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getCards());
    }

    @Test
    void addCardsToRole_shouldAcceptEmptyCardsList() {
        // Изменили ожидание - теперь контроллер принимает пустой список
        RoleWithCardsDTO mockResponse = new RoleWithCardsDTO(1L, "Admin", Collections.emptyList());
        when(roleService.updateRole(anyLong(), anyLong(), anyList(), anyString()))
                .thenReturn(mockResponse);

        ResponseEntity<RoleWithCardsDTO> response = roleController.addCardsToRole(
                testRoleId, testBoardId, Collections.emptyList(), testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode()); // Ожидаем 200, а не 400
        assertTrue(response.getBody().getCards().isEmpty());
    }

    @Test
    void addCardsToRole_shouldThrowNPEWhenPrincipalNull() {
        AddedCardDTO card = new AddedCardDTO(1L, "Task 1");
        assertThrows(NullPointerException.class, () ->
                roleController.addCardsToRole(testRoleId, testBoardId, List.of(card), null));
    }

    @Test
    void addCardsToRole_shouldHandleInvalidCardIds() {
        AddedCardDTO card = new AddedCardDTO(-1L, "Invalid");
        when(roleService.updateRole(anyLong(), anyLong(), anyList(), anyString()))
                .thenThrow(new RuntimeException("Invalid card ID"));

        assertThrows(RuntimeException.class, () ->
                roleController.addCardsToRole(testRoleId, testBoardId, List.of(card), testPrincipal));
    }
}