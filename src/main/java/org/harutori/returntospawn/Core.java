package org.harutori.returntospawn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		// config.ymlが存在しないとき、デフォルトを持ってくる
		this.saveDefaultConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rts")) {
			String msg0 = ChatColor.GRAY + "/rts <addworld/removeworld> <spawn/home> [world name]";
			String msg1 = ChatColor.RED + "[ReturnToSpawn] Invalid command. Please enter in the following format.";
			String msg2 = ChatColor.RED + "[ReturnToSpawn] Invalid command. Please specify at least one world name.";
			// 引数長チェック
			if (args.length < 2) {
				sender.sendMessage(msg1 + "\n" + msg0);
				return true;
			}
			if (args.length < 3) {
				sender.sendMessage(msg2 + "\n" + msg0);
				return true;
			}

			switch (args[0]) {
				case "addworld":
					if (args[1].equals("spawn") || args[1].equals("home")) {
						// プレイヤーなら権限チェック
						if (sender instanceof Player) {
							if (!sender.hasPermission("returntospawn.addworld." + args[1])) {
								sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
								return true;
							}
						}

						// 本処理
						sender.sendMessage(ChatColor.GREEN + "[ReturnToSpawn] The following world(s) have been added for /" + args[1] + " command:");
						List<String> worldsList = getConfig().getStringList("enabled_worlds." + args[1]);
						for (int i = 2; i < args.length; i++) {
							worldsList.add(args[i]);
							sender.sendMessage(ChatColor.YELLOW + "- " + args[i]);
						}
						getConfig().set("enabled_worlds." + args[1], worldsList);
						saveConfig();

						return true;
					} else {
						sender.sendMessage(msg1 + "\n" + msg0);
						return true;
					}
				case "removeworld":
					if (args[1].equals("spawn") || args[1].equals("home")) {
						// プレイヤーなら権限チェック
						if (sender instanceof Player) {
							if (!sender.hasPermission("returntospawn.removeworld." + args[1])) {
								sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
								return true;
							}
						}

						// 本処理
						sender.sendMessage(ChatColor.GREEN + "[ReturnToSpawn] The following world(s) have been removed from /" + args[1] + " command:");
						List<String> worldsList = getConfig().getStringList("enabled_worlds." + args[1]);
						List<String> worldsListNew = new ArrayList<String>();

						for (String world : worldsList) {
							boolean remain = true;
							for (int i = 0; i < args.length; i++) {
								if (world.equals(args[i])) {
									remain = false;
									break;
								}
							}
							if (remain) {
								worldsListNew.add(world);
							} else {
								sender.sendMessage(ChatColor.YELLOW + "- " + world);
							}
						}

						getConfig().set("enabled_worlds." + args[1], worldsListNew);
						saveConfig();

						return true;
					} else {
						sender.sendMessage(msg1 + "\n" + msg0);
						return true;
					}
				default:
					sender.sendMessage(msg1 + "\n" + msg0);
					return true;
			}
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("[ReturnToSpawn] Only online players can execute this command.");
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		World world = player.getWorld();

		List<String> enabledWorlds_spawn = getConfig().getStringList("enabled_worlds.spawn");
		List<String> enabledWorlds_home = getConfig().getStringList("enabled_worlds.home");

		if (cmd.getName().equalsIgnoreCase("spawn")) {
			// ワールドチェック
			if (!enabledWorlds_spawn.contains(world.getName())) {
				player.sendMessage(ChatColor.RED + "This command can't be used in this world.");
				return true;
			}

			// 権限チェック
			if (!player.hasPermission("returntospawn.spawn")) {
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
				return true;
			}

			Location spawnpoint = world.getSpawnLocation();
			spawnpoint.setX(Math.floor(spawnpoint.getX()) + 0.5);
			spawnpoint.setZ(Math.floor(spawnpoint.getZ()) + 0.5);

			// 本処理
			player.teleport(spawnpoint, PlayerTeleportEvent.TeleportCause.PLUGIN);
			player.sendMessage(ChatColor.GREEN + "You have teleported to " + ChatColor.YELLOW + "the Spawn Point" + ChatColor.GREEN + " of this world.");

			return true;
		} else if (cmd.getName().equalsIgnoreCase("sethome")) {
			// ワールドチェック
			if (!enabledWorlds_home.contains(world.getName())) {
				player.sendMessage(ChatColor.RED + "This command can't be used in this world.");
				return true;
			}

			// 権限チェック
			if (!player.hasPermission("returntospawn.sethome")) {
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
				return true;
			}

			Location location = player.getLocation();
			int x = (int)Math.floor(location.getX());
			int y = (int)Math.floor(location.getY());
			int z = (int)Math.floor(location.getZ());

			// 本処理
			getConfig().set("homes." + uuid.toString() + ".name", player.getName());
			getConfig().set("homes." + uuid.toString() + ".coords." + world.getName(), x + " " + y + " " + z);
			saveConfig();
			player.sendMessage(ChatColor.GREEN + "Your home point has been set to " + ChatColor.YELLOW + x + " " + y + " " + z + ChatColor.GREEN + ".");

			return true;
		} else if (cmd.getName().equalsIgnoreCase("home")) {
			// ワールドチェック
			if (!enabledWorlds_home.contains(world.getName())) {
				player.sendMessage(ChatColor.RED + "This command can't be used in this world.");
				return true;
			}

			// 権限チェック
			if (!player.hasPermission("returntospawn.home")) {
				player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
				return true;
			}

			// そのワールドで一度でもhome設定した？
			if (getConfig().contains("homes." + uuid.toString() + ".coords." + world.getName())) {
				String coords = getConfig().getString("homes." + uuid.toString() + ".coords." + world.getName());

				// nullや空文字じゃない？
				if (coords != null) {
					if (!coords.isEmpty()) {
						String[] coordsArray = coords.split(" ");

						// 長さは正しい？
						if (coordsArray.length == 3) {
							// parseできる？
							try {
								double x = Double.parseDouble(coordsArray[0]) + 0.5;
								double y = Double.parseDouble(coordsArray[1]);
								double z = Double.parseDouble(coordsArray[2]) + 0.5;

								Location spawnpoint = new Location(world, x, y, z);

								// 本処理
								player.teleport(spawnpoint, PlayerTeleportEvent.TeleportCause.PLUGIN);
								player.sendMessage(ChatColor.GREEN + "You have teleported to " + ChatColor.YELLOW + "Your Home Point" + ChatColor.GREEN + " of this world.");

								return true;
							} catch (NumberFormatException e) {
								getLogger().info(e.toString());
							}
						}

						// なにか書かれているけど内容がおかしいとき
						getLogger().info(ChatColor.RED + "[ReturnToSpawn] Unexpected format!" + ChatColor.DARK_RED
							+ "\nCommand: " + cmd.getName()
							+ "\nSender: " + player.getName()
							+ "\nUUID: " + uuid.toString()
							+ "\nHome Coords: " + coords);
						player.sendMessage(ChatColor.RED + "You could not teleport because of server side problem...");
						return true;
					}
				}
			}

			// まだsethomeされてないとき
			player.sendMessage(ChatColor.RED + "You have not yet set your home (" + ChatColor.YELLOW + "/sethome" + ChatColor.RED + ") in this world.");
			return true;
		} else {
			return true;
		}
	}
}
