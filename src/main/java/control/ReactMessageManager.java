package control;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.concurrent.TimeUnit;

public class ReactMessageManager {

    static {
        reactMap = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).
                removalListener(remove -> ((ReactMessage) remove.getValue()).cleanup()).build();
    }

    private static Cache<Long, ReactMessage> reactMap;


    public void addReactMessage(long id, ReactMessage reactMessage) {
        reactMap.put(id, reactMessage);
    }

    public void react(long id, MessageReaction react) {
        ReactMessage reactMessage = reactMap.getIfPresent(id);

        if (reactMessage != null) {
            if (reactMessage.react(react)) {
                reactMap.invalidate(id);
            }
        }
    }


}
