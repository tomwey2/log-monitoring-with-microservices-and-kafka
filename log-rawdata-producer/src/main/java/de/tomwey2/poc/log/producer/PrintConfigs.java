package de.tomwey2.poc.log.producer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PrintConfigs {
    @Value("${client.app}")
    private String clientApp;
    @Value("${client.mode}")
    private String clientMode;

    @PostConstruct
    public void printConfiguration() {
        System.out.println("Client data:");
        System.out.println("client.app=" + clientApp);
        System.out.println("client.mode=" + clientMode);
    }
}
