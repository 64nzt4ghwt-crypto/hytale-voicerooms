package com.howlstudio.voicerooms;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** VoiceRooms — Organize players into named teams/rooms for voice chat coordination. /room join, leave, list. */
public final class VoiceRoomsPlugin extends JavaPlugin {
    public VoiceRoomsPlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[VoiceRooms] Loading...");
        RoomManager mgr=new RoomManager();
        CommandManager.get().register(mgr.getRoomCommand());
        System.out.println("[VoiceRooms] Ready.");
    }
    @Override protected void shutdown(){System.out.println("[VoiceRooms] Stopped.");}
}
