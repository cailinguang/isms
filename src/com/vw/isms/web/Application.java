package com.vw.isms.web;

import com.vw.isms.RepositoryException;
import com.vw.isms.standard.model.User;
import com.vw.isms.standard.repository.JdbcStandardRepository;
import com.vw.isms.standard.repository.StandardRepository;
import com.vw.isms.standard.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;

import com.vw.isms.util.BackupApplicationUtil;
import com.vw.isms.util.ZipUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class Application {
    private String basicInfoPath;
    private String uploadDir;
    private String databasePath;
    private String backupPath;

    private String restoreDbPath;

    public Application()
            throws IOException {
        if (!StringUtils.isEmpty(System.getProperty("root.dir"))) {
            this.uploadDir = (System.getProperty("root.dir") + "/upload");
            this.databasePath = (System.getProperty("root.dir") + "/db");
            this.backupPath = (System.getProperty("root.dir") + "/backup");
            this.basicInfoPath = (System.getProperty("root.dir") + "/basic_info.html");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            this.uploadDir = "c:/isms/upload";
            this.databasePath = "c:/isms/db";
            this.backupPath = "c:/isms/backup";
            this.basicInfoPath = "c:/isms/basic_info.html";
        } else {
            this.uploadDir = "/var/tmp/isms/upload";
            this.databasePath = "/var/tmp/isms/db";
            this.backupPath = "/var/tmp/isms/backup";
            this.basicInfoPath = "/var/tmp/isms/basic_info.html";
        }

        if(!StringUtils.isEmpty(System.getProperty(BackupApplicationUtil.SP_restore_key))){
            String zipFile = System.getProperty(BackupApplicationUtil.SP_restore_key);
            String tempUNZipPath = this.backupPath + File.separator + "temp";

            File tempFile = new File(tempUNZipPath);
            if(tempFile.exists()){
                FileUtils.deleteDirectory(tempFile);
            }
            tempFile.mkdirs();

            File uploadFile = new File(this.uploadDir);
            if(uploadFile.exists()){
                FileUtils.deleteDirectory(uploadFile);
            }
            uploadFile.mkdirs();

            ZipUtil.upzipFile(new File(zipFile),tempUNZipPath);

            FileUtils.copyDirectory(new File(tempUNZipPath+File.separator+"upload"),uploadFile);



            this.restoreDbPath = tempUNZipPath + File.separator + "db";
        }
    }

    @Bean
    public String basicInfoPath() {
        return this.basicInfoPath;
    }

    @Bean
    public String backupPath() {
        return this.backupPath;
    }

    @Bean
    public String uploadDir() {
        return this.uploadDir;
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
        if(this.restoreDbPath!=null){
            ds.setUrl(ds.getUrl()+";restoreFrom="+this.restoreDbPath);
        }
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

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST,"/400"));
                container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN,"/403"));
                container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"/404"));
            }
        };
    }

    //update database
    public static void updateDatabase(ConfigurableApplicationContext context) {
        System.out.println("Updating database.");
        JdbcTemplate template = (JdbcTemplate) context.getBean(JdbcTemplate.class);

    }

    public static void main(String[] args)
            throws RepositoryException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        try {
            updateDatabase(context);
        } catch (Exception localException) {
        }
        if (System.getProperty("resetAdmin") != null) {
            UserRepository repo = (UserRepository) context.getBean(UserRepository.class);
            try {
                repo.changePassword("useradmin", "useradmin");
                System.out.println("admin password has been reset to admin");
            } catch (RepositoryException e) {
                User admin = new User();
                admin.setUserName("useradmin");
                admin.setRole("Admin");
                admin.setPassword("useradmin");
                repo.createUser(admin);
                repo.changePassword("useradmin", "useradmin");
                System.out.println("Created admin with password admin");
            }
        }
    }
}
