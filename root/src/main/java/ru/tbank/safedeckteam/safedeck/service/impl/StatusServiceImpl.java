package ru.tbank.safedeckteam.safedeck.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.safedeckteam.safedeck.model.Client;
import ru.tbank.safedeckteam.safedeck.model.SecureLog;
import ru.tbank.safedeckteam.safedeck.model.Status;
import ru.tbank.safedeckteam.safedeck.model.TrustedUserIP;
import ru.tbank.safedeckteam.safedeck.model.enums.AuthStatus;
import ru.tbank.safedeckteam.safedeck.repository.SecureLogRepository;
import ru.tbank.safedeckteam.safedeck.repository.StatusRepository;
import ru.tbank.safedeckteam.safedeck.service.StatusService;
import ru.tbank.safedeckteam.safedeck.web.dto.ClientDataDTO;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;
    private final SecureLogRepository secureLogRepository;

    @Override
    public Status determineStatus(ClientDataDTO clientDataDTO) {
        Client client = clientDataDTO.getClient();
        List<TrustedUserIP> trustedUserIps = client.getTrustedUserIps();
        boolean isIPTrusted = trustedUserIps.stream().map(TrustedUserIP::getIp).toList().contains(clientDataDTO.getIP());
        boolean isDeviceTrusted = String.valueOf(client.getDevice())
                .equals(String.valueOf(clientDataDTO.getDevice()));
        boolean isCountryTrusted = String.valueOf(client.getCountry())
                .equals(String.valueOf(clientDataDTO.getCountry()));
        boolean isProviderTrusted = String.valueOf(client.getProvider())
                .equals(String.valueOf(clientDataDTO.getProvider()));

        System.out.println("isIPTrusted: " + isIPTrusted);
        System.out.println("isDeviceTrusted: " + isDeviceTrusted);
        System.out.println("isCountryTrusted: " + isCountryTrusted);
        System.out.println("isProviderTrusted: " + isProviderTrusted);

        List<SecureLog> secureLogs = secureLogRepository
                .findByUserAndIpAndCountryAndDeviceAndBoard(
                        client,
                        clientDataDTO.getIP(),
                        clientDataDTO.getCountry(),
                        clientDataDTO.getDevice(),
                        clientDataDTO.getBoard())
                .orElse(null);


        for (SecureLog secureLog: secureLogs) {
            System.out.println(secureLog.getIp() + " " + secureLog.getCountry() + " " + secureLog.getDevice());
        }

        SecureLog secureLog = null;
        if (secureLogs != null && !secureLogs.isEmpty()) {
            secureLogs.sort(Comparator.comparing(SecureLog::getViewTime).reversed());
            secureLog = secureLogs.get(0);
        }

        if (secureLog != null && secureLog.getStatus().getName().equals(AuthStatus.OK.name())) {
            return statusRepository.findByName("OK").orElseThrow(() -> new RuntimeException("No status found")); // OK
        } else if (isIPTrusted && isDeviceTrusted && isCountryTrusted) {
            return statusRepository.findByName("OK").orElseThrow(() -> new RuntimeException("No status found")); // OK
        } else if (isIPTrusted || isDeviceTrusted || isCountryTrusted || isProviderTrusted) {
            return statusRepository.findByName("SUSPECT").orElseThrow(() -> new RuntimeException("No status found")); // SUSPECT
        } else {
            return statusRepository.findByName("HACK").orElseThrow(() -> new RuntimeException("No status found")); // HACK
        }
    }
}
