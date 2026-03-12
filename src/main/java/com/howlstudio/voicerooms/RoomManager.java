package com.howlstudio.voicerooms;
import com.hypixel.hytale.component.Ref; import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class RoomManager {
    private final Map<String,Set<UUID>> rooms=new ConcurrentHashMap<>();
    private final Map<UUID,String> playerRoom=new ConcurrentHashMap<>();
    private final Map<UUID,String> playerName=new ConcurrentHashMap<>();
    public AbstractPlayerCommand getRoomCommand(){
        return new AbstractPlayerCommand("room","Team/voice room grouping. /room join <name> | leave | list | who <name>"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                playerName.put(playerRef.getUuid(),playerRef.getUsername());
                String[]args=ctx.getInputString().trim().split("\\s+",2); String sub=args.length>0?args[0].toLowerCase():"list";
                switch(sub){
                    case"join"->{
                        if(args.length<2){playerRef.sendMessage(Message.raw("Usage: /room join <name>"));break;}
                        String roomName=args[1].toLowerCase();
                        String current=playerRoom.get(playerRef.getUuid());
                        if(current!=null){rooms.getOrDefault(current,new HashSet<>()).remove(playerRef.getUuid());}
                        rooms.computeIfAbsent(roomName,k->ConcurrentHashMap.newKeySet()).add(playerRef.getUuid());
                        playerRoom.put(playerRef.getUuid(),roomName);
                        Set<UUID> members=rooms.get(roomName);
                        playerRef.sendMessage(Message.raw("[Room] Joined §6"+roomName+"§r ("+members.size()+" members)"));
                        for(UUID uid:members){if(!uid.equals(playerRef.getUuid())){for(PlayerRef p:Universe.get().getPlayers())if(p.getUuid().equals(uid)){p.sendMessage(Message.raw("[Room] §e"+playerRef.getUsername()+"§r joined §6"+roomName));break;}}}
                    }
                    case"leave"->{
                        String r=playerRoom.remove(playerRef.getUuid());
                        if(r==null){playerRef.sendMessage(Message.raw("[Room] Not in a room."));break;}
                        rooms.getOrDefault(r,new HashSet<>()).remove(playerRef.getUuid());
                        playerRef.sendMessage(Message.raw("[Room] Left §6"+r+"§r."));
                    }
                    case"list"->{
                        if(rooms.isEmpty()){playerRef.sendMessage(Message.raw("[Room] No active rooms."));break;}
                        playerRef.sendMessage(Message.raw("=== Active Rooms ==="));
                        for(var e:rooms.entrySet()){if(!e.getValue().isEmpty())playerRef.sendMessage(Message.raw("  §6"+e.getKey()+"§r ("+e.getValue().size()+" members)"));}
                    }
                    case"who"->{
                        if(args.length<2)break;
                        String rn=args[1].toLowerCase();Set<UUID> m=rooms.get(rn);
                        if(m==null||m.isEmpty()){playerRef.sendMessage(Message.raw("[Room] Room empty or not found: "+rn));break;}
                        playerRef.sendMessage(Message.raw("[Room] §6"+rn+"§r members ("+m.size()+"):"));
                        for(UUID uid:m)playerRef.sendMessage(Message.raw("  "+playerName.getOrDefault(uid,"?")));
                    }
                    case"say"->{
                        String r=playerRoom.get(playerRef.getUuid());if(r==null){playerRef.sendMessage(Message.raw("[Room] Join a room first."));break;}
                        String msg=args.length>1?args[1]:"";if(msg.isEmpty())break;
                        String fmt="[Room:§6"+r+"§r] §e"+playerRef.getUsername()+"§r: "+msg;
                        for(UUID uid:rooms.getOrDefault(r,Set.of()))for(PlayerRef p:Universe.get().getPlayers())if(p.getUuid().equals(uid)){p.sendMessage(Message.raw(fmt));break;}
                    }
                    default->playerRef.sendMessage(Message.raw("Usage: /room join <name> | leave | list | who <name> | say <msg>"));
                }
            }
        };
    }
}
