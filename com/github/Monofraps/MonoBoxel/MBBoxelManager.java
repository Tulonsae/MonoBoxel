package com.github.Monofraps.MonoBoxel;

import java.util.ArrayList;
import java.util.List;

public class MBBoxelManager {

	MonoBoxel master = null;
	List<MBBoxel> boxels = null;

	public MBBoxelManager(MonoBoxel plugin) {
		master = plugin;
		boxels = new ArrayList<MBBoxel>();
	}

	public void LoadConfig() {

	}

	public List<MBBoxel> getBoxels() {
		return boxels;
	}
}
