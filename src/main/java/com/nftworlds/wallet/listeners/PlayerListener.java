package com.nftworlds.wallet.listeners;

import com.nftworlds.wallet.NFTWorlds;
import com.nftworlds.wallet.event.PlayerWalletReadyEvent;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.qrmaps.QRMapManager;
import com.nftworlds.wallet.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final NFTWorlds plugin;

    public PlayerListener() {
        this.plugin = NFTWorlds.getInstance();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        new NFTPlayer(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        new PlayerWalletReadyEvent(event.getPlayer()).callEvent();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        if (!NFTPlayer.getByUUID(uuid).isLinked()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                final Player player = Bukkit.getPlayer(uuid);
                if (Objects.isNull(player) || !player.isOnline())
                    return;

                final String noLinkedWallet = NFTWorlds.getInstance().getLangConfig().getNoLinkedWallet();
                final Component text = Component.text(ColorUtil.rgb(noLinkedWallet))
                        .clickEvent(ClickEvent.openUrl("https://nftworlds.com/login"));
                player.sendMessage(text);

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            }, 20L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        NFTPlayer.remove(player.getUniqueId());
        restoreItemReplacedWithMap(player);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            restoreItemReplacedWithMap(event.getPlayer());
        }
    }

    /**
     * If the player has a previous item, set it back to their inventory
     *
     * @param player The player who is holding the map.
     */
    private void restoreItemReplacedWithMap(@NotNull Player player) {
        final ItemStack previousItem = QRMapManager.playerPreviousItem.get(player.getUniqueId());
        if (Objects.isNull(previousItem))
            return;

        player.getInventory().setItem(0, previousItem);
        QRMapManager.playerPreviousItem.remove(player.getUniqueId());
    }

}
