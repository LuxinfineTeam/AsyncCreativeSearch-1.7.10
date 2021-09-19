package ml.luxinfine.asynccreative;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class SearchHandler {
    private static CompletableFuture<Void> SEARCH_TASK;

    public static void search(GuiContainerCreative gui) {
        stopSearch();
        GuiContainerCreative.ContainerCreative containercreative = (GuiContainerCreative.ContainerCreative)gui.inventorySlots;
        containercreative.itemList.clear();
        SEARCH_TASK = CompletableFuture.runAsync(() -> {
            CreativeTabs tab = CreativeTabs.creativeTabArray[GuiContainerCreative.selectedTabIndex];
            if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch) {
                tab.displayAllReleventItems(containercreative.itemList);
                filterItems(gui, containercreative);
                return;
            }
            for (Object o : Item.itemRegistry) {
                Item item = (Item) o;
                if (item != null && item.getCreativeTab() != null) {
                    item.getSubItems(item, null, containercreative.itemList);
                }
            }
            filterItems(gui, containercreative);
        });
    }

    private static void filterItems(GuiContainerCreative gui, GuiContainerCreative.ContainerCreative container) {
        Enchantment[] aenchantment = Enchantment.enchantmentsList;
        int j = aenchantment.length;

        if (CreativeTabs.creativeTabArray[GuiContainerCreative.selectedTabIndex] != CreativeTabs.tabAllSearch) j = 0; //Forge: Don't add enchants to custom tabs.
        for (int i = 0; i < j; ++i) {
            Enchantment enchantment = aenchantment[i];
            if (enchantment != null && enchantment.type != null) {
                Items.enchanted_book.func_92113_a(enchantment, container.itemList);
            }
        }

        Iterator<?> iterator = container.itemList.iterator();
        String s1 = gui.searchField.getText().toLowerCase();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack)iterator.next();
            boolean flag = false;
            Iterator<?> iterator1 = itemstack.getTooltip(gui.mc.thePlayer, gui.mc.gameSettings.advancedItemTooltips).iterator();
            while (true) {
                if (iterator1.hasNext()) {
                    String s = (String)iterator1.next();
                    if (!s.toLowerCase().contains(s1)) {
                        continue;
                    }
                    flag = true;
                }
                if (!flag) {
                    iterator.remove();
                }
                break;
            }
        }
        gui.currentScroll = 0.0F;
        container.scrollTo(0.0F);
    }

    public static void stopSearch() {
        if(SEARCH_TASK != null) {
            SEARCH_TASK.cancel(true);
            SEARCH_TASK = null;
        }
    }

}
