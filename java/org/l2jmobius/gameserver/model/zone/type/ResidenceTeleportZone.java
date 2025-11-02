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
package org.l2jmobius.gameserver.model.zone.type;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneRespawn;

/**
 * Residence teleport zone (castle/clan hall owner restart areas).
 * <b>Usage:</b> Defines the spawn/teleport area for residence owners (typically inside the castle/clan hall).
 * <b>Behavior:</b> Prevents Summon Friend skill to avoid exploits (summoning players into restricted areas).
 * Based on Kerberos work for custom CastleTeleportZone.
 * @author Nyaran
 */
public class ResidenceTeleportZone extends ZoneRespawn
{
	private int _residenceId;
	
	public ResidenceTeleportZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("residenceId"))
		{
			_residenceId = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		// Prevent Summon Friend (skill 1403) to avoid exploits
		// Residence teleport zones are typically inside castles/clan halls (owner-only areas)
		// Allowing summon friend would let non-owners bypass entry restrictions
		// Behavior: Retail-like protection, also used in SiegeZone, OlympiadStadiumZone, JailZone
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
	}
	
	@Override
	public void oustAllPlayers()
	{
		for (Player player : getPlayersInside())
		{
			if ((player != null) && player.isOnline())
			{
				player.teleToLocation(getSpawnLoc(), 200);
			}
		}
	}
	
	public int getResidenceId()
	{
		return _residenceId;
	}
}
