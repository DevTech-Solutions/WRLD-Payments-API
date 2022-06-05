package com.nftworlds.wallet.menus;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.util.ColorUtil;
import com.nftworlds.wallet.util.NumberUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BalanceItem extends MenuItem {
    private static final ChatColor LIME = ChatColor.of("#4DFB4A");
    private static final ChatColor AQUA = ChatColor.of("#A9D9FB");

    private static final String DISPLAY_NAME = ColorUtil.rgb(LIME + "&l$WRLD Balance");
    private static final ItemStack ICON = new ItemStack(Material.EMERALD);

    public BalanceItem() {
        super(DISPLAY_NAME, ICON);
    }

    /**
     * When the user clicks on an item in the list,
     * we want to show the details of that item
     *
     * @param event The event that was triggered.
     */
    @Override
    public void onItemClick(ItemClickEvent event) {

    }

    /**
     * Get the final icon for the player,
     * and add the player's balance to the lore.
     *
     * @param player The player who is viewing the menu
     * @return The final icon of the item.
     */
    @Override
    public ItemStack getFinalIcon(Player player) {
        ItemStack finalIcon = super.getFinalIcon(player);
        try {
            double balance = NFTPlayer.getByUUID(player.getUniqueId()).getPrimaryWallet().getPolygonWRLDBalance();
            ItemMeta meta = finalIcon.getItemMeta();
            List<Component> lore = List.of(
                    Component.text(ColorUtil.rgb(AQUA.toString() + NumberUtil.round(balance, 3) + " $WRLD"))
            );
            meta.lore(lore);
            finalIcon.setItemMeta(meta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalIcon;
    }
}
