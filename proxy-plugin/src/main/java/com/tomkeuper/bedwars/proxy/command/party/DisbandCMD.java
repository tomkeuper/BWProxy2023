package com.tomkeuper.bedwars.proxy.command.party;

import com.tomkeuper.bedwars.proxy.api.command.SubCommand;
import com.tomkeuper.bedwars.proxy.api.Messages;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.tomkeuper.bedwars.proxy.language.Language.getMsg;

public class DisbandCMD extends SubCommand {

    public DisbandCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;

        if (!BedWarsProxy.getParty().hasParty(p.getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
            return;
        }
        if (!BedWarsProxy.getParty().isOwner(p.getUniqueId())) {
            p.sendMessage(getMsg(p, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
            return;
        }
        BedWarsProxy.getParty().disband(p.getUniqueId());
    }
}
