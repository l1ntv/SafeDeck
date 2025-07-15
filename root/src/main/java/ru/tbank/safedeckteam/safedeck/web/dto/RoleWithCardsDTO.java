package ru.tbank.safedeckteam.safedeck.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleWithCardsDTO {

    private Long roleId;

    private String roleName;

    private List<CardDTO> cards;
}
