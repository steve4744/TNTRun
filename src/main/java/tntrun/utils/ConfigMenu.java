package tntrun.utils;

public enum ConfigMenu {

	RED_WOOL(4),
	BLAZE_ROD(10),
	WOODEN_AXE(11),
	BONE(12),
	MAGMA_CREAM(14),
	NETHER_STAR(15),
	ENDER_PEARL(16),
	GUNPOWDER(19),
	REDSTONE(20),
	GLOWSTONE_DUST(21),
	OAK_SIGN(23),
	DIAMOND(25);

	private int slot;

	ConfigMenu(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}
}
