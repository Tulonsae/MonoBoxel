package com.github.Monofraps.MonoBoxel;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MBPlayerManager {

	HashMap<Player, Location> BackportLocations = new HashMap<Player, Location>();

	public void setBackportLocation(Player player) {
		BackportLocations.put(player, player.getLocation());
	}
}
