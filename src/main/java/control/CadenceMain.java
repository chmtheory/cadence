package control;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CadenceMain {

    private final String botToken;
    private final static Logger log = LoggerFactory.getLogger(CadenceMain.class);

    public static void main(String[] args) {

        try {
            String token = getToken();
            CadenceMain main = new CadenceMain(token);
            main.start();
        } catch (Exception ex) {
            log.error("Unexpected exception while initializing!", ex);
        }

    }

    public CadenceMain(String token) {
        botToken = token;
    }

    public void start() throws LoginException {
        JDA jda = JDABuilder.createDefault(botToken).build();
        jda.addEventListener(new CadenceListener());
    }

    private static String getToken() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("token"));
        return reader.readLine();
    }

}
