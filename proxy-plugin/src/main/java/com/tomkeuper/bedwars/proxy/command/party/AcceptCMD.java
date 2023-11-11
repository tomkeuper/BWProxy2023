package com.tomkeuper.bedwars.proxy.command.party;

import com.tomkeuper.bedwars.proxy.api.command.SubCommand;
import com.tomkeuper.bedwars.proxy.api.Messages;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.tomkeuper.bedwars.proxy.language.Language.getMsg;

public class AcceptCMD extends SubCommand {

    public AcceptCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;
        if (args.length < 1) {
            return;
        }
        if (BedWarsProxy.getParty().hasParty(p.getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_DENIED_ALREADY_IN_PARTY));
            return;
        }
        if (Bukkit.getPlayer(args[0]) == null || !Bukkit.getPlayer(args[0]).isOnline()) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INVITE_DENIED_PLAYER_OFFLINE).replace("{player}", args[0]));
            return;
        }
        if (!PartyCommand.getPartySessionRequest().containsKey(Bukkit.getPlayer(args[0]).getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE));
            return;
        }
        if (PartyCommand.getPartySessionRequest().get(Bukkit.getPlayer(args[0]).getUniqueId()).toString().equalsIgnoreCase(p.getUniqueId().toString())) {
            PartyCommand.getPartySessionRequest().remove(Bukkit.getPlayer(args[0]).getUniqueId());
            if (BedWarsProxy.getParty().hasParty(Bukkit.getPlayer(args[0]).getUniqueId())) {
                BedWarsProxy.getParty().addMember(Bukkit.getPlayer(args[0]).getUniqueId(), p);
                Player pl;
                for (UUID on : BedWarsProxy.getParty().getMembers(Bukkit.getPlayer(args[0]).getUniqueId())) {
                    pl = Bukkit.getPlayer(on);
                    if (pl == null) continue;
                    pl.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_SUCCESS).replace("{player}", p.getName()));
                }
            } else {
                BedWarsProxy.getParty().createParty(Bukkit.getPlayer(args[0]), p);
                Player pl;
                for (UUID on : BedWarsProxy.getParty().getMembers(Bukkit.getPlayer(args[0]).getUniqueId())) {
                    pl = Bukkit.getPlayer(on);
                    if (pl == null) continue;
                    pl.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_SUCCESS).replace("{player}", p.getName()));
                }
            }
        } else {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE));
        }
    }
}
