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
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;

/**
 * Zone where 'Build Headquarters' is allowed during sieges.
 * <b>Interlude implementation:</b> HQ Zones are used for siege mechanics to define areas
 * where clan members can place temporary headquarters (outposts) during castle sieges.
 * <b>Skills:</b> Build Headquarters (247), Build Advanced Headquarters (326)
 * @author Gnacik
 */
public class HqZone extends ZoneType
{
	public HqZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		// Note: HQ Zone parameters from XML are parsed but not stored
		// HqZone extends ZoneType (not ResidenceZone), so it doesn't have residence ID storage
		// Current implementation: Zone identification is handled by zone name and location only
		// These parameters are defined in XML for documentation/future use but have no functional impact
		if ("castleId".equals(name))
		{
			// Parameter parsed but not stored - HQ zones for castles work by location only
		}
		else if ("fortId".equals(name))
		{
			// Parameter parsed but not used - Forts not implemented in Interlude
		}
		else if ("clanHallId".equals(name))
		{
			// Parameter parsed but not stored - HQ zones for clan halls work by location only
		}
		else if ("territoryId".equals(name))
		{
			// Parameter parsed but not used - Territories not implemented in Interlude
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.HQ, true);
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature.isPlayer())
		{
			creature.setInsideZone(ZoneId.HQ, false);
		}
	}
}
