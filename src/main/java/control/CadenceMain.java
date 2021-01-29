package control;

import command.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

public class CadenceMain {

    private final String botToken;
    private final HashMap<String, Command> commandMap;
    private final static Logger log = LoggerFactory.getLogger(CadenceMain.class);

    public static void main(String[] args) {
        try {
            String token = getToken();
            HashMap<String, Command> map = getCommandMap();
            CadenceMain main = new CadenceMain(token, map);
            main.start();
        } catch (Exception ex) {
            log.error("Unexpected exception while initializing!", ex);
        }
    }

    public CadenceMain(String token, HashMap<String, Command> commandMap) {
        botToken = token;
        this.commandMap = commandMap;
    }

    public void start() throws LoginException {
        JDA jda = JDABuilder.createDefault(botToken).build();
        jda.addEventListener(new CadenceListener(commandMap));
    }

    private static String getToken() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("token"));
        return reader.readLine();
    }

    private static HashMap<String, Command> getCommandMap() {
        Reflections reflections = new Reflections("command");
        Set<Class<? extends Command>> subtypes = reflections.getSubTypesOf(Command.class);
        HashMap<String, Command> map = new HashMap<>();

        for (Class<? extends Command> c : subtypes){
            try {
                Command command = c.getConstructor().newInstance();
                map.put(command.getKeyword(), command);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
                log.error("Unexpected exception while loading command!", ex);
            }
        }

        return map;
    }

}
