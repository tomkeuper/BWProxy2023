package com.tomkeuper.bedwars.proxy.command.party;

import com.tomkeuper.bedwars.proxy.api.Language;
import com.tomkeuper.bedwars.proxy.api.command.SubCommand;
import com.tomkeuper.bedwars.proxy.api.Messages;
import com.tomkeuper.bedwars.proxy.BedWarsProxy;
import com.tomkeuper.bedwars.proxy.api.party.Party;
import com.tomkeuper.bedwars.proxy.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ListCMD extends SubCommand {

    public ListCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return;
        Player p = (Player) s;

        Language l = LanguageManager.get().getPlayerLanguage(p);
        if (!BedWarsProxy.getParty().hasParty(p.getUniqueId())) {
            p.sendMessage(l.getMsg(Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
            return;
        }

        Party partyManager = BedWarsProxy.getParty();
        UUID partyOwnerUUID = partyManager.getOwner(p.getUniqueId());

        for (String msg: l.getList(Messages.COMMAND_PARTY_LIST_MESSAGE)) {
            String message = msg.replace("{owner}", (Bukkit.getPlayer(partyOwnerUUID) != null) ? Bukkit.getPlayer(partyOwnerUUID).getDisplayName() : Bukkit.getOfflinePlayer(partyOwnerUUID).getName());
            message = message.replace("{size}", String.valueOf(partyManager.getMembers(p.getUniqueId()).size()));
            message = message.replace("{members}", String.join(" ", partyManager.getMembers(p.getUniqueId())
                    .stream()
                    .filter(uuid -> !uuid.equals(partyOwnerUUID)) // Exclude party owner
                    .map(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        return (player != null) ? player.getDisplayName() : "§7" + Bukkit.getOfflinePlayer(uuid).getName();
                    })
                    .toArray(String[]::new)));

            if (message.contains("{members_as_list}")){
                for (UUID partyMember: partyManager.getMembers(p.getUniqueId())) {
                    p.sendMessage(l.getMsg(Messages.COMMAND_PARTY_LIST_MEMBER_LIST_ITEM).replace("{member}",Bukkit.getOfflinePlayer(partyMember).getName()));
                }
                continue; // Skip this line because we already sent the members list.
            }

            p.sendMessage(message);

        }
    }
}
