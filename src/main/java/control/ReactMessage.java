package control;

import net.dv8tion.jda.api.entities.MessageReaction;

public interface ReactMessage {

    boolean react(MessageReaction react);
    void cleanup();

}
