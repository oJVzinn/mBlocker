package me.meiallu.mblocker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MBlocker extends JavaPlugin implements Listener {
    String lastTab = "";

    @Override
    public void onEnable() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        getServer().getPluginManager().registerEvents(this, this);

        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                String msg = e.getPacket().getStrings().read(0);

                if ( !lastTab.equals(e.getPlayer().getName() + " issued server tab: " + msg) ) {
                    lastTab = e.getPlayer().getName() + " issued server tab: " + msg;
                    System.out.println(lastTab);
                }

                if ( msg.contains(":") ) {
                    e.setCancelled(true);
                } else if ( msg.startsWith("/sk") || msg.startsWith("/ver") || msg.startsWith("/about") || msg.startsWith("/?") || msg.startsWith("/help") ) {
                    e.setCancelled(true);
                }
            }
        });

        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.TAB_COMPLETE) {
            @Override
            public void onPacketSending(PacketEvent e) {
                String[] v = e.getPacket().getStringArrays().read(0);
                for (int i = 0; i < v.length; i++) {
                    if ( v[i].contains(":") ) {
                        v[i] = "";
                    }
                }
                e.getPacket().getStringArrays().write(0, v);
            }
        });
    }

    public String printMsg(String msg) { return ChatColor.translateAlternateColorCodes('&', msg); }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String m = e.getMessage().toLowerCase();
        String split = m.split(" ", 2)[0];
        Player p = e.getPlayer();

        if ( m.startsWith("/say ") || m.startsWith("/me ") || m.startsWith("/about") || m.startsWith("/help") || m.startsWith("/icanhasbukkit") || m.startsWith("/info") || m.startsWith("/?") || m.startsWith("/pl") || m.startsWith("/ver") ) {
            if ( !m.startsWith("/play") ) {
                e.setCancelled(true);
                p.sendMessage( paintMsg("&c&lERRO! &cVocê não tem permissão para executar isso.") );
            }
        } else if ( split.contains(":") ) {
            e.setCancelled(true);
            p.sendMessage( paintMsg("&c&lERRO! &cComandos com \":\" estão bloqueados.") );
        } else if ( m.startsWith("/op ") || m.startsWith("/deop ") || m.startsWith("/stop ") || m.startsWith("/reload ") || m.startsWith("/restart ") ) {
            e.setCancelled(true);
            p.sendMessage( paintMsg("&c&lERRO! &cVocê só pode fazer isso pelo console.") );
        } else if ( m.startsWith("/sk") && !p.hasPermission("op") ) {
            e.setCancelled(true);
            p.sendMessage("Comando não encontrado.");
        } else if ( m.startsWith("/lp ") || m.startsWith("/luckperms ") ) {
            if ( m.contains("clear") || m.contains("permission") || m.contains("builder") || m.contains("studio") || m.contains("admin") || m.contains("dono") || m.contains("mod") || m.contains("trial") || m.contains("plus") ) {
                e.setCancelled(true);
                p.sendMessage( paintMsg("&c&lERRO! &cVocê só pode fazer isso pelo console.") );
            }
        }
    }
}
