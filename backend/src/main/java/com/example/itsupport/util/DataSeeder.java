package com.example.itsupport.util;

import com.example.itsupport.entity.*;
import com.example.itsupport.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;
    private final IncidentCommentRepository commentRepository;
    private final IncidentHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data seeding.");
            return;
        }

        log.info("Starting initial seed data population for B.Tech project demo...");

        String defaultPass = passwordEncoder.encode("Password123!");

        // 1. Create Users
        User admin = userRepository.save(User.builder()
                .name("System Administrator")
                .email("admin@itsupport.com")
                .password(defaultPass)
                .role(Role.ROLE_ADMIN)
                .department("IT Operations")
                .active(true)
                .build());

        User agentSmith = userRepository.save(User.builder()
                .name("Agent John Smith")
                .email("agent.smith@itsupport.com")
                .password(defaultPass)
                .role(Role.ROLE_SUPPORT_AGENT)
                .department("IT Helpdesk Tier 2")
                .active(true)
                .build());

        User agentJones = userRepository.save(User.builder()
                .name("Agent Sarah Jones")
                .email("agent.jones@itsupport.com")
                .password(defaultPass)
                .role(Role.ROLE_SUPPORT_AGENT)
                .department("Network & Infrastructure")
                .active(true)
                .build());

        User emp1 = userRepository.save(User.builder()
                .name("John Doe")
                .email("john.doe@enterprise.com")
                .password(defaultPass)
                .role(Role.ROLE_EMPLOYEE)
                .department("Sales & Marketing")
                .active(true)
                .build());

        User emp2 = userRepository.save(User.builder()
                .name("Jane Smith")
                .email("jane.smith@enterprise.com")
                .password(defaultPass)
                .role(Role.ROLE_EMPLOYEE)
                .department("Human Resources")
                .active(true)
                .build());

        User emp3 = userRepository.save(User.builder()
                .name("Mike Wilson")
                .email("mike.wilson@enterprise.com")
                .password(defaultPass)
                .role(Role.ROLE_EMPLOYEE)
                .department("Software Engineering")
                .active(true)
                .build());

        User emp4 = userRepository.save(User.builder()
                .name("Sarah Connor")
                .email("sarah.connor@enterprise.com")
                .password(defaultPass)
                .role(Role.ROLE_EMPLOYEE)
                .department("Finance & Payroll")
                .active(true)
                .build());

        User emp5 = userRepository.save(User.builder()
                .name("David Miller")
                .email("david.miller@enterprise.com")
                .password(defaultPass)
                .role(Role.ROLE_EMPLOYEE)
                .department("Logistics & Supply Chain")
                .active(true)
                .build());

        log.info("Created 8 default user accounts with password 'Password123!'");

        // 2. Seed 30+ Incidents with Realistic Content
        seedIncidents(emp1, emp2, emp3, emp4, emp5, agentSmith, agentJones, admin);

        log.info("Data seeding completed successfully!");
    }

    private void seedIncidents(User emp1, User emp2, User emp3, User emp4, User emp5, User agentSmith, User agentJones, User admin) {
        Object[][] incidentData = {
            {"Unable to connect to office Wi-Fi network in Block B", "Since morning my laptop shows connected no internet on corporate Wi-Fi.", Category.Network, Priority.HIGH, Status.IN_PROGRESS, emp1, agentJones, "Block B Floor 2"},
            {"Laptop screen flickering and turns completely black", "Dell Latitude 5420 monitor screen flickers whenever HDMI is connected.", Category.Hardware, Priority.HIGH, Status.ASSIGNED, emp3, agentSmith, "Engineering Bay 4"},
            {"Password reset requested for internal HR portal account", "Forgot password for self-service portal after returning from leave.", Category.Access_Login, Priority.LOW, Status.RESOLVED, emp2, agentSmith, "HR Department"},
            {"Outlook application crashing continuously when sending emails", "Outlook 365 crashes whenever attaching files larger than 5MB.", Category.Software, Priority.MEDIUM, Status.NEW, emp4, null, "Finance Tower 3rd Floor"},
            {"Cannot send or receive emails from corporate email client", "Server error 550 Mailbox unavailable returned for all external domains.", Category.Email, Priority.HIGH, Status.IN_PROGRESS, emp1, agentSmith, "Sales Executive Desk"},
            {"Paper jam in main department laser printer on 3rd floor", "HP LaserJet Pro jam error inside roller compartment.", Category.Printer, Priority.LOW, Status.RESOLVED, emp2, agentJones, "HR Floor 3 Copy Room"},
            {"Database query timing out on sales analytics database cluster", "SQL queries against sales_fact table taking over 120s timing out web UI.", Category.Database, Priority.CRITICAL, Status.IN_PROGRESS, emp3, agentJones, "Dev Operations Center"},
            {"Suspicious phishing email received with suspicious link", "Received urgent wire transfer request email from spoofed CEO domain.", Category.Security, Priority.CRITICAL, Status.RESOLVED, emp4, agentJones, "Finance Admin Suite"},
            {"Need software installation permission for VS Code development tool", "Requesting admin privilege escalation to install VS Code & Node.js.", Category.Software, Priority.LOW, Status.CLOSED, emp3, agentSmith, "R&D Software Lab"},
            {"VPN connection dropping randomly during remote work", "Cisco AnyConnect VPN disconnects every 15 minutes on home Wi-Fi.", Category.Network, Priority.HIGH, Status.NEW, emp5, null, "Remote Work Desk"},
            {"Monitor power button not responding and display is dead", "LG 27 inch dual monitor has no power light indicator.", Category.Hardware, Priority.LOW, Status.ASSIGNED, emp1, agentSmith, "Sales Room 102"},
            {"Two-factor authentication 2FA code not received on mobile phone", "Authenticator push notification expired unable to log into cloud SSO.", Category.Access_Login, Priority.HIGH, Status.RESOLVED, emp2, agentJones, "HR Admin Office"},
            {"MySQL connection pool exhausted error in production app", "Spring Boot API throwing CannotGetJdbcConnectionException pool max 50.", Category.Database, Priority.CRITICAL, Status.IN_PROGRESS, emp3, agentJones, "Server Ops Center"},
            {"Network printer offline error across entire finance department", "Canon iR-ADV C3530 printing queued jobs stuck in spooling status.", Category.Printer, Priority.MEDIUM, Status.ASSIGNED, emp4, agentSmith, "Finance Department"},
            {"Ransomware threat message displayed on computer screen", "Workstation PC-402 showing encrypted file popup immediate isolation required.", Category.Security, Priority.CRITICAL, Status.RESOLVED, emp5, agentJones, "Logistics Warehouse"},
            {"Barcode printer printing blank labels in logistics warehouse", "Zebra ZD421 thermal printer printing blank white sticker labels.", Category.Printer, Priority.HIGH, Status.NEW, emp5, null, "Dispatch Bay 1"},
            {"Blue screen of death BSOD error on startup Windows 11", "KERNEL_DATA_INPAGE_ERROR 0x0000007A stop code on startup.", Category.Software, Priority.HIGH, Status.IN_PROGRESS, emp1, agentSmith, "Sales Block B"},
            {"Office 365 license expired notification showing in Word", "Product Unlicensed dialog popping up on MS Office apps.", Category.Software, Priority.MEDIUM, Status.CLOSED, emp2, agentSmith, "HR Office 201"},
            {"Cannot connect to company intranet or local network shares", "System error 53 Network path not found for //fileserver/shared.", Category.Network, Priority.HIGH, Status.NEW, emp4, null, "Finance Room 304"},
            {"Mouse double-clicking automatically and scroll wheel faulty", "Logitech optical mouse left click button registering double clicks.", Category.Hardware, Priority.LOW, Status.RESOLVED, emp5, agentSmith, "Warehouse Desk 3"},
            {"Biometric attendance scanner not recognizing fingerprint", "Main entrance fingerprint scanner throwing sensor timeout error.", Category.Access_Login, Priority.MEDIUM, Status.ASSIGNED, emp2, agentJones, "Ground Floor Lobby"},
            {"Active Directory account disabled by administrator", "Account locked out error message on Windows Ctrl+Alt+Del login screen.", Category.Access_Login, Priority.HIGH, Status.RESOLVED, emp3, agentSmith, "Software Lab 2"},
            {"PostgreSQL database disk space 95% full clean up required", "Partition /var/lib/postgresql/data reaching max disk capacity limit.", Category.Database, Priority.HIGH, Status.IN_PROGRESS, emp3, agentJones, "Data Center Rack 4"},
            {"Docker container failing to start due to port binding issue", "Error response from daemon port 8080 already in use by java process.", Category.Software, Priority.MEDIUM, Status.RESOLVED, emp3, agentSmith, "R&D Workstation 9"},
            {"Spam emails flooding inbox despite junk mail filter", "Over 50 marketing spam emails landing in inbox within 2 hours.", Category.Email, Priority.MEDIUM, Status.NEW, emp1, null, "Sales Team Area"},
            {"External display showing no signal on ceiling projector", "HDMI wall plate connection to Epson boardroom projector dead.", Category.Hardware, Priority.LOW, Status.NEW, emp2, null, "Executive Boardroom"},
            {"SSL certificate expired on enterprise staging website", "NET::ERR_CERT_DATE_INVALID warning browser blocking access.", Category.Security, Priority.HIGH, Status.RESOLVED, emp3, agentJones, "Staging Cluster"},
            {"Keyboard spacebar stuck and missing two keycaps", "Lenovo ThinkPad keyboard replacement needed due to damaged keycaps.", Category.Hardware, Priority.LOW, Status.RESOLVED, emp4, agentSmith, "Finance Desk 12"},
            {"VoIP phone line no dial tone in customer support desk", "Cisco IP phone display shows Request Timeout on SIP registration.", Category.Network, Priority.HIGH, Status.IN_PROGRESS, emp1, agentJones, "Support Floor 1"},
            {"3D printer nozzle clogged failure to print prototype", "Ender 3 printer extrusion gear skipping filament.", Category.Printer, Priority.LOW, Status.NEW, emp3, null, "Prototyping Lab"}
        };

        for (int i = 0; i < incidentData.length; i++) {
            Object[] row = incidentData[i];
            String title = (String) row[0];
            String desc = (String) row[1];
            Category cat = (Category) row[2];
            Priority pri = (Priority) row[3];
            Status stat = (Status) row[4];
            User creator = (User) row[5];
            User agent = (User) row[6];
            String loc = (String) row[7];

            Incident incident = Incident.builder()
                    .ticketNumber("INC-" + (1001 + i))
                    .title(title)
                    .description(desc)
                    .category(cat)
                    .predictedCategory(cat)
                    .categoryConfidence(0.88 + (i % 10) * 0.01)
                    .priority(pri)
                    .predictedPriority(pri)
                    .priorityConfidence(0.82 + (i % 8) * 0.01)
                    .status(stat)
                    .createdBy(creator)
                    .assignedTo(agent)
                    .department(creator.getDepartment())
                    .location(loc)
                    .createdAt(LocalDateTime.now().minusDays(15 - (i % 14)).minusHours(i * 2))
                    .resolvedAt((stat == Status.RESOLVED || stat == Status.CLOSED) ? LocalDateTime.now().minusDays(15 - (i % 14)).plusHours(4) : null)
                    .build();

            Incident saved = incidentRepository.save(incident);

            // Audit Record
            historyRepository.save(IncidentHistory.builder()
                    .incident(saved)
                    .changedBy(creator)
                    .newStatus(Status.NEW)
                    .newPriority(pri)
                    .actionNote("Incident reported via Employee Portal")
                    .changedAt(saved.getCreatedAt())
                    .build());

            if (agent != null) {
                historyRepository.save(IncidentHistory.builder()
                        .incident(saved)
                        .changedBy(admin)
                        .oldStatus(Status.NEW)
                        .newStatus(stat == Status.NEW ? Status.NEW : Status.ASSIGNED)
                        .oldPriority(pri)
                        .newPriority(pri)
                        .actionNote("Assigned to " + agent.getName())
                        .changedAt(saved.getCreatedAt().plusMinutes(15))
                        .build());
            }

            // Seed Comments on select tickets
            if (i % 3 == 0) {
                commentRepository.save(IncidentComment.builder()
                        .incident(saved)
                        .user(agent != null ? agent : admin)
                        .comment("Under investigation by IT Support Team. Diagnostic logs requested.")
                        .createdAt(saved.getCreatedAt().plusMinutes(30))
                        .build());

                commentRepository.save(IncidentComment.builder()
                        .incident(saved)
                        .user(creator)
                        .comment("Provided screenshot and system specs as requested.")
                        .createdAt(saved.getCreatedAt().plusHours(1))
                        .build());
            }
        }
    }
}
