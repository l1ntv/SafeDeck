package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.Status;
import ru.tbank.safedeckteam.safedeck.model.TrustedUserIP;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.repository.StatusRepository;
import ru.tbank.safedeckteam.safedeck.service.StatusService;
import ru.tbank.safedeckteam.safedeck.web.dto.ClientDataDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;

    @Override
    public Status determineStatus(ClientDataDTO clientDataDTO) {
        Client client = clientDataDTO.getClient();
        List<TrustedUserIP> trustedUserIps = client.getTrustedUserIps();
        boolean isIPTrusted = trustedUserIps.stream().map(TrustedUserIP::getIp).toList().contains(clientDataDTO.getIP());
        boolean isDeviceTrusted = String.valueOf(client.getDevice())
                .equals(String.valueOf(clientDataDTO.getDevice()));
        boolean isCountryTrusted = String.valueOf(client.getCountry())
                .equals(String.valueOf(clientDataDTO.getCountry()));
        boolean isProviderTrusted =  String.valueOf(client.getProvider())
                .equals(String.valueOf(clientDataDTO.getProvider()));
        if (isIPTrusted && isDeviceTrusted && isCountryTrusted && isProviderTrusted) {
            return statusRepository.findById(0).orElseThrow(() -> new RuntimeException("No status found")); // OK
        }
        else if (isIPTrusted || isDeviceTrusted || isCountryTrusted || isProviderTrusted) {
            return statusRepository.findById(1).orElseThrow(() -> new RuntimeException("No status found")); // SUSPECT
        } else {
            return statusRepository.findById(2).orElseThrow(() -> new RuntimeException("No status found")); // HACK
        }
    }
}
