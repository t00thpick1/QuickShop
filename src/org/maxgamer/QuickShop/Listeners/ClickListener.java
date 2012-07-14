package org.maxgamer.QuickShop.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.maxgamer.QuickShop.Info;
import org.maxgamer.QuickShop.QuickShop;
import org.maxgamer.QuickShop.Shop;
import org.maxgamer.QuickShop.ShopAction;

/**
 * Handles players clicking on chests:
 * Left click on chest with item	: 	Ask for price
 * Left click on shop		 		: 	Send price
 * Right click on others shop		: 	Send sell menu
 * Right click on own shop	 		: 	Open chest normally
 * @author Netherfoam
 *
 */
public class ClickListener implements Listener{
	QuickShop plugin;
	public ClickListener(QuickShop plugin){
		this.plugin = plugin;
	}
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(e.isCancelled() || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR || e.getClickedBlock().getType() != Material.CHEST) return;
		
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		
		/* ToDo: Move action check to first line of event
		 * Purchase Handling
		 */
		if(e.getAction() == Action.LEFT_CLICK_BLOCK && plugin.getShops().containsKey(e.getClickedBlock().getLocation())){
			Shop shop = plugin.getShop(b.getLocation());
			
			//Text menu
			sendShopInfo(p, shop);
			p.sendMessage(ChatColor.GREEN + "Enter how many you wish to purchase in chat.");
			
			//Add the new action
			HashMap<String, Info> actions = plugin.getActions();
			actions.remove(p.getName());
			Info info = new Info(b.getLocation(), ShopAction.BUY, null);
			actions.put(p.getName(), info);
			
			return;
		}
		/*
		 * Creation handling
		 */
		else if(e.getAction() == Action.LEFT_CLICK_BLOCK && item != null && item.getType() != Material.AIR){
			//Send creation menu.
			Info info = new Info(b.getLocation(), ShopAction.CREATE, e.getItem());
			plugin.getActions().put(p.getName(), info);
			p.sendMessage(ChatColor.GREEN + "Enter how much you wish to sell one "+ ChatColor.YELLOW  + item.getType().toString() + ChatColor.GREEN + " for.");
		}
	}
	
	private void sendShopInfo(Player p, Shop shop){
		sendShopInfo(p, shop, shop.getRemainingStock());
	}
	private void sendShopInfo(Player p, Shop shop, int stock){
		ItemStack items = shop.getItem();
		
		p.sendMessage(ChatColor.DARK_PURPLE + "+---------------------------------------------------+");
		p.sendMessage(ChatColor.DARK_PURPLE + "| " + ChatColor.GREEN + "Shop Information:");
		p.sendMessage(ChatColor.DARK_PURPLE + "| " + ChatColor.YELLOW + stock + " " + items.getType());
		if(plugin.isTool(items.getType())) p.sendMessage(ChatColor.DARK_PURPLE + "| " + ChatColor.GREEN + plugin.getToolPercentage(items) + "% Remaining"); 
		
		Map<Enchantment, Integer> enchs = items.getEnchantments();
		if(enchs!= null){
			for(Entry<Enchantment, Integer> entries : enchs.entrySet()){
				p.sendMessage(ChatColor.DARK_PURPLE + "| " + ChatColor.YELLOW + entries.getKey() .getName() + " " + entries.getValue() );
			}
		}
		p.sendMessage(ChatColor.DARK_PURPLE + "+---------------------------------------------------+");
	}
}