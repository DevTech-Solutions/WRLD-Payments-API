package com.nftworlds.wallet.menus;

import com.nftworlds.wallet.util.ColorUtil;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.StaticMenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CloseItem extends StaticMenuItem {

    public CloseItem() {
        super(ColorUtil.rgb("&c&lClose"), new ItemStack(Material.BARRIER), new String[0]);
    }

    /**
     * If you want to close the menu when an item is clicked, set the event's willClose property to true.
     *
     * @param event The event object.
     */
    public void onItemClick(@NotNull ItemClickEvent event) {
        event.setWillClose(true);
    }
}

