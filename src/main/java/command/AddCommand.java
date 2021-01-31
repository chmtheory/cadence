package command;

import control.Server;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.DiscordUtil;
import util.YoutubeUtil;

public class AddCommand implements Command {
    @Override
    public void execute(GuildMessageReceivedEvent event, Server server) {
        String[] args = DiscordUtil.parseArgumentsFromMessage(event.getMessage());

        if (args.length == 0) {
            DiscordUtil.sendMessage(event, "No argument specified!");
            return;
        }
        // Maybe handle too many arguments case?

        String id = YoutubeUtil.extractYoutubeVideoIdFromMessage(args[0]);
        if (id == null) {
            DiscordUtil.sendMessage(event, "This doesn't appear to be a valid Youtube video link!");
        } else if (server.hasTrack(id)) {
            // The server already has an entry with this id.
            DiscordUtil.sendMessage(event, "This track has already been added!");
        } else {
            String name = YoutubeUtil.getVideoName(id);
            if (name == null) {
                name = ""; // I'm not sure if this case will ever come up. Safety!
            }
            server.addTrack(id, name);
            DiscordUtil.sendMessage(event, "Added track " + name + " successfully!");
        }
    }

    @Override
    public String getKeyword() {
        return "add";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }
}
