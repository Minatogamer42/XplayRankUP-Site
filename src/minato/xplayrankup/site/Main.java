package minato.xplayrankup.site;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;

public class Main extends JavaPlugin implements Listener{
	public static Main main;
	
	public void onEnable() {
		main = this;
		getServer().getPluginManager().registerEvents(main, main);
		
		getServer().getConsoleSender().sendMessage("[XplaySite] iniciado com sucesso");
		
		
	 if(!new File(getDataFolder(), "config.yml").exists()) {
		 saveDefaultConfig();
	 }
	}
	@EventHandler
	public void on(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().equalsIgnoreCase(getConfig().getString("comando"))) {
	    	e.setCancelled(true);
	    	abrirLivro(pegarlivro(), e.getPlayer());
	    	}
		if (e.getMessage().equalsIgnoreCase("/loja")) {
	    	e.setCancelled(true);
	    	abrirLivro(pegarlivro(), e.getPlayer());
		}
		
	}
	
	public ItemStack pegarlivro() {
		ItemStack livro = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) livro.getItemMeta();
		java.util.List<IChatBaseComponent> paginas;
		
		try {
			paginas = (java.util.List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(meta);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return livro;
	   }
		TextComponent textosemclick = new TextComponent(getConfig().getString("Texto1").replace("!espaco!", "\n").replace("&", "§"));
		TextComponent textocomclick = new TextComponent("§bLoja do servidor");
		textocomclick.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("§b§lSite Do Servidor").create()));
		textocomclick.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, "http://loja.xplayrankup.tk/"));
		
		textosemclick.addExtra(textocomclick);
		
		IChatBaseComponent pagina1 = ChatSerializer.a(ComponentSerializer.toString(textosemclick));
		
		paginas.add(pagina1);
		
		meta.setAuthor("xplayrankup");
		
		livro.setItemMeta(meta);
		return livro;
	}
	public void abrirLivro(ItemStack stack, Player p) {
		int slot = p.getInventory().getHeldItemSlot();
		ItemStack playeritem = p.getInventory().getItem(slot);
		p.getInventory().setItemInHand(stack);
		ByteBuf buf = Unpooled.buffer(256);
		buf.setByte(0, 0);
		buf.writerIndex();
		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
		p.getInventory().setItem(slot, playeritem);
	}
	
}
