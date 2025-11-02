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
package org.l2jmobius.gameserver.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.managers.CastleManager;
import org.l2jmobius.gameserver.managers.ZoneManager;
import org.l2jmobius.gameserver.model.actor.instance.Merchant;
import org.l2jmobius.gameserver.model.siege.Castle;

/**
 * Merchant Price Config Data
 * @author KenM
 */
public class MerchantPriceConfigTable implements IXmlReader
{
	
	public static MerchantPriceConfigTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private final Map<Integer, MerchantPriceConfig> _mpcs = new HashMap<>();
	private MerchantPriceConfig _defaultMpc;
	
	protected MerchantPriceConfigTable()
	{
		load();
	}
	
	public MerchantPriceConfig getMerchantPriceConfig(Merchant npc)
	{
		for (MerchantPriceConfig mpc : _mpcs.values())
		{
			if (ZoneManager.getInstance().getRegion(npc).getZones().containsKey(mpc.getZoneId()))
			{
				return mpc;
			}
		}
		
		return _defaultMpc;
	}
	
	public MerchantPriceConfig getMerchantPriceConfig(int id)
	{
		return _mpcs.get(id);
	}
	
	@Override
	public void load()
	{
		_mpcs.clear();
		parseDatapackFile("data/MerchantPriceConfig.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _mpcs.size() + " merchant price configs.");
	}
	
	/**
	 * Loads merchant price configs. This method is kept for backwards compatibility.
	 * @deprecated Use {@link #load()} instead.
	 */
	@Deprecated
	public void loadInstances()
	{
		load();
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, "list", listNode ->
		{
			final NamedNodeMap listAttrs = listNode.getAttributes();
			final Integer defaultPriceConfigId = parseInteger(listAttrs, "defaultPriceConfig");
			if (defaultPriceConfigId == null)
			{
				throw new IllegalStateException("merchantPriceConfig must define a 'defaultPriceConfig' attribute");
			}
			
			forEach(listNode, "priceConfig", priceConfigNode ->
			{
				final NamedNodeMap attrs = priceConfigNode.getAttributes();
				
				// Parse required attributes
				final Integer id = parseInteger(attrs, "id");
				if (id == null)
				{
					throw new IllegalStateException("Must define the priceConfig 'id'");
				}
				
				final String name = parseString(attrs, "name");
				if (name == null)
				{
					throw new IllegalStateException("Must define the priceConfig 'name'");
				}
				
				final Integer baseTax = parseInteger(attrs, "baseTax");
				if (baseTax == null)
				{
					throw new IllegalStateException("Must define the priceConfig 'baseTax'");
				}
				
				// Parse optional attributes
				final int castleId = parseInteger(attrs, "castleId", -1);
				final int zoneId = parseInteger(attrs, "zoneId", -1);
				
				_mpcs.put(id, new MerchantPriceConfig(id, name, baseTax, castleId, zoneId));
			});
			
			// Set default merchant price config
			final MerchantPriceConfig defaultMpc = getMerchantPriceConfig(defaultPriceConfigId);
			if (defaultMpc == null)
			{
				throw new IllegalStateException("'defaultPriceConfig' points to a non-loaded priceConfig: " + defaultPriceConfigId);
			}
			_defaultMpc = defaultMpc;
		});
	}
	
	public void updateReferences()
	{
		for (MerchantPriceConfig mpc : _mpcs.values())
		{
			mpc.updateReferences();
		}
	}
	
	/**
	 * @author KenM
	 */
	public static class MerchantPriceConfig
	{
		private final int _id;
		private final String _name;
		private final int _baseTax;
		private final int _castleId;
		private Castle _castle;
		private final int _zoneId;
		
		public MerchantPriceConfig(int id, String name, int baseTax, int castleId, int zoneId)
		{
			_id = id;
			_name = name;
			_baseTax = baseTax;
			_castleId = castleId;
			_zoneId = zoneId;
		}
		
		/**
		 * @return Returns the id.
		 */
		public int getId()
		{
			return _id;
		}
		
		/**
		 * @return Returns the name.
		 */
		public String getName()
		{
			return _name;
		}
		
		/**
		 * @return Returns the baseTax.
		 */
		public int getBaseTax()
		{
			return _baseTax;
		}
		
		/**
		 * @return Returns the baseTax / 100.0.
		 */
		public double getBaseTaxRate()
		{
			return _baseTax / 100.0;
		}
		
		/**
		 * @return Returns the castle.
		 */
		public Castle getCastle()
		{
			return _castle;
		}
		
		/**
		 * @return Returns the zoneId.
		 */
		public int getZoneId()
		{
			return _zoneId;
		}
		
		public boolean hasCastle()
		{
			return _castle != null;
		}
		
		public double getCastleTaxRate()
		{
			return hasCastle() ? _castle.getTaxRate() : 0.0;
		}
		
		public int getTotalTax()
		{
			return hasCastle() ? (_castle.getTaxPercent() + _baseTax) : _baseTax;
		}
		
		public double getTotalTaxRate()
		{
			return getTotalTax() / 100.0;
		}
		
		public void updateReferences()
		{
			if (_castleId > 0)
			{
				_castle = CastleManager.getInstance().getCastleById(_castleId);
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final MerchantPriceConfigTable INSTANCE = new MerchantPriceConfigTable();
	}
}
