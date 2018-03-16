package com.vw.isms.web;

import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.User;
import com.vw.isms.standard.repository.JdbcStandardRepository;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.standard.repository.UserRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class Application {
    private String basicInfoPath;
    private String uploadDir;
    private String databasePath;

    public Application()
            throws IOException {
        if (!StringUtils.isEmpty(System.getProperty("root.dir"))) {
            this.uploadDir = (System.getProperty("root.dir") + "/upload");
            this.databasePath = (System.getProperty("root.dir") + "/db");
            this.basicInfoPath = (System.getProperty("root.dir") + "/basic_info.html");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            this.uploadDir = "c:/isms/upload";
            this.databasePath = "c:/isms/db";
            this.basicInfoPath = "c:/isms/basic_info.html";
        } else {
            this.uploadDir = "/var/tmp/isms/upload";
            this.databasePath = "/var/tmp/isms/db";
            this.basicInfoPath = "/var/tmp/isms/basic_info.html";
        }
    }

    @Bean
    public String basicInfoPath() {
        return this.basicInfoPath;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("1024MB");
        factory.setMaxRequestSize("1024MB");
        return factory.createMultipartConfig();
    }

    @Bean
    @Autowired
    public StandardRepository repository(DataSource dataSource)
            throws SQLException {
        JdbcStandardRepository repo = new JdbcStandardRepository(new JdbcTemplate(dataSource));
        repo.setUploadDir(this.uploadDir);
        return repo;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        ds.setUrl("jdbc:derby:" + this.databasePath);
        ds.setUsername("isms");
        ds.setPassword("isms");
        return ds;
    }

    @Bean
    @Autowired
    public UserRepository userRepository(DataSource dataSource)
            throws SQLException {
        UserRepository repo = new UserRepository(new JdbcTemplate(dataSource));
        return repo;
    }

    public static void updateDatabase(ConfigurableApplicationContext context) {
        System.out.println("Updating database.");
        JdbcTemplate template = (JdbcTemplate) context.getBean(JdbcTemplate.class);
        template.execute("ALTER TABLE APP.ISMS_USERS ADD COLUMN REALNAME VARCHAR(64)");
        template.execute("ALTER TABLE APP.ISMS_USERS ADD COLUMN TEL VARCHAR(64)");
        template.execute("ALTER TABLE APP.ISMS_USERS ADD COLUMN DEPARTMENT VARCHAR(64)");
        template.execute("ALTER TABLE APP.ISMS_USERS ADD COLUMN EMAIL VARCHAR(64)");
    }

    public static void main(String[] args)
            throws RepositoryException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        /*try {
            updateDatabase(context);
        } catch (Exception localException) {
        }*/
        if (System.getProperty("resetAdmin") != null) {
            UserRepository repo = (UserRepository) context.getBean(UserRepository.class);
            try {
                User admin = repo.getUser("admin");
                repo.changePassword(admin, "admin");
                System.out.println("admin password has been reset to admin");
            } catch (RepositoryException e) {
                User admin = new User();
                admin.setUserName("admin");
                admin.setRole("Admin");
                admin.setPassword("admin");
                repo.createUser(admin);
                repo.changePassword(admin, "admin");
                System.out.println("Created admin with password admin");
            }
        }
    }
}
