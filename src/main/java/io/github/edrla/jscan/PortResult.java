package io.github.edrla.jscan;

record PortResult(int port, boolean isOpen) {

    String openStatus() {
        if (isOpen) return "is OPEN";
        else return "is NOT OPEN";
    }

    String serviceName() {
        return switch (port) {
            // File Transfer & Remote Access
            case 20, 21 -> "FTP";
            case 22 -> "SSH";
            case 23 -> "Telnet";
            case 25 -> "SMTP (Email)";
            case 53 -> "DNS";
            case 67, 68 -> "DHCP";
            case 69 -> "TFTP";

            // Web Services
            case 80 -> "HTTP";
            case 110 -> "POP3 (Email)";
            case 143 -> "IMAP (Email)";
            case 443 -> "HTTPS";
            case 445 -> "SMB (File Sharing)";
            case 8080 -> "HTTP-Proxy/Alternative";
            case 8443 -> "HTTPS-Alternative";

            // Database Services
            case 1433 -> "MS SQL Server";
            case 1521 -> "Oracle DB";
            case 3306 -> "MySQL/MariaDB";
            case 5432 -> "PostgreSQL";
            case 6379 -> "Redis";
            case 27017 -> "MongoDB";

            // Infrastructure & Monitoring
            case 123 -> "NTP (Time)";
            case 161, 162 -> "SNMP";
            case 389 -> "LDAP";
            case 636 -> "LDAPS";
            case 2375, 2376 -> "Docker API";
            case 9092 -> "Kafka";

            // Remote Desktop & Gaming
            case 3389 -> "RDP (Remote Desktop)";
            case 5900 -> "VNC";
            case 25565 -> "Minecraft";

            default -> "Unknown Service";
        };
    }
}
