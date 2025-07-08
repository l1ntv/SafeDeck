package ru.tbank.safedeckteam.safedeck.web.dto;



import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePublicNameRequestDto {

    private String newPublicName;
}
