package tntrun.arena.structure;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import tntrun.TNTRun;
import tntrun.arena.Arena;

public class GameZone {

	private HashSet<Block> blockstodestroy = new HashSet<>();
	private LinkedList<BlockState> blocks = new LinkedList<>();

	public Arena arena;
	private final int SCAN_DEPTH = 1;

	public GameZone(Arena arena){
		this.arena = arena;
	}

	public void destroyBlock(Location loc) {
		int y = loc.getBlockY() + 1;
		Block block = null;
		for (int i = 0; i <= SCAN_DEPTH; i++) {
			block = getBlockUnderPlayer(y, loc);
			y--;
			if (block != null) {
				break;
			}
		}
		if (block != null) {
			final Block fblock = block;
			if (!blockstodestroy.contains(fblock)) {
				blockstodestroy.add(fblock);
				new BukkitRunnable() {
					@Override
					public void run() {
						if (arena.getStatusManager().isArenaRunning()) {
							blockstodestroy.remove(fblock);
							TNTRun.getInstance().getSound().BLOCK_BREAK(fblock);
							removeGLBlocks(fblock);
						}
					}
				}.runTaskLater(TNTRun.getInstance(), arena.getStructureManager().getGameLevelDestroyDelay());
			}
		}
	}

	public void regenNow() {
		Iterator<BlockState> bsi = blocks.iterator();
		while (bsi.hasNext()) {
			BlockState bs = bsi.next();
			bs.update(true);
			bsi.remove();
		}
	}

	private void removeGLBlocks(Block block) {
		blocks.add(block.getState());
		saveBlock(block);
		block = block.getRelative(BlockFace.DOWN);
		blocks.add(block.getState());
		saveBlock(block);
	}

	private static double PLAYER_BOUNDINGBOX_ADD = 0.3;

	private Block getBlockUnderPlayer(int y, Location location) {
		PlayerPosition loc = new PlayerPosition(location.getX(), y, location.getZ());
		Block b11 = loc.getBlock(location.getWorld(), +PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
		if (b11.getType() != Material.AIR) {
			return b11;
		}
		Block b12 = loc.getBlock(location.getWorld(), -PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
		if (b12.getType() != Material.AIR) {
			return b12;
		}
		Block b21 = loc.getBlock(location.getWorld(), +PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
		if (b21.getType() != Material.AIR) {
			return b21;
		}
		Block b22 = loc.getBlock(location.getWorld(), -PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
		if (b22.getType() != Material.AIR) {
			return b22;
		}
		return null;
	}

	private final int MAX_BLOCKS_PER_TICK = 10;

	public void saveBlock(Block b) {
		b.setType(Material.AIR);
	}

	/**
	 * Regenerate the broken blocks in the arena. Clear the map containing
	 * the 'blocks to destroy' as this can potentially have residual blocks if
	 * a player quits during a game.
	 *
	 * @return delay in ticks before arena regeneration begins.
	 */
	public int regen() {
		final Iterator<BlockState> bsit = blocks.iterator();
		new BukkitRunnable() {
			@Override
			public void run() {
				for(int i = MAX_BLOCKS_PER_TICK; i >= 0;i--){
					if(bsit.hasNext()){
						try {
							BlockState bs = bsit.next();
							bs.update(true);
							bsit.remove();
						} catch(ConcurrentModificationException ex) {

						}
					} else {
						cancel();
					}
				}
			}
		}.runTaskTimer(TNTRun.getInstance(), 0L, 1L);
		blockstodestroy.clear();

		return arena.getStructureManager().getRegenerationDelay();
	}

	private static class PlayerPosition {

		private double x;
		private int y;
		private double z;

		public PlayerPosition(double x, int y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Block getBlock(World world, double addx, double addz) {
			return world.getBlockAt(NumberConversions.floor(x + addx), y, NumberConversions.floor(z + addz));
		}
	}
}
