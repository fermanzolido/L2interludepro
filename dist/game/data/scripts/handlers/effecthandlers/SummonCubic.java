/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Cubic;
import org.l2jmobius.gameserver.model.conditions.Condition;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Summon Cubic effect implementation.
 * @author Zoey76
 */
public class SummonCubic extends AbstractEffect
{
	// Enchant calculation constants
	private static final int ENCHANT_SKILL_LEVEL_OFFSET = 100; // Skills above level 100 are enchanted
	private static final int ENCHANT_LEVEL_DIVIDER = 7; // Each 7 enchant levels = +1 cubic skill level
	private static final int BASE_ENCHANTED_CUBIC_LEVEL = 8; // Starting level for enchanted cubic skills
	
	/** Cubic ID. */
	private final int _cubicId;
	/** Cubic power. */
	private final int _cubicPower;
	/** Cubic duration. */
	private final int _cubicDuration;
	/** Cubic activation delay. */
	private final int _cubicDelay;
	/** Cubic maximum casts before going idle. */
	private final int _cubicMaxCount;
	/** Cubic activation chance. */
	private final int _cubicSkillChance;
	
	public SummonCubic(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_cubicId = params.getInt("cubicId", -1);
		
		// Custom AI data.
		_cubicPower = params.getInt("cubicPower", 0);
		_cubicDuration = params.getInt("cubicDuration", 0);
		_cubicDelay = params.getInt("cubicDelay", 0);
		_cubicMaxCount = params.getInt("cubicMaxCount", -1);
		_cubicSkillChance = params.getInt("cubicSkillChance", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		if ((effected == null) || !effected.isPlayer() || effected.isAlikeDead())
		{
			return;
		}
		
		if (_cubicId < 0)
		{
			LOGGER.warning(SummonCubic.class.getSimpleName() + ": Invalid Cubic ID:" + _cubicId + " in skill ID: " + skill.getId());
			return;
		}
		
		final Player player = effected.asPlayer();
		if (player.inObserverMode() || player.isMounted())
		{
			return;
		}
		
		// Calculate cubic skill level based on skill enchant level.
		// For non-enchanted skills (level <= 100): use skill level directly
		// For enchanted skills (level > 100): convert enchant to cubic level
		// Formula: Each 7 enchant levels = +1 cubic skill level, starting at level 8
		// Examples: Level 101 (+1) = cubic level 8, Level 130 (+30) = cubic level 12
		// Maximum cubic skill level is 12 (for skills like 5115-5117)
		int cubicSkillLevel = skill.getLevel();
		if (cubicSkillLevel > ENCHANT_SKILL_LEVEL_OFFSET)
		{
			cubicSkillLevel = ((skill.getLevel() - ENCHANT_SKILL_LEVEL_OFFSET) / ENCHANT_LEVEL_DIVIDER) + BASE_ENCHANTED_CUBIC_LEVEL;
		}
		
		// If cubic is already present, it's replaced.
		final Cubic cubic = player.getCubicById(_cubicId);
		if (cubic != null)
		{
			cubic.stopAction();
			cubic.cancelDisappear();
			player.getCubics().remove(_cubicId);
		}
		else
		{
			// If maximum amount is reached, random cubic is removed.
			// Players with no mastery can have only one cubic.
			final int allowedCubicCount = player.getStat().getMaxCubicCount();
			final int currentCubicCount = player.getCubics().size();
			
			// Extra cubics are removed, one by one, randomly.
			for (int i = 0; i <= (currentCubicCount - allowedCubicCount); i++)
			{
				final int removedCubicId = (int) player.getCubics().keySet().toArray()[Rnd.get(currentCubicCount)];
				final Cubic removedCubic = player.getCubicById(removedCubicId);
				removedCubic.stopAction();
				removedCubic.cancelDisappear();
				player.getCubics().remove(removedCubic.getId());
			}
		}
		
		// Adding a new cubic.
		player.addCubic(_cubicId, cubicSkillLevel, _cubicPower, _cubicDelay, _cubicSkillChance, _cubicMaxCount, _cubicDuration, effected != effector);
		player.broadcastUserInfo();
	}
}
